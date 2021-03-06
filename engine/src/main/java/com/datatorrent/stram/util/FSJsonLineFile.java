/**
 * Copyright (C) 2015 DataTorrent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.stram.util;

import java.io.Closeable;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>FSJsonLineFile class.</p>
 *
 * @since 1.0.2
 */
public class FSJsonLineFile implements Closeable
{
  private final FileSystem fs;
  private final ObjectMapper objectMapper;
  private final FSDataOutputStream os;
  private static final Logger LOG = LoggerFactory.getLogger(FSJsonLineFile.class);

  public FSJsonLineFile(Path path, FsPermission permission) throws IOException
  {
    fs = FileSystem.newInstance(path.toUri(), new Configuration());
    FSDataOutputStream myos;
    if (fs.exists(path)) {
      try {
        // happens if not the first application attempt
        myos = fs.append(path);
      }
      catch (IOException ex) {
        LOG.warn("Caught exception (OK during unit test): {}", ex.getMessage());
        myos = FileSystem.create(fs, path, permission);
      }
    }
    else {
      myos = FileSystem.create(fs, path, permission);
    }
    os = myos;
    this.objectMapper = (new JSONSerializationProvider()).getContext(null);
  }

  public synchronized void append(JSONObject json) throws IOException
  {
    os.writeBytes(json.toString() + "\n");
    os.hflush();
  }

  public synchronized void append(Object obj) throws IOException
  {
    os.writeBytes(objectMapper.writeValueAsString(obj) + "\n");
    os.hflush();
  }

  @Override
  public void close() throws IOException
  {
    os.close();
    fs.close();
  }

}
