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
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class AppConfiguration {
    final static Logger logger = Logger.getLogger(AppConfiguration.class);

    @Value("${stream.adminuser}")
    String streamDbAdminUserAutoInitialized;

    @Value("${stream.adminpass}")
    String streamDbAdminPassAutoInitialized;

    @Value("${stream.accessurl}")
    String streamaccessurlAutoInitialized;

    @Value("${kafka.endpoint}")
    String kafkaEndPointAutoInitialized;

    @Value("${zookeeper.endpoint}")
    String zookeeperEndPointAutoInitialized;

    @Value("${stream.dbtype}")
    String streamDbTypeAutoInitialized;

    @Value("${stream.dbendpoint}")
    String streamDbUrlAutoInitialized;

    @Value("${sentinel.db.type}")
    String sentinelDbTypeAutoInitialized;

    @Value("${sentinel.db.endpoint}")
    String sentinelDbUrlAutoInitialized;

    @Value("${topic.check.interval}")
    long topicwaitperiodAutoInitialized;

    @Value("${series.format.cache.size}")
    int sFormatCSizeAutoInitialized;

    @Value("${admin.token}")
    String adminPassAutoInitialized;

    @Value("${published.api.version}")
    String apiVersionAutoInitialized;

    @Value("${kafka.key.serializer}")
    String kafkakeyserializerAutoInitialized;

    @Value("${kafka.value.serializer}")
    String kafkavalueserializerAutoInitialized;

    @Value("${dashboard.title}")
    String dashboardtitleAutoInitialized;

    @Value("${dashboard.endpoint}")
    String dashboardendpointAutoInitialized;

    @Value("${proxy.workaround.enable}")
    String proxyworkaroundenableAutoInitialized;

    @Value("${proxy.workaround.type}")
    String proxyworkaroundtypeAutoInitialized;

    @Value("${proxy.workaround.location}")
    String proxyworkaroundlocationAutoInitialized;


    private static String streamDBUser;
    private static String streamDBPass;
    private static String KafkaURL;
    private static String ZookeeperURL;
    private static String streamDBType;
    private static String streamDBURL;
    private static String sentinelDBType;
    private static String sentinelDBURL;
    private static long topicCheckWaitingPeriod;
    private static String adminToken;
    private static int seriesFormatCacheSize;
    private static String publishedApiVersion;
    private static String kafkaKeySerializer;
    private static String kafkaValueSerializer;
    private static String streamAccessUrl;
    private static String dashboardTitle;
    private static String dashboardEndpoint;
    private static String proxyType;
    private static String proxyWorkaroundEnabled;
    private static String proxyLocation;


    public static String getDashboardTitle()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("DASHBOARD_TITLE"))
            dashboardTitle = env.get("DASHBOARD_TITLE");

        return dashboardTitle;
    }

    public static String getDashboardEndpoint()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("DASHBOARD_ENDPOINT"))
            dashboardEndpoint = env.get("DASHBOARD_ENDPOINT");

        return dashboardEndpoint;
    }

    public static String getStreamDBUser()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("STREAM_ADMINUSER"))
            streamDBUser = env.get("STREAM_ADMINUSER");

        return streamDBUser;
    }

    public static String getStreamDBPass()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("STREAM_ADMINPASS"))
            streamDBPass = env.get("STREAM_ADMINPASS");
        return streamDBPass;
    }

    public static String getStreamDBType()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("STREAM_DBTYPE"))
            streamDBType = env.get("STREAM_DBTYPE");
        return streamDBType;
    }

    public static String getStreamDBURL()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("STREAM_DBENDPOINT"))
            streamDBURL = env.get("STREAM_DBENDPOINT");
        return streamDBURL;
    }

    public static String getStreamAccessUrl()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("STREAM_ACCESSURL"))
            streamAccessUrl = env.get("STREAM_ACCESSURL");
        return streamAccessUrl;
    }

    public static String getSentinelDBType()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("SENTINEL_DB_TYPE"))
            sentinelDBType = env.get("SENTINEL_DB_TYPE");
        return sentinelDBType;
    }

    public static String getSentinelDBURL()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("SENTINEL_DB_ENDPOINT"))
            sentinelDBURL = env.get("SENTINEL_DB_ENDPOINT");
        return sentinelDBURL;
    }

    public static String getKafkaURL()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("KAFKA_ENDPOINT"))
            KafkaURL = env.get("KAFKA_ENDPOINT");
        logger.info("returning kafka endpoint as: " + KafkaURL);
        return KafkaURL;
    }

    public static String getZookeeperURL()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("ZOOKEEPER_ENDPOINT"))
            ZookeeperURL = env.get("ZOOKEEPER_ENDPOINT");
        return ZookeeperURL;
    }

    public static long getTopicCheckWaitingPeriod()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("TOPIC_CHECK_INTERVAL"))
            topicCheckWaitingPeriod = Long.parseLong(env.get("TOPIC_CHECK_INTERVAL"));
        return topicCheckWaitingPeriod;
    }

    public static String getAdminToken()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("ADMIN_TOKEN"))
            adminToken = env.get("ADMIN_TOKEN");
        return adminToken;
    }

    public static String getProxyType()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("PROXY_WORKAROUND_TYPE"))
            proxyType = env.get("PROXY_WORKAROUND_TYPE");

        return proxyType;
    }

    public static boolean isProxyWorkaroundEnabled()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("PROXY_WORKAROUND_ENABLE"))
            proxyWorkaroundEnabled = env.get("PROXY_WORKAROUND_ENABLE");

        return (proxyWorkaroundEnabled != null && proxyWorkaroundEnabled.equalsIgnoreCase("true")) ? true : false;
    }

    public static String getProxyLocation()
    {
        Map<String, String> env = System.getenv();
        if(env.containsKey("PROXY_WORKAROUND_LOCATION"))
            proxyLocation = env.get("PROXY_WORKAROUND_LOCATION");

        return proxyLocation;
    }

    public static int getSeriesFormatCacheSize()
    {
        return seriesFormatCacheSize;
    }

    public static String getPublishedApiVersion()
    {
        return publishedApiVersion;
    }

    public static String getKafkaKeySerializer()
    {
        return kafkaKeySerializer;
    }

    public static String getKafkaValueSerializer()
    {
        return kafkaValueSerializer;
    }

    @PostConstruct
    public void init() {
        Map<String, String> env = System.getenv();
        if(env.containsKey("STREAM_ADMINUSER"))
            streamDBUser = env.get("STREAM_ADMINUSER");
        else
            streamDBUser = streamDbAdminUserAutoInitialized;
        if(env.containsKey("STREAM_ADMINPASS"))
            streamDBPass = env.get("STREAM_ADMINPASS");
        else
            streamDBPass = streamDbAdminPassAutoInitialized;
        if(env.containsKey("STREAM_ACCESSURL"))
            streamAccessUrl = env.get("STREAM_ACCESSURL");
        else
            streamAccessUrl = streamaccessurlAutoInitialized;
        if(env.containsKey("KAFKA_ENDPOINT"))
            KafkaURL = env.get("KAFKA_ENDPOINT");
        else
            KafkaURL = kafkaEndPointAutoInitialized;
        if(env.containsKey("ZOOKEEPER_ENDPOINT"))
            ZookeeperURL = env.get("ZOOKEEPER_ENDPOINT");
        else
            ZookeeperURL = zookeeperEndPointAutoInitialized;
        if(env.containsKey("STREAM_DBTYPE"))
            streamDBType = env.get("STREAM_DBTYPE");
        else
            streamDBType = streamDbTypeAutoInitialized;
        if(env.containsKey("STREAM_DBENDPOINT"))
            streamDBURL = env.get("STREAM_DBENDPOINT");
        else
            streamDBURL = streamDbUrlAutoInitialized;
        if(env.containsKey("SENTINEL_DB_TYPE"))
            sentinelDBType = env.get("SENTINEL_DB_TYPE");
        else
            sentinelDBType = sentinelDbTypeAutoInitialized;
        if(env.containsKey("SENTINEL_DB_ENDPOINT"))
            sentinelDBURL = env.get("SENTINEL_DB_ENDPOINT");
        else
            sentinelDBURL = sentinelDbUrlAutoInitialized;
        if(env.containsKey("TOPIC_CHECK_INTERVAL"))
            topicCheckWaitingPeriod = Long.parseLong(env.get("TOPIC_CHECK_INTERVAL"));
        else
            topicCheckWaitingPeriod = topicwaitperiodAutoInitialized;
        if(env.containsKey("ADMIN_TOKEN"))
            adminToken = env.get("ADMIN_TOKEN");
        else
            adminToken = adminPassAutoInitialized;
        if(env.containsKey("PROXY_WORKAROUND_TYPE"))
            proxyType = env.get("PROXY_WORKAROUND_TYPE");
        else
            proxyType = proxyworkaroundtypeAutoInitialized;
        if(env.containsKey("PROXY_WORKAROUND_ENABLE"))
            proxyWorkaroundEnabled = env.get("PROXY_WORKAROUND_ENABLE");
        else
            proxyWorkaroundEnabled = proxyworkaroundenableAutoInitialized;
        if(env.containsKey("PROXY_WORKAROUND_LOCATION"))
            proxyLocation = env.get("PROXY_WORKAROUND_LOCATION");
        else
            proxyLocation = proxyworkaroundlocationAutoInitialized;

        seriesFormatCacheSize = sFormatCSizeAutoInitialized;
        publishedApiVersion = apiVersionAutoInitialized;
        kafkaKeySerializer = kafkakeyserializerAutoInitialized;
        kafkaValueSerializer = kafkavalueserializerAutoInitialized;
        dashboardTitle = dashboardtitleAutoInitialized;
        dashboardEndpoint = dashboardendpointAutoInitialized;
    }

}
