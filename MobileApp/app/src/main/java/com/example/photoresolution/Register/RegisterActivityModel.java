package com.example.photoresolution.Register;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivityModel implements RegisterActivityContract.Model {
    private FirebaseAuth fAuth;
    private RegisterActivityContract.onRegisterListener mOnRegisterListener;

    public RegisterActivityModel(RegisterActivityContract.onRegisterListener mOnRegisterListener) {
        this.mOnRegisterListener = mOnRegisterListener;
    }

    @Override
    public void AutheticateUser(Activity activity, String username, String password) {
        fAuth = FirebaseAuth.getInstance();

        System.out.println(username + " " + password);

        fAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mOnRegisterListener.onSuccess(task.getResult().toString());
                        } else {
                            mOnRegisterListener.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

}
