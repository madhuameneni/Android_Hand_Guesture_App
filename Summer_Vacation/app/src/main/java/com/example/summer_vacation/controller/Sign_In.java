package com.example.summer_vacation.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.summer_vacation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Sign_In extends AppCompatActivity {
    EditText login, password, dob;
    TextView mText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__in);
        login = (EditText) findViewById(R.id.field1);
        password = (EditText) findViewById(R.id.field2);
        dob = (EditText) findViewById(R.id.field3);
        mText = (TextView) findViewById(R.id.Resultview);
        mText.setText("");
        auth = FirebaseAuth.getInstance();
    }

    public void LogIn(View view) {
        Intent intent = new Intent(Sign_In.this,
                MainActivity.class);
        startActivity(intent);
    }

    public void SignUp(View view) {
        if( TextUtils.isEmpty(login.getText()) && TextUtils.isEmpty(password.getText()) && TextUtils.isEmpty(dob.getText())){
            mText.setText("Some data is missing!!!");
        }else if(password.getText().toString().length() < 6){
            mText.setText("Entered password" + password.getText().toString() +"is less than 6 characters.");
        } else{
            auth.createUserWithEmailAndPassword(login.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Sign_In.this, "SignIn Successfull!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else{
                        Toast.makeText(Sign_In.this, "SignIn Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}