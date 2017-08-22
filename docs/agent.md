# Sentinel agents
Currently three sentinel agents are available and more are being planned and will be released in the near future.
* docker stats agent
* system stats agent
* logfile agent
* inline logging guidelines

In addition, it is very easy to use inline code to directly send your logs into sentinel. All agents are written in **Python3** and need **pip3** to install all dependencies.

## common configuration
The agent configuration file is called **sentinel-agent.conf**. Depending on the type of agent you are executing / configuring, the configuration sections may be different. Here we present the common sections present in every configuration file:
```
[kafka-endpoint]
endpoint = kafka-endpoint:9092
keySerializer = StringSerializer
valueSerializer = StringSerializer
```

* endpoint - set this to the end point of your kafka cluster associated with sentinel framework

```
[sentinel]
topic = some-topic-name
seriesName = your-series-name
```

* topic - the kafka topic-name that was allocated to sentinel user for a given monitoring space in sentinel
* seriesname - the series that was created within the monitoring space

Unless one is writing their own sentinel agent, there is probably no need to change any other configuration parameters under **[kafka-endpoint]** or **[sentinel]** sections. Agent specific configuration parameters are covered in each agents subsection next.

## docker stats agent
The agent is located in the *sentinel-agents/dockerstats/* subdirectory in the downloaded git repository. To install all dependencies please use -

```
# pip3 install -f requirements.txt
```

The agent can simply be executed via this command -

```
$ python3 sentinel-docker-agent.py
```

## system stats agent
The agent is located in the *sentinel-agents/systemstats/* subdirectory in the downloaded git repository. To install all dependencies please use -

```
# pip3 install -f requirements.txt
```

The agent can simply be executed via this command -

```
$ python3 sentinel-sys-agent.py
```

## logfile agent
The agent is located in the *sentinel-agents/logparsing/* subdirectory in the downloaded git repository. To install all dependencies please use -

```
# pip3 install -f requirements.txt
```

The agent can simply be executed via this command -

```
$ python3 sentinel-log-agent.py
```

## inline logger guidelines