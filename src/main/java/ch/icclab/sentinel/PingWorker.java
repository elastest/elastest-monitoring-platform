package ch.icclab.sentinel;

/*
 * Copyright (c) 2017. ZHAW - ICCLab
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

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
import ch.icclab.sentinel.dao.PingEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class PingWorker implements Runnable
{
    final static Logger logger = Logger.getLogger(PingWorker.class);
    private String pingURL;
    private String reportURL;
    private int toleranceCount;
    private String method;
    private OkHttpClient client;

    public PingWorker(String pUrl, String rUrl, int tCount, String met)
    {
        pingURL = pUrl;
        reportURL = rUrl;
        toleranceCount = tCount;
        method = met;
        client = new OkHttpClient();
    }

    @Override
    public void run() {
        logger.info("Perform ping run - " + pingURL + ", check method is: " + method);
        Request request = new Request.Builder().url(pingURL).addHeader("Content-Type", "application/json").build();
        String outcome = "OK";
        try
        {
            Response response = client.newCall(request).execute();
            if(response.code() == HttpStatus.OK.value() || response.code() == HttpStatus.ACCEPTED.value())
            {
                if(method.equalsIgnoreCase("code"))
                {
                    outcome = "OK";
                }
                else
                {
                    if(method.startsWith("body"))
                    {
                        String bodyFound = response.body().string();
                        logger.info("pingrun:: for ping-url: " + pingURL + ", received body: " + bodyFound);
                        if(HelperMethods.isJSONValid(bodyFound))
                        {
                            String[] parts = method.split(",");
                            String jsonField = (parts!=null && parts.length>=2 && parts[0].startsWith("body"))? parts[1] : null;
                            if(jsonField != null)
                            {
                                JSONParser parser = new JSONParser();
                                try
                                {
                                    JSONObject json = (JSONObject) parser.parse(bodyFound);
                                    String pingValue = "";
                                    for (Object jsonKey : json.keySet())
                                    {
                                        if (((String) (jsonKey)).equalsIgnoreCase(jsonField))
                                        {
                                            pingValue = (String) json.get(jsonField);
                                            logger.info("received ping value: " + pingValue);
                                            //need to think how to extend it for general use case
                                            String desiredState = (parts!=null && parts.length>=3 && parts[0].startsWith("body"))? parts[2] : null;
                                            if(desiredState != null)
                                            {
                                                if(pingValue.equalsIgnoreCase(desiredState)) outcome = "OK";
                                                else
                                                    outcome = "NOK";
                                            }
                                            else
                                            {
                                                outcome = "NOK";
                                            }
                                        }
                                    }
                                }
                                catch(Exception ex)
                                {
                                    logger.warn("PingWorker caught exception: " + ex.getMessage());
                                    outcome = "NOK";
                                }
                            }
                            else
                            {
                                logger.warn("unable to parse json field name, falling back to default assumption: NOK");
                                outcome = "NOK";
                            }
                        }
                        else
                        {
                            logger.warn("do not know how to proceed, falling back to default assumption: NOK");
                            outcome = "NOK";
                        }
                    }
                }
            }
            else //any other http status code implicitly says not ok
            {
                logger.warn("Ping request to url: " + pingURL + " returned: " + response.code());
                outcome = "NOK";
            }
        }
        catch(IOException ioex)
        {
            logger.warn("PingWorker caught exception: " + ioex.getMessage());
            outcome = "NOK";
        }

        Application.eventsCache.insertEvent(pingURL, reportURL, System.currentTimeMillis(), outcome);
        PingEvent[] trace = Application.eventsCache.getEventTraceHistory(pingURL, reportURL);
        //now checking if reportingURL needs to be notified or not
        int counter = 1;
        boolean trigger = true;

        if(trace.length < toleranceCount) trigger = false;

        for(PingEvent event:trace)
        {
            if(counter <= toleranceCount && event.status.equalsIgnoreCase("OK")) trigger = false;
            counter++;
        }
        if(trigger)
        {
            request = new Request.Builder().url(reportURL).addHeader("Content-Type", "application/json").build();
            try
            {
                client.newCall(request).execute();
            }
            catch(IOException ioex)
            {
                logger.error("Exception occurred while executing callback for reporting url: " + reportURL + ", msg: " + ioex.getMessage());
            }
        }
    }
}
