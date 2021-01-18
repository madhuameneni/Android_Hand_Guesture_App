package com.example.summer_vacation.controller;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.summer_vacation.R;
import com.example.summer_vacation.ml.Model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import java.io.IOException;
import java.util.List;


public class Instruction extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private int[] intValues = new int[200 * 200];
    int rated_data = 0;
    Boolean rated;
    Query query;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_instruction);
        myRef = FirebaseDatabase.getInstance().getReference();
        Button photoButton = (Button) this.findViewById(R.id.Image);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage(v);
            }
        });
    }


    private void getImage(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            try {
                Model model = Model.newInstance(getApplicationContext());

                // Creates inputs for reference.
                TensorImage image = TensorImage.fromBitmap(resized);

                // Runs model inference and gets result.
                Model.Outputs outputs = model.process(image);
                List<Category> probability = outputs.getProbabilityAsCategoryList();
                if(!probability.isEmpty()){
                    if(probability.get(0).getLabel().equalsIgnoreCase("one") && probability.get(0).getScore() > 0.5){
                        rated = true;
                        rated_data = 1;
                    }
                    else if(probability.get(1).getLabel().equalsIgnoreCase("three") && probability.get(1).getScore() > 0.5){
                        rated = true;
                        rated_data = 3;
                    }
                    else if(probability.get(2).getLabel().equalsIgnoreCase("two") && probability.get(2).getScore() > 0.5){
                        rated = true;
                        rated_data = 2;
                    }else {
                        rated = false;
                    }
                }
                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {
                // TODO Handle the exception
            }
        }
        if(rated){
            Bundle bd = getIntent().getExtras();
            updateDataFromFirebase(bd.getString("places"), bd.getString("place_name"), rated_data, rated);
        }
        Intent intent = new Intent(Instruction.this,
                Main_Screen.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                Instruction.this);
        alert.setTitle("Attention!!!");
        alert.setMessage("Not intrested to rate?");
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

    public float[] findMaximumIndex(float[][] a) {
        float maxVal = -99999;
        float[] answerArray = new float[2];
        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a[row].length; col++) {
                if (a[row][col] > maxVal) {
                    maxVal = a[row][col];
                    answerArray[0] = row;
                    answerArray[1] = col;
                }
            }
        }
        return answerArray;
    }

    private boolean updateDataFromFirebase(String place, String place_name, int current_rate, boolean change_rate) {
        if(place.equalsIgnoreCase("Beaches")){
            query  = myRef.child("Beaches");
        }else{
            query = myRef.child("Cities");
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(change_rate){
                        if(dataSnapshot.child("name").getValue().toString().equals(place_name)){
                            String rate = dataSnapshot.child("rating").getValue().toString();
                            String[] array = rate.split(":");
                            if(current_rate == 1){
                                array[0] = String.valueOf(Integer.parseInt(array[0]) + 1);
                            }else if(current_rate == 2){
                                array[1] = String.valueOf(Integer.parseInt(array[1]) + 1);
                            }else{
                                array[2] = String.valueOf(Integer.parseInt(array[2]) + 1);
                            }
                            dataSnapshot.child("rating").getRef().setValue(array[0] + ":"+ array[1] + ":" + array[2]);
                            rated = false;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v(getString(R.string.error), error.getMessage());
            }
        });
        return false;
    }
}
