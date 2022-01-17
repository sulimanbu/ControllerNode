package com.example.controllernode.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class ResponseModel<T> implements Serializable {
    private T Result;
    private boolean isSuccess;
    private String message;

    public T getResult() {
        return Result;
    }
    public boolean isSuccess() {
        return isSuccess;
    }
    public String getMessage() {
        return message;
    }

    public static class Builder<T> {
        private T Result;
        private boolean isSuccess;
        private String message;

        public Builder(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }
        public Builder<T> Result(T val) { Result = val; return this; }
        public Builder<T> message(String val) { message = val; return this; }
        public ResponseModel<T> build() {
            return new ResponseModel<T>(this);
        }

    }

    private ResponseModel(Builder<T> builder) {
        Result  = builder.Result;
        message     = builder.message;
        isSuccess     = builder.isSuccess;
    }

    @Override
    public String toString() {
        try {
            ObjectMapper Obj = new ObjectMapper();
            return Obj.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
