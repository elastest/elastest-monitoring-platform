# Elastest Monitoring Platform
Elastest Monitoring Platform (EMP) is a monitoring framework built from grounds
up with first class support for streams of system and application metrics as 
well as logs. The service will be used to primarily monitor the health of 
various componets of ElasTest platform and allows correlated queries aiding the
fault location within the platform in an optimized manner.

## Features
The version 0.1 of EMP provides the following featues:

- Management APIs to control space and series creation
- Ability to send system metrics through systemstats agent
- Ability to send docker stats through dockerstats agent
- Ability to send Java application log messages from a log file through logparsing agent
- Capability to directly send application logs to emp
- Metrics visualization through Grafana dashboards

EMP is designed to gather metrics as well as logs specifically from the ElasTest platform 
components. In contrast the system under test (SuT) sends their metrics and logs to EMS
which gets created on demand and performs online  metric analysis for the duration
the SuT is running. EMP is for platform metrics and EMS is for SuT metrics.

## How to run

### Installation
Sentinel can be easily installed using docker. The docker compose file is provided for 
convenience.

#### Download sentinel
```
git clone https://github.com/elastest/elastest-platform-monitoring.git
```
Now that you have the source code, you can either use docker to start sentinel, or 
you can build and package from source.

## Using docker-compose
Change into 'docker-support' subfolder under the root folder of the git repo 
clone. Then execute docker-compose as shown below:

```
docker-compose up
```
This command brings all the dependencies needed for sentinel:
* Grafana - grafana/grafana:4.3.2
* InfluxDB - influxdb:1.2.4-alpine
* Java8 - rolvlad/alpine-oraclejdk8
* Kafka - spotify/kafka:latest

The sentinel framework allows certain parameters to be set via environment 
variables. An example environment block is shown next:
```
      - STREAM_ADMINUSER=root
      - STREAM_ADMINPASS=pass1234
      - STREAM_DBENDPOINT=influxdb:8086
      - STREAM_ACCESSURL=localhost:8083
      - STREAM_DBTYPE=influxdb
      - ZOOKEEPER_ENDPOINT=kafka:2181
      - KAFKA_ENDPOINT=kafka:9092
      - TOPIC_CHECK_INTERVAL=30000
```
Currently, sentinel works only with InfluxDB time-series backend. Support 
for emerging alternatives such as Timescaledb is planned and will be added very soon.

* STREAM_ADMINUSER - the admin user for InfluxDB
* STREAM_ADMINPASS - choose a secure password for the just declared admin user
* STREAM_DBENDPOINT - the API endpoint of InfluxDB service, typically it is at port 8086
* STREAM_ACCESSURL - the InfluxDB UI URL that sentinel will return back to users, if your service is running on an externally accessible node, change localhost with the FQDN or the IP of the node.
* ZOOKEEPER_ENDPOINT - the endpoint of the Zookeeper service
* KAFKA_ENDPOINT - the endpoint where Kafka cluster is reachable by sentinel
* TOPIC_CHECK_INTERVAL - defined in milliseconds, denotes the time interval between Kafka Topic query by topic manager in Sentinel.

### Configuring Kafka container
The kafka container allows certain parameters to be set via environment block.
```
      - ADVERTISED_PORT=9092
      - ADVERTISED_HOST=kafka
```
Care must be taken in defining **ADVERTISED_HOST** value. The best solution 
is to provide a FQDN or a public IP if Kafka is to be accessed by external 
processes which will be the most common use-case of sentinel. Setting an 
incorrect value of this parameter may leave your kafka cluster unreachable 
for external services, or even sentinel process running in a container.

Our recommendation is to setup kafka cluster is a separate node entirely, 
and configure **KAFKA_ENDPOINT** parameter for sentinel as a FQDN string.

#### Install from source
Sentinel framework is written in Java and requires Oracle Java 8 for proper 
working. OpenJDK 8 should also work but the codebase has not been tested with 
openJDK 8.

##### Requirements
* Maven 3.0.5 and higher
* Oracle Java 8

##### Packaging
```
mvn clean package
```
The self-contained jar file is created under ./target/ folder. Unless the pom file was changed, the self contained jar file is named **sentinel-0.1.jar**

To execute, simply run the jar as follows -
```
$ java -jar /path/to/jar/sentinel-0.1.jar
```
The above assumes that the **application.properties** file is in the classpath, or in the same folder as the jar file. In case the **application.properties** file is kept some other location, please use the following command instead -
```
$ java -jar /path/to/jar/sentinel-0.1.jar --spring.config.location=/path/to/config/application.properties
```

