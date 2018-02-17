/*
 * Copyright (c) 2017. ZHAW - ICCLab
 *    All Rights Reserved.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License"); you may
 *      not use this file except in compliance with the License. You may obtain
 *      a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *      WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *      License for the specific language governing permissions and limitations
 *      under the License.
 *
 *
 *      Author: Piyush Harsh,
 *      URL: piyush-harsh.info
 */

package ch.icclab.sentinel;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.icclab.sentinel.dao.*;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;

@org.springframework.stereotype.Controller
public class Controller {
    final static Logger logger = Logger.getLogger(Controller.class);

    @RequestMapping(value="/error", method = RequestMethod.GET)
    public String showError(Model model)
    {
        return "error";
    }

    @RequestMapping(value="/statuslist", method = RequestMethod.GET)
    public String showIndex(HttpServletRequest request, HttpServletResponse response, Model model)
    {
        LinkedList<HealthCheckOutput> pingList = SqlDriver.getPingList();
        for(HealthCheckOutput data:pingList)
        {
            data.callHistory = Application.eventsCache.getEventTraceHistory(data.pingURL, data.reportURL);
        }
        model.addAttribute("pinglist", pingList);
        return "pinglist";
    }

    @RequestMapping(value="/visualization", method = RequestMethod.GET)
    public String showDashboard(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie, HttpServletRequest request, HttpServletResponse response, Model model)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);

        if (myCookie != null && myCookie.isLogged.matches("no"))
            return "login";
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }

        String src = "http://" + AppConfiguration.getDashboardEndpoint() + "/dashboard/db/" +
                AppConfiguration.getDashboardTitle() + "?refresh=30s&orgId=1&theme=light";
        model.addAttribute("iframesrc", src);
        model.addAttribute("dashboard", AppConfiguration.getDashboardEndpoint());
        model.addAttribute("username", userName);
        return "visualization";
    }

    @RequestMapping(value="/profile", method = RequestMethod.GET)
    public String showProfileData(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie, HttpServletRequest request, HttpServletResponse response, Model model)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);

        if (myCookie != null && myCookie.isLogged.matches("no"))
            return "login";
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        UserDataOutput data = new UserDataOutput();
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }
        data.id = userId;
        data.accessUrl = "/api/user/" + userId;
        model.addAttribute("username", userName);
        model.addAttribute("apikey", SqlDriver.getAPIKey(userId));
        model.addAttribute("userdata", data);

        //agent configuration common data
        EndpointInfo kafkadata = new EndpointInfo();
        kafkadata.endpoint = AppConfiguration.getKafkaURL();
        kafkadata.keySerializer = AppConfiguration.getKafkaKeySerializer();
        kafkadata.valueSerializer = AppConfiguration.getKafkaValueSerializer();
        model.addAttribute("kafkadata", kafkadata);
        return "profile";
    }

    @RequestMapping(value="/series/{seriesid}", method = RequestMethod.GET)
    public String showSeriesDetails(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie, @PathVariable(value="seriesid") String seriesid, HttpServletRequest request, HttpServletResponse response, Model model)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);

        if (myCookie != null && myCookie.isLogged.matches("no"))
            return "login";
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }

        int seriesId = -1;
        try {
            seriesId = Integer.parseInt(seriesid);
        } catch(NumberFormatException nex)
        {
            //supplied value is not an id but a login
            seriesId = -1;
        }
        if(seriesId == -1) {
            model.addAttribute("createmsg", "bad series request data, check input");
            return "seriesdetails";
        }

        String seriesName = SqlDriver.getSeriesName(seriesId);
        int spaceId = SqlDriver.getSpaceId(seriesId);
        String spaceName = SqlDriver.getSpaceName(spaceId);

        String dbName = "user-" + userId + "-" + spaceName;
        HashMap<String, Object>[] points = InfluxDBClient.getLastPoints(dbName, seriesName, 100);

        model.addAttribute("username", userName);
        return "seriesdetails";
    }

    @RequestMapping(value="/space/{spaceid}", method = RequestMethod.GET)
    public String showSpaceDetails(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie, @PathVariable(value="spaceid") String spaceid, HttpServletRequest request, HttpServletResponse response, Model model)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);

        if (myCookie != null && myCookie.isLogged.matches("no"))
            return "login";
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }
        UserDataOutput data = new UserDataOutput();
        data.id = userId;
        data.accessUrl = "/api/user/" + userId;
        data.spaces = SqlDriver.getUserSpaces(userId).toArray(new SpaceOutput[SqlDriver.getUserSpaces(userId).size()]);

        int spaceId = -1;
        try {
            spaceId = Integer.parseInt(spaceid);
        } catch(NumberFormatException nex)
        {
            //supplied value is not an id but a login
            spaceId = -1;
        }
        if(spaceId == -1) {
            model.addAttribute("createmsg", "bad series request data, check input");
            return "spacedetails";
        }

        for(SpaceOutput space: data.spaces)
        {
            if(space.id == spaceId)
            {
                SeriesOutput[] list = space.seriesList;
                model.addAttribute("serieslist", Arrays.asList(list));
                model.addAttribute("username", userName);
                model.addAttribute("spacename", space.name);
                model.addAttribute("topicname", space.topicName);
                //agent configuration common data
                EndpointInfo kafkadata = new EndpointInfo();
                kafkadata.endpoint = AppConfiguration.getKafkaURL();
                kafkadata.keySerializer = AppConfiguration.getKafkaKeySerializer();
                kafkadata.valueSerializer = AppConfiguration.getKafkaValueSerializer();
                model.addAttribute("kafkadata", kafkadata);
                return "spacedetails";
            }
        }

        model.addAttribute("username", userName);
        return "spacedetails";
    }

    @RequestMapping(value="/spaces", method = RequestMethod.GET)
    public String showSpaceData(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie, HttpServletRequest request, HttpServletResponse response, Model model)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);

        if (myCookie != null && myCookie.isLogged.matches("no"))
            return "login";
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }
        UserDataOutput data = new UserDataOutput();
        data.id = userId;
        data.accessUrl = "/api/user/" + userId;
        data.spaces = SqlDriver.getUserSpaces(userId).toArray(new SpaceOutput[SqlDriver.getUserSpaces(userId).size()]);
        model.addAttribute("userdata", data);
        model.addAttribute("spacelist", Arrays.asList(data.spaces));
        model.addAttribute("username", userName);
        return "space";
    }

    @RequestMapping(value="/newspace", method = RequestMethod.POST)
    public String processCreateSpace(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie, @RequestParam(value = "spacename", required = true) String spacename,
                               HttpServletResponse response, Model model, RedirectAttributes redirectAttributes)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);

        if (myCookie != null && myCookie.isLogged.matches("no"))
            return "login";
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }
        SpaceInput incomingData = new SpaceInput();
        incomingData.name = spacename;
        if(!incomingData.isValidData())
        {
            redirectAttributes.addFlashAttribute("createmsg","bad data, check input");
            return "redirect:/spaces";
        }

        if(SqlDriver.isDuplicateSpace(userName, incomingData.name))
        {
            redirectAttributes.addFlashAttribute("createmsg","space already exists");
            return "redirect:/spaces";
        }

        //now add this space to this user
        String topicName = "user-" + SqlDriver.getUserId(userName) + "-" + incomingData.name;
        String qUserName = "user" + SqlDriver.getUserId(userName) + incomingData.name;
        String qUserPass = HelperMethods.randomString(16);
        int spaceId = SqlDriver.addSpace(userName, incomingData.name, qUserName, qUserPass);
        String[] kafkaTopics = KafkaClient.listTopics();
        if(Arrays.asList(kafkaTopics).contains("user" + userId + "-" + incomingData.name))
        {
            logger.info("This space " + incomingData.name + " for user: " + userName + " is already with Kafka cluster.");
        }
        else
        {
            boolean status = KafkaClient.createTopic("user-" + userId + "-" + incomingData.name);
            if(status)
                logger.info("Topic registered with kafka cluster: " + "user-" + userId + "-" + incomingData.name);
            else
                logger.warn("Topic could not be registered with kafka cluster: " + "user" + userId + "-" + incomingData.name);
        }
        if(spaceId != -1)
        {
            SpaceOutput outputData = new SpaceOutput();
            outputData.id = spaceId;
            outputData.accessUrl = "/api/space/" + spaceId;
            outputData.name = incomingData.name;
            outputData.topicName = topicName;
            outputData.dataDashboardPassword = qUserPass;
            //InfluxDBClient.addDB(outputData.topicName); //just in case this has not been created by kafka topic monitoring thread
            boolean status = InfluxDBClient.addUser(outputData.topicName, outputData.topicName, outputData.dataDashboardPassword);
            if(status)
            {
                outputData.dataDashboardUser = qUserName;
                outputData.dataDashboardUrl = "http://" + AppConfiguration.getStreamAccessUrl() + "/";
            }
            else
            {
                outputData.dataDashboardPassword = null;
                logger.warn("Problem creating new user for this scape: " + outputData.topicName);
            }
            redirectAttributes.addFlashAttribute("createmsg","space created");
            return "redirect:/spaces";
        }
        else
        {
            redirectAttributes.addFlashAttribute("createmsg","space could not be created, contact system administrator");
        }
        return "redirect:/spaces";
    }

    @RequestMapping(value="/newseries", method = RequestMethod.POST)
    public String processCreateSeries(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie,
                                      @RequestParam(value = "spacename", required = true) String spacename,
                                      @RequestParam(value = "seriesname", required = true) String seriesname,
                                      @RequestParam(value = "msgformat", required = false) String msgformat,
                                      @RequestParam(value = "selectvalue", required = false) String selectedformat,
                                      @RequestParam(value = "override", required = false) String override,
                                     HttpServletResponse response, Model model, RedirectAttributes redirectAttributes)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);

        if (myCookie != null && myCookie.isLogged.matches("no"))
            return "login";
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }
        //decide if to use selectvalue or not
        SeriesInput incomingData = new SeriesInput();
        incomingData.name = seriesname;
        incomingData.spaceName = spacename;
        if(override == null)
            incomingData.msgSignature = selectedformat;
        else
            incomingData.msgSignature = msgformat;

        int spaceId = SqlDriver.getSpaceId(userName, incomingData.spaceName);
        System.out.println("format to use " + incomingData.msgSignature);
        if(!incomingData.isValidData())
        {
            redirectAttributes.addFlashAttribute("createmsg","bad data, check input");
            return "redirect:/space/" + spaceId;
        }
        if(SqlDriver.isDuplicateSeries(userName, incomingData.name, incomingData.spaceName))
        {
            redirectAttributes.addFlashAttribute("createmsg","series already exists");
            return "redirect:/space/" + spaceId;
        }

        int seriesId = SqlDriver.addSeries(incomingData.name, incomingData.msgSignature, spaceId);

        if(seriesId != -1)
        {
            SeriesOutput outputData = new SeriesOutput();
            outputData.id = seriesId;
            outputData.accessUrl = "/api/series/" + seriesId;
            outputData.name = incomingData.name;
            redirectAttributes.addFlashAttribute("createmsg","series created");
            return "redirect:/space/" + spaceId;
        }
        else
        {
            redirectAttributes.addFlashAttribute("createmsg","series could not be created, contact system administrator");
        }
        return "redirect:/space/" + spaceId;
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String showOverview(@CookieValue(value = "islogged", defaultValue = "eyJpc0xvZ2dlZCI6Im5vIn0=") String loggedCookie, HttpServletRequest request, HttpServletResponse response, Model model)
    {
        byte [] barr = Base64.getDecoder().decode(loggedCookie);
        String cookievalue = new String(barr);
        Gson gson = new Gson();
        MyCookie myCookie = gson.fromJson(cookievalue, MyCookie.class);
        if (myCookie != null && myCookie.isLogged.matches("no"))
        {
            if(model.asMap().get("loginmsg") != null)
            {
                model.addAttribute("loginmsg", (String) model.asMap().get("loginmsg"));
            }
            return "login";
        }
        else
        {
            myCookie.isLogged = "yes";
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = myCookie.username;
        UserDataOutput data = new UserDataOutput();
        int userId = SqlDriver.getUserId(userName);
        if(userId == -1)
        {
            return "redirect:/logout";
        }
        data.id = userId;
        data.accessUrl = "/api/user/" + userId;
        data.spaces = SqlDriver.getUserSpaces(userId).toArray(new SpaceOutput[SqlDriver.getUserSpaces(userId).size()]);
        model.addAttribute("userdata", data);
        model.addAttribute("username", userName);

        LinkedList<HealthCheckOutput> pingList = SqlDriver.getFilteredPingList(userId);
        for(HealthCheckOutput pingdata:pingList)
        {
            pingdata.callHistory = Application.eventsCache.getEventTraceHistory(pingdata.pingURL, pingdata.reportURL);
        }
        model.addAttribute("pinglist", pingList);

        return "index2";
    }

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public String processLogin(@RequestParam(value = "username", required = true) String username, @RequestParam(value = "password", required = true) String password,
                               HttpServletResponse response, Model model, RedirectAttributes redirectAttributes)
    {
        boolean isValidLogin = SqlDriver.isValidPassword(SqlDriver.getUserId(username), password);
        if(isValidLogin)
        {
            Gson gson = new Gson();
            MyCookie myCookie = new MyCookie();
            myCookie.isLogged = "yes";
            myCookie.username = username;
            String rawValue = gson.toJson(myCookie);
            String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
            Cookie foo = new Cookie("islogged", encoded); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
            redirectAttributes.addFlashAttribute("username",username);
        }
        else
        {
            redirectAttributes.addFlashAttribute("loginmsg","invalid login, please try with valid credentials");
        }
        return "redirect:/";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String showLogout(HttpServletResponse response,Model model)
    {
        Gson gson = new Gson();
        MyCookie myCookie = new MyCookie();
        myCookie.isLogged = "no";
        String rawValue = gson.toJson(myCookie);
        String encoded = Base64.getEncoder().encodeToString(rawValue.getBytes());
        Cookie foo = new Cookie("islogged", encoded); //bake cookie
        response.addCookie(foo);
        return "redirect:/";
    }

}
