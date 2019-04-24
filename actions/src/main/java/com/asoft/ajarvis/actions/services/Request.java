package com.asoft.ajarvis.actions.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Request {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    private static final String CONTENT_TYPE = "content-type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";

    public  Map<String, Object> sendRequest(String hostAddress, List<String> ids, Map<String, Object> arg){
        HttpClient httpClient = HttpClientBuilder.create().build(); // Use this instead
        logger.info(String.format("Request with (%s,%s,%s)", hostAddress, ids, arg));

        try {
            HttpPost request = new HttpPost(hostAddress);

            StringEntity params = new StringEntity(
                String.format("{\"ids\":%s, \n\t\"kwargs\":\n%s}", ids.toString(), new ObjectMapper().writeValueAsString(arg)),
                UTF_8);
            request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            request.setEntity(params);

            logger.info(String.format("Request: %s\n\n\t%s", request.toString(), request.getEntity().toString()));
            HttpResponse response = httpClient.execute(request);
            logger.info(String.format("Response: %s\n\t\t\t%s", response.toString(), response.getStatusLine()));

            String resultJson = new BasicResponseHandler().handleResponse(response);
            HashMap<String,Object> result = new ObjectMapper().readValue(resultJson, HashMap.class);
            return result;
        } catch (Exception ex) {
            logger.error("Request error", ex.getMessage());
        }

        return arg;
    }
}
