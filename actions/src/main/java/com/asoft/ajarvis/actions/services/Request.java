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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class Request {


    private static final Logger logger = LoggerFactory.getLogger(Request.class);






    public  Map<String, Object> sendRequest(String hostAddress,ArrayList<String> ids, Map arg){
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
 logger.info("request with("+hostAddress+","+ids+","+  arg);
        try {
            HttpPost request = new HttpPost(hostAddress);
            StringEntity params =new StringEntity( "{\"ids\":"+ids.toString()+",\n" +
                    "\t\"kwargs\":\n" +new ObjectMapper().writeValueAsString(arg)+
                    "}","UTF-8");
            request.addHeader("content-type", "application/json");
            request.setEntity(params);


            logger.info("request :"+request.toString()+"\n\n\t"+request.getEntity().toString());
            HttpResponse response = httpClient.execute(request);
                logger.info("response :"+response.toString()+"\n\t\t\t"+response.getStatusLine());


                String resultJson = new BasicResponseHandler().handleResponse(response);
                //logger.info("resultJson");
                HashMap<String,Object> result =
                        new ObjectMapper().readValue(resultJson, HashMap.class);
                return result;


        }catch (Exception ex) {
            logger.error("request error ",ex);


        } finally {
            //Deprecated
            //httpClient.getConnectionManager().shutdown();
        }


        return arg;



    }





}
