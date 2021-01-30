package com.example.user.androideatit.Service;

import com.example.user.androideatit.Common.Common;
import com.example.user.androideatit.Model.Token;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser != null){
            updateTokenFirebase(tokenRefreshed);
        }

    }

    private void updateTokenFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(tokenRefreshed,false);//false because this token send client app
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }
}
