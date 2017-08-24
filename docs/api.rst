===================
Using sentinel APIs
===================

Sentinel monitoring exposes a rich set of APIs for user and space management. The current release of sentinel has APIs supporting bare-minimal features and as the features set get richer, so will be the APIs. Below are the list of APIs currently offered by the framework -

* /v1/api/ - shows list of supported APIs
* /v1/api/user/ - everything to do with user management
* /v1/api/space/ - management of monitoring space
* /v1/api/series/ - management of series with any space
* /v1/api/key/ - API to retrieve user's API key if forgotten
* /v1/api/endpoint - API to retrieve Sentinel's data interface parameters

Key concepts
============

**Space**: Think of it as a collection of metrics belonging to different streams but somehow belonging to the same scope, application or service. A space could be allocated to metrics of smaller services making up a larger application or service.

**Series**: A series in Sentinel is a stream of metrics coming from the same source.

API return codes at a glance
============================
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

Header fields at a glance
=========================
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

APIs in details
===============
Now that we have all the basic building buildings in place, lets explore each API endpoint in more details. In the following subsections lets assume that the sentinel API service is available at https://localhost:9000/. Also API example will be provided as a valid cURL command.

/v1/api/ GET
------------
This API allows a quick check on the health status, if the service is alive a 200 status code is returned along with a list of supported API endpoints.

::

  curl -X GET https://localhost:9000/v1/api/

The response is similar to one shown below -
::

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

The output above is representative, and the actual API supported by sentinel varied during the time of writing of this document.

/v1/api/user/ POST
------------------
Use this API to create a new user of sentinel. User account creation is an admin priviledged operation and the *admin-token* is required as header for the call to be executed successfully.

::

  curl -X POST https://localhost:9000/v1/api/user/ --header "Content-Type: application/json" 
  --header "x-auth-token: <admin-token>" -d '{"login":"username", "password":"some-password"}'

If the user already exists, you will get a *409 Conflict* status response back. An example response upon successful creation of an account looks as shown below, the actual value is for representation purposes only -

::

  {
    "login": "username",
    "apiKey": "b6af63b9-f699-4259-8548-2a60e0d88661",
    "id": 2,
    "accessUrl": "/api/user/2"
  }

The *apiKey* and *id* values should be saved as they are needed in some of the management API requests as you will see later.

/v1/api/user/{id} GET
---------------------
Use this API to retrieve the complete information about an user account, the monitoring spaces and series info included. A valid *api-key* needs to be provided as a header field while making this call.

::

  curl -X GET https://localhost:9000/v1/api/user/{id} --header "Content-Type: application/json"
  --header "x-auth-apikey: valid-api-key"

If the call succeeds then the complete details of the account is returned back. A sample value returned is shown next.

::

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

/v1/api/space/ POST
-------------------
Use this API to create a new monitored space for a given user account in sentinel. A matching *username* and the *api-key* needs to be provided as header fields. The body just contains the *name* of the space that one wishes to create.

::

  curl -X POST https://localhost:9000/v1/api/space/ --header "Content-Type: application/json"
  --header "x-auth-login: username" --header "x-auth-apikey: some-api-key"
  -d '{"name":"space-name"}'

If the call is successful, the *space id* is returned back as confirmation. A sample response is shown next.

::

  {
    "id": 3,
    "accessUrl": "/api/space/3",
    "topicName": "user-1-new-space",
    "name": "new-space",
    "dataDashboardUrl": "http://localhost:8083/",
    "dataDashboardUser": "user1new-space",
    "dataDashboardPassword": "GeMHPDUwKc5621ZI"
  }

/v1/api/series/ POST
--------------------
A space by itself does not handle data streams, it is a container and needs a series to be defined before the metrics sent to it can be persisted and analyzed later. This API allows creation of a *series* within an existing *space*. The *msgSignature* allows sentinel to parse the incoming messages properly. 

If the message being sent into sentinel is a single level JSON string, the *unixtime:s msgtype:json* value is sufficient.

::

  curl -X POST https://localhost:9000/v1/api/series/ --header "Content-Type: application/json"
  --header "x-auth-login: username" --header "x-auth-apikey: some-api-key"
  -d '{"name":"series-name", "spaceName":"parent-space-name", "msgSignature":"msg-signature"}'

If the call is successful, a *series id* is returned. An example response block is shown for completeness.

::

  {
    "id": 1,
    "accessUrl": "/api/series/2",
    "name": "some-app-logs"
  }

/v1/api/key/{id} GET
--------------------
One can use this API if there is a need to retrieve the user api-key. The *username* should be a registered account and the *some-password* header field should be the matching password for this account.

::

  curl -X GET https://localhost:9000/v1/api/key/{username} 
  --header "Content-Type: application/json"
  --header "x-auth-password: some-password"

If the call is successful, the API-key is returned. A sample response is shown next.

::

  {
    "apiKey": "f3549958-8884-4649-9661-8ca338dfe141",
    "id": 1,
    "accessUrl": "/api/user/1"
  }

/v1/api/endpoint GET
--------------------
This API call can be used to retrieve the connection parameters for the sentinel agents to send data streams to. The call is available only to registered accounts, therefore a valid *username* and *api-key* needs to be supplied as header fields.

::

  curl -X GET https://localhost:9000/v1/api/endpoint --header "Content-Type: application/json"
  --header "x-auth-login: username" --header "x-auth-apikey: some-api-key"

If the call succeeds, the parameter block is returned that can be used to properly configure the sentinel agents. A sample response is shown next.

::

  {
    "endpoint": "kafka:9092",
    "keySerializer": "StringSerializer",
    "valueSerializer": "StringSerializer"
  }
