package com.example.tripplanner3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    final String TAG = "Info_log";

    // UI Element decleration
    Button login_btn;
    EditText email_textfield;
    EditText password_textfield;
    TextView auth;

    // End <-- UI Element decleration -->


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (mAuth.getCurrentUser() != null) {
//            // User is logged in
//            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//            finish();
//        }
        setContentView(R.layout.activity_login);
        // getting firebase instance

        mAuth = FirebaseAuth.getInstance();


        // End <-- getting firebase instance -->


        // linking ui to view by code

        login_btn = findViewById(R.id.login_btn);
        email_textfield = findViewById(R.id.email_txt);
        password_textfield = findViewById(R.id.password_txt);


        //// END <-- linking ui to view by code -->


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email_textfield.getText().toString(), password_textfield.getText().toString());
                Intent goToHome = new Intent(getApplicationContext(), HomeActivity.class);
                goToHome.putExtra("EMAIL",email_textfield.getText().toString());
                startActivity(goToHome);
            }
        });


    }
    private void updateUI(FirebaseUser user) {
//        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
//
//        startActivity(intent);
        Intent goToHome = new Intent(getApplicationContext(), HomeActivity.class);
        goToHome.putExtra("EMAIL",user.getEmail());
        startActivity(goToHome);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }



        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
//                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
////                            intent.putExtra("Email",user.getEmail());
//                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.\n chech your Credentials",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
//                            Toast.makeText(LoginActivity.this, "Check your Email/password.",
//                                    Toast.LENGTH_SHORT).show();
                        }
//                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }


    private boolean validateForm() {
        boolean valid = true;

        String email_txt = email_textfield.getText().toString();
        if (TextUtils.isEmpty(email_txt)) {
            email_textfield.setError("Required.");
            valid = false;
        } else {
            email_textfield.setError(null);
        }

        String password_txt = password_textfield.getText().toString();
        if (TextUtils.isEmpty(password_txt)) {
            password_textfield.setError("Required.");
            valid = false;
        } else {
            password_textfield.setError(null);
        }

        return valid;
    }

}
