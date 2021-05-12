package com.example.deming;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

// Activitate pentru a crea un sondaj (poll) - pagina a doua (scriere intrebari, optiuni de raspuns etc)

public class CreatePoll2Activity extends AppCompatActivity {

    String[] userDataGlobal;



    // lungimi minime si maxime pentru campuri
    // titlu sondaj
    public static int pollTitleMinLen = 5;
    public static int pollTitleMaxLen = 30;

    // descriere sondaj
    public static int pollDescriptionMinLen = 10;
    public static int pollDescriptionMaxLen = 80;

    // intrebari sondaj
    public static int questionHeaderMinLen = 5;
    public static int questionHeaderMaxLen = 40;

    // raspuns sondaj
    public static int optionMinLen = 1;
    public static int optionMaxLen = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll2);

        String[] userData = getIntent().getStringArrayExtra("userData"); // preluare date utilizator
        userDataGlobal = userData;


        ArrayList<String> pollConfiguration = new ArrayList<>();
        pollConfiguration = getIntent().getStringArrayListExtra("pollConfiguration");
        Log.d("tag", "Numar intrebari: " + pollConfiguration.get(0));
        for(int i=1;i<pollConfiguration.size();i++)
            Log.d("tag", "Numar optiuni intrebarea " + (i+1) + ": " + pollConfiguration.get(i));


        LinearLayout layout = (LinearLayout)findViewById(R.id.rootlayout);

        TextView pollTitleLabel = new TextView(this);
        EditText pollTitleField = new EditText(this);

        TextView pollDescriptionLabel = new TextView(this);
        EditText pollDescriptionField = new EditText(this);

        pollTitleLabel.setText("Nume sondaj: ");
        pollDescriptionLabel.setText("Descriere sondaj:");


        // Margini si proprietati setate dinamic
        LinearLayout.LayoutParams pollLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pollLayoutParams.setMargins(5, 15, 0, 0);
        pollTitleLabel.setLayoutParams(pollLayoutParams);
        pollDescriptionLabel.setLayoutParams(pollLayoutParams);
        pollTitleLabel.setTextAppearance(this, android.R.style.TextAppearance_Material_Body2);
        pollDescriptionLabel.setTextAppearance(this, android.R.style.TextAppearance_Material_Body2);


        LinearLayout.LayoutParams pollLayoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pollLayoutParams2.setMargins(5, 0, 0, 0);
        pollTitleField.setLayoutParams(pollLayoutParams2);
        pollDescriptionField.setLayoutParams(pollLayoutParams2);

        pollTitleField.setMaxLines(1);
        pollDescriptionField.setMaxLines(1);

        pollTitleField.setInputType(InputType.TYPE_CLASS_TEXT);
        pollDescriptionField.setInputType(InputType.TYPE_CLASS_TEXT);

        // adauga noile elemente GUI la layout
        layout.addView(pollTitleLabel);
        layout.addView(pollTitleField);
        layout.addView(pollDescriptionLabel);
        layout.addView(pollDescriptionField);


        pollTitleLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        pollDescriptionLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        TreeMap<Integer,TextView> questionsHeadersViews = new TreeMap<>(); // va contine textview pentru corpul intrebarii
        TreeMap<Integer,EditText> questionsHeadersFields = new TreeMap<>(); // va contine textedit pentru corpul intrebarilor
        TreeMap<Integer,List<TextView>> questionsOptionsViews = new TreeMap<>(); // pentru intrebarea i, lista va contine
                                                                                // toate textview folosite
        TreeMap<Integer,List<EditText>> questionsOptionsFields = new TreeMap<>(); // pentru intrebarea i, lista va contine
                                                                                 // toate textedit folosite pentru optiuni de raspuns

        // Generare obiecte grafice
        Log.d("tag", pollConfiguration.get(0));
        for(int i=1;i<pollConfiguration.size();i++){
            int numberOfOptions = Integer.valueOf(pollConfiguration.get(i)); // numarul de optiuni pentru intrebare i
            TextView headerView = new TextView(this);
            headerView.setText("Intrebarea " + i + ":");
            questionsHeadersViews.put(i, headerView);
            EditText headerField = new EditText(this);
            questionsHeadersFields.put(i, headerField);

            // generare texte si campuri pt completat
            for(int j = 1;j<=numberOfOptions;j++){

                if( j == 1){ // pentru primul element se creaza si lista
                    List<TextView> aux = new LinkedList<>();
                    questionsOptionsViews.put(i, aux);


                    // pentru campuri
                    List<EditText> aux2 = new LinkedList<>();
                    questionsOptionsFields.put(i, aux2);
                }

                TextView optionView = new TextView(this);
                optionView.setText("Intrebarea " + i + " optiunea " + j);
                List<TextView> aux = questionsOptionsViews.get(i);
                aux.add(optionView);
                questionsOptionsViews.put(i, aux);


                EditText optionField = new EditText(this);
                List<EditText> aux2 = questionsOptionsFields.get(i);
                aux2.add(optionField);
                questionsOptionsFields.put(i, aux2);

            }
        }


        // Adaugare elemente generate anterior
        for(Integer i : questionsHeadersViews.keySet()){

            // adauga headers

            questionsHeadersViews.get(i).setTextAppearance(this, android.R.style.TextAppearance_Material_Body2);
            LinearLayout.LayoutParams viewsLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            viewsLayoutParams1.setMargins(5, 30, 0, 0);
            questionsHeadersViews.get(i).setLayoutParams(viewsLayoutParams1);
            questionsHeadersViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            layout.addView(questionsHeadersViews.get(i));



            LinearLayout.LayoutParams fieldsLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            fieldsLayoutParams1.setMargins(5, 0, 0, 0);
            questionsHeadersFields.get(i).setLayoutParams(fieldsLayoutParams1);
            questionsHeadersFields.get(i).setMaxLines(1);
            questionsHeadersFields.get(i).setInputType(InputType.TYPE_CLASS_TEXT);
            layout.addView(questionsHeadersFields.get(i));


            // adauga text si campuri de completat pentru fiecare optiune
            for(int j = 0;j<questionsOptionsViews.get(i).size();j++) {

                questionsOptionsViews.get(i).get(j).setTextAppearance(this, android.R.style.TextAppearance_Material_Body1);
                LinearLayout.LayoutParams viewsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                viewsLayoutParams.setMargins(5, 30, 0, 0);
                questionsOptionsViews.get(i).get(j).setLayoutParams(viewsLayoutParams);
                questionsOptionsViews.get(i).get(j).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                LinearLayout.LayoutParams fieldsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                fieldsLayoutParams.setMargins(5, 0, 0, 0);
                questionsOptionsFields.get(i).get(j).setLayoutParams(fieldsLayoutParams);
                questionsOptionsFields.get(i).get(j).setMaxLines(1);
                questionsOptionsFields.get(i).get(j).setInputType(InputType.TYPE_CLASS_TEXT);

                layout.addView(questionsOptionsViews.get(i).get(j));
                layout.addView(questionsOptionsFields.get(i).get(j));
            }

        }


        // textview care arata situatia pentru input
        TextView inputStatus = new TextView(this);
        inputStatus.setText("");
        inputStatus.setTextAppearance(this, android.R.style.TextAppearance_Material_Body2);
        inputStatus.setTextColor(Color.parseColor("#fa1a0a"));
        LinearLayout.LayoutParams inputStatusLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputStatusLayoutParams.setMargins(0, 20, 0, 0);
        inputStatus.setLayoutParams(inputStatusLayoutParams);
        inputStatus.setGravity(Gravity.CENTER);
        inputStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        layout.addView(inputStatus);


        // button pentru submit
        Button submitPoll = new Button(this);
        submitPoll.setText("Creare sondaj");
        submitPoll.setBackgroundColor(Color.parseColor("#3BD141"));
        submitPoll.setTextColor(Color.parseColor("#ffffff"));
        submitPoll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(0, 40, 0, 25);
        submitPoll.setLayoutParams(buttonLayoutParams);
        layout.addView(submitPoll);



        // Creare si trimitere sondaj
        submitPoll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(validateFields(inputStatus, pollTitleField, pollDescriptionField, questionsHeadersFields, questionsOptionsFields) == true){

                    if(DBOperations.validateUniquePollName(pollTitleField.getText().toString().trim().replaceAll("\\s{2,}"," ").toUpperCase()) == true)
                        inputStatus.setText("Exista deja un sondaj cu acest nume!");
                    else{
                        inputStatus.setText("");
                        Log.d("tag","SONDAJUL POATE FI CREAT!");

                        String pollDetails = createPollDetails(questionsHeadersFields, questionsOptionsFields);

                        boolean result = DBOperations.insertPoll(pollTitleField.getText().toString().trim().replaceAll("\\s{2,}"," ").toUpperCase(), pollDescriptionField.getText().toString().trim().replaceAll("\\s{2,}"," "), pollDetails, questionsHeadersFields.size());
                        if(result == true) {
                            Log.d("tag", "SONDAJUL A FOST CREAT");
                            Intent intent = new Intent(CreatePoll2Activity.this,   CreatePoll3Activity.class);
                            intent.putExtra("userData", userDataGlobal);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            // mergi la activitate noua
                        }
                        else {
                            Log.d("tag", "SONDAJUL A FOST CREAT");
                            inputStatus.setText("Sondajul nu a fost creat - problema cu baza de date");
                        }
                    }


                }


            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Primeste textul (String) si verifica daca contine cel putin 2 cuvinte - separator este spatiu
    // Daca contine cel putin 2 cuvinte returneaza true
    public static boolean checkWordsNumber(String text) {
        text = text.trim().replaceAll("\\s{2,}"," ");
        String[] splited = text.split("\\s+");
        //for(String i: splited)
        //System.out.println(i);
        if(splited.length >= 2)
            return true;
        return false;
    }


    // converteste un nume de sondaj intr-un format care va fi folosit pentru numele de tabel
    // SONDAJ FUMAT 2018 ==> SONDAJ_FUMAT_2018
    public static String titleToTableName(String title) {
        title = title.trim().replaceAll("\\s{2,}"," ");
        StringBuilder rez = new StringBuilder();
        String[] splited = title.split("\\s+");
        for(int i=0;i<splited.length - 1;i++)
            rez.append(splited[i] + "_");
        rez.append(splited[splited.length - 1]);

        return rez.toString();
    }



    // Validează toate datele introduse in campuri (textele pentru titlu sondaj, descriere, intrebari, variante de raspuns)
    // Lungimi corespunzatoare, sa nu contina caracterele |, _
    public boolean validateFields(TextView inputStatus ,EditText pollTitleField, EditText pollDescriptionField,  TreeMap<Integer,EditText> questionsHeadersFields, TreeMap<Integer,List<EditText>> questionsOptionsFields){
        if(pollTitleField.getText().toString().trim().replaceAll("\\s{2,}"," ").length() < pollTitleMinLen){
            inputStatus.setText("Titlul sondajului trebuie să conțină minim " + pollTitleMinLen + " caractere");
            return false;
        }

        String aux = pollTitleField.getText().toString().trim().replaceAll("\\s{2,}"," ");
        for (int i = 0; i < aux.length();i++)
            if(aux.charAt(i) == '_'){
                inputStatus.setText("Titlul sondajului nu poate să conțină caracterul _");
                return false;
            }

        if(pollTitleField.getText().toString().trim().replaceAll("\\s{2,}"," ").length() > pollTitleMaxLen){
            inputStatus.setText("Titlul sondajului poate să conțină maxim " + pollTitleMaxLen + " caractere");
            return false;
        }
        else if(checkWordsNumber(pollTitleField.getText().toString()) == false){
            inputStatus.setText("Titlul sondajului trebuie să conțină minim 2 cuvinte");
            return false;
        }
        else if(pollDescriptionField.getText().toString().trim().replaceAll("\\s{2,}"," ").length() < pollDescriptionMinLen){
            inputStatus.setText("Descrierea sondajului trebuie să conțină minim " + pollDescriptionMinLen + " caractere");
            return false;
        }
        else if(pollDescriptionField.getText().toString().trim().replaceAll("\\s{2,}"," ").length() > pollDescriptionMaxLen){
            inputStatus.setText("Descrierea sondajului poate să conțină maxim " + pollDescriptionMaxLen + " caractere");
            return false;
        }
        for(Integer i :questionsHeadersFields.keySet())
            if(questionsHeadersFields.get(i).getText().toString().trim().replaceAll("\\s{2,}"," ").length() < questionHeaderMinLen){
                inputStatus.setText("Fiecare întrebare trebuie să conțină minim " + questionHeaderMinLen + " caractere");
                return false;
            }
        for(Integer i :questionsHeadersFields.keySet())
            if(questionsHeadersFields.get(i).getText().toString().trim().replaceAll("\\s{2,}"," ").length() > questionHeaderMaxLen){
                inputStatus.setText("Fiecare întrebare poate să conțină maxim " + questionHeaderMaxLen + " caractere");
                return false;
            }
        for(Integer i : questionsOptionsFields.keySet())
            for(EditText j : questionsOptionsFields.get(i))
                if(j.getText().toString().trim().replaceAll("\\s{2,}"," ").length() < optionMinLen){
                    inputStatus.setText("Fiecare opțiune de răspuns trebuie să conțină minim un caracter");
                    return false;
                }

        for(Integer i : questionsOptionsFields.keySet())
            for(EditText j : questionsOptionsFields.get(i))
                if(j.getText().toString().trim().replaceAll("\\s{2,}"," ").length() > optionMaxLen){
                    inputStatus.setText("Fiecare opțiune de răspuns poate să conțină maxim " + optionMaxLen + " caractere");
                    return false;
                }

        // validare ca sa nu contine caracterul "|" deoarece se foloseste ca separator
        for(Integer i :questionsHeadersFields.keySet()) {
            String text = questionsHeadersFields.get(i).getText().toString().trim().replaceAll("\\s{2,}"," ");
            for(int p = 0;p<text.length();p++)
                if(text.charAt(p) == '|'){
                    inputStatus.setText("Textul din întrebări nu poate conține caracterul '|'");
                    return false;
                }
        }

        for(Integer i : questionsOptionsFields.keySet())
            for(EditText j : questionsOptionsFields.get(i)) {
                String text = j.getText().toString().trim().replaceAll("\\s{2,}"," ");
                for(int p = 0; p < text.length() ; p++)
                    if(text.charAt(p) == '|'){
                        inputStatus.setText("Textul din opțiunile de răspuns nu poate conține caracterul '|'");
                        return false;
                }
            }

        return true;
    }


        /*
       A | intrebarea 1 | optiunea 1 | optiune 2 |..   || B | intrebarea 2 | optiune 1| ....
       Separator "|" intre textul din aceeasi intrebare si optiunile lor
       Separator "||" intre intrebari
       A, B, C, ... se refera la un indicator care va fi folosit pentru numele coloanelor din tabelul corespunzator sondajului
        */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String createPollDetails(TreeMap<Integer,EditText> questionsHeadersFields, TreeMap<Integer,List<EditText>> questionsOptionsFields){

        List<String> global = new LinkedList<>();
        for(Integer i : questionsHeadersFields.keySet()){
            List<String> local = new LinkedList<>();
            local.add(numberToLetter(i)); // adauga 1 -> "A"
            local.add(questionsHeadersFields.get(i).getText().toString().trim().replaceAll("\\s{2,}"," ")); // adauga intrebarea
            for(EditText j : questionsOptionsFields.get(i)) // adauga optiunile de raspuns
                local.add(j.getText().toString().trim().replaceAll("\\s{2,}"," "));

            global.add(String.join("|", local)); // converteste lista in string delimitat prin "|"

        }

        String rez = "";
        rez = String.join("||", global);
        
        return rez;
    }


    // Converteste o litera in numarul corespunzator
    public String numberToLetter(Integer n){
        // primeste un numar intre 1 si 7 si returneaza litera corespunzatoare din alfabet, ex : 1 -> A
        if (n == 1)
            return "A";
        if (n == 2)
            return "B";
        if (n == 3)
            return "C";
        if (n == 4)
            return "D";
        if (n == 5)
            return "E";
        if (n == 6)
            return "F";
        if (n == 7)
            return "G";
        Log.d("tag", "numar nu este intre 1 si 7");
        return "";
    }

    // Butonul de back trimite la CreatePollActivity
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), CreatePollActivity.class);
        myIntent.putExtra("userData", userDataGlobal);
        startActivityForResult(myIntent, 0);
        return true;
    }
}