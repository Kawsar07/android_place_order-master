package com.example.user.androideatit.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class SignUp extends AppCompatActivity {

    TextInputEditText edtPhone, edtName, edtPassword,edtSecureCode;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtSecureCode = findViewById(R.id.edtSecureCode);

        btnSignUp = findViewById(R.id.btnSignUp);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectToInternet(getBaseContext())){
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Check if already user phone
                        String phoneNumber = edtPhone.getText().toString();
                        String name = edtName.getText().toString();
                        String password = edtPassword.getText().toString();
                        String secureCode = edtSecureCode.getText().toString();

                        if (phoneNumber.isEmpty()){
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Please fill this field!!", Toast.LENGTH_SHORT).show();
                            edtPhone.setError("Phone field is empty!!");
                            edtPhone.requestFocus();
                            return;
                        }else {
                            if (dataSnapshot.child(phoneNumber).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone Number already register", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else if(!name.isEmpty()) {

                                if (!password.isEmpty()){
                                    mDialog.dismiss();
                                    User user = new User(name,password,secureCode);

                                    table_user.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp.this, "Sign Up successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    mDialog.dismiss();
                                    //Toast.makeText(SignUp.this, "You must have fill this field!!", Toast.LENGTH_SHORT).show();
                                    edtPassword.setError("Password field is empty!!");
                                    edtPassword.requestFocus();
                                    return;
                                }
                            }
                            else {
                                mDialog.dismiss();
                                //Toast.makeText(SignUp.this, "You must have fill this field!!", Toast.LENGTH_SHORT).show();
                                edtName.setError("Name field is empty!!");
                                edtName.requestFocus();
                                return;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
                else {
                    Toast.makeText(SignUp.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    public void backPressed(View view) {
        startActivity(new Intent(SignUp.this,MainActivity.class));
        finish();
    }
}
