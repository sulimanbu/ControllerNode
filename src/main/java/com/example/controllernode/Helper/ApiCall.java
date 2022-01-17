package com.example.controllernode.Helper;

import com.example.controllernode.Model.ResponseModel;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ApiCall {

    public static ResponseModel<String> post(String Url, String body) throws UnsupportedEncodingException, UnirestException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", JWT.createJWTForNode());

        var response =  Unirest.post(Url).headers(headers).body(body)
                .asJson();

        return new ResponseModel.Builder<String>(true).build();
    }
}
