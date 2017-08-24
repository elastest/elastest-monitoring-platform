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

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.junit.Assert;
import org.junit.Test;

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class InfluxDBClientTest {

    @Test
    public void testinit()
    {
        Assert.assertTrue("initialize root user", InfluxDBClient.init());
    }

    @Test
    public void testaddUser()
    {
        InfluxDBClient.init();
        Assert.assertFalse("adding influx user", InfluxDBClient.addUser("testspace", "someuser", "somepass"));
    }

    @Test
    public void testaddPoint()
    {
        InfluxDBClient.init();
        Assert.assertFalse("adding a dockerstat message", InfluxDBClient.addPoint("some-topic", "docker-stats", "{\"agent\": \"sentinel-docker-agent\", \"host\": \"clt-mob-t-6285\", \"unixtime\": \"1503578338.443743\", \"values\": []}"));
    }

    @Test
    public void testaddDb()
    {
        InfluxDBClient.init();
        Assert.assertFalse("adding a test DB", InfluxDBClient.addDB("sometopic"));
    }

    @Test
    public void testremoveDb()
    {
        InfluxDBClient.init();
        Assert.assertFalse("removing a test db", InfluxDBClient.removeDB("sometopic"));
    }
}
