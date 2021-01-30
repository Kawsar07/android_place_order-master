package com.example.user.androideatit.Remote;

import com.example.user.androideatit.Model.MyResponse;
import com.example.user.androideatit.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAzf3ErhA:APA91bE9IzShGr23psMTqbrw2yVFCpRN0wPxhRc9ZhUw0n7jD6U-sNAedwREY-LwNm-Mbqxo6kVWiK8IulzzDaa2e1ELp1Ps3BqwzReucXWiQXiRfVw_H2H3ODJB_S1B78R2m2J2KNwH"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
