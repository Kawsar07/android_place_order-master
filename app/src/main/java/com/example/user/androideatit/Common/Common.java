package com.example.user.androideatit.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.user.androideatit.Model.User;
import com.example.user.androideatit.Remote.APIService;
import com.example.user.androideatit.Remote.RetrofitClient;

public class Common {
    public static User currentUser;

    public static final String INTENT_FOOD_ID = "FoodId";
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static final String DELETE = "Delete";

    public static boolean isConnectToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info!=null){
                for(int i=0;i<info.length;i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }

                }
            }
        }
        return false;
    }


    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

}
