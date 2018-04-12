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

import ch.splab.cab.sentinel.dao.HealthCheckInput;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;

public class HealthCheckManager extends Thread
{
    final static Logger logger = Logger.getLogger(HealthCheckManager.class);
    private static long periodicity = 30000l; //30 seconds periodicity
    private HashMap<String, HealthCheckInput> activeChecks;
    private boolean isSafe;

    public HealthCheckManager()
    {
        activeChecks = new HashMap<>(100);
        isSafe = true;
    }

    void updatePingList(LinkedList<HealthCheckInput> list)
    {
        while (!isSafe)
        {
            try
            {
                Thread.sleep(5);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        isSafe = false;
        logger.info("Received a new ping list: Size=" + list.size());
        activeChecks.clear(); // empties the entries
        for(HealthCheckInput element:list)
        {
            String key = HelperMethods.generateSHA256Hash(element.pingURL+element.reportURL);
            activeChecks.put(key, element);
            //if(activeChecks.containsKey(key)) activeChecks.replace(key, element);
            //else
            //    activeChecks.put(key, element);
        }
        isSafe = true;
    }

    public void run()
    {
        logger.info("Starting health check manager thread.");
        while(true)
        {
            while (!isSafe)
            {
                try
                {
                    Thread.sleep(5);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            isSafe = false;
            //iterating over ping elements
            for(String key:activeChecks.keySet())
            {
                HealthCheckInput temp = activeChecks.get(key);
                long lastEvent = Application.eventsCache.getLastEventTime(temp.pingURL, temp.reportURL);
                if(System.currentTimeMillis() - lastEvent > temp.periodicity)
                {
                    Runnable checkWorker = new PingWorker(temp.pingURL, temp.reportURL, temp.toleranceFactor, temp.method);
                    Application.PingWorkerPool.execute(checkWorker);
                }
            }
            //lock can be released here
            isSafe = true;
            try
            {
                Thread.sleep(periodicity);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            logger.info("Health check manager starting another run.");
        }
    }
}
