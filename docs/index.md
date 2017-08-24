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


[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
[ElasTest]: http://elastest.io/
[ElasTest Logo]: http://elastest.io/images/logos_elastest/elastest-logo-gray-small.png
[ElasTest Twitter]: https://twitter.com/elastestio
[GitHub ElasTest Group]: https://github.com/elastest
[Bugtracker]: https://github.com/elastest/bugtracker
