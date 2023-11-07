package com.example.appsupermercado;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Create Handler and Delay
        Handler handler = new Handler();
        int delay = 3000;

        //Create Runnable that calls method
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(newIntent());
            }
        };

        //Invoke handler and execute delay
        handler.postDelayed(runnable, delay);
    }

    private Intent newIntent(){
        this.finish();
        return new Intent(this, MainActivity2.class);
    }
}