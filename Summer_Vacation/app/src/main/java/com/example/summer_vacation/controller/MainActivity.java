package com.example.summer_vacation.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.summer_vacation.R;
import com.example.summer_vacation.model.SummerVacationItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText login, password;
    TextView mText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (EditText) findViewById(R.id.field1);
        password = (EditText) findViewById(R.id.field2);
        mText = (TextView) findViewById(R.id.Resultview);
        mText.setText("");
        auth = FirebaseAuth.getInstance();
    }

    public void loginButton(View v) {
        if(TextUtils.isEmpty(login.getText()) && TextUtils.isEmpty(password.getText())){
            mText.setText("Entered username : " + login.getText().toString() + " or password are worng!");
        }else{
            auth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "LogIn Successfull!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Main_Screen.class));
                    }else{
                        Toast.makeText(MainActivity.this, "LogIn Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
        public void onBackPressed () {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    MainActivity.this);
            alert.setTitle("Attention!!!");
            alert.setMessage("Do you really want to Close the App?");
            alert.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface,
                                            int i) {
                            finish();
                        }
                    });
            alert.setNegativeButton(android.R.string.no, null);
            AlertDialog dialog = alert.create();
            dialog.show();
        }


    public void signIn(View view) {
        startActivity(new Intent(getApplicationContext(), Sign_In.class));
    }

    public void forgotpassword(View view) {
    }
}