##### Configuration options
All application configuration is provided via **application.properties** file. 
A sample file content is listed below.

```
spring.thymeleaf.mode=LEGACYHTML5
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=ERROR
logging.level.org.apache.kafka=WARN
logging.level.org.jooq=WARN
spring.mvc.throw-exception-if-no-handler-found=true
logging.file=sentinel.log
server.port=9000
server.ssl.enabled=true
server.ssl.key-alias=sentinel
server.ssl.key-store=keystore.p12
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=pass1234
server.ssl.key-password=pass1234
displayexceptions=true
sentinel.db.type=sqlite
sentinel.db.endpoint=sentinel.db
kafka.endpoint=localhost:9092
kafka.key.serializer=StringSerializer
kafka.value.serializer=StringSerializer
zookeeper.endpoint=localhost:2181
topic.check.interval=30000
# stream.db.type=postgres
# stream.db.endpoint=localhost:5432
# stream.db.adminuser=postgres
# stream.db.adminpass=postgres
stream.dbtype=influxdb
stream.dbendpoint=localhost:8086
stream.accessurl=localhost:8083
stream.adminuser=root
stream.adminpass=1ccl@b2017
admin.token=eedsR2v5n4uh7Gjy
series.format.cache.size=100
published.api.version=v1
```
Many of the entries in the **application.properties** file are self-explanatory. 
A few non-obvious ones are explained next -

* server.port - on what port number sentinel APIs are accessible
* displayexceptions - set this to **true** if you want to include exceptions full trace in the log outputs
* sentinel.db.type - currently only *sqlite* is supported
* sentinel.db.endpoint - relative or absolute path of the DB file
* topic.check.interval - value in milliseconds indicating the gap between checking list of monitoring spaces for subscription
* stream.dbtype - the time series DB where the monitor stream will be stored, currently only *influxdb* is supported
* stream.accessurl - the url /IP where the InfluxDB admin UI is accessible to the user (if enabled), this should be an externally accessible FQDN ideally
* stream.adminuser - the name of the admin account in the stream DB (here influxdb), this value is meaningful only when *authentication* and *authorization* is enabled in InfluxDB, otherwise the values are not enforced by the DB
* admin.token - this is the master token using which a new user account can be created within sentinel, this value should be accessible only to the administrators of the system, or within the API engine in case you wish to support self registration by general public.

## Basic usage
Sentinel framework has an extensive management and a planned query interface.
Before it can be used for monitoring a service, appropriate user account, monitoring
spaces and series must be created. The management API allows account management and
setup.

### Using EMP APIs
Sentinel monitoring exposes a rich set of APIs for user and space management. The 
current release of sentinel has APIs supporting bare-minimal features and as the 
features set get richer, so will be the APIs. Below are the list of APIs currently 
offered by the framework -

* /v1/api/ - shows list of supported APIs
* /v1/api/user/ - everything to do with user management
* /v1/api/space/ - management of monitoring space
* /v1/api/series/ - management of series with any space
* /v1/api/key/ - API to retrieve user's API key if forgotten
* /v1/api/endpoint - API to retrieve Sentinel's data interface parameters

#### Key concepts
**Space**: Think of it as a collection of metrics belonging to different streams 
but somehow belonging to the same scope, application or service. A space could be 
allocated to metrics of smaller services making up a larger application or service.

**Series**: A series in Sentinel is a stream of metrics coming from the same source.

#### API return codes at a glance
```
+-------------------+-------+---------------+--------------------------------+
| API endpoint      | Verb  | Return codes  | Comments                       |
+===================+=======+===============+================================+
| /v1/api/          | GET   | 200           | ok                             |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 500           | service down                   |
+-------------------+-------+---------------+--------------------------------+
| /v1/api/user/     | POST  | 201           | created                        |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 400           | check data                     |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 401           | valid admin token needed       |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 409           | user account already exists    |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 500           | system error                   |
+-------------------+-------+---------------+--------------------------------+
| /v1/api/user/{id} | GET   | 200           | ok                             |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 401           | unauthorized                   |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 400           | check data                     |
+-------------------+-------+---------------+--------------------------------+
| /v1/api/space/    | POST  | 201           | created                        |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 400           | check data                     |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 401           | invalid api key                |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 409           | space already exists for user  |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 500           | system error                   |
+-------------------+-------+---------------+--------------------------------+
| /v1/api/series/   | POST  | 201           | created                        |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 400           | check data                     |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 401           | invalid api key                |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 409           | series already exists for user |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 500           | system error                   |
+-------------------+-------+---------------+--------------------------------+
|/v1/api/key/{id}   | GET   | 200           | ok                             |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 400           | no such user exist             |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 401           | invalid password               |
+-------------------+-------+---------------+--------------------------------+
|/v1/api/endpoint   | GET   | 200           | ok                             |
+-------------------+-------+---------------+--------------------------------+
|                   |       | 401           | invalid api key                |
+-------------------+-------+---------------+--------------------------------+
```

