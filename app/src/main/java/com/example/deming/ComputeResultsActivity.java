package com.example.deming;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Activitate pentru a vizualiza rezultatele sondajului folosind grafice

public class ComputeResultsActivity extends AppCompatActivity {

    public String[] userDataGlobal;


    // Culorile pentru sondaj
    public static final int[] COLORS = {
            Color.rgb(0, 114, 214), // ALBASTRU
            Color.rgb(212, 13, 0), // ROSU
            Color.rgb(255, 194, 18), // GALBEN
            Color.rgb(210, 33, 219), // MOV
            Color.rgb(90, 194, 6), // VERDE
            Color.rgb(255, 165, 10), // PORTOCALIU
            Color.rgb(146, 17, 245) // PURPLE
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compute_results);


        String[] userData = getIntent().getStringArrayExtra("userData"); // accesare date utilizator
        userDataGlobal = userData;
        for(String i: userData)
            Log.d("tag", i);
        User user = new User(Integer.valueOf(userData[0]),userData[1],userData[2],userData[3],userData[4]);

        Log.d("tag", "USER: " + user.toString());

        String pollTitle = getIntent().getStringExtra("pollTitle");
        Log.d("tag", "POLL TITLE: " + pollTitle);


        Poll sondaj = DBOperations.getPoll(pollTitle); // preluare sondaj
        if(sondaj != null)
            Log.d("tag", sondaj.toString());


        TextView getDataStatus = (TextView)findViewById(R.id.getDataStatus);
        getDataStatus.setVisibility(View.GONE);

        List<List<String>> records = DBOperations.getPollRecords(pollTitle);
        if(records != null){
            // putem crea sondajul
            for(List<String> i : records)
                System.out.println(i.toString());

            List<PieChart> lCharts = new LinkedList<>();
            PieChart A = (PieChart)findViewById(R.id.chartA);
            PieChart B = (PieChart)findViewById(R.id.chartB);
            PieChart C = (PieChart)findViewById(R.id.chartC);
            PieChart D = (PieChart)findViewById(R.id.chartD);
            PieChart E = (PieChart)findViewById(R.id.chartE);
            PieChart F = (PieChart)findViewById(R.id.chartF);
            PieChart G = (PieChart)findViewById(R.id.chartG);

            lCharts.add(A);
            lCharts.add(B);
            lCharts.add(C);
            lCharts.add(D);
            lCharts.add(E);
            lCharts.add(F);
            lCharts.add(G);

            for(PieChart i : lCharts)
                i.setVisibility(View.GONE);

            TextView pollTitleChart = (TextView)findViewById(R.id.pollTitleChart);
            pollTitleChart.setText(sondaj.getTitle());
            TextView noOfPeople = (TextView)findViewById(R.id.noOfPeople);
            noOfPeople.setText("Date colectate de la " + sondaj.getRowsNumber() + " oameni");

            List<List<String>> pollDetails = sondaj.getDetails();

            for(int i=0;i<records.size();i++){ // pentru fiecare intrebare din sondaj
                lCharts.get(i).setVisibility(View.VISIBLE);

                ArrayList<PieEntry> entries = new ArrayList<>();
                List<String> l = new LinkedList<>();
                l = records.get(i); // din record, ia raspunsurile de pe pozitia i
                String[] array = l.toArray(new String[l.size()]);
                Map<String, Long> freq = // creaza vector de frecventa pentru array-ul de mai sus (se folosesc stream-uri)
                        Stream.of(array)
                                .collect(Collectors.groupingBy(Function.identity(),
                                        Collectors.counting()));

                // avand vectorul de frecventa - cate raspunsuri de fiecare tip sunt pentru intrebarea i
                // se creaza intrari pentru grafic
                for(String j: freq.keySet())
                    entries.add(new PieEntry(freq.get(j), j));
                setChartProperties(lCharts.get(i), pollDetails.get(i).get(1), entries);
            }


        }
        else{
            getDataStatus.setVisibility(View.VISIBLE);
            // nu am putut lua datele despre sondaj
        }


        // Intoarcere la meniul utilizatorului
        Button backToUserActivity = (Button) findViewById(R.id.backToUserActivity);
		double windowWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams((int) (windowWidth * 0.7), LinearLayout.LayoutParams.WRAP_CONTENT);
        btnLayout.setMargins(0, 10, 0, 25);
        backToUserActivity.setLayoutParams(btnLayout);
        backToUserActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComputeResultsActivity.this,   UserMainActivity.class);
                intent.putExtra("userData", userDataGlobal);
                startActivity(intent);
            }
        });

    }

    // Seteaza proprietatile pentru chart
    public void setChartProperties(PieChart pieChart, String chartTitle,  ArrayList<PieEntry> entries){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText(chartTitle);
        pieChart.setCenterTextSize(18);
        pieChart.getDescription().setEnabled(false);

        Legend lgd = pieChart.getLegend();
        lgd.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        lgd.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        lgd.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lgd.setDrawInside(false);
        lgd.setEnabled(true);
        lgd.setTextSize(13);
        lgd.setWordWrapEnabled(true);

        double windowWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams chartLayout = new LinearLayout.LayoutParams((int) (windowWidth * 1.15),(int) (windowWidth * 1.05) );
        chartLayout.setMargins(0, 20, 0, 25);
        pieChart.setLayoutParams(chartLayout);

        pieChart.setExtraTopOffset(-10);
        ArrayList<Integer> colors = new ArrayList<>();
        for(int i=0;i<COLORS.length;i++)
            colors.add(COLORS[i]);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.setTouchEnabled(false);
        pieChart.animateY(1500, Easing.EaseInOutQuad);
    }
}