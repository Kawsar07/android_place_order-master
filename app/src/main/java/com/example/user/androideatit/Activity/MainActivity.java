package com.example.user.androideatit.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.androideatit.Common.Common;
import com.example.user.androideatit.Model.User;
import com.example.user.androideatit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button btnSignIn, btnSingUp;
    private TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnSignIn = findViewById(R.id.btnSignIn);
        btnSingUp = findViewById(R.id.btnSignUp);

       /* txtSlogan = findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);*/
        Paper.init(this);

       /* btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUp = new Intent(MainActivity.this, com.example.user.androideatit.Activity.SignUp.class);
                startActivity(SignUp);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignIn = new Intent(MainActivity.this, com.example.user.androideatit.Activity.SignIn.class);
                startActivity(SignIn);
            }
        });

        //Check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if (user !=null && pwd !=null){
            if (!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);
            }
        }
    }


    //check login
    private void login(final String phone, final String pwd) {
        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Check if user not exist in database
                    if (dataSnapshot.child(phone).exists()) {
                        //Get User information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);//Set Phone
                        if (user.getPassword().equals(pwd)) {

                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();

                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User doesn't exist in database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}

