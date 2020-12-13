package com.example.photoresolution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.photoresolution.Database.DatabaseActivity;
import com.example.photoresolution.model.ImageDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonCamera;
    private Button buttonGallery;
    private Button buttonDatabase;
    private ImageView imageView;
    private TextView toolbar_text;
    private TextView text_resolution;

    private Bitmap bitmap;

    //Request code gallery
    private static final int GALLERY_REQUEST = 9;

    //Request code for camera
    private static final int CAMERA_REQUEST = 11;

    private static final String URL = "http://192.168.1.69:1000/img";

    private RequestQueue mRequestQueue;
    private String text;
    private JSONObject jsonObject;
    SharedPreferences localPrefs;

    private String imageId = "";

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageId = getIntent().getStringExtra("imageId");

        getImageFromDatabase();

        localPrefs = this.getPreferences(Context.MODE_PRIVATE);
        String savedImg = localPrefs.getString("Image", "");

        toolbar = findViewById(R.id.myToolbar);
        toolbar_text = findViewById(R.id.text_user_login_toolbar);

        buttonCamera = (Button) findViewById(R.id.button_camera);
        buttonGallery = (Button) findViewById(R.id.button_gallery);
        buttonDatabase = (Button) findViewById(R.id.button_database);
        imageView = (ImageView) findViewById(R.id.reconstruct_image);
        text_resolution = findViewById(R.id.text_resolution);

        imageView.setDrawingCacheEnabled(true);

        setSupportActionBar(toolbar);
        setToolbarText();

        setUserLogin();

        final Button button = findViewById(R.id.button_reconstruct);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Bitmap bitmap = imageView.getDrawingCache();

                if (bitmap != null) {
                    boolean upload_result = uploadImageFile();

                    if (upload_result) {
                        Intent intent = new Intent(MainActivity.this, ReconstructActivity.class);
                        // intent.putExtra("Image", bitmap);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                        intent.putExtra("Image", bs.toByteArray());

                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplication(), "Image doesn't uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIintent = new Intent();
                galleryIintent.setType("image/*");
                galleryIintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIintent, GALLERY_REQUEST);
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent();
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


        buttonDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DatabaseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setToolbarText() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String login = user.getEmail();
            Log.d("Login", login);
            toolbar_text.setText(login);
        } else {
            toolbar_text.setText("");
        }
    }


    private void setUserLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String login = user.getEmail();
            Log.d("Login", login);
        } else {
            buttonDatabase.setEnabled(false);
        }
    }

    private void getImageFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (imageId != null) {
            Log.d("imageId......", imageId);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("images").child(user.getUid());;

            myRef.child(imageId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        ImageDatabase image = snapshot.getValue(ImageDatabase.class);
                        Glide.with(MainActivity.this)
                                .asBitmap()
                                .load(Uri.parse(image.getImg()))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        bitmap = resource;
                                        imageView.setBackgroundColor(Color.TRANSPARENT);
                                        imageView.setImageBitmap(resource);
                                    }
                                });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check if the intent was to pick image, was successful and an image was picked
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {

            //Get selected image uri here
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageView.setBackgroundColor(Color.TRANSPARENT);
                imageView.setImageBitmap(bitmap);
//                uploadImageFile(getStringImage(bitmap));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Handle camera request
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            //We need a bitmap variable
            bitmap = (Bitmap) data.getExtras().get("data");

            bitmap = rotateImage(bitmap, 90);

            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setImageBitmap(bitmap);

            if (bitmap != null) {
                uploadImageFile();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    public String getStringImage() {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, BAOS);
        byte[] imageBytes = BAOS.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public boolean uploadImageFile() {
        String stringImage = getStringImage();

        String uniqueID = UUID.randomUUID().toString();

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        Log.d("uniqueID", uniqueID);

        try {
            String time = formatter.format(calendar.getTime());
            String resolution = text_resolution.getText().toString();
            int int_resolution = checkResolution(resolution.trim());
            if (int_resolution == 0) {
                return false;
            }

            jsonObject = new JSONObject();
            jsonObject.put("time", time);
            jsonObject.put("img", stringImage);
            jsonObject.put("name", uniqueID);
            jsonObject.put("timestamp", ts);
            jsonObject.put("resolution", int_resolution);
        } catch (JSONException e) {
            Log.e("JSON ERROR", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("POST OK", jsonObject.toString());
                        saveImageToLocalStorage(jsonObject);
                        Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("POST ERROR", volleyError.toString());
//                uploadImageFile(stringImage);
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
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonObjectRequest);

        return true;
    }

    private int checkResolution(String resolution) {
        double double_res = 0;
        if (resolution != "") {
            try {
                double_res = Double.parseDouble(resolution.trim());
                if ((double_res % 1) != 0) {
                    Toast.makeText(getApplication(), "Resolution index is not an integer value", Toast.LENGTH_SHORT).show();
                } else {
                    return (int) Math.floor(double_res);
                }
            } catch (NumberFormatException e) {
                double_res = 0; // your default value
                Toast.makeText(getApplication(), "Resolution index can't be null", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        Toast.makeText(getApplication(), "Resolution index can't be null", Toast.LENGTH_SHORT).show();
        return 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }

    private void saveImageToLocalStorage(JSONObject jsonObject) {
        String img = (String) jsonObject.opt("result_img");

        SharedPreferences.Editor editor = localPrefs.edit();
        editor.putString("Image", img);
        editor.commit();

        Log.d("Saving ok", img);

        getImageFromLocalStorage();
    }

    private void getImageFromLocalStorage() {
        String savedImg = localPrefs.getString("Image", "");

        Log.d("Getting ok", savedImg);
    }
}


