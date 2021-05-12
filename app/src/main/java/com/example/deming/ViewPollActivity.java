package com.example.deming;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

// Activitatea unde se vor afisa informatiile despre un anumit sondaj
// MANAGER va avea posibilitatea sa creeze un grafic cu rezultatele si sa faca export la date sub forma unui fisier CSV
// ANGAJAT va avea posibiltatea sa adauge inregistrari

public class ViewPollActivity extends AppCompatActivity {

    public String[] userDataGlobal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_poll);



        String[] userData = getIntent().getStringArrayExtra("userData"); // accesare date utilizator
        userDataGlobal = userData;
        for(String i: userData)
            Log.d("tag", i);
        User user = new User(Integer.valueOf(userData[0]),userData[1],userData[2],userData[3],userData[4]);

        Log.d("tag", "USER: " + user.toString());

        String pollTitle = getIntent().getStringExtra("pollTitle");
        Log.d("tag", "POLL TITLE: " + pollTitle);


        Poll sondaj = DBOperations.getPoll(pollTitle);
        if(sondaj != null) {
            //Log.d("tag", sondaj.toString());

            Button viewPollActionBtn = (Button) findViewById(R.id.viewPollActionBtn);
            TextView pollTitleView = (TextView) findViewById(R.id.pollTitleView);
            TextView pollDescriptionView = (TextView) findViewById(R.id.pollDescriptionView);
            TextView noOfQuestionsView = (TextView) findViewById(R.id.noOfQuestionsView);
            TextView noOfOptionsView = (TextView) findViewById(R.id.noOfOptionsView);
            TextView noOfRowsView = (TextView) findViewById(R.id.noOfRowsView);

            // butonul pt export ca csv este vazut doar de MANAGER
            Button exportAsCSV = (Button) findViewById(R.id.exportAsCSV);
            if (user.getAccountType().equals("ANGAJAT"))
                exportAsCSV.setVisibility(View.GONE);

            // text pe butoane in functie de accounttype
            if (user.getAccountType().equals("MANAGER"))
                viewPollActionBtn.setText("Generare grafic");
            else
                viewPollActionBtn.setText("Adauga inregistrare");

            viewPollActionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.getAccountType().equals("MANAGER")) {
                        // daca este MANAGER atunci genereaza grafic
                        if (sondaj.getRowsNumber() == 0) { // trebuie minim o inregistrare pentru a genera grafice
                            Context context = getApplicationContext();
                            CharSequence text = "Sondajul nu contine inca inregistrari";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            Intent intent = new Intent(ViewPollActivity.this, ComputeResultsActivity.class);
                            intent.putExtra("userData", userDataGlobal);
                            intent.putExtra("pollTitle", pollTitle);
                            startActivity(intent);
                        }

                    } else {
                        // daca este ANGAJAT atunci adauga inregistrare
                        Intent intent = new Intent(ViewPollActivity.this, AddRecordActivity.class);
                        intent.putExtra("userData", userDataGlobal);
                        intent.putExtra("pollTitle", pollTitle);
                        startActivity(intent);
                    }
                }
            });


            // Pentru a exporta ca CSV - doar conturile MANAGER pot vedea si folosi acest buton
            exportAsCSV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sondaj.getRowsNumber() == 0) { // daca nu avem inregistrari nu avem ce exporta
                        Context context = getApplicationContext();
                        CharSequence text = "Sondajul nu contine inca inregistrari";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "";
                        int duration = Toast.LENGTH_SHORT;
                        int state = exportAsCSVFile(sondaj.getTitle(), sondaj.getDetails());
                        if (state == 1)
                            text = "Date exportate cu succes!";
                        else if (state == 0)
                            text = "Fisierul a fost deja exportat";
                        else
                            text = "A aparut o problema la exportarea datelor";

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            });

            pollTitleView.setText("TITLU: " + sondaj.getTitle());
            pollDescriptionView.setText("DESCRIERE: " + sondaj.getDescription());
            noOfQuestionsView.setText("NUMAR DE INTREBARI: " + sondaj.getDetails().size());
            noOfOptionsView.setText("NUMAR TOTAL OPTIUNI DE RASPUNS: " + sondaj.ComputeNoOfOptions());
            noOfRowsView.setText("Contine " + sondaj.getRowsNumber() + " inregistrari");
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

    // Exporteaza datele intr-un fisier CSV
    // Primeste ca parametru numele sondajului precum si detaliile acestuia
    // Returneaza 1 daca fisierul a fost creat, scris si exportat cu succes
    // Returneaza -1 daca fisierul au aparut probleme la creare / scrierea / exportarea fisierului
    public int exportAsCSVFile(String pollTitle, List<List<String>> pollDetails){
        String fileName = CreatePoll2Activity.titleToTableName(pollTitle);
        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + ".csv";
        System.out.println("PATH: " + path);
        File newFile = new File(path);

        try (BufferedWriter buffer = new BufferedWriter(new FileWriter(newFile))) {
            // Inregistrarile pentru sondaj
            List<List<String>> records = DBOperations.getPollCompleteRecords(pollTitle);
            if(records == null)
                return -1;
            int n = pollDetails.size(); // lungimea acestei liste = numarul de intrebari = numarul de elemente de pe o linie a unui record

            // Adaugare date in fisierul csv

            // Capetele de tabel
            List<String> header = new LinkedList<>();
            header.add("ID");
            for(int i=0;i<pollDetails.size();i++)
                header.add(pollDetails.get(i).get(1)); // in fiecare lista, pe pozitia 1 se afla corpul unei intrebari
            header.add("DATA");
            header.add("ID_ANGAJAT");
            buffer.write(listToCSVLine(header)); // scrie capetele de tabel
            

            // Scrie datele (inregistrarile)
            for(int i=0;i<records.get(0).size();i++) { // toate listele din records au aceeasi lungime
                List<String> aux = new LinkedList<>();
                for(int j=0;j<records.size();j++)
                    aux.add(records.get(j).get(i));
                buffer.write(listToCSVLine(aux));
            }

            buffer.close();
            System.out.println("Scriere cu succes!");
            return 1;
        } catch (IOException e) {
            // in caz ca ceva nu functioneaza bine cu scrierea atunci sterge fisierul nou creat
            newFile.delete();
            e.printStackTrace();
            return -1;
        }

    }

    // Primeste o lista de String si returneaza un String care reprezinta o linie de fisier CSV formata cu elementele din lista primita
    // Elementele vor fi separate cu virgula(,), la final de linie se pune "\n"
    // Elementele vor avea extra ghilimele
    //Ex: l = [azi, este, vineri] => "azi","este","vineri"\n
    public String listToCSVLine(List<String> l){
        StringBuilder rez = new StringBuilder();
        for(int i = 0;i<l.size() - 1;i++) // nu se pune virgula dupa ultimul element
            rez.append("\"" + l.get(i) + "\"" + ",");
        rez.append("\"" + l.get(l.size() - 1) + "\"" + "\n"); // new line dupa ultimul element
        return rez.toString();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), UserMainActivity.class);
        myIntent.putExtra("userData", userDataGlobal);
        startActivityForResult(myIntent, 0);
        return true;
    }
}