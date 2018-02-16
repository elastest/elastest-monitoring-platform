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

import ch.icclab.sentinel.dao.HealthCheckOutput;
import ch.icclab.sentinel.dao.MyCookie;
import ch.icclab.sentinel.dao.SpaceOutput;
import ch.icclab.sentinel.dao.UserDataOutput;
import com.google.gson.Gson;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.log4j.Logger;

import java.util.Base64;
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
        data.id = userId;
        data.accessUrl = "/api/user/" + userId;
        model.addAttribute("username", userName);
        model.addAttribute("apikey", SqlDriver.getAPIKey(userId));
        model.addAttribute("userdata", data);

        return "profile";
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



        return "space";
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
