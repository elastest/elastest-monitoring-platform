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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.log4j.Logger;

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
        return "pinglist";
    }
}
