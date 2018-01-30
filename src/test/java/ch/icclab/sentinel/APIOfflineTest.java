package ch.icclab.sentinel;/*
 * Copyright (c) 2017. Cyclops-Labs Gmbh
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

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertEquals;

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class APIOfflineTest {
    private APIController apiController;

    private void setUp()
    {
        Initialize.prepareDbInitScripts();
        Initialize.initializeTestDb();
    }

    @Test
    public void testgetAPIs() {
        apiController = new APIController();
        ResponseEntity response = apiController.getApis();
        assertEquals("status code", 200, response.getStatusCodeValue());
    }

    @Test
    public void testcreateUser() {
        apiController = new APIController();
        ResponseEntity response = apiController.createUser("", "");
        assertEquals("status code", 401, response.getStatusCodeValue());
        response = apiController.createUser("{\"login\":\"username\", \"password\":\"some-password\"}", "sometoken");
        assertEquals("status code", 201, response.getStatusCodeValue());
        response = apiController.createUser("{\"login\":\"username\", \"password\":\"some-password\"}", "sometoken");
        assertEquals("status code", 409, response.getStatusCodeValue());
        response = apiController.createUser("{\"login\":\"username1\", \"password\":\"some-password\"}", "somewrongtoken");
        assertEquals("status code", 401, response.getStatusCodeValue());
        response = apiController.createUser(null, "somewrongtoken");
        assertEquals("status code", 401, response.getStatusCodeValue());
    }

    @Test
    public void testlocateUserData() {
        setUp();
        apiController = new APIController();
        ResponseEntity response = apiController.locateUserData("", "testuser");
        assertEquals("status code", 401, response.getStatusCodeValue());
        response = apiController.locateUserData("7ddbba60-8667-11e7-bb31-be2e44b06b34", "testuser");
        assertEquals("status code", 200, response.getStatusCodeValue());
    }

    @Test
    public void testlocateUserKey() {
        setUp();
        apiController = new APIController();
        ResponseEntity response = apiController.locateUserKey("test", "testuser");
        assertEquals("status code", 200, response.getStatusCodeValue());
        response = apiController.locateUserKey("test1", "testuser");
        assertEquals("status code", 401, response.getStatusCodeValue());
        response = apiController.locateUserKey("test", "someelse");
        assertEquals("status code", 400, response.getStatusCodeValue());
    }

    @Test
    public void testcreateSpace() {
        setUp();
        apiController = new APIController();
        ResponseEntity response = apiController.createSpace("{}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 400, response.getStatusCodeValue());
        response = apiController.createSpace("{\"name\":\"space-name\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 201, response.getStatusCodeValue());
        response = apiController.createSpace("{\"name\":\"space-name\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 409, response.getStatusCodeValue());
        response = apiController.createSpace("{\"name\":\"space-name\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b35");
        assertEquals("status code", 401, response.getStatusCodeValue());
    }

    @Test
    public void testcreateSeries() {
        setUp();
        apiController = new APIController();
        ResponseEntity response = apiController.createSeries("{}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 400, response.getStatusCodeValue());
        response = apiController.createSeries("{\"name\":\"series-name\", \"spaceName\":\"parent-space-name\", \"msgSignature\":\"msg-signature\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 400, response.getStatusCodeValue());
        response = apiController.createSeries("{\"name\":\"series-name\", \"spaceName\":\"testspace\", \"msgSignature\":\"msg-signature\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b35");
        assertEquals("status code", 401, response.getStatusCodeValue());
        response = apiController.createSeries("{\"name\":\"series-name\", \"spaceName\":\"testspace\", \"msgSignature\":\"msg-signature\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 201, response.getStatusCodeValue());
        response = apiController.createSeries("{\"name\":\"series-name\", \"spaceName\":\"testspace\", \"msgSignature\":\"msg-signature\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 409, response.getStatusCodeValue());
    }

    @Test
    public void testgetEndpointInfo() {
        setUp();
        apiController = new APIController();
        ResponseEntity response = apiController.getEndpointInfo("testuser", "testuser");
        assertEquals("status code", 401, response.getStatusCodeValue());
        response = apiController.getEndpointInfo("testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 200, response.getStatusCodeValue());
        response = apiController.getEndpointInfo("test-user4", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 401, response.getStatusCodeValue());
    }

    @Test
    public void testreturnError() {
        apiController = new APIController();
        ResponseEntity response = apiController.returnError();
        assertEquals("status code", 501, response.getStatusCodeValue());
    }

    @Test
    public void testCreatePingBack() {
        setUp();
        apiController = new APIController();
        ResponseEntity response = apiController.createPingBack("{}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        assertEquals("status code", 400, response.getStatusCodeValue());
        //response = apiController.createPingBack("{\"pingURL\":\"admin-dashboard.cyclops-labs.io:8888/\",\"reportURL\":\"localhost:5000/\",\"periodicity\":30000,\"toleranceFactor\":2,\"method\":\"body,status,up\"}", "testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34");
        //assertEquals("status code", 201, response.getStatusCodeValue());
    }
}
