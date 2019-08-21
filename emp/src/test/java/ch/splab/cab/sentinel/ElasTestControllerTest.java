package ch.splab.cab.sentinel;
/*
 *  Copyright (c) 2018. Service Prototyping Lab, ZHAW
 *   All Rights Reserved.
 *
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may
 *       not use this file except in compliance with the License. You may obtain
 *       a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *       WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *       License for the specific language governing permissions and limitations
 *       under the License.
 *
 *
 *       Author: Piyush Harsh,
 *       URL: piyush-harsh.info
 *       Email: piyush.harsh@zhaw.ch
 */

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertEquals;

import ch.splab.cab.sentinel.controller.extensions.ElasTest;

public class ElasTestControllerTest {
    private ElasTest elasTestController;

    private void setUp()
    {
        Initialize.prepareDbInitScripts();
        Initialize.initializeTestDb();
    }

    @Test
    public void testgetAPIs() {
        elasTestController = new ElasTest();
        ResponseEntity response = elasTestController.getApis();
        assertEquals("status code", 200, response.getStatusCodeValue());
    }

    @Test
    public void testgetTJobStats() {
        InfluxDBClient.init();
        elasTestController = new ElasTest();
        //assertNull(elasTestController.getTJobStats("7ddbba60-8667-11e7-bb31-be2e44b06b34", "testuser", "user-1-testspace", "testseries", "1"));
    }
}
