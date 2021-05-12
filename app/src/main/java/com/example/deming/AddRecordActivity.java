package com.example.deming;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;


import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

// Pentru a adauga o inregistrare intr-un anumit sondaj

public class AddRecordActivity extends AppCompatActivity {

    public String[] userDataGlobal;
    public String pollTitle;


    public static final String basicColor = "#85c5ff"; // culoarea normala pentru butoanele cu optiuni
    public static final String clickedColor = "#1954ff"; // culoare pentru butonul apasat


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        String[] userData = getIntent().getStringArrayExtra("userData"); // preluare date utilizator
        userDataGlobal = userData;
        for(String i: userData)
            Log.d("tag", i);
        User user = new User(Integer.valueOf(userData[0]),userData[1],userData[2],userData[3],userData[4]);

        Log.d("tag", "USER: " + user.toString());

        pollTitle = getIntent().getStringExtra("pollTitle");
        Log.d("tag", "POLL TITLE: " + pollTitle);


        Poll sondaj = DBOperations.getPoll(pollTitle); // preluare sondaj
        if(sondaj != null) {
            Log.d("tag", sondaj.toString());


            TextView pollTitleHeaderView = (TextView) findViewById(R.id.pollTitleHeaderView);
            pollTitleHeaderView.setText(sondaj.getTitle());

            TreeMap<Integer, TextView> questions = new TreeMap<>();
            TreeMap<Integer, List<Button>> options = new TreeMap<>();

            TreeMap<Integer, String> selectedOptions = new TreeMap<>(); // aici se va tine optiunea selectata pt fiecare intrebare

            LinearLayout layout = (LinearLayout) findViewById(R.id.rootlayout);


            // adaugare intrebari si variante de raspuns
            // TextView generate dinamic - nu pot fi puse din XML deoarece nu stiu cate intrebari si cate variante de raspuns vor fi
            // Unele intrebari pot avea de ex. 2 variante de raspuns, altele 7, prin urmare nu pot stii de la inceput cate TextView pun in fisierul XML
            // Astfel, generez dinamic (din cod) cate am nevoie
            for (int i = 0; i < sondaj.getDetails().size(); i++) {
                TextView a = new TextView(this);
                a.setTextAppearance(this, android.R.style.TextAppearance_Material_Body1);
                LinearLayout.LayoutParams questionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                questionLayoutParams.setMargins(0, 50, 0, 0);
                a.setLayoutParams(questionLayoutParams);
                a.setGravity(Gravity.CENTER);
                a.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                a.setText(sondaj.getDetails().get(i).get(1)); // elementul de pe pozitia 1 (corespunde intrebarii) din lista i
                questions.put(i + 1, a);
                layout.addView(questions.get(i + 1));

                List<Button> auxList = new LinkedList<>();
                options.put(i + 1, auxList);

                // Butoanele cu optiuni de raspuns
                for (int j = 2; j < sondaj.getDetails().get(i).size(); j++) {
                    Button btn = new Button(this);
                    btn.setText(sondaj.getDetails().get(i).get(j));
                    btn.setBackgroundColor(Color.parseColor(basicColor));
                    btn.setTextColor(Color.parseColor("#ffffff"));
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    btn.setAllCaps(false);
                    LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);
                    btnLayoutParams.setMargins(0, 10, 0, 0);
                    btn.setLayoutParams(btnLayoutParams);

                    layout.addView(btn);
                    List<Button> auxL = options.get(i + 1);
                    auxL.add(btn);
                    options.put(i + 1, auxL);
                }

            }


            // evenimente pentru butoane
            for (Integer i : options.keySet()) {
                for (Button j : options.get(i))
                    j.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (Button p : options.get(i))
                                p.setBackgroundColor(Color.parseColor(basicColor));
                            j.setBackgroundColor(Color.parseColor(clickedColor));
                            selectedOptions.put(i, j.getText().toString());
                            Log.d("tag", selectedOptions.toString());

                        }
                    });
            }


            // asta va aparea daca se vrea finalizarea inregistrarii si nu s-a ales optiune (raspuns) pentru toate intrebarile
            TextView recordStatus = new TextView(this);
            recordStatus.setTextAppearance(this, android.R.style.TextAppearance_Material_Body1);
            recordStatus.setText("\n");
            LinearLayout.LayoutParams statusLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            statusLayoutParams.setMargins(0, 30, 0, 20);
            recordStatus.setLayoutParams(statusLayoutParams);
            recordStatus.setTextColor(Color.parseColor("#ff0303")); // red
            recordStatus.setGravity(Gravity.CENTER);
            recordStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            layout.addView(recordStatus);


            // butonul de submit
            Button submitRecord = new Button(this);
            submitRecord.setText("Adauga inregistrare");
            submitRecord.setBackgroundColor(Color.parseColor("#ff8812"));
            submitRecord.setTextColor(Color.parseColor("#ffffff"));
            submitRecord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);


            double windowWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams((int) (windowWidth * 0.7), LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(0, 20, 0, 30);
            submitRecord.setLayoutParams(buttonLayoutParams);
            layout.addView(submitRecord);


            submitRecord.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {

                    if (selectedOptions.size() != sondaj.getDetails().size()) {
                        recordStatus.setText("Unele intrebari nu au primit inca raspuns!");

                    } else {
                        // Adauga inregistrarea

                        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(new Date());
                        List<String> record = new LinkedList<>(); // va contine datele noii inregistrari

                        for (Integer i : selectedOptions.keySet())
                            record.add(selectedOptions.get(i)); // optiunile selectate

                        record.add(String.valueOf(date)); // data la care a fost trimisa
                        record.add(String.valueOf(user.getId())); // ID -ul utilizatorului


                        Log.d("tag", record.toString());

                        if (DBOperations.addCustomRecord(pollTitle, record)) { // daca se adauga cu succes

                            recordStatus.setTextColor(Color.parseColor("#ffffff")); // white

                            Context context = getApplicationContext();
                            CharSequence text = "Inregistrarea a fost adaugata!";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            Intent intent = new Intent(AddRecordActivity.this, UserMainActivity.class);
                            intent.putExtra("userData", userDataGlobal);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else { // daca nu se adauga cu succes
                            recordStatus.setText("Inregistrarea nu a fost adaugata!");
                        }
                    }
                }
            });

        }
        else{
            Context context = getApplicationContext();
            CharSequence text = "S-a pierdut conexiunea cu baza de date";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Butonul back sa te duca la ViewPollActivity, sa trimita user data si pollTitle
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ViewPollActivity.class);
        myIntent.putExtra("userData", userDataGlobal);
        myIntent.putExtra("pollTitle", pollTitle);
        startActivityForResult(myIntent, 0);
        return true;
    }
}