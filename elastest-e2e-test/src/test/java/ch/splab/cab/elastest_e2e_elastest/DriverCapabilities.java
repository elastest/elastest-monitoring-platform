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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface DriverCapabilities {
    String[] value() default {""};
}
