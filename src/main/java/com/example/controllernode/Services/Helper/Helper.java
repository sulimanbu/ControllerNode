package com.example.controllernode.Services.Helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class Helper {

    private Helper(){
        throw new AssertionError();
    }

    public static boolean isMatch(String fileContent,String filter) throws JsonProcessingException {
        var fileContentNode=new JSONObject(fileContent);
        var filterNode = new JSONObject(filter);

        var fieldNames=filterNode.names().toList();
        var isEqual=false;
        for (var fieldName:fieldNames){
            if(!fileContentNode.has(fieldName.toString())){
                isEqual=false;
                break;
            }

            var i1=fileContentNode.get(fieldName.toString());
            var i2=filterNode.get(fieldName.toString());

            JSONObject dataObject = fileContentNode.optJSONObject(fieldName.toString());
            if(dataObject != null){
                isEqual = isMatch(i1.toString(), i2.toString());

                if(!isEqual)
                    break;
            } else {
                if(i1!=null && i1.equals(i2)){
                    isEqual=true;
                }else {
                    isEqual=false;
                    break;
                }
            }
        }

        return isEqual;
    }

}
