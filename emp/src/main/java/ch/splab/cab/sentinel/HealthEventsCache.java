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

import ch.splab.cab.sentinel.dao.PingEvent;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;

public class HealthEventsCache {
    final static Logger logger = Logger.getLogger(HealthEventsCache.class);
    HashMap<String, LinkedList<PingEvent>> eventsCache;

    HealthEventsCache(int size)
    {
        eventsCache = new HashMap<>(size);
    }

    public PingEvent[] getEventTraceHistory(String pingURL, String reportURL)
    {
        String key = HelperMethods.generateSHA256Hash(pingURL+reportURL);
        if(eventsCache.containsKey(key)) return eventsCache.get(key).toArray(new PingEvent[eventsCache.get(key).size()]);
        return null;
    }

    synchronized boolean insertEvent(String pingURL, String reportURL, long timestamp, String outcome)
    {
        String key = HelperMethods.generateSHA256Hash(pingURL+reportURL);
        if(!eventsCache.containsKey(key))
        {
            eventsCache.put(key, new LinkedList<PingEvent>());
        }
        while(eventsCache.get(key).size() >= 10) {
            PingEvent discarded = eventsCache.get(key).removeLast();
            logger.info("removed stale event for url: " + pingURL + ", report-url: " + reportURL + ": " + discarded.eventTime + ", " + discarded.status);
        }
        eventsCache.get(key).addFirst(new PingEvent(timestamp, outcome));
        logger.info("inserted event for url: " + pingURL + ", report-url: " + reportURL + ": " + timestamp + ", status=" + outcome);
        return true;
    }

    long getLastEventTime(String pingURL, String reportURL)
    {
        String key = HelperMethods.generateSHA256Hash(pingURL+reportURL);
        if(eventsCache.containsKey(key) && eventsCache.get(key).size() > 0) return eventsCache.get(key).getFirst().eventTime;
        return -1l;
    }

    void cleanCache()
    {
        //do a pass over all keys -
    }
}

