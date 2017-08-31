# Log parsing agent
This agent continually monitors a log file and sends all logs sent to it to EMP 
framework in a realtime manner.

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
- seriesPattern: update this with the pattern that was defined in emp while the 
target series was created using the management API, the format of the pattern 
is **unixtime:timeunit [fieldname:fieldtype] [fieldname:fieldtype] ...** where 
**fieldname** is something meaningful and **fieldtype** can be one of the 
following parameters:
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
- logPattern: actual log4j logging pattern used in the code which generates the 
logs in the targeted file
- [mapping] block: it defines the mapping between the logpattern elements and 
the elements defined in the **seriesPattern**