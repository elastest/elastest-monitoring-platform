# sentinel-agents aka EMP agents

EMP framework comes with a few agents covering most of the common use cases 
for a microservices design pattern influenced distributed system deployments. 
Current list of agents are -
- Docker containers stats agent
- System stats agent
- Java application log parser agent
Many more agents are under development and will be released along with future 
releases of EMP.

System requirements to run these agants:
- Python 3.0
- pip3

For instructions on how to execute each agent, please refer to the user guide 
included within each agent folder.

Along with featured agents, there is an example provided showing how an inline 
code instrumentation can be achieved and metrics and logs directly sent to EMP 
as a flat (simple one level) json.