#### Header fields at a glance
```
+-----------------+--------------------------------+
| field key       | value / interpretations        |
+=================+================================+
| Content-Type    | application/json is typical    |
+-----------------+--------------------------------+
| x-auth-token    | admin user master token        |
+-----------------+--------------------------------+
| x-auth-password | password associated with user  |
+-----------------+--------------------------------+
| x-auth-login    | username or userid             |
+-----------------+--------------------------------+
| x-auth-apikey   | api key associated with user   |
+-----------------+--------------------------------+
```

#### APIs in details
Now that we have all the basic building buildings in place, lets explore each API 
endpoint in more details. In the following subsections lets assume that the 
sentinel API service is available at https://localhost:9000/. Also API example will 
be provided as a valid cURL command.

##### /v1/api/ GET
This API allows a quick check on the health status, if the service is alive a 200 
status code is returned along with a list of supported API endpoints.
```
curl -X GET https://localhost:9000/v1/api/
```
The response is similar to one shown below -
```
  [
   {
     "endpoint": "/v1/api/",
     "method": "GET",
     "description": "get list of all supported APIs",
     "contentType": "application/json"
   },
   {
     "endpoint": "/v1/api/user/",
     "method": "POST",
     "description": "add a new user ",
     "contentType": "application/json"
   },
   {
     "endpoint": "/v1/api/user/{id}",
     "method": "GET",
     "description": "retrieve info about existing user",
     "contentType": "application/json"
   },
   {
     "endpoint": "/v1/api/space/",
     "method": "POST",
     "description": "register a new monitored space",
     "contentType": "application/json"
   },
   {
     "endpoint": "/v1/api/series/",
     "method": "POST",
     "description": "register a new series within a space",
     "contentType": "application/json"
   },
   {
     "endpoint": "/v1/api/key/{id}",
     "method": "GET",
     "description": "retrieve the api-key for an user",
     "contentType": "application/json"
   },
   {
     "endpoint": "/v1/api/endpoint",
     "method": "GET",
     "description": "retrieve the agent's connection endpoint parameters",
     "contentType": "application/json"
   }
  ]
```
The output above is representative, and the actual API supported by sentinel varied 
during the time of writing of this document.

##### /v1/api/user/ POST
Use this API to create a new user of sentinel. User account creation is an admin 
priviledged operation and the *admin-token* is required as header for the call to 
be executed successfully.

```
  curl -X POST https://localhost:9000/v1/api/user/ 
  --header "Content-Type: application/json" 
  --header "x-auth-token: <admin-token>" 
  -d '{"login":"username", "password":"some-password"}'
```
If the user already exists, you will get a *409 Conflict* status response back. An 
example response upon successful creation of an account looks as shown below, the 
actual value is for representation purposes only -
```
  {
    "login": "username",
    "apiKey": "b6af63b9-f699-4259-8548-2a60e0d88661",
    "id": 2,
    "accessUrl": "/api/user/2"
  }
```
The *apiKey* and *id* values should be saved as they are needed in some of the 
management API requests as you will see later.

##### /v1/api/user/{id} GET
Use this API to retrieve the complete information about an user account, the 
monitoring spaces and series info included. A valid *api-key* needs to be provided 
as a header field while making this call.

```
  curl -X GET https://localhost:9000/v1/api/user/{id} 
  --header "Content-Type: application/json"
  --header "x-auth-apikey: valid-api-key"
```
If the call succeeds then the complete details of the account is returned back. A 
sample value returned is shown next.

