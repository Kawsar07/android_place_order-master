package com.example.user.androideatit.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {


    private TextInputEditText edtPhone, edtPassword;
    private Button btnSignIn;
    private CheckBox checkRemember;
    private TextView txtForgotPwd;

    FirebaseDatabase database;
    DatabaseReference table_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        btnSignIn = findViewById(R.id.btnSignIn);
        checkRemember = findViewById(R.id.chkRemember);
        txtForgotPwd = findViewById(R.id.txtForgotPwd);

        //init paper
        Paper.init(this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPwdDialog();
            }
        });

            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Common.isConnectToInternet(getBaseContext())) {

                        //save user and password
                        if (checkRemember.isChecked()) {
                            Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                            Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                        }

                        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                        mDialog.setMessage("Please waiting...");
                        mDialog.show();

                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //Check if user not exist in database
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    //Get User information
                                    String phoneNumber =edtPhone.getText().toString();
                                    String password = edtPassword.getText().toString();
                                    mDialog.dismiss();
                                    User user = dataSnapshot.child(phoneNumber).getValue(User.class);
                                    user.setPhone(phoneNumber);//Set Phone

                                    if (phoneNumber.isEmpty()){
                                      //  Toast.makeText(SignIn.this, "phone number field is empty", Toast.LENGTH_SHORT).show();
                                        edtPhone.setError("phone number field is empty!!");
                                        edtPhone.requestFocus();
                                        return;
                                    }
                                    else {

                                        if (password.isEmpty()){
                                            edtPassword.setError("password field is empty!!");
                                            edtPassword.requestFocus();
                                            return;
                                        }
                                        else {

                                            if (user.getPassword().equals(password)) {
                                                Intent homeIntent = new Intent(SignIn.this, Home.class);
                                                Common.currentUser = user;
                                                startActivity(homeIntent);
                                                finish();

                                                table_user.removeEventListener(this);

                                            } else {
                                                Toast.makeText(SignIn.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }


                                    }

                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "User doesn't exist in database", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Toast.makeText(SignIn.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            });


    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");
        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_pwd_layout,null);
        builder.setView(forgot_view);
        builder.setIcon(R.drawable.security_image);
        final MaterialEditText edtPhone = forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //check user available
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString())
                                .getValue(User.class);
                        if (user.getSecureCode().equals(edtSecureCode.getText().toString())){
                            Toast.makeText(SignIn.this, "Your password: " +user.getPassword(),Toast.LENGTH_LONG).show();
                        }else
                            Toast.makeText(SignIn.this, "wrong secure Code !", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public void backPressed(View view) {
        startActivity(new Intent(SignIn.this,SignUp.class));
        finish();
    }
}
