package com.example.photoresolution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.photoresolution.model.ImageDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class ReconstructActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ObjectAnimator progressAnimator;

    private static final String URL = "http://192.168.1.69:1000/status";
    private RequestQueue mRequestQueue;
    int progress = 0;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    private Toolbar toolbar;
    private TextView toolbar_text;

    private ImageView imageView;
    String imageId = "";

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconstruct);

        bitmap = getIntent().getParcelableExtra("Image");

        toolbar = findViewById(R.id.myToolbar);
        toolbar_text = findViewById(R.id.text_user_login_toolbar);
        setToolbarText();

        imageView = (ImageView) findViewById(R.id.reconstruct_image);

        initProgressBar();
        startTimer();
        setImage();
    }

    private void setImage() {
        imageView.setBackgroundColor(Color.TRANSPARENT);
        if (getIntent().hasExtra("Image")) {
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("Image"), 0, getIntent()
                            .getByteArrayExtra("Image").length);
            imageView.setImageBitmap(b);
        }
    }

    private void initProgressBar() {
        progress = 0;
        progressBar = findViewById(R.id.progressBar_reconstructing);
        progressBar.setProgress(progress);
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


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer.schedule(timerTask, 0, 10000);
    }

    Runnable my_runnable = new Runnable() {
        @Override
        public void run() {
//            Toast.makeText(ReconstructActivity.this, "GET!", Toast.LENGTH_SHORT).show();
            volleyGet();
        }
    };

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(my_runnable);
            }
        };
    }

    // to stop the handler
    public void stop() {
        handler.removeCallbacks(my_runnable);
        timer.cancel();
        timer.purge();
    }


    public void volleyGet() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        progress = Integer.parseInt(response.trim());
                        if (progress == 100) {
                            stop();
                            Intent intent = new Intent(ReconstructActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                        progressBar.setProgress(progress);
                        Toast.makeText(ReconstructActivity.this, "Reconstructing: " + progress + "%", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(ReconstructActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                ;
            }
        });
        mRequestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(200, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
        Log.d("STOPPING", "Stop");

    }
}