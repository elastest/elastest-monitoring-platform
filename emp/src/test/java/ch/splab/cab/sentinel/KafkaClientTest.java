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

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class KafkaClientTest {
    @Test
    public void testlistTopics()
    {
        Assert.assertNotNull("list of kafka topics", KafkaClient.listTopics());
    }

    @Test
    public void testTopicManagement()
    {
        Assert.assertTrue("checking topic creation", KafkaClient.createTopic("testtopicVY23SFUB"));
        Assert.assertTrue("checking topic deletion", KafkaClient.deleteTopic("testtopicVY23SFUB"));
    }
}
