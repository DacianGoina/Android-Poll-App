package com.example.deming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// Activitate pentru creare sondaj - selectare numarul de intrebari si numarul de optiuni de raspuns pt fiecare intrebare

public class CreatePollActivity extends AppCompatActivity {

    public String[] userDataGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        String[] userData = getIntent().getStringArrayExtra("userData");
        userDataGlobal = userData;

        // elemente principale
        TextView nrOfQuestionsLabel = (TextView)findViewById(R.id.nrOfQuestionsLabel);
        Spinner nrOfQuestionsSpinner = (Spinner)findViewById(R.id.nrOfQuestionsSpiner);
        Button createPollGoToStep2 = (Button)findViewById(R.id.createPollGoToStep2);


        //labels pentru fiecare intrebare
        TextView question1Label = (TextView)findViewById(R.id.question1Label);
        TextView question2Label = (TextView)findViewById(R.id.question2Label);
        TextView question3Label = (TextView)findViewById(R.id.question3Label);
        TextView question4Label = (TextView)findViewById(R.id.question4Label);
        TextView question5Label = (TextView)findViewById(R.id.question5Label);
        TextView question6Label = (TextView)findViewById(R.id.question6Label);
        TextView question7Label = (TextView)findViewById(R.id.question7Label);

        // spinner pentru fiecare intrebare
        Spinner question1Spinner = (Spinner) findViewById(R.id.question1Spinner);
        Spinner question2Spinner = (Spinner) findViewById(R.id.question2Spinner);
        Spinner question3Spinner = (Spinner) findViewById(R.id.question3Spinner);
        Spinner question4Spinner = (Spinner) findViewById(R.id.question4Spinner);
        Spinner question5Spinner = (Spinner) findViewById(R.id.question5Spinner);
        Spinner question6Spinner = (Spinner) findViewById(R.id.question6Spinner);
        Spinner question7Spinner = (Spinner) findViewById(R.id.question7Spinner);



        String[] arraySpinner = new String[] {
                 "2", "3", "4", "5", "6", "7"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);

        //Asignare meniu pentru spinners
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nrOfQuestionsSpinner.setAdapter(adapter);
        question1Spinner.setAdapter(adapter);
        question2Spinner.setAdapter(adapter);
        question3Spinner.setAdapter(adapter);
        question4Spinner.setAdapter(adapter);
        question5Spinner.setAdapter(adapter);
        question6Spinner.setAdapter(adapter);
        question7Spinner.setAdapter(adapter);

        // lista pentru a tine TextViews de la intrebari
        List<TextView> viewsList = new LinkedList<>();
        viewsList.add(question1Label);
        viewsList.add(question2Label);
        viewsList.add(question3Label);
        viewsList.add(question4Label);
        viewsList.add(question5Label);
        viewsList.add(question6Label);
        viewsList.add(question7Label);

        // lista pentru a tine Spinners de la intrebari
        List<Spinner> spinnersList = new LinkedList<>();
        spinnersList.add(question1Spinner);
        spinnersList.add(question2Spinner);
        spinnersList.add(question3Spinner);
        spinnersList.add(question4Spinner);
        spinnersList.add(question5Spinner);
        spinnersList.add(question6Spinner);
        spinnersList.add(question7Spinner);


        nrOfQuestionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString(); //this is your selected item
                int n = Integer.valueOf(selectedItem);
                questionsControl(viewsList, spinnersList, n);
                Log.d("tag", selectedItem);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        createPollGoToStep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // pentru a stoca numarul de intrebari si numarul de optiuni pentru fiecare intrebare
                ArrayList<String> pollConfiguration = new ArrayList<>();
                String numberOfQuestions = nrOfQuestionsSpinner.getSelectedItem().toString();
                pollConfiguration.add(numberOfQuestions);

                int n = Integer.valueOf(nrOfQuestionsSpinner.getSelectedItem().toString()); // cate intrebari vor fi
                for(int i=0;i<n;i++){
                    pollConfiguration.add(spinnersList.get(i).getSelectedItem().toString());
                }


                Intent intent = new Intent(CreatePollActivity.this,   CreatePoll2Activity.class);
                intent.putExtra("userData", userDataGlobal);
                intent.putExtra("pollConfiguration", pollConfiguration);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    // ascunde unele spinners in functie de numarul selectat in primul spinner (daca se doresc doar 2 intrebari, afiseaza doar inca 2 spinners
    // deoarece trebuie selectate numarul de optiuni de raspuns DOAR pentru 2 intrebari)
    public void questionsControl(List<TextView> vL, List<Spinner> sL, int n){
        for(int i=0;i<=n-1;i++){
            vL.get(i).setVisibility(View.VISIBLE);
            sL.get(i).setVisibility(View.VISIBLE);
        }
        for(int i = n ;i < 7;i++){
            vL.get(i).setVisibility(View.GONE);
            sL.get(i).setVisibility(View.GONE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
        myIntent.putExtra("userData", userDataGlobal);
        startActivityForResult(myIntent, 0);
        return true;
    }
}