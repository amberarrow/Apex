/*
 *  Copyright (c) 2012-2013 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.bufferserver.packet;

import com.malhartech.bufferserver.util.Codec;

/**
 *
 * @author Chetan Narsude <chetan@malhar-inc.com>
 */
public class PayloadTuple extends Tuple
{
  public PayloadTuple(byte[] array, int offset, int length)
  {
    super(array, offset, length);
  }

  @Override
  public MessageType getType()
  {
    return MessageType.PAYLOAD;
  }

  @Override
  public int getPartition()
  {
    return readVarInt(offset + 1, offset + length);
  }

  @Override
  public int getWindowId()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getDataOffset()
  {
    int dataOffset = this.offset + 1;
    while (buffer[dataOffset++] < 0) {
    }
    return dataOffset;
  }

  @Override
  public int getBaseSeconds()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getWindowWidth()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public static byte[] getSerializedTuple(int partition, int size)
  {
    int bits = 32 - Integer.numberOfLeadingZeros(partition);
    do {
      size++;
    }
    while ((bits -= 7) > 0);

    byte[] array = new byte[size];
    Codec.writeRawVarint32(size, array, 0);
    return array;
  }

}