package com.example.computer.itechnology;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private EditText useredit,passedit;
    private Button logbtn,twitter;
    private FirebaseAuth mauth;
    private DatabaseReference UserRef;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializefields();
        mauth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseUser = mauth.getCurrentUser();
        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             logintohome();
            }
        });
       GotoHome();
    }
    public void GotoHome()
    {
        UserRef.child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("UserName")&&(dataSnapshot.hasChild("Password")))){
                    String retrieveusername = dataSnapshot.child("UserName").getValue().toString();
                    String retrievestatus = dataSnapshot.child("Password").getValue().toString();
                    useredit.setText(retrieveusername);
                    passedit.setText(retrievestatus);
                    SendUserToHomeActivity();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please enter your status...",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void logintohome() {
        String name = useredit.getText().toString();
        String password = passedit.getText().toString();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please provide email...",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please provide first name...",Toast.LENGTH_LONG).show();
        } else {
            if (name.equals("admin") && password.equals("admin")) {
                progressDialog.setTitle("Logging in");
                progressDialog.setMessage("Please wait, process is under construction...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();
                Map<String, Object> profilemap = new HashMap<>();
                profilemap.put("UserName", name);
                profilemap.put("Password", password);
                UserRef.child("Info").setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SendUserToHomeActivity();
                            Toast.makeText(LoginActivity.this, "Logged Successfully..", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(LoginActivity.this,"wrong credentials...",Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void initializefields() {
        useredit = (EditText)findViewById(R.id.atvEmailLog);
        passedit = (EditText)findViewById(R.id.atvPasswordLog);
        logbtn = (Button)findViewById(R.id.btnSignIn);
        progressDialog = new ProgressDialog(this);
    }
    private void SendUserToHomeActivity() {
        Intent loginInt = new Intent(LoginActivity.this,Home.class);
        loginInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginInt);
    }
}
