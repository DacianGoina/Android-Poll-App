package com.example.deming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

 // Activitate pentru profilul utilizatorului

public class UserProfileActivity extends AppCompatActivity {

    public String[] userDataGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String[] userData = getIntent().getStringArrayExtra("userData");
        userDataGlobal = userData;

        User user = new User(Integer.valueOf(userData[0]),userData[1],userData[2],userData[3],userData[4]);

        // text pentru a afisa datele utilizatorului
        TextView userProfileLabel = (TextView)findViewById(R.id.userProfileLabel);
        TextView userProfileUsername = (TextView)findViewById(R.id.userProfileUsername);
        TextView userProfileEmail = (TextView)findViewById(R.id.userProfileEmail);
        TextView userProfileAccountType = (TextView)findViewById(R.id.userProfileAccountType);
        TextView userProfileId = (TextView)findViewById(R.id.userProfileId);
        TextView actionsLabel = (TextView)findViewById(R.id.actionsLabel);

        Button logoutButton = (Button)findViewById(R.id.logoutButton);
        Button createPoll = (Button)findViewById(R.id.createPoll);
        Button countEmployeeWork = (Button)findViewById(R.id.countEmployeeWork);

        this.setTextValues(userProfileUsername, userProfileEmail, userProfileAccountType,userProfileId, createPoll, countEmployeeWork);

        // buton logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this,   MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // buton creare sondaj
        createPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this,   CreatePollActivity.class);
                intent.putExtra("userData", userDataGlobal);
                startActivity(intent);
            }
        });

        // buton calculeaza pontaj angajati
        countEmployeeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this,   WorkProgressActivity.class);
                intent.putExtra("userData", userDataGlobal);
                startActivity(intent);
            }
        });




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // afiseaza Creare sondaj si Calculare pontaj angajati doar daca utilizatorul are accounttype MANAGER
    // un ANGAJAT nu trebuie sa vada aceste 2 butoane
    public void setTextValues(TextView userProfileUsername, TextView userProfileEmail, TextView userProfileAccountType, TextView userProfileId, Button createPoll, Button countEmployeeWork) {
        if(userDataGlobal[4].equals("MANAGER")){
            createPoll.setVisibility(View.VISIBLE);
            countEmployeeWork.setVisibility(View.VISIBLE);
        }
        else{
            createPoll.setVisibility(View.GONE);
            countEmployeeWork.setVisibility(View.GONE);
        }
        userProfileUsername.setText("Nume utilizator: " + userDataGlobal[1]);
        userProfileEmail.setText("Email: " + userDataGlobal[2]);
        userProfileAccountType.setText("Tip cont: " + userDataGlobal[4]);
        userProfileId.setText("ID utilizator: " + userDataGlobal[0]);
    }


    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), UserMainActivity.class);
        myIntent.putExtra("userData", userDataGlobal);
        startActivityForResult(myIntent, 0);
        return true;
    }
}