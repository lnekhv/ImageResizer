package com.example.photoresolution.Login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.photoresolution.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginActivityModel implements LoginActivityContract.Model{
    private FirebaseAuth fAuth;
    private LoginActivityContract.onLoginListener mOnLoginListener;

    public LoginActivityModel(LoginActivityContract.onLoginListener onLoginListener){
        this.mOnLoginListener = onLoginListener;
    }

    public void AutheticateUser(Activity activity, String username, String password) {
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        System.out.println(username + " " + password);
        fAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = fAuth.getCurrentUser();
////                            Toast.makeText(LoginActivity.this, "User: " + user, Toast.LENGTH_SHORT).show();
//                            System.out.println("CurrentUser: " + user.getEmail());
//
//                            Intent intent = new Intent(activity, MainActivity.class);
//                            startActivity(intent);
                            mOnLoginListener.onSuccess(task.getResult().toString());
                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(LoginActivity.this, "Authentication failed.." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mOnLoginListener.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }
}
