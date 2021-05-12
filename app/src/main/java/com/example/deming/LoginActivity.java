package com.example.deming;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// Activitate pentru autentificare

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView loginInputStatus = (TextView)findViewById(R.id.loginInputStatus);
        EditText loginEmailInput = (EditText)findViewById(R.id.loginEmailInput);
        EditText loginPasswordInput = (EditText)findViewById(R.id.loginPasswordInput);

        this.resetFieldsValues(loginInputStatus, loginEmailInput, loginPasswordInput);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = loginEmailInput.getText().toString();
                String password = loginPasswordInput.getText().toString();

                if(email.length() < 1 || password.length() < 1 ){
                    loginInputStatus.setText("Te rugam sa completezi cele 2 campuri");
                }
                else{
                    loginInputStatus.setText("");
                    if(DBOperations.getUser(email, password) == null){ // valideaza existenta contului in baza de date
                        loginInputStatus.setText("Datele introduse nu corespund unui cont");
                    }
                    else{
                        User a = DBOperations.getUser(email, password);
                        Intent intent = new Intent(LoginActivity.this,   UserMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // goleste stiva de activitati

                        String[] userData = {String.valueOf(a.getId()), a.getUsername(),a.getEmail(),a.getPassword(),a.getAccountType()};
                        intent.putExtra("userData", userData); // trimite datele utilizatorului spre urmatoarea activitate
                        startActivity(intent);

                    }
                }



            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void resetFieldsValues(TextView loginInputStatus, EditText loginEmailInput, EditText loginPasswordInput) {
        loginInputStatus.setText("");
        loginEmailInput.setText("");
        loginPasswordInput.setText("");
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}