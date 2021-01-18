package com.example.summer_vacation.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summer_vacation.R;
import com.example.summer_vacation.model.SummerVacationItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Main_Screen extends AppCompatActivity {
    RecyclerView mainRecycleView;
    RecycleViewAdapter recycleViewAdapter;
    private DatabaseReference myRef;
    private ArrayList<SummerVacationItems> items;
    private FirebaseAuth auth;
    Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__screen);
        mainRecycleView = findViewById(R.id.mainRecycleView);
        myRef = FirebaseDatabase.getInstance().getReference();
        items = new ArrayList<>();
        clearAll();
        getDataFromFirebase("Beaches");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecycleView.setLayoutManager(layoutManager);
        mainRecycleView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();

    }

    private void clearAll() {
        if(items != null){
            items.clear();
            if(recycleViewAdapter != null){
                recycleViewAdapter.notifyDataSetChanged();
            }
        }else{
            items = new ArrayList<>();
        }
    }

    private void getDataFromFirebase(String input) {
        if(input.equalsIgnoreCase("Beaches")){
            query  = myRef.child("Beaches");
        }else{
            query = myRef.child("Cities");
        }
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearAll();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    SummerVacationItems summerVacationItems = new SummerVacationItems();
                    summerVacationItems.setImageURL(dataSnapshot.child("image").getValue().toString());
                    summerVacationItems.setInfo(dataSnapshot.child("info").getValue().toString());
                    summerVacationItems.setRating(dataSnapshot.child("rating").getValue().toString());
                    summerVacationItems.setName(dataSnapshot.child("name").getValue().toString());
                    items.add(summerVacationItems);
                }
                recycleViewAdapter = new RecycleViewAdapter(getApplicationContext(), items);
                recycleViewAdapter.setOnItemClickListener(new RecycleViewAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Intent intent = new Intent(Main_Screen.this,
                                Instruction.class);
                        intent.putExtra("index", position);
                        intent.putExtra("places", input);
                        SummerVacationItems summerVacationItems = items.get(position);
                        intent.putExtra("place_name", summerVacationItems.getName());
                        startActivity(intent);
                    }
                });
                mainRecycleView.setAdapter(recycleViewAdapter);
                recycleViewAdapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v(getString(R.string.error), error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_beaches){
            Toast.makeText(this, "Your watching Beaches in France!", Toast.LENGTH_LONG).show();
            items = new ArrayList<>();
            clearAll();
            getDataFromFirebase("Beaches");
        }else if(id == R.id.action_cities){
            Toast.makeText(this, "Your watching Cities in France!", Toast.LENGTH_LONG).show();
            items = new ArrayList<>();
            clearAll();
            getDataFromFirebase("Cities");
        }else if(id == R.id.action_logout){
            Toast.makeText(this, "You are succesfully Logged out!", Toast.LENGTH_LONG).show();
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return true;
    }

    @Override
    public void onBackPressed () {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                Main_Screen.this);
        alert.setTitle("Attention!!!");
        alert.setMessage("Do you really want to logout?");
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
        auth.signOut();
    }

}