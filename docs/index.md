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



[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
[ElasTest]: http://elastest.io/
[ElasTest Logo]: http://elastest.io/images/logos_elastest/elastest-logo-gray-small.png
[ElasTest Twitter]: https://twitter.com/elastestio
[GitHub ElasTest Group]: https://github.com/elastest
[Bugtracker]: https://github.com/elastest/bugtracker
