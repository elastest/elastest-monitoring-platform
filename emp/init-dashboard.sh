# script to see if InfluxDB is alive, Grafana is alive, if not wait
# else populate the db and dashboard in grafana, assuming defaults
# or values from environment variables

influxURL=${INFLUX_URL:=http://localhost:8086}
influxURLGrafana=${INFLUX_URL_GRAFANA:=http://localhost:8086}
grafanaURL=${GRAFANA_URL:=http://localhost:3000}
username=${GRAFANA_ADMIN:=admin}
password=${GRAFANA_PASSWORD:=1ccl@b2017}
influxuser=${INFLUX_USER:=root}
influxpassword=${INFLUX_PASSWORD:=1ccl@b2017}
sentineldb=${SENTINEL_DB_ENDPOINT:=sentinel.db}

until $(curl --output /dev/null --silent --head --fail ${influxURL}/ping); do
    printf 'waiting for influxdb to get ready\n'
    sleep 5
done

response=$(curl --write-out %{http_code} --silent --output /dev/null ${grafanaURL}/api/login/ping)

while [ $response -ne 401 ]; do
    response=$(curl --write-out %{http_code} --silent --output /dev/null ${grafanaURL}/api/login/ping)
    printf 'waiting for grafana to get ready\n'
    sleep 5
done

credentials="$(echo -n "$username:$password" | base64)"
header="Authorization: Basic $credentials"

# adding the datasource in Grafana
generate_post_data()
{
  cat <<EOF
{
        "id": null,
        "orgId": 1,
        "name": "elastest",
        "type": "influxdb",
        "typeLogoUrl": "public/app/plugins/datasource/influxdb/img/influxdb_logo.svg",
        "access": "proxy",
        "url": "$influxURLGrafana",
        "password": "$influxpassword",
        "user": "$influxuser",
        "database": "user-1-elastest_core",
        "basicAuth": false,
        "isDefault": true,
        "jsonData": {
            "timeInterval": "30s"
        }
}
EOF
}

curl -X POST \
  ${grafanaURL}/api/datasources \
  -H "$header" \
  -H 'content-type: application/json' \
  -d "$(generate_post_data)"

# adding the preconfigured elastest dashboard

curl -X POST \
  ${grafanaURL}/api/dashboards/db \
  -H "$header" \
  -H 'content-type: application/json' \
  -d "@dashboard.json"

curl -X PUT \
  ${grafanaURL}/api/org/preferences \
  -H "$header" \
  -H 'content-type: application/json' \
  -d '{"theme": "", "homeDashboardId":1, "timezone":"utc"}'

sha256()
{
 echo -n "$*" | sha256sum | cut -d' ' -f1
}

# proconfiguring the elastest space and series and table schema
if [ -e ${sentineldb} ]
then
  echo "table already exists, will try to populate defaults anyway"
  sqlite3 ${sentineldb} "INSERT INTO user VALUES (1, 'elastest', '$(sha256 pass1234)', '480f410f-f506-4b7d-ac2b-dc51758d8d15')"
  sqlite3 ${sentineldb} "INSERT INTO space VALUES (1, 'elastest_core', 'user1elastest_core', 'Tv8W4qShV2SaGNRV', 1)"
  sqlite3 ${sentineldb} "INSERT INTO series VALUES (1, 'sys-stats', 'unixtime:ms host:string cpu_user:float cpu_system:float cpu_idle:float cpu_percent:float ram_percent:float disk_percent:float', 1)"
  sqlite3 ${sentineldb} "INSERT INTO series VALUES (2, 'docker-stats', 'unixtime:s msgtype:json', 1)"
else
  sqlite3 ${sentineldb} "CREATE TABLE user (id INTEGER PRIMARY KEY AUTOINCREMENT, login VARCHAR(64), passwordhash VARCHAR(128), apikey VARCHAR(128))"
  sqlite3 ${sentineldb} "CREATE TABLE space (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32), queryuser VARCHAR(32), querypass VARCHAR(32), userid INT);"
  sqlite3 ${sentineldb} "CREATE TABLE series (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32), structure VARCHAR(512), spaceid INT);"
  sqlite3 ${sentineldb} "CREATE TABLE healthcheck (id INTEGER PRIMARY KEY AUTOINCREMENT, pingurl VARCHAR(256), reporturl VARCHAR(256), periodicity INTEGER, tolerance INTEGER, method VARCHAR(32), userid INT);"
  sqlite3 ${sentineldb} "INSERT INTO user VALUES (1, 'elastest', '$(sha256 pass1234)', '480f410f-f506-4b7d-ac2b-dc51758d8d15')"
  sqlite3 ${sentineldb} "INSERT INTO space VALUES (1, 'elastest_core', 'user1elastest_core', 'Tv8W4qShV2SaGNRV', 1)"
  sqlite3 ${sentineldb} "INSERT INTO series VALUES (1, 'sys-stats', 'unixtime:ms host:string cpu_user:float cpu_system:float cpu_idle:float cpu_percent:float ram_percent:float disk_percent:float', 1)"
  sqlite3 ${sentineldb} "INSERT INTO series VALUES (2, 'docker-stats', 'unixtime:s msgtype:json', 1)"
fi
