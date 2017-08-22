# Installation
Sentinel can be easily installed using docker. The docker compose file is provided for convenience.

## Download sentinel
```
git clone https://github.com/elastest/elastest-platform-monitoring.git
```
Now that you have the source code, you can either use docker to start sentinel, or you can build and package from source.

## Using docker-compose
Change into 'docker-support' subfolder under the root folder of the git repo clone. Then execute docker-compose as shown below:

```
docker-compose up
```
This command brings all the dependencies needed for sentinel:
* Grafana - grafana/grafana:4.3.2
* InfluxDB - influxdb:1.2.4-alpine
* Java8 - rolvlad/alpine-oraclejdk8
* Kafka - spotify/kafka:latest

The sentinel framework allows certain parameters to be set via environment variables. An example environment block is shown next:
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
Currently, sentinel works only with InfluxDB time-series backend. Support for emerging alternatives such as Timescaledb is planned and will be added very soon.

* STREAM_ADMINUSER - the admin user for InfluxDB
* STREAM_ADMINPASS - choose a secure password for the just declared admin user
* STREAM_DBENDPOINT - the API endpoint of InfluxDB service, typically it is at port 8086
* STREAM_ACCESSURL - the InfluxDB UI URL that sentinel will return back to users, if your service is running on an externally accessible node, change localhost with the FQDN or the IP of the node.
* ZOOKEEPER_ENDPOINT - the endpoint of the Zookeeper service
* KAFKA_ENDPOINT - the endpoint where Kafka cluster is reachable by sentinel
* TOPIC_CHECK_INTERVAL - defined in milliseconds, denotes the time interval between Kafka Topic query by topic manager in Sentinel.