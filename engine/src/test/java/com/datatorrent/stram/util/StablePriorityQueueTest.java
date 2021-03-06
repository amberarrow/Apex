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

import static org.junit.Assert.*;
import org.junit.*;

import com.datatorrent.stram.util.StablePriorityQueue;

/**
 *
 */
@Ignore
public class StablePriorityQueueTest
{

  public StablePriorityQueueTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @Before
  public void setUp()
  {
  }

  @After
  public void tearDown()
  {
  }

  /**
   * Test of element method, of class StablePriorityQueue.
   */
  @Test
  public void testElement()
  {
    System.out.println("element");
    StablePriorityQueue<Integer> instance = new StablePriorityQueue<Integer>(1);
    Integer i = 10;
    instance.add(i);
    Object result = instance.element();
    assertEquals(i, result);
  }

  /**
   * Test of offer method, of class StablePriorityQueue.
   */
  @Test
  public void testOffer()
  {
    System.out.println("offer");
    StablePriorityQueue<Integer> instance = new StablePriorityQueue<Integer>(1);
    Integer i = 10;
    assertTrue(instance.offer(i));
    Object result = instance.peek();
    assertEquals(i, result);
  }

}
