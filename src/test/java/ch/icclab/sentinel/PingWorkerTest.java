package ch.icclab.sentinel;

/*
 * Copyright (c) 2017. ZHAW - ICCLab
 *  All Rights Reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may obtain
 *     a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *     WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *     License for the specific language governing permissions and limitations
 *     under the License.
 */

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PingWorkerTest {
    @Test
    public void testrun()
    {
        PingWorker test = new PingWorker("http://blog.zhaw.ch/icclab/", "http://localhost:5000/", 1, "code");
        ExecutorService PingWorkerPool = Executors.newFixedThreadPool(1);
        PingWorkerPool.submit(test);
        PingWorker test1 = new PingWorker("https://blog.zhaw.ch/icclab/", "http://localhost:5000/", 1, "code");
        PingWorkerPool.submit(test1);
        HealthEventsCache testCache = new HealthEventsCache(10);
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        testCache.insertEvent("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/", 1234561l, "NOK");
        assertTrue("testing if insertion succeeded or not", testCache.getEventTraceHistory("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/").length >= 1);
        assertEquals("testing for last event time", 1234561l, testCache.getLastEventTime("http://sentinel.demonstrator.info:9000/", "http://localhost:5000/"));
        //test.run();
    }
}
