package com.company.project.core;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Feedback {
    public static void feedback(String user, String item, String type) throws IOException {
        //@Headers({"Content-Type:application/json","Accept:application/json"});
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            if(user!=null){
                json.put("UserId",user);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            json.put("ItemId",item);
            json.put("FeedbackType",type);
            json.put("Timesramp",df.format(new Date()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Headers.Builder headers_builder=new Headers.Builder();
        Headers headers = headers_builder.build();
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(String.valueOf(json),JSON);
        Request request = new Request.Builder()
                .url("http://45.77.21.236:8087/api/feedback")
                .method("POST",requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if(response.code()!=200){
            System.out.println(response.message());
        }
    }
}
