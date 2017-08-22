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
+----------------+-------+---------------+--------------------------------+
| API endpoint   | Verb  | Return codes  | Comments                       |
+================+=======+===============+================================+
| /v1/api/       | GET   | 200           | ok                             |
+----------------+-------+---------------+--------------------------------+
|                |       | 500           | service down                   |
+----------------+-------+---------------+--------------------------------+
| /v1/api/user/  | POST  | 201           | created                        |
+----------------+-------+---------------+--------------------------------+
|                |       | 400           | check data                     |
+----------------+-------+---------------+--------------------------------+
|                |       | 401           | valid admin token needed       |
+----------------+-------+---------------+--------------------------------+
|                |       | 409           | user account already exists    |
+----------------+-------+---------------+--------------------------------+
|                |       | 500           | system error                   |
+----------------+-------+---------------+--------------------------------+
| /v1/api/space/ | POST  | 201           | created                        |
+----------------+-------+---------------+--------------------------------+
|                |       | 400           | check data                     |
+----------------+-------+---------------+--------------------------------+
|                |       | 401           | invalid api key                |
+----------------+-------+---------------+--------------------------------+
|                |       | 409           | space already exists for user  |
+----------------+-------+---------------+--------------------------------+
|                |       | 500           | system error                   |
+----------------+-------+---------------+--------------------------------+
| /v1/api/series/| POST  | 201           | created                        |
+----------------+-------+---------------+--------------------------------+
|                |       | 400           | check data                     |
+----------------+-------+---------------+--------------------------------+
|                |       | 401           | invalid api key                |
+----------------+-------+---------------+--------------------------------+
|                |       | 409           | series already exists for user |
+----------------+-------+---------------+--------------------------------+
|                |       | 500           | system error                   |
+----------------+-------+---------------+--------------------------------+
|/v1/api/key/{id}| GET   | 200           | ok                             |
+----------------+-------+---------------+--------------------------------+
|                |       | 400           | no such user exist             |
+----------------+-------+---------------+--------------------------------+
|                |       | 401           | invalid password               |
+----------------+-------+---------------+--------------------------------+
|/v1/api/endpoint| GET   | 200           | ok                             |
+----------------+-------+---------------+--------------------------------+
|                |       | 401           | invalid api key                |
+----------------+-------+---------------+--------------------------------+

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
        "endpoint": "/api/space/",
        "method": "GET",
        "description": "list of monitored spaces",
        "contentType": "application/json"
    },
    {
        "endpoint": "/api/space/",
        "method": "POST",
        "description": "register / create a new monitored space",
        "contentType": "application/json"
    },
    {
        "endpoint": "/api/",
        "method": "GET",
        "description": "get list of all supported APIs",
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

/v1/api/space/ POST
-------------------
::

  curl -X POST https://localhost:9000/v1/api/space/ --header "Content-Type: application/json"
  --header "x-auth-login: username" --header "x-auth-apikey: some-api-key"
  -d '{"name":"space-name"}'


/v1/api/series/ POST
--------------------
::

  curl -X POST https://localhost:9000/v1/api/series/ --header "Content-Type: application/json"
  --header "x-auth-login: username" --header "x-auth-apikey: some-api-key"
  -d '{"name":"series-name", "spaceName":"parent-space-name", "msgSignature":"msg-signature"}'


/v1/api/key/{id} GET
--------------------
::

  curl -X GET https://localhost:9000/v1/api/key/{username} 
  --header "Content-Type: application/json"
  --header "x-auth-password: some-password"

/v1/api/endpoint GET
--------------------
::

  curl -X GET https://localhost:9000/v1/api/endpoint --header "Content-Type: application/json"
  --header "x-auth-login: username" --header "x-auth-apikey: some-api-key"

