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
import ch.icclab.sentinel.dao.SpaceOutput;
import ch.icclab.sentinel.dao.UserDataOutput;
import com.google.gson.Gson;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.log4j.Logger;

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

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String showOverview(@CookieValue(value = "islogged", defaultValue = "no") String loggedCookie, HttpServletRequest request, HttpServletResponse response, Model model)
    {
        if (loggedCookie.matches("no"))
            return "login"; //login sets username model attribute
        else
        {
            Cookie foo = new Cookie("islogged", "yes"); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = (String) model.asMap().get("username");
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

        return "index";
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public String showRefreshedOverview(@RequestParam(value = "username", required = true) String username, @CookieValue(value = "islogged", defaultValue = "no") String loggedCookie,
                                        HttpServletRequest request, HttpServletResponse response, Model model)
    {
        if (loggedCookie.matches("no"))
            return "login"; //login sets username model attribute
        else
        {
            Cookie foo = new Cookie("islogged", "yes"); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
        }

        String userName = username;
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

        return "index";
    }

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public String processLogin(@RequestParam(value = "username", required = true) String username, @RequestParam(value = "password", required = true) String password,
                               HttpServletResponse response, Model model, RedirectAttributes redirectAttributes)
    {
        boolean isValidLogin = SqlDriver.isValidPassword(SqlDriver.getUserId(username), password);
        if(isValidLogin)
        {
            Cookie foo = new Cookie("islogged", "yes"); //bake cookie
            foo.setMaxAge(600); //10 minutes expiery
            response.addCookie(foo);
            redirectAttributes.addFlashAttribute("username",username);
        }
        model.addAttribute("username", username);
        return "redirect:/";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String showLogout(HttpServletResponse response,Model model)
    {
        Cookie foo = new Cookie("islogged", "no"); //bake cookie
        response.addCookie(foo);
        return "redirect:/";
    }

}
