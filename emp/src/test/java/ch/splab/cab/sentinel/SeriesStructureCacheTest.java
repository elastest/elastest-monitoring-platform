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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class SeriesStructureCacheTest {

    public void setUp()
    {
        Initialize.prepareDbInitScripts();
        Initialize.initializeTestDb();
    }

    @Test
    public void testgetSeriesSignature()
    {
        setUp();
        SeriesStructureCache cache = new SeriesStructureCache(10);
        assertEquals("testing series cache size", 2, cache.getSeriesSignature("user-1-testspace", "testseries").size());
        assertNull("checking for no entry for series structure", cache.getSeriesSignature("testspace", "testseries"));
    }
}
