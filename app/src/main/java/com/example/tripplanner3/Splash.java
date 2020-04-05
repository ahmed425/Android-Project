package com.example.tripplanner3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(Splash.this, RegisterActivity.class));
                finish();

            }
        }, 3000);
    }
}





//    long loadTime = 4000;
//    SharedPreferences saving;
//    public static final String saveData = "NewData";
//    public static final String newLogin = "NewLogin";
//    String user;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////        this.setContentView(R.layout.activity_splash);
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                saving = getSharedPreferences(saveData, 0);
//                boolean firstTime = saving.getBoolean(newLogin, false);
//                user = saving.getString("user", "null");
//                Log.i("user", user);
////                if (!firstTime) {
////                    Intent login = new Intent(Splash.this, Login.class);
////                    startActivity(login);
////                    finish();
////                } else {
//                    Intent Home = new Intent(Splash.this, Login.class);
//                    startActivity(Home);
//                    finish();
////                }
////                finish();
//
//            }
//        }, 3000);
//    }



//

