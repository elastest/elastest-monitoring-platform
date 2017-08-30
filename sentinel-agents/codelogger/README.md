# How to send logs to EMP directly from code
This is a guide to show how easy it is to send log messages directly into EMP 
framework. An example code for sending meaningful log messages from a python 
code is included in this folder.

## Key considerations
The requirement for sending inline log messages into EMP are very simple:
- a flat structured json message should be sent
- the EMP series part of an existing space decleration must have this 
**msgSignature** value: *seriesPattern = unixtime:s msgtype:json*
- the json message must have this element in it: 
**"agent":"sentinel-internal-log-agent"**

## Example json messages
```
{
	"agent": "sentinel-internal-log-agent",
	"file": "/sample/codelogger/code-agent.py",
	"level": "trace",
	"method": "<module>:128",
	"msg": "some meaningful log msg goes in here"
}
```

As long as **"agent":"sentinel-internal-log-agent"** field is set in the json 
message, all flat messages are valid messages to be sent into emp. Here is 
another example:
```
{
	"msg": "Returning registered services. Count: 0",
	"agent": "sentinel-internal-log-agent",
	"level": "DEBUG",
	"file": "/app/adapters/log.py"
}
```

## Extending the logging for other languages
The included python file implements a logging class called *SentinelLogger* 
that enables rest of the software to simply instatiate an object of this class 
and simple send log messages the most natural way.
```
logger = SentinelLogger()
logger.warn("some warning worthy message")
logger.error("some error worthy message")
logger.debug("some debug worthy message")
logger.info("some info worthy message")
logger.trace("some trace worthy message")
```

Similar libraries can be developed for popular languages such as Java, Go, etc. 
The future release of EMP will include more language adapters enabling inline 
logging.

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