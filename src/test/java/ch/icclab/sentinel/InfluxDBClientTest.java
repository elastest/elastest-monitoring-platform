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
        Assert.assertFalse("adding a dockerstat message", InfluxDBClient.addPoint("user-1-testspace", "testseries", "{\"agent\": \"sentinel-docker-agent\", \"host\": \"clt-mob-t-6285\", \"unixtime\": \"1503582686.029212\", \"values\": [{\"id\": \"f7052201beb871318e2aa979f2d834244b96a7499f564f132c5dcf0dcfa7df64\", \"metrics\": [{\"py/object\": \"__main__.SentinelElement\", \"key\": \"networks_eth0_rx_bytes\", \"type\": \"long\", \"value\": 0}, {\"py/object\": \"__main__.SentinelElement\", \"key\": \"networks_eth0_tx_bytes\", \"type\": \"long\", \"value\": 0}, {\"py/object\": \"__main__.SentinelElement\", \"key\": \"memory_stats_usage\", \"type\": \"long\", \"value\": 301469696}, {\"py/object\": \"__main__.SentinelElement\", \"key\": \"cpu_usage_total\", \"type\": \"long\", \"value\": 0}], \"name\": \"dockersupport_sentinel_1\"}]}"));
        Assert.assertFalse("adding a zane-sensor message", InfluxDBClient.addPoint("zane-sensor-data", "id-00001", "1503227532.99 CO=0.0049632693076 LPG=0.00765900724799 SMOKE=0.0204345961303"));
        Assert.assertFalse("adding a codelogger message", InfluxDBClient.addPoint("user-1-testspace", "testseries", "{\"agent\": \"sentinel-internal-log-agent\", \"file\": \"code-agent.py\", \"level\": \"info\", \"method\": \"m:126\", \"msg\": \"this is an info method\"}"));
        Assert.assertFalse("adding a codelogger message", InfluxDBClient.addPoint("user-1-testspace", "testseries", "166244162"));
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
