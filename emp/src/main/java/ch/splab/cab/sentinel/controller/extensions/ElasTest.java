package ch.splab.cab.sentinel.controller.extensions;

import ch.splab.cab.sentinel.*;
import ch.splab.cab.sentinel.dao.*;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//@RequestMapping("/v1")
//@RequestMapping("/${published.api.version}") //using this breaks Spring jUnit test
@RestController
@RequestMapping("/v1/extension/elastest/")
public class ElasTest {
    final static Logger logger = Logger.getLogger(ElasTest.class);

    @RequestMapping(value = {"/api/"}, method = RequestMethod.GET, produces = {"application/json"})
    @ApiOperation(value = "getApis", notes = "List of all supported API calls")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok")
    })
    public @ResponseBody
    ResponseEntity getApis()
    {
        LinkedList<APIEndpoints> value = new LinkedList<>();
        APIEndpoints value3 = new APIEndpoints();
        value3.endpoint = "/v1/extension/elastest/api/";
        value3.method = "GET";
        value3.description = "get list of all supported extra APIs for ElasTest";
        value3.contentType = "application/json";
        value.add(value3);
        APIEndpoints value1 = new APIEndpoints();
        value1.endpoint = "/v1/extension/elastest/tjobstat/{id}"; //each space corresponds to an individual db and vectors in a space make for tables in a db
        value1.method = "GET";
        value1.description = "get execution statistics ";
        value1.contentType = "application/json";
        value.add(value1);

        Gson gson = new Gson();
        String jsonInString = gson.toJson(value);
        return ResponseEntity.status(HttpStatus.OK).body(jsonInString);
    }

    @RequestMapping(value = {"/tjobstat/{tjobid}"}, method = RequestMethod.GET, produces = {"application/json"})
    @ApiOperation(value = "getTJobStats", notes = "get execution statistics of all runs of this TJob")
    @ApiResponses({
            @ApiResponse(code = 401, message = "invalid api key"),
            @ApiResponse(code = 200, message = "ok")
    })
    public @ResponseBody
    ResponseEntity getTJobStats(@RequestHeader(value = "x-auth-apikey") String apiKey,
                                @RequestHeader(value = "x-auth-login") String login,
                                @RequestHeader(value = "x-topic-name") String topic,
                                @RequestHeader(value = "x-series-name") String series,
                                @PathVariable(value="tjobid") String tjobid)
    {
        Gson gson = new Gson();
        int userId = -1;
        try {
            userId = Integer.parseInt(login);
        } catch(NumberFormatException nex)
        {
            //supplied value is not an id but a login
            userId = SqlDriver.getUserId(login);
        }
        if(!SqlDriver.isValidApikey(userId, apiKey))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid api key");
        String filter = "\"io.elastest.tjob.id\"='"+tjobid+"'";
        LinkedList<String> execIds = InfluxDBClient.getTJobExecIds(topic, series, filter);
        HashMap<String, Object>[] dataSet = new HashMap[execIds.size()];
        int counter = 0;
        for (String tJobExecId : execIds) {
            HashMap<String, Object> data = InfluxDBClient.getTJobExecData(topic, series, tjobid, tJobExecId);
            dataSet[counter] = data;
            counter++;
        }
        ElasTestTJobExecSpecs response = new ElasTestTJobExecSpecs();
        response.TJobId = tjobid;
        response.ExecutionRun = dataSet;
        String jsonInString = gson.toJson(response);

        return ResponseEntity.status(HttpStatus.OK).body(jsonInString);
    }

}
