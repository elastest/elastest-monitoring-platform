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
import org.springframework.util.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class SqlDriverTest
{
    public void setUp()
    {
        Initialize.prepareDbInitScripts();
        Initialize.initializeTestDb();
    }

    @Test
    public void testgetDBConnection()
    {
        Assert.notNull(SqlDriver.getDBConnection(), "Dbconnection object");
    }

    @Test
    public void testgetDbTablesList()
    {
        setUp();
        assertEquals("list of tables", 5, SqlDriver.getDbTablesList().size());
    }

    @Test
    public void testgetUserSpaces()
    {
        setUp();
        assertEquals("list of spaces for test user", 1, SqlDriver.getUserSpaces(1).size());
    }

    @Test
    public void testgetSpaceSeries()
    {
        setUp();
        assertEquals("list of series for a space for test user", 1, SqlDriver.getSpaceSeries(1).size());
    }

    @Test
    public void testisDuplicateUser()
    {
        setUp();
        Assert.isTrue(SqlDriver.isDuplicateUser("testuser"), "testing for duplicate user");
    }

    @Test
    public void testisValidApikey()
    {
        setUp();
        Assert.isTrue(SqlDriver.isValidApikey("testuser", "7ddbba60-8667-11e7-bb31-be2e44b06b34"), "testing for api key validation");
        Assert.isTrue(SqlDriver.isValidApikey(1, "7ddbba60-8667-11e7-bb31-be2e44b06b34"), "testing for api key validation");
    }

    @Test
    public void testisValidPassword()
    {
        setUp();
        Assert.isTrue(SqlDriver.isValidPassword(1, "test"), "testing for password verification");
    }

    @Test
    public void testisDuplicateSpace()
    {
        setUp();
        Assert.isTrue(SqlDriver.isDuplicateSpace("testuser", "testspace"), "testing for duplicate space");
    }

    @Test
    public void testisDuplicateSeries()
    {
        setUp();
        Assert.isTrue(SqlDriver.isDuplicateSeries("testuser", "testseries", "testspace"), "testing for duplicate series");
    }

    @Test
    public void testaddSpace()
    {
        setUp();
        assertNotEquals("add a new space", -1, SqlDriver.addSpace("testuser", "somespace", "somequeryuser", "blah-blah"));
    }

    @Test
    public void testaddSeries()
    {
        setUp();
        assertNotEquals("add a new series", -1, SqlDriver.addSeries("newseries", "unixtime:s msgtype:json", 1));
    }

    @Test
    public void testaddUser()
    {
        setUp();
        assertNotEquals("add a new user", -1, SqlDriver.addUser("newuser", "somepassword", "some-key"));
    }

    @Test
    public void testgetUserId()
    {
        setUp();
        assertEquals("get user id", 1, SqlDriver.getUserId("testuser"));
    }

    @Test
    public void testgetSpaceId()
    {
        setUp();
        assertEquals("get space id", 1, SqlDriver.getSpaceId("testuser", "testspace"));
        assertEquals("get space id", 1, SqlDriver.getSpaceId(1, "testspace"));
    }

    @Test
    public void testgetSeriesId()
    {
        setUp();
        assertEquals("get series id", 1, SqlDriver.getSeriesId("testseries", 1));
    }

    @Test
    public void testgetSeriesMsgFormat()
    {
        setUp();
        assertEquals("get series msg format id", "unixtime:s msgtype:json", SqlDriver.getSeriesMsgFormat("testseries", 1));
    }

    @Test
    public void testgetGlobalTopicsList()
    {
        setUp();
        assertEquals("list of spaces", 1, SqlDriver.getGlobalTopicsList().size());
    }

    @Test
    public void testaddPingEntry()
    {
        setUp();
        assertNotEquals("add a new ping entry", -1, SqlDriver.addPingEntry("testurl", "testurl", 30000, 1, "code", "testuser"));
        assertNotEquals("update an existing ping entry", -1, SqlDriver.addPingEntry("testurl", "testurl", 30000, 1, "code", "testuser"));
        assertNotNull("getting a ping object", SqlDriver.getPingData(1,1));
        assertNotNull("getting list of pings", SqlDriver.getGlobalPingList());
        assertEquals("getting list of pings", 2, SqlDriver.getGlobalPingList().size());
    }
}
