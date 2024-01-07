package com.loan.staffmgr.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import org.json.JSONObject;

public class BaseResponseBean {

    private int code;
    private String message;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isRequestSuccess() {
        return code == 200;
    }

    public boolean isLogout() {
        if (code == 401 || code == 405) {
            return true;
        }
        return false;
    }

    public String getBodyStr() {
        //             # 将DEFAULT改为STRING
        Gson gson = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .serializeNulls().create();
        return gson.toJson(data);
    }

}
