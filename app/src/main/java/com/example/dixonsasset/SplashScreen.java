package com.example.dixonsasset;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.dixonsasset.Auth.Login;
import com.example.dixonsasset.Kru.Home;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(() -> {
            try {
                SharedPreferences mSettings = getSharedPreferences("akun", Context.MODE_PRIVATE);
                String role = mSettings.getString("username",null);
                if (!role.equals(null)){
                    startActivity(new Intent(this, Home.class));
                    finish();
                }else {
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }catch (Exception ignore){
                Intent intent = new Intent(SplashScreen.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 500);
    }
}