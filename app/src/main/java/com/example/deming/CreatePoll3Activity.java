package com.example.deming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// Activitate pentru create sondaj, partea 3 - confirmarea

public class CreatePoll3Activity extends AppCompatActivity {

    String[] userDataGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll3);

        String[] userData = getIntent().getStringArrayExtra("userData");
        userDataGlobal = userData;

        Button backToUserMainActivity = (Button)findViewById(R.id.backToUserMainActivity);
        backToUserMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePoll3Activity.this,   UserMainActivity.class);
                intent.putExtra("userData", userDataGlobal);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}