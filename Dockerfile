# Copyright (c) 2017. ZHAW - Service Prototyping Lab
# All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may obtain
# a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# Author: Piyush Harsh,
# URL: piyush-harsh.info
#
# Thanks to: https://hub.docker.com/r/frolvlad/alpine-oraclejdk8/

FROM frolvlad/alpine-oraclejdk8:8.161.12-cleaned

LABEL maintainer="elastest-users@googlegroups.com"
LABEL version="0.9"
LABEL description="Builds the emp docker image."

EXPOSE 9000
RUN apk --update add sqlite curl
RUN apk add --no-cache util-linux
COPY emp/target/sentinel-0.9.0.jar /emp.jar
COPY emp/application.properties /application.properties
ADD emp/init-dashboard.sh init-dashboard.sh
ADD emp/dashboard.json dashboard.json
ADD emp/start.sh start.sh
RUN chmod +x init-dashboard.sh start.sh
CMD ["/bin/ash", "start.sh"]
