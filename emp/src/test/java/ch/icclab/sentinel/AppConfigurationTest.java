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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class AppConfigurationTest {
    @Test
    public void testgetStreamDBUser()
    {
        assertEquals("get stream DB user", "testroot", AppConfiguration.getStreamDBUser());
    }

    @Test
    public void testgetStreamDBPass()
    {
        assertEquals("get stream DB user pass", "testpass", AppConfiguration.getStreamDBPass());
    }

    @Test
    public void testgetStreamDBType()
    {
        assertEquals("get stream DB type", "influxdb", AppConfiguration.getStreamDBType());
    }

    @Test
    public void testgetStreamDBURL()
    {
        assertEquals("get stream url value", "localhost:8086", AppConfiguration.getStreamDBURL());
    }

    @Test
    public void testgetStreamAccessUrl()
    {
        assertEquals("get stream access url value", "localhost:8083", AppConfiguration.getStreamAccessUrl());
    }

    @Test
    public void testgetSentinelDBType()
    {
        assertEquals("get sentinel db type value", "sqlite", AppConfiguration.getSentinelDBType());
    }

    @Test
    public void testgetSentinelDBURL()
    {
        assertEquals("get sentinel db url value", "/tmp/test.db", AppConfiguration.getSentinelDBURL());
    }

    @Test
    public void testgetKafkaURL()
    {
        assertEquals("get kafka url value", "kafka.cloudlab.zhaw.ch:9092", AppConfiguration.getKafkaURL());
    }

    @Test
    public void testgetZookeeperURL()
    {
        assertEquals("get zookeeper url value", "kafka.cloudlab.zhaw.ch:2181", AppConfiguration.getZookeeperURL());
    }

    @Test
    public void testgetTopicCheckWaitingPeriod()
    {
        assertEquals("get periodicity value", 30000, AppConfiguration.getTopicCheckWaitingPeriod());
    }

    @Test
    public void testgetAdminToken()
    {
        assertEquals("get admin token value", "sometoken", AppConfiguration.getAdminToken());
    }

    @Test
    public void testinit()
    {
        AppConfiguration conf = new AppConfiguration();
        conf.init();
        assertNull("get kafka serializer value", AppConfiguration.getKafkaKeySerializer());
    }
}
