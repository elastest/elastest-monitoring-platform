# Log parsing agent
This agent continually monitors a log file and sends all logs sent to it to 
EMP framework in a realtime manner.

## Installing dependencies
The agent dependencies are listed in *requirements.txt* file. With **pip3** 
the requirements can be easily installed on the node to be monitored.

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
seriesPattern = unixtime:ms loglevel:string class:string logmsg:string

[target-file]
filePath = /your/target/log/file/some-log.log
logPattern = %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

[mapping]
unixtime = %d{yyyy-MM-dd HH:mm:ss}
loglevel = level
class = logger
logmsg = msg
```

**IMP**: Make sure that the series signature matches in the EMP setup to the 
one mentioned above to where this agent sends to.

Please update these fields with correct values (remaining values can be left 
unchanged):
- endpoint: point it to the emp kafka endpoint, this value should be available 
via one of the management APIs of emp framework
- topic: change this with correct monitoring-space topic name that was created
- seriesName: update this with the series name within the topic (space) that 
was created using the management APIs
- seriesPattern: update this with the pattern that was defined in emp while 
the target series was created using the management API, the format of the 
pattern is **unixtime:timeunit [fieldname:fieldtype] [fieldname:fieldtype] ...
**where **fieldname** is something meaningful and **fieldtype** can be one of 
the following parameters:
	- string
	- int
	- long
	- float
	- double

	The **timeunit** defining the **unixtime** value can be one of the 
	following values:
	- s: second
	- ms: milliseconds
	- ns: nanoseconds
	- us: microseconds 
- filePath: the full path of the targetted log file which needs to tracked
- logPattern: actual log4j logging pattern used in the code which generates 
the logs in the targeted file
- [mapping] block: it defines the mapping between the logpattern elements and 
the elements defined in the **seriesPattern**

## Execution
Execution is simple. Simple execute the agent as a python3 process.
```
python3 sentinel-log-agent.py
```

If one wishes to run the agent process as a systemd process, please follow the 
typical systemd user guide to do so. If you wish to run the process as a 
background job, one can use **nohup** system command is unix styled OS.

```
nohup python3 sentinel-log-agent.py > sentinel-log-agent.log 2>&1 &
```

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