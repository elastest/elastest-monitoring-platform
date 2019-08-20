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

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Initialize {
    final static Logger logger = Logger.getLogger(Initialize.class);
    static String[] tables = {"user", "space", "series", "healthcheck"};
    public static HashMap<String, String> tableInitScripts = new HashMap<String, String>();

    static void prepareDbInitScripts()
    {
        if(AppConfiguration.getSentinelDBType().equalsIgnoreCase("sqlite") || AppConfiguration.getSentinelDBType().equalsIgnoreCase("sqlite3"))
        {
            tableInitScripts.put("user", "create table user (id INTEGER PRIMARY KEY AUTOINCREMENT, login VARCHAR(64), passwordhash VARCHAR(128), apikey VARCHAR(128))");
            tableInitScripts.put("space", "create table space (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32), queryuser VARCHAR(32), querypass VARCHAR(32), userid INT)");
            tableInitScripts.put("series", "create table series (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32), structure VARCHAR(512), spaceid INT)");
            tableInitScripts.put("healthcheck", "create table healthcheck (id INTEGER PRIMARY KEY AUTOINCREMENT, pingurl VARCHAR(256), reporturl VARCHAR(256), periodicity INTEGER," +
                    " tolerance INTEGER, method VARCHAR(32), userid INT)");
        }
        logger.info("Table initialization scripts have been initialized.");
    }

    static boolean isDbValid()
    {
        ArrayList<String> tablesFound = SqlDriver.getDbTablesList();
        for(int i=0; i < tables.length; i++)
        {
            String candidate = tables[i];
            if(!tablesFound.contains(candidate))
            {
                logger.info("The following table: {" + candidate + "} was not found in the existing database!");
                return false;
            }
        }
        return true;
    }

    static boolean initializeDb()
    {
        Connection con = SqlDriver.getDBConnection();
        Statement statement = null;
        try {
            statement = con.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            for (int i=0; i < tables.length; i++)
            {
                statement.executeUpdate("drop table if exists " + tables[i]);
                statement.executeUpdate(tableInitScripts.get(tables[i]));
                logger.info("(Re)Created table: " + tables[i]);
            }
            logger.info("Database (re)initialized successfully!");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            logger.error("Exception caught while initializing sentinel sql database.");
            return false;
        }
        finally {
            try {
                if(statement != null)
                    statement.close();
                con.close();
            } catch (SQLException ex)
            {

            }
        }
        return true;
    }

    static boolean initializeTestDb()
    {
        boolean status = initializeDb();

        if(!status) return false;

        Connection con = SqlDriver.getDBConnection();
        Statement statement = null;
        try {
            statement = con.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("INSERT INTO user " + "VALUES (1, 'testuser', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', '7ddbba60-8667-11e7-bb31-be2e44b06b34')");
            statement.executeUpdate("INSERT INTO space " + "VALUES (1, 'testspace', 'test', 'test', 1)");
            statement.executeUpdate("INSERT INTO series " + "VALUES (1, 'testseries', 'unixtime:s msgtype:json', 1)");
            statement.executeUpdate("INSERT INTO healthcheck " + "VALUES (1, 'https://blog.zhaw.ch/icclab/', 'https://requestb.in/1gznano1', 30000, 2, 'code', 1)");
            logger.info("Database (re)initialized with test entries successfully!");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            logger.error("Exception caught while initializing sentinel sql database.");
            return false;
        }
        finally {
            try {
                if(statement != null)
                    statement.close();
                con.close();
            } catch (SQLException ex)
            {

            }
        }
        return isDbValid();
    }

}
