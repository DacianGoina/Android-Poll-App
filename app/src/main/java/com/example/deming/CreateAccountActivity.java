package com.example.deming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Activitate pentru crearea unui cont

public class CreateAccountActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // campuri de completat
        TextView inputStatus = (TextView) findViewById(R.id.inputStatus);
        EditText usernameInput = (EditText) findViewById(R.id.userNameInput);
        EditText emailInput = (EditText)findViewById(R.id.emailInput);
        EditText passwordInput = (EditText)findViewById(R.id.passwordInput);
        EditText repeatPasswordInput = (EditText)findViewById(R.id.repeatPasswordInput);

        this.resetFieldsValues(inputStatus, usernameInput, emailInput, passwordInput, repeatPasswordInput); // reseteaza valorile campurilor

        // trecere la inregistrare reusita
        Button createAccount = (Button)findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String repeatPassword = repeatPasswordInput.getText().toString();


                // valideaza datele, creaza contul si trece la urmatoarea activitate
                // daca metoda va returna fals atunci se va modifica valoarea lui inputStatus
                if(validateData(username, email, password, repeatPassword,inputStatus)){
                    if(DBOperations.insertUser(username,email,password,"ANGAJAT") == true) {
                        Intent intent = new Intent(CreateAccountActivity.this,   SuccessRegistrationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else{
                        Log.d("tag", "Inserare esuata");
                        inputStatus.setText("Problema de la baza de date!"); // afisare mesaj corespunzator
                    }

                }

            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    public void resetFieldsValues(TextView inputStatus, EditText usernameInput, EditText emailInput, EditText passwordInput, EditText repeatPasswordInput) {
        inputStatus.setText("");
        usernameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        repeatPasswordInput.setText("");
    }

    // verifica cu un regex daca adresa de email este valida
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public boolean validateEmail(String email){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    // verifica ca parola sa contina cel putin o litera mare si cel putin o litera mica
    public static boolean validatePassword(String text) {

        boolean lower = false;
        boolean upper = false;


        for(int i=0;i<text.length() && !(lower && upper);i++) {
            if (Character.isUpperCase(text.charAt(i)))
                upper = true;
            if (Character.isLowerCase(text.charAt(i)))
                lower = true;
        }

            if(upper && lower)
                return true;
            return false;

    }

    // Valideaza toate datele introduse
    public boolean validateData(String username, String email, String password, String repeatPassword, TextView inputStatus){
        if(username.length() < 3){
            inputStatus.setText("Numele de utilizator este prea scurt");
            return false;
        }
        if(email.length() < 5){
            inputStatus.setText("Adresa de email este prea scurta");
            return false;
        }
        if(!validateEmail(email)){
            inputStatus.setText("Adresa de email este invalida");
            return false;
        }
        else if(password.length() < 3){
            inputStatus.setText("Parola este prea scurta");
            return false;
        }
        else if(validatePassword(password) == false){
            inputStatus.setText("Parola trebuie sa contina litere mici si mari");
            return false;
        }
        else if(!password.equals(repeatPassword)){
            inputStatus.setText("Va rugam repetati corect parola");
            return false;
        }
        else if(DBOperations.checkUsernameUnique(username) == false){
            inputStatus.setText("Numele de utilizator este deja folosit");
            return false;
        }
        else if(DBOperations.checkEmailUnique(email) == false){
            inputStatus.setText("Adresa de email este deja folosita");
            return false;
        }

        return true;
    }

    // Butonul de back de sus sa trimita inapoi la MainActivity
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}