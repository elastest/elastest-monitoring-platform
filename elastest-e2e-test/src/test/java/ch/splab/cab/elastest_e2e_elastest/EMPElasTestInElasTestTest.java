/*
 *  Copyright (c) 2019. Service Prototyping Lab, ZHAW
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

package ch.splab.cab.elastest_e2e_elastest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.lang.System.getProperty;
import static org.junit.jupiter.api.Assertions.*;

public class EMPElasTestInElasTestTest extends ElastestBaseTest {
    private static final Logger logger = LogManager.getLogger(EMPElasTestInElasTestTest.class);

    @Test
    @DisplayName("Test to start EMP")
    void check4emp()
    {
        String grafanaPass = "someincorrectvalue";
        boolean hasEMPStarted = false;
        // elastest_url = env.ET_SUT_PROTOCOL + '://elastest:3xp3r1m3nt47@' + env.ET_SUT_HOST + ':' + env.ET_SUT_PORT
        tormUrl = "http://elastest:3xp3r1m3nt47@nightly.elastest.io:37000/";
        logger.info("Torm Url: " + tormUrl);

        driver.manage().window().setSize(new Dimension(1400, 1200));
        driver.get(tormUrl);

        try
        {
            logger.info("Page title: " + driver.getTitle());
            logger.info("Clicking side menu link");
            driver.findElement(By.id("nav_emp")).click();
        }
        catch(Exception ex)
        {
            logger.info("Unable to find side navigation link. Directly accessing emp");
            driver.get(tormUrl + "/#/emp");
        }

        logger.info("EMP Page title: " + driver.getTitle());
        logger.info("logging in grafana directly");
        driver.get(tormUrl + "/grafana/");
        WebElement cpuUsagePanel = driver.findElement(By.xpath("//span[text()='Component's CPU Usage']"));
        assertNotNull(cpuUsagePanel);

        try {
            Thread.sleep(5000);
        } catch (Exception ex)
        {

        }
    }
}
