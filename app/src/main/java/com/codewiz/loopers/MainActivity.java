package com.codewiz.loopers;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.codewiz.loopers.auto.AutoLooper;
import com.codewiz.loopers.auto.AutoLooperRunnable;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AutoLooper.getInstance("dd", getLifecycle())
                .setTime(1000, TimeUnit.MILLISECONDS)
                .post(new AutoLooperRunnable() {
                    @Override
                    public void run() {
                        System.out.println("Looping ");

                    }
                });
    }
}