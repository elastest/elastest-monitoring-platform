# Docker stats agent
This agent periodically sends resource consumtion metrics for every container 
running in the node where it is deployed.

## Installing dependencies
The agent dependencies are listed in *requirements.txt* file. With **pip3** the 
requirements can be easily installed on the node to be monitored.

```
$ sudo pip3 install -f requirements.txt
```

Once all the dependencies are successfully installed, the agent process is 
ready to be launched after proper configuration values are set.

## Configuring the agent properties
Included you will find **sentinel-agent.conf** that contains the necessary 
configuration parameters for the agent process. Let's look at them now:

```
[kafka-endpoint]
endpoint = kafka-endpoint:9092
keySerializer = StringSerializer
valueSerializer = StringSerializer

[sentinel]
topic = some-topic-name
seriesName = your-series-name
seriesPattern = unixtime:s msgtype:json

[docker]
socket = unix://var/run/docker.sock

[agent]
period = 15
```

Please update these fields with correct values (remaining values can be left 
unchanged):
- endpoint: point it to the emp kafka endpoint, this value should be available 
via one of the management APIs of emp framework
- topic: change this with correct monitoring-space topic name that was created
- seriesName: update this with the series name within the topic (space) that 
was created using the management APIs
- period: this defines the periodicity of metrics collection in seconds

## Execution
Execution is simple. Simple execute the agent as a python3 process.
```
python3 sentinel-docker-agent.py
```