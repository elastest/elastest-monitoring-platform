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

package ch.splab.cab.emp.e2e;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.lang.System.getProperty;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EMPEnd2EndTests {
    private static final Logger logger = LogManager.getLogger("EMPEnd2EndTests");
    String tormURL = "http://localhost:37000";
    String grafanaPass = "someincorrectvalue";
    private boolean isEMPPresent = false;

    WebDriver driver;

    @Before
    public void setup()
    {
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOs = osName.startsWith("mac os x");
        if(isMacOs)
            System.setProperty("webdriver.chrome.driver", getProperty("user.dir") + "/src/test/resources/chromedrivermac");
        else
            System.setProperty("webdriver.chrome.driver", getProperty("user.dir") + "/src/test/resources/chromedriver");

        try
        {
            String etmUrl = getProperty("etmUrl");
            tormURL = (etmUrl==null ? "http://localhost:37000" : etmUrl);

            logger.info("Opening TORM at "  + tormURL);

            driver = new ChromeDriver();
            driver.manage().timeouts().implicitlyWait(30, SECONDS);
            driver.manage().window().setSize(new Dimension(1400, 1200));
            driver.get(tormURL);
            try
            {
                logger.info("Page title: " + driver.getTitle());
                logger.info("Clicking side menu link");
                driver.findElement(By.id("nav_emp")).click();
            }
            catch(Exception ex)
            {
                logger.info("Unable to find side navigation link. Directly accessing emp");
                driver.get(tormURL + "/#/emp");
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        logger.info("EMP Page title: " + driver.getTitle());

    }

    @Test
    public void invokeEmp()
    {
        logger.info("logging in grafana directly");
        driver.get(tormURL + "/grafana/");
        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));
        username.sendKeys("admin");
        password.sendKeys(grafanaPass);
        driver.findElement(By.xpath("//span[text()='Password']//following::button")).click();
        WebElement alert = driver.findElement(By.className("alert-title"));
        logger.info("Result of login attempt: " + alert.getText() + ", expected should be: Invalid username or password");
        assertEquals("checking alert message", "Invalid username or password", alert.getText());
    }

    @After
    public void cleanup()
    {
        try
        {
//            driver.navigate().back();
//            logger.info("Wrapping up: switching focus back to TORM Dashboard");
//            driver.switchTo().defaultContent();
//            logger.info("Cleaning up");
//            driver.findElement(By.id("tjobexecs")).click();
        }
        catch(Exception e)
        {
            logger.info("Issue with changing focus back to dashboard");
        }
        if (driver != null)
            driver.quit();
    }
}
