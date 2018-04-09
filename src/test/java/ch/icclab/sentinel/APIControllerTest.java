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

import ch.qos.logback.core.encoder.EchoEncoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
@PropertySource("classpath:application.properties")
public class APIControllerTest
{
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public APIController apiController() {
            return new APIController();
        }
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        Initialize.prepareDbInitScripts();
        Initialize.initializeTestDb();
    }


    @Test
    public void getRootAPI() throws Exception {
        mockMvc.perform(get("/v1/api/").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        ;
    }

    @Test
    public void getUserData() throws Exception {
        mockMvc.perform(get("/v1/api/user/1").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createUserTest() throws Exception {
        mockMvc.perform(post("/v1/api/user/").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void getUserKeyData() throws Exception {
        mockMvc.perform(get("/v1/api/key/1").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createSpaceTest() throws Exception {
        mockMvc.perform(post("/v1/api/space/").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createSeriesTest() throws Exception {
        mockMvc.perform(post("/v1/api/series/").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void getAPIEndpoint() throws Exception {
        mockMvc.perform(get("/v1/api/endpoint").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void getDashboardEndpoint() throws Exception {
        mockMvc.perform(get("/v1/dashboard/").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void createPingBackTest() throws Exception {
        mockMvc.perform(post("/v1/api/pingback/").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

//    @Test
//    public void createPingBackTestV2() throws Exception {
//        mockMvc.perform(post("/v1/api/pingback/")
//                .header("x-auth-login","testuser")
//                .header("x-auth-apikey","7ddbba60-8667-11e7-bb31-be2e44b06b34")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"pingURL\": \"https://admin-dashboard.cyclops-labs.io:8888/\",\"reportURL\": \"http://localhost:5000/\",\"periodicity\": 30000,\"toleranceFactor\": 2,\"method\": \"body,status,up\"}")
//                .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated())
//        ;
//    }

    @Test
    public void createPingBackTestV3() throws Exception {
        mockMvc.perform(post("/v1/api/pingback/")
                .header("x-auth-login","testuser")
                .header("x-auth-apikey","7ddbba60-8667-11e7-bb31-be2e44b06b35")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pingURL\": \"https://admin-dashboard.cyclops-labs.io:8888/\",\"reportURL\": \"http://localhost:5000/\",\"periodicity\": 30000,\"toleranceFactor\": 2,\"method\": \"body,status,up\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    public void getPingBackEndpoint() throws Exception {
        mockMvc.perform(get("/v1/api/pingback/1").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
        ;
    }

//    @Test
//    public void getPingBackEndpointV2() throws Exception {
//        mockMvc.perform(get("/v1/api/pingback/1")
//                .header("x-auth-login","testuser")
//                .header("x-auth-apikey","7ddbba60-8667-11e7-bb31-be2e44b06b34")
//                .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//        ;
//    }

    @Test
    public void getPingBackEndpointV3() throws Exception {
        mockMvc.perform(get("/v1/api/pingback/1")
                .header("x-auth-login","testuser")
                .header("x-auth-apikey","7ddbba60-8667-11e7-bb31-be2e44b06b35")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    public void getNotImplementedAPI() throws Exception {
        mockMvc.perform(get("/v1/api/unknown/").accept(MediaType.IMAGE_GIF))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void showDashboardIframeSrcTest() throws Exception {
        mockMvc.perform(get("/v1/dashboardsrc").accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

}