```
  {
    "apiKey": "f3549958-8884-4649-9661-8ca338dfe141",
    "id": 1,
    "accessUrl": "/api/user/1",
    "spaces": [
        {
            "id": 1,
            "accessUrl": "/api/space/1",
            "topicName": "user-1-cyclops",
            "name": "cyclops",
            "seriesList": [
                {
                    "id": 1,
                    "accessUrl": "/api/series/1",
                    "name": "app-logs",
                    "msgFormat": "unixtime:s msgtype:json"
                }
            ],
            "dataDashboardUrl": "http://localhost:8083/",
            "dataDashboardUser": "user1cyclops",
            "dataDashboardPassword": "qkDaFQ8gJEokApS6"
        }
    ]
  }
```

##### /v1/api/space/ POST
Use this API to create a new monitored space for a given user account in sentinel. 
A matching *username* and the *api-key* needs to be provided as header fields. The 
body just contains the *name* of the space that one wishes to create.

```
  curl -X POST https://localhost:9000/v1/api/space/ 
  --header "Content-Type: application/json"
  --header "x-auth-login: username" 
  --header "x-auth-apikey: some-api-key"
  -d '{"name":"space-name"}'
```
If the call is successful, the *space id* is returned back as confirmation. A 
sample response is shown next.

```
  {
    "id": 3,
    "accessUrl": "/api/space/3",
    "topicName": "user-1-new-space",
    "name": "new-space",
    "dataDashboardUrl": "http://localhost:8083/",
    "dataDashboardUser": "user1new-space",
    "dataDashboardPassword": "GeMHPDUwKc5621ZI"
  }
```

##### /v1/api/series/ POST
A space by itself does not handle data streams, it is a container and needs a 
series to be defined before the metrics sent to it can be persisted and analyzed 
later. This API allows creation of a *series* within an existing *space*. The 
*msgSignature* allows sentinel to parse the incoming messages properly. 

If the message being sent into sentinel is a single level JSON string, the 
*unixtime:s msgtype:json* value is sufficient.

```
curl -X POST https://localhost:9000/v1/api/series/ 
--header "Content-Type: application/json"
--header "x-auth-login: username" 
--header "x-auth-apikey: some-api-key"
-d '{"name":"series-name", "spaceName":"parent-space-name", "msgSignature":"msg-signature"}'
```
If the call is successful, a *series id* is returned. An example response block is 
shown for completeness.

```
  {
    "id": 2,
    "accessUrl": "/api/series/2",
    "name": "some-app-logs"
  }
```

##### /v1/api/key/{id} GET
One can use this API if there is a need to retrieve the user api-key. The *username*
should be a registered account and the *some-password* header field should be the 
matching password for this account.

```
  curl -X GET https://localhost:9000/v1/api/key/{username} 
  --header "Content-Type: application/json"
  --header "x-auth-password: some-password"
```
If the call is successful, the API-key is returned. A sample response is shown next.

```
  {
    "apiKey": "f3549958-8884-4649-9661-8ca338dfe141",
    "id": 1,
    "accessUrl": "/api/user/1"
  }
```

##### /v1/api/endpoint GET
This API call can be used to retrieve the connection parameters for the sentinel 
agents to send data streams to. The call is available only to registered accounts, 
therefore a valid *username* and *api-key* needs to be supplied as header fields.

```
  curl -X GET https://localhost:9000/v1/api/endpoint 
  --header "Content-Type: application/json"
  --header "x-auth-login: username" 
  --header "x-auth-apikey: some-api-key"
```
If the call succeeds, the parameter block is returned that can be used to properly 
configure the sentinel agents. A sample response is shown next.

```
  {
    "endpoint": "kafka:9092",
    "keySerializer": "StringSerializer",
    "valueSerializer": "StringSerializer"
  }
```

### Running agents
Currently three sentinel agents are available and more are being planned and will 
be released in the near future.

* docker stats agent
* system stats agent
* logfile agent

Apart from these a python library enables Python application developers to directly 
send the application logs to EMP.

* inline logging guidelines for Python

Through the use of the APIs listed above, any user or process has all releveant 
information necessary to properly configure the agents for sending appropriate 
metrics and logs into EMP. *Look for steps to run EMP agents inside respective agent
folders*.

## Development documentation

### Architecture
![alt text](https://github.com/elastest/elastest-monitoring-platform/tree/master/docs/img/emp-arch.png "EMP Architecture")

### Prepare development environment

### Development procedure

[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
[ElasTest]: http://elastest.io/
[ElasTest Logo]: http://elastest.io/images/logos_elastest/elastest-logo-gray-small.png
[ElasTest Twitter]: https://twitter.com/elastestio
[GitHub ElasTest Group]: https://github.com/elastest
[Bugtracker]: https://github.com/elastest/bugtracker
