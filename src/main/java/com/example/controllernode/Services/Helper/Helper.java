package com.example.controllernode.Services.Helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Helper {

    private Helper(){
        throw new AssertionError();
    }

    public static boolean isMatch(String fileContent,String filter) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode fileContentNode = mapper.readTree(fileContent);
        JsonNode filterNode = mapper.readTree(filter);

        var fieldNames=filterNode.fieldNames();
        var isEqual=false;
        while(fieldNames.hasNext()){
            var fieldName=fieldNames.next();
            var i1=fileContentNode.findValue(fieldName);
            var i2=filterNode.findValue(fieldName);

            if(i1!=null && i1.equals(i2)){
                isEqual=true;
            }else {
                isEqual=false;
                break;
            }
        }

        return isEqual;
    }

}
