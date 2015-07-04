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
package com.datatorrent.stram;

import java.net.InetSocketAddress;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.datatorrent.api.Context.OperatorContext;

import com.datatorrent.common.util.FSStorageAgent;
import com.datatorrent.stram.StreamingContainerManager.ContainerResource;
import com.datatorrent.stram.engine.GenericTestOperator;
import com.datatorrent.stram.engine.TestGeneratorInputOperator;
import com.datatorrent.stram.plan.logical.LogicalPlan;
import com.datatorrent.stram.plan.physical.PTOperator;
import com.datatorrent.stram.support.StramTestSupport.TestMeta;

/**
 *
 */
public class AlertsManagerTest
{
  @Rule
  public TestMeta testMeta = new TestMeta();

  @Test
  public void testAlertManager() throws JSONException
  {
    LogicalPlan dag = new LogicalPlan();
    dag.setAttribute(com.datatorrent.api.Context.DAGContext.APPLICATION_PATH, testMeta.dir);
    dag.setAttribute(OperatorContext.STORAGE_AGENT, new FSStorageAgent(testMeta.dir, null));

    TestGeneratorInputOperator in = dag.addOperator("in", TestGeneratorInputOperator.class);
    GenericTestOperator o = dag.addOperator("o", GenericTestOperator.class);
    dag.addStream("stream", in.outport, o.inport1);

    final StreamingContainerManager dnm = new StreamingContainerManager(dag);
    Assert.assertNotNull(dnm.assignContainer(new ContainerResource(0, "container1", "localhost", 0, 0, null), InetSocketAddress.createUnresolved("localhost", 0)));

    Thread t = new Thread()
    {
      @Override
      public void run()
      {
        while (true) {
          for (PTOperator o : dnm.getPhysicalPlan().getAllOperators().values()) {
            o.setState(PTOperator.State.ACTIVE);
          }
          dnm.processEvents();
          try {
            Thread.sleep(500);
          }
          catch (InterruptedException ex) {
            return;
          }
        }
      }

    };

    t.start();

    AlertsManager alertsManager = dnm.getAlertsManager();
    JSONObject json = new JSONObject();
    JSONObject filter = new JSONObject();
    JSONObject escalation = new JSONObject();
    JSONArray actions = new JSONArray();
    JSONObject action = new JSONObject();
    filter.put("class", GenericTestOperator.class.getName());
    filter.put("inputPort", GenericTestOperator.IPORT1);
    filter.put("outputPort", GenericTestOperator.OPORT1);
    escalation.put("class", GenericTestOperator.class.getName());
    escalation.put("inputPort", GenericTestOperator.IPORT1);
    action.put("class", GenericTestOperator.class.getName());
    action.put("outputPort", GenericTestOperator.OPORT1);
    action.put("inputPort", GenericTestOperator.IPORT1);
    actions.put(action);
    json.put("streamName", "o." + GenericTestOperator.OPORT1);
    json.put("filter", filter);
    json.put("escalation", escalation);
    json.put("actions", actions);

    // create the alert
    alertsManager.createAlert("alertName", json.toString());
    JSONObject alerts = alertsManager.listAlerts();
    Assert.assertEquals("alert name should match", "alertName", alerts.getJSONArray("alerts").getJSONObject(0).getString("name"));
    Assert.assertNotNull(alertsManager.getAlert("alertName"));

    // make sure we have the operators
    Assert.assertEquals("there should be 5 operators", 5, dag.getAllOperators().size());
    Assert.assertEquals("there should be 4 streams", 4, dag.getAllStreams().size());

    // delete the alert
    alertsManager.deleteAlert("alertName");
    alerts = alertsManager.listAlerts();
    Assert.assertEquals("there should be no more alerts after delete", 0, alerts.getJSONArray("alerts").length());

    // make sure the operators are removed
    Assert.assertEquals("there should be 2 operator", 2, dag.getAllOperators().size());
    Assert.assertEquals("there should be 1 stream", 1, dag.getAllStreams().size());

    t.interrupt();
    try {
      t.join();
    }
    catch (InterruptedException ex) {
      // ignore
    }
  }

}
