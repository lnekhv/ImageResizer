package com.example.photoresolution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.photoresolution.model.Image;
import com.example.photoresolution.model.ImageDatabase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private String text;
    private static final String URL = "http://192.168.1.69:1000/img";
    private RequestQueue mRequestQueue;

    private ImageView image;
    private TextView textImgName;
    private TextView textImgTimestamp;
    private TextView textImgResolution;
    private Button saveGallery;
    private Button buttonSaveDatabase;

    FirebaseUser user;

    private Toolbar toolbar;
    private TextView toolbar_text;
    private Button button_cancel;
    private ImageButton button_refresh;

    FirebaseDatabase database;
    DatabaseReference myRef;

    private StorageReference mStorageRef;

    Image imageModel;
    Bitmap bitmap;
    public String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        image = (ImageView) findViewById(R.id.reconstruct_image);
        textImgName = findViewById(R.id.text_imageName);
        textImgTimestamp = findViewById(R.id.text_imageTimestamp);
        textImgResolution = findViewById(R.id.text_imageResolution);
        saveGallery = findViewById(R.id.button_result_gallery);
        buttonSaveDatabase = findViewById(R.id.button_result_database);
        button_cancel = findViewById(R.id.button_cancel);
        button_refresh = (ImageButton) findViewById(R.id.button_refresh);


        buttonSaveDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToStorage();
                Toast.makeText(ResultActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultActivity.this, ResultActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        saveGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveToGallery();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference().child("images");

        toolbar = findViewById(R.id.myToolbar);
        toolbar_text = findViewById(R.id.text_user_login_toolbar);

        setSupportActionBar(toolbar);
        setToolbarText();

        volleyGet();
        checkUser();
    }

    private void checkUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
        } else {
            buttonSaveDatabase.setEnabled(false);
        }
    }


    private void setToolbarText() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String login = user.getEmail();
            Log.d("Login", login);
            toolbar_text.setText(login);
        } else {
            toolbar_text.setText("");
        }
    }

    private void SaveToGallery() {
        Toast.makeText(this, "Saving to gallery", Toast.LENGTH_SHORT).show();
        Log.d("bitmap", bitmap.toString());

        if (bitmap == null) {
            Toast.makeText(this, "There is no image to save", Toast.LENGTH_SHORT).show();
            return;
        }

        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                imageModel.getName(),
                "Reconstructed image"
        );
        Log.d("savedImageURL", savedImageURL);
    }

    public void volleyGet() {
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        setData(jsonObject);
                        text = "Data uploaded";
                        Toast.makeText(ResultActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                text = "That didn't work!";
                Log.e("POST ERROR", error.toString());
                Toast.makeText(ResultActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }

        };
        mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(200, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(JsonObjectRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }

    private void setData(JSONObject jsonObject) {
        String img = (String) jsonObject.opt("result_img");
        String name = (String) jsonObject.opt("name");
        String timestamp = (String) jsonObject.opt("timestamp");
        String resolution = (String) jsonObject.opt("resolution");

        imageModel = new Image(name, timestamp, resolution);
        Log.d("imageModel", imageModel.getName() + imageModel.getTimestamp() + imageModel.getResolution());

        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        imageModel.setImg(img);
        bitmap = decodedImg;

        image.setBackgroundColor(Color.TRANSPARENT);
        image.setImageBitmap(decodedImg);

        textImgName.setText("Image name: " + name);
        textImgTimestamp.setText("Timestamp: " + timestamp);
        textImgResolution.setText("Resolution: " + resolution);
    }

    private void saveDataToDatabase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("images").child(user.getUid());

        ImageDatabase imageDatabaseModel = new ImageDatabase(imageModel.getName(), imageModel.getTimestamp(), imageModel.getResolution(), downloadUrl);
        myRef.push().setValue(imageDatabaseModel);
    }

    private void uploadImageToStorage() {
        String extensions = "." + FilenameUtils.getExtension(imageModel.getName());
        String fileName = FilenameUtils.removeExtension(imageModel.getName());
        final StorageReference imageRef = mStorageRef.child(fileName + extensions);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(ResultActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ResultActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
//                        downloadUrl = task.getResult().toString();
//                        Log.d("downloadUrl", downloadUrl);
//                        saveDataToDatabase();
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult().toString();
                            Log.d("downloadUrl", downloadUrl);
                            Toast.makeText(ResultActivity.this, "Image uploadeded to storage successfully", Toast.LENGTH_SHORT).show();
                            saveDataToDatabase();
                        }
                    }
                });
            }
        });
    }

}