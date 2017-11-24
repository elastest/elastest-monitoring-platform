package ch.icclab.sentinel.dao;

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

public class HealthCheckInput {
    public String pingURL;
    public String reportURL;
    public Long periodicity;
    public Integer toleranceFactor;

    public boolean isValidData()
    {
        if(pingURL == null || reportURL == null || periodicity == null || pingURL.trim().length() == 0 ||
                reportURL.trim().length() == 0 || toleranceFactor == null) return false;
        if(periodicity < 30000l || (periodicity % 30000l) != 0) return false;
        return true;
    }
}
