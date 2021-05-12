package com.example.deming;

import java.util.List;

// Clasa pentru a tine (data structure) un sondaj (poll)

public class Poll {
    private String title;
    private String description;
    private List<List<String>> details;
    private int rowsNumber;



    // Constructor mai simplu care foloseste doar titlu si numarul de inregistrari din acel sondaj
    public Poll(String title, int rowsNumber){
        this.title = title;
        this.rowsNumber = rowsNumber;
    }

    // nume sondaj, descriere, string care reprezinta intrebarile si optiunile de raspuns
    // extrage datele din acel string si face List<List<String>> folosind functia din DBOperations

    public Poll(String title, String description, String details, int rowsNumber) {
        this.title = title;
        this.description = description;
        this.details = DBOperations.getPollDetails(details);
        this.rowsNumber = rowsNumber;
    }

    // returneaza un numar care reprezinta numarul total al optiunilor de raspuns
    // de ex daca sondajul contine 2 intrebari: prima cu 5 optiuni si a doua cu 2 optiuni
    // atunci numarul returnat va fi 7 = 5 + 2
    public int ComputeNoOfOptions(){
        int S = 0;
        for(List<String> i: this.details){
            S = S + i.size() - 2; // deoarece in fiecare lista, primii doi itemi se refera la altceva decat optiuni de raspuns
        }
        return S;
    }

    // toString pentru constructorul mic
    public String toStringMini() {
        return "Poll{" +
                "title='" + title + '\'' +
                ", rowsNumber=" + rowsNumber +
                '}';
    }


    // toString pentru constructorul mare
    @Override
    public String toString() {
        return "Poll{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", details=" + details +
                ", rowsNumber=" + rowsNumber +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<List<String>> getDetails() {
        return details;
    }

    public int getRowsNumber() {
        return rowsNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetails(List<List<String>> details) {
        this.details = details;
    }

    public void setRowsNumber(int rowsNumber) {
        this.rowsNumber = rowsNumber;
    }
}
