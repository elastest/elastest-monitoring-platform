# Elastest Monitoring Platform

## Development documentation

### Architecture
![EMP Architecture](https://raw.githubusercontent.com/elastest/elastest-monitoring-platform/master/docs/img/emp-arch.png "EMP Architecture")
The proposed architecture of EMP is shown above. In this release, the basic 
functionalities are ready including capability of configuring the spaces and 
series by framework users, and via available agents as well as inline 
instrumentation, the ability to send in metrics and log streams into EMP.

The technologies used in release 0.5.0 are -
- Apache Kafka
- InfluxDB 1.2.4
- Grafana 4.6.1
- Oracle Java 8
- Maven 3.0.5 or higher
- Python 3.0 (for agents implementation)

Currently (release 0.5.0) the query interface is the native query APIs as well 
as admin dashboard exposed by InfluxDB. EMP's own query interface allowing 
online queries, correlated analysis and much more will be made available soon.

Stream visualization in this release is supported via Grafana dashboards. An 
example is shown here.
![Sample Visualization](https://raw.githubusercontent.com/elastest/elastest-monitoring-platform/master/docs/img/grafana.png "Sample Visualization")

### Prepare development environment
We recommend use of IntelliJ IDE CE as development environment for EMP. Since 
the project uses Maven as build environmetn, any supporting IDE will do. 

- download the code base using git
```
git clone https://github.com/elastest/elastest-monitoring-platform.git
```
- change directory
```
cd elastest-monitoring-platform
```
- clean and compile the source code
```
mvn clean compile
```

### Development procedure
Simply load the downloaded codebase folder into any IDE - eclipse or IntelliJ 
Idea CE and choose **import maven project** as your option. You are ready to 
contribute to the EMP codebase. Please do not forget to send us a pull request
if you add a new feature or find and have fixed a bug.

  Copyright (c) 2017. Zuercher Hochschule fuer Angewandte Wissenschaften
   All Rights Reserved.
 
      Licensed under the Apache License, Version 2.0 (the "License"); you may
      not use this file except in compliance with the License. You may obtain
      a copy of the License at
 
           http://www.apache.org/licenses/LICENSE-2.0
 
      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
      WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
      License for the specific language governing permissions and limitations
      under the License.


[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
[ElasTest]: http://elastest.io/
[ElasTest Logo]: http://elastest.io/images/logos_elastest/elastest-logo-gray-small.png
[ElasTest Twitter]: https://twitter.com/elastestio
[GitHub ElasTest Group]: https://github.com/elastest
[Bugtracker]: https://github.com/elastest/bugtracker
