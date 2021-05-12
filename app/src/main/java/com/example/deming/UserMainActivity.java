package com.example.deming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;


// Activitate pentru pagina principala a utilizatorului - contine butonul care duce spre profil, sunt afisate (titlu, nr inregistrari) sondajele existente

public class UserMainActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);



        String[] userData = getIntent().getStringArrayExtra("userData"); // preluare date utilizator
        for(String i: userData)
            Log.d("tag", i);
        User user = new User(Integer.valueOf(userData[0]),userData[1],userData[2],userData[3],userData[4]); // creare obiect User
        List<Poll> sondaje = DBOperations.getPollsName(); // ia lista de sondaje, doar nume si numarul de inregistrari


        // buton pentru a accesa activitatea cu profilul utilizatorului
        Button goToProfile = (Button)findViewById(R.id.goToProfile);
        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this,   UserProfileActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });


        // TextView generate dinamic (din cod) pentru a afisa informatii despre sondaje
        // Nu stiu de la inceput cate sondaje sunt, prin urmare nu stiu cate TextView sa setez folosind XML, prin urmare le generez dinamic
        if(sondaje != null) {
            LinearLayout root = (LinearLayout) findViewById(R.id.rootlayout);
            List<TextView> sondajeViews = new LinkedList<TextView>();
            for (int i = 0; i < sondaje.size(); i++) {
                TextView a = new TextView(this);
                a.setText(sondaje.get(i).getTitle() + "\nContine " + sondaje.get(i).getRowsNumber() + " inregistrari");
                a.setTextAppearance(this, android.R.style.TextAppearance_Material_Body1);
                LinearLayout.LayoutParams views = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);
                views.setMargins(0, 1, 0, 0);
                a.setLayoutParams(views);
                a.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                a.setGravity(Gravity.CENTER);
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.parseColor("#ffd000"));
                gd.setCornerRadius(2);
                gd.setStroke(2, 0xFF000000);
                a.setBackground(gd);
                sondajeViews.add(a);
                sondajeViews.get(i).setClickable(true);
                root.addView(sondajeViews.get(i)); // adauga TextView la layout

            }


            // Click event pentru TextViews generate anterior
            // Cand se apasa pe un sondaj se deschide o activitate, acolo apar alte informatii despre sondaj (ex descriere)
            // in activitatea care se va deschide vor fi butoane care permit adaugarea de inregistrari, generarea de grafice etc
            for (TextView i : sondajeViews) {
                i.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("tag", "AI APASAT:" + i.getText().toString());
                        Intent intent = new Intent(UserMainActivity.this, ViewPollActivity.class);
                        intent.putExtra("userData", userData);
                        intent.putExtra("pollTitle", i.getText().toString().split("\\n")[0]);
                        startActivity(intent);
                    }
                });
            }

        }
    }


}