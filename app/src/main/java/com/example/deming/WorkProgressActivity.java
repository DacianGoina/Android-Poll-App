package com.example.deming;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.res.Resources;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;



import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

// Activitate pentru a calcula pontajul angajatilor

public class WorkProgressActivity extends AppCompatActivity {

    public String[] userDataGlobal;

    public static final int YEAR = 2021;
    public static final List<String> MONTHS = new LinkedList<String>(Arrays.asList("Ianuarie " + YEAR,
            "Februarie "+ YEAR,
            "Martie "+ YEAR,
            "Aprilie "+ YEAR,
            "Mai "+ YEAR,
            "Iunie "+ YEAR,
            "Iulie "+ YEAR,
            "August "+ YEAR,
            "Septembrie "+ YEAR,
            "Octombrie "+ YEAR,
            "Noiembrie "+ YEAR,
            "Decembrie "+ YEAR));

    public static String monthValue = ""; // valoarea selectata pentru luna
    public static String empNameValue = ""; // valoarea selectata pentru nume angajat
    TreeMap<String,Integer> empInfo = new TreeMap<>(); // pentru a tine informatii (nume, id ) despre angajati

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_progress);


        String[] userData = getIntent().getStringArrayExtra("userData");
        userDataGlobal = userData;

        TextView getInfoStatus = (TextView)findViewById(R.id.getInfoStatus);
        getInfoStatus.setVisibility(View.GONE); // momentan nu apare

        // Selecteaza luna - anul
        TextView getMonthLabel = (TextView)findViewById(R.id.getMonthLabel);
        Spinner getMonth = (Spinner)findViewById(R.id.getMonth);


        // Selecteaza nume angajat
        TextView getEmployeeNameLabel = (TextView)findViewById(R.id.getEmployeeNameLabel);
        Spinner getEmployeeName = (Spinner)findViewById(R.id.getEmployeeName);


        // Setare optiuni de selectat pentru luna - an (monthSpinner)
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, MONTHS);

        getMonth.setAdapter(monthAdapter);


        // Setare optiuni de selectat pentru nume angajat

        empInfo  = DBOperations.getEmployeeInfo();

        if(empInfo != null) {
            // Adauga toate numele angajatilor intr-o lista
            List<String> empNameList = new LinkedList<>();
            for (String i : empInfo.keySet())
                empNameList.add(i);

            // pune valorile pentru selectat la spinner
            ArrayAdapter<String> empNameAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, empNameList);
            getEmployeeName.setAdapter(empNameAdapter);


            // Event pentru cand se selecteaza optiune pentru luna, an
            getMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = adapterView.getItemAtPosition(i).toString(); // valoarea selectata
                    monthValue = selectedItem; // actualizeaza luna selectata
                    Log.d("LUNA - AN SELECTAT", selectedItem);
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });


            // Event pentru cand se selecteaza optiune pentru nume angajat
            getEmployeeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = adapterView.getItemAtPosition(i).toString();
                    empNameValue = selectedItem; // actualizeaza numele selectat
                    Log.d("NUME ANGAJAT SELECTAT", selectedItem);
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });


            TextView totalSum = (TextView)findViewById(R.id.totalSum); // pentru a afisa pontajul total a unui angajat
            TextView sumPerTable = (TextView)findViewById(R.id.sumPerTable); // pentru a afisa cat a colectat pt fiecare sondaj
            totalSum.setText("");
            sumPerTable.setText("");


            Button computeWorkProgress = (Button) findViewById(R.id.computeWorkProgress);
            double windowWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams((int) (windowWidth * 0.5), LinearLayout.LayoutParams.WRAP_CONTENT);
            btnLayout.setMargins(0, 40, 0, 0);
            computeWorkProgress.setLayoutParams(btnLayout);
            computeWorkProgress.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    List<String> getRes = new LinkedList<>();
                    List<Integer> date = extractMonthYear(monthValue);
                    Integer id  = empInfo.get(empNameValue); // ia ID -ul angajatului corespunzator, din Map NUME => ID
                    getRes = DBOperations.computeEmployeeWork(date, id, empNameValue,monthValue);
                    if(getRes != null) { // verifica daca am primit ceva diferit de null
                        totalSum.setText(getRes.get(0));
                        sumPerTable.setText(getRes.get(1));
                    }
                    else{
                        getInfoStatus.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

       else{ // in cazul in care empInfo este null
            Button computeWorkProgress = (Button) findViewById(R.id.computeWorkProgress);
            computeWorkProgress.setVisibility(View.GONE); // ascunde si butonul pentru calcul pontaj
            getInfoStatus.setVisibility(View.VISIBLE); // afiseaza mesajul corespunzator
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
        myIntent.putExtra("userData", userDataGlobal);
        startActivityForResult(myIntent, 0);
        return true;
    }


    // Primeste un String de forma "Luna An" (ex: "Ianuarie 2021") si extrage luna si anul din el
    // Converteste luna in "forma" numerica, ex: "Ianuarie" => 1
    // Returneaza o lista care contine 2 valorile Integer: prima este luna in format numeric, a doua este anul
    // ex: "Mai 2021" => [5, 2021]
    public List<Integer> extractMonthYear(String date){
        List<Integer> l = new LinkedList<>();
        String[] rez = date.split("\\s+");
        String stringLuna = rez[0];
        Integer intLuna = 0;
        if(stringLuna.equals("Ianuarie"))
            intLuna = 1;
        else if(stringLuna.equals("Februarie"))
            intLuna = 2;
        else if(stringLuna.equals("Martie"))
            intLuna = 3;
        else if(stringLuna.equals("Aprilie"))
            intLuna = 4;
        else if(stringLuna.equals("Mai"))
            intLuna = 5;
        else if(stringLuna.equals("Iunie"))
            intLuna = 6;
        else if(stringLuna.equals("Iulie"))
            intLuna = 7;
        else if(stringLuna.equals("August"))
            intLuna = 8;
        else if(stringLuna.equals("Septembrie"))
            intLuna = 9;
        else if(stringLuna.equals("Octombrie"))
            intLuna = 10;
        else if(stringLuna.equals("Noiembrie"))
            intLuna = 11;
        else if(stringLuna.equals("Decembrie"))
            intLuna = 12;
        l.add(intLuna); // adauga luna in lista
        l.add(Integer.valueOf(rez[1])); // adauga anul in lista
        return l;
    }

}