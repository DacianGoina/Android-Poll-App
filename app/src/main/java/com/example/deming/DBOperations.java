package com.example.deming;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

// Clasa pentru operatiile pe baza de date

public class DBOperations{

    private static String dbUser = "sa";
    private static String dbPasswd = "";
    private static String url = "jdbc:h2:tcp://A.B.C.D/~/test"; // A.B.C.D reprezinta o adresa IP,
    // am scris cu A,B,C,D deoarece nu vreau sa fac publica adresa personala de IP



    // Verifica daca un exista contul de utilzator cu adresa de email si parola date ca parametri
    // In caz afirmativ returneaza datele utilizatorului, altfel returneaza null
    // Se foloseste pentru LoginActivity - pentru logare
    public static User getUser(String email, String password){
        final User[] a = {null};

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("SELECT ID, USERNAME, EMAIL, PASSWORD, ACCOUNTTYPE FROM UTILIZATORI WHERE EMAIL=?");
                    st.setString(1, email);
                    ResultSet rez = st.executeQuery();
                    if(rez.toString().contains("rows: 0")) { // NU A GASIT LINII
                        a[0] = null;
                    }

                    rez.next();
                    int id = rez.getInt(1);
                    String username = rez.getString(2);
                    String getEmail = rez.getString(3);
                    String getPassword = rez.getString(4);
                    String accounttype = rez.getString(5);

                    a[0] = new User(id,username, email, password, accounttype);

                    System.out.println(getEmail + " " + getPassword);
                    if(getEmail.equals(email) && getPassword.equals(password)) {
                        Log.d("TAG","--------------------Logare cu succes H2!-----------------");
                    }
                    else { // daca nu sunt datele bune atunci a devine null
                        a[0] =  null;
                    }
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Logare esuata");
                    e.printStackTrace();
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return a[0];
    }

    // Valideaza ca un anumit username sa fie unic
    // Returneaza true daca nu exista deja username-ul dat, false altfel (false - exista deja)
    public static boolean checkUsernameUnique(String username){
        final boolean[] REZ = {false};

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("SELECT USERNAME FROM UTILIZATORI WHERE USERNAME=?");
                    st.setString(1, username);
                    ResultSet rez = st.executeQuery();
                    if(rez.toString().contains("rows: 0")) { // NU A GASIT LINII
                        REZ[0] = true;
                    }
                    //REZ[0] = false;
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    REZ[0] =  false;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return REZ[0];

    }

    // Valideaza ca un anumit email sa fie unic
    public static boolean checkEmailUnique(String email){
        final boolean[] REZ = {false};

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("SELECT EMAIL FROM UTILIZATORI WHERE EMAIL=?");
                    st.setString(1, email);
                    ResultSet rez = st.executeQuery();
                    if(rez.toString().contains("rows: 0")) { // NU A GASIT LINII
                        REZ[0] = true;
                    }
                    //REZ[0] = false;

                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    REZ[0] = false;
                }

            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return REZ[0];
    }

    // Creaza un cont de utilizator
    // Returneaza true daca inserarea s-a realizat cu succes, false altfel (de ex nu e activa baza de date)
    public static boolean insertUser(String username, String email, String password, String accountType){
        final boolean[] REZ = {false};

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("INSERT INTO UTILIZATORI (USERNAME,EMAIL, PASSWORD,ACCOUNTTYPE) VALUES (?,?,?,?)");
                    st.setString(1, username);
                    st.setString(2,email);
                    st.setString(3, password);
                    st.setString(4,accountType);

                    st.executeUpdate();
                    REZ[0] = true; // va returna true pentru succes
                    // daca intra pe exceptie va returna false
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("INSERARE ESUATA");
                    e.printStackTrace();
                    REZ[0] =  false;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return REZ[0];

    }

    //Primeste un nume de sondaj si verifica daca acesta exista deja
    //Returneaza true daca exista, false daca nu exista un sondaj cu numele respectiv
    public static boolean validateUniquePollName(String title){
        title = title.toUpperCase();

        final boolean[] REZ = {true};
        String finalTitle = title;
        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("SELECT * FROM SONDAJE WHERE NUME=?");
                    st.setString(1, finalTitle);
                    ResultSet rez = st.executeQuery();
                    if(rez.toString().contains("rows: 0")) { // NU A GASIT LINII
                        REZ[0] = false;
                    }
                    //REZ[0] = true;

                    conn.close();
                } catch (SQLException e) {
                    System.out.println("CONEXIUNE PIERDUTA");
                    e.printStackTrace();
                    REZ[0] = true;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return true;
        }
        return REZ[0];

    }


    // Creaza un tabel dinamic - numarul de coloane si numele tabelul sunt date ca parametrii
    // Returneaza true daca tabelul a fost creat si adaugat cu succes in baza de date, altfel returneaza false
    public static boolean createCustomTable(String tableName, int numberOfColumns) {
        final boolean[] REZ = {false};

        tableName = tableName.toUpperCase(); // cu litere mari
        tableName = CreatePoll2Activity.titleToTableName(tableName); // nume tabel

        String finalTableName = tableName;
        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    Statement st = conn.createStatement();


                    String CREATE_NEW_TABLE_STRING = "";
                    if(numberOfColumns == 2)
                        CREATE_NEW_TABLE_STRING = "CREATE TABLE " + finalTableName +  " (ID int AUTO_INCREMENT, A TEXT, B TEXT, DATA DATE, ID_ANGAJAT int, PRIMARY KEY (ID) )";
                    if(numberOfColumns == 3){
                        CREATE_NEW_TABLE_STRING = "CREATE TABLE " + finalTableName + "  (ID int AUTO_INCREMENT, A TEXT, B TEXT, C TEXT, DATA DATE, ID_ANGAJAT int, PRIMARY KEY (ID) )";
                    }
                    if(numberOfColumns == 4){
                        CREATE_NEW_TABLE_STRING = "CREATE TABLE " + finalTableName + "  (ID int AUTO_INCREMENT, A TEXT, B TEXT, C TEXT, D TEXT, DATA DATE, ID_ANGAJAT int, PRIMARY KEY (ID) )";
                    }
                    if(numberOfColumns == 5){
                        CREATE_NEW_TABLE_STRING = "CREATE TABLE " + finalTableName + "  (ID int AUTO_INCREMENT, A TEXT, B TEXT, C TEXT, D TEXT, E TEXT, DATA DATE, ID_ANGAJAT int, PRIMARY KEY (ID) )";
                    }
                    if(numberOfColumns == 6){
                        CREATE_NEW_TABLE_STRING = "CREATE TABLE " + finalTableName + "  (ID int AUTO_INCREMENT, A TEXT, B TEXT, C TEXT, D TEXT, E TEXT, F TEXT, DATA DATE, ID_ANGAJAT int, PRIMARY KEY (ID) )";
                    }
                    if(numberOfColumns == 7){
                        CREATE_NEW_TABLE_STRING = "CREATE TABLE " + finalTableName + "  (ID int AUTO_INCREMENT, A TEXT, B TEXT, C TEXT, D TEXT, E TEXT, F TEXT, G TEXT, DATA DATE, ID_ANGAJAT int, PRIMARY KEY (ID) )";
                    }
                    st.executeUpdate(CREATE_NEW_TABLE_STRING);
                    REZ[0] = true;
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("NU S-A ADAUGAT TABELUL");
                    e.printStackTrace();
                    REZ[0] =  false;

                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return REZ[0];
    }


    // Creaza un nou sondaj - se creaza o linie in tabelul SONDAJE si un nou tabel pentru sondajul nou creat
    // Returneaza true daca sondajul a fost creat cu succes, altfel returneaza false
    public static boolean insertPoll(String title, String description, String details, int numberOfColumns){
        title = title.toUpperCase(); // numele sondajului va fi cu litera mare dar va fi simplu, fara _

        final boolean[] REZ = {false};

        String finalTitle = title;
        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("INSERT INTO SONDAJE (NUME, DESCRIERE, DETALII) VALUES (?,?,?)");
                    st.setString(1, finalTitle);
                    st.setString(2,description);
                    st.setString(3, details);

                    if(createCustomTable(finalTitle, numberOfColumns) == false) // daca nu se poate crea tabelul metoda se opreste aici
                        REZ[0] = false;

                    st.executeUpdate();
                    REZ[0] =  true; // va returna true pentru succes
                    // daca intra pe exceptie va returna false
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("INSERARE ESUATA");
                    e.printStackTrace();
                    REZ[0] = false;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return REZ[0];
    }

    // Returneaza o lista cu obiecte de tip Poll - se foloseste in UserMainActivity
    // Fiecare obiect va contine numele unui sondaj si numarul de inregistrari
    // Se foloseste constructorul 2 parametri - titlu si numar inregistrari
    public static List<Poll> getPollsName(){
        final List<Poll>[] l = new List[]{new LinkedList<>()};
        List<String> numeSondaje = new LinkedList<>();

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("SELECT NUME FROM SONDAJE");
                    ResultSet rez = st.executeQuery();

                    // ia toate sondajele (numele)
                    while(rez.next()){
                        numeSondaje.add(rez.getString(1));
                    }

                    rez.close();

                    // pentru fiecare sondaj du-te la tabelul corespunzator si ia numarul de inregistrari
                    for(String i:numeSondaje){
                        String tableName = CreatePoll2Activity.titleToTableName(i);
                        String query = "SELECT COUNT(*) FROM " + tableName;
                        PreparedStatement st2 = conn.prepareStatement(query);
                        ResultSet rez2 = st2.executeQuery();
                        rez2.next();
                        int rows = rez2.getInt(1);
                        Poll sondaj = new Poll(i, rows);
                        l[0].add(sondaj);
                        rez2.close();
                        st2.close();
                    }

                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    l[0] = null;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return l[0];
    }


    // Returneaza informatiile despre un sondaj (nume, detalii, descriere, nr inregistrari)
    // Se foloseste constructorul cu 4 parametrii
    public static Poll getPoll(String pollTitle){
        final Poll[] rez = {null};

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("SELECT * FROM SONDAJE WHERE NUME = ?");
                    st.setString(1, pollTitle);

                    ResultSet rs = st.executeQuery();
                    rs.next();
                    rez[0] = new Poll(rs.getString(1), rs.getString(2),rs.getString(3),5);
                    rs.close();
                    st.close();


                    // numarul de inregistrari din sondajul respectiv
                    String tableName = CreatePoll2Activity.titleToTableName(pollTitle);
                    String query = "SELECT COUNT(*) FROM " + tableName;
                    PreparedStatement st2 = conn.prepareStatement(query);
                    ResultSet rs2 = st2.executeQuery();
                    rs2.next();
                    rez[0].setRowsNumber(rs2.getInt(1));
                    rs2.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    rez[0] = null;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return  null;
        }

        return rez[0];

    }

    // Primeste details de la un Sondaj (Poll) sub forma unui string codificat in forma respectiva
    // Face din el o lista de liste, adica in face utilizabil
    // Fiecare lista contine: [0] literaIndex,  [1] corpul intrebarii, [2] optiune 1, [3] optiune 2, ....
    public static List<List<String>> getPollDetails(String details){
        List<List<String>> l = new LinkedList<>();
        String[] global = details.split("\\|\\|");
        for(String i: global) {
            String local[] = i.split("\\|");
            List<String> aux = new LinkedList<>();
            aux = Arrays.asList(local);
            l.add(aux);
        }

        return l;
    }

    // Se foloseste in AddRecordActivity
    // Adauga intregistrare intr-un anumit sondaj - in tabelul respectiv
    // In record pe primele pozitii sunt raspunsuri la intrebari
    // Pe penultima pozitie este data
    // Pe ultima pozitie este ID-ul angajatului care a trimis inregistrarea
    // Returneaza true daca inregistrarea a fost adaugata cu succes, false altfel
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean addCustomRecord(String pollTitle, List<String> record){
        final boolean[] REZ = {false};

        String tableName = pollTitle.toUpperCase();
        tableName = CreatePoll2Activity.titleToTableName(tableName);

        Date a = java.sql.Date.valueOf(String.valueOf(java.time.LocalDate.now()));

        String finalTableName = tableName;
        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = null;

                    if(record.size() == 4){ // 2 intrebari
                        st = conn.prepareStatement("INSERT INTO " + finalTableName + " (A, B, DATA, ID_ANGAJAT) VALUES (?,?,?,?)");
                        st.setString(1, record.get(0));
                        st.setString(2, record.get(1));
                        st.setDate(3, (java.sql.Date) a);
                        st.setInt(4, Integer.parseInt(record.get(record.size() - 1)));
                    }
                    else if(record.size() == 5){ // 3 intrebari
                        st = conn.prepareStatement("INSERT INTO " + finalTableName + " (A, B, C, DATA, ID_ANGAJAT) VALUES (?,?,?,?,?)");
                        st.setString(1, record.get(0));
                        st.setString(2, record.get(1));
                        st.setString(3, record.get(2));
                        st.setDate(4, (java.sql.Date) a);
                        st.setInt(5, Integer.parseInt(record.get(record.size() - 1)));
                    }

                    else if(record.size() == 6){ // 4 intrebari
                        st = conn.prepareStatement("INSERT INTO " + finalTableName + " (A, B, C, D, DATA, ID_ANGAJAT) VALUES (?,?,?,?,?,?)");
                        st.setString(1, record.get(0));
                        st.setString(2, record.get(1));
                        st.setString(3, record.get(2));
                        st.setString(4, record.get(3));
                        st.setDate(5, (java.sql.Date) a);
                        st.setInt(6, Integer.parseInt(record.get(record.size() - 1)));
                    }

                    else if(record.size() == 7){ // 5 intrebari
                        st = conn.prepareStatement("INSERT INTO " + finalTableName + " (A, B, C, D, E, DATA, ID_ANGAJAT) VALUES (?,?,?,?,?,?,?)");
                        st.setString(1, record.get(0));
                        st.setString(2, record.get(1));
                        st.setString(3, record.get(2));
                        st.setString(4, record.get(3));
                        st.setString(5, record.get(4));
                        st.setDate(6, (java.sql.Date) a);
                        st.setInt(7, Integer.parseInt(record.get(record.size() - 1)));
                    }

                    else if(record.size() == 8){ // 6 intrebari
                        st = conn.prepareStatement("INSERT INTO " + finalTableName + " (A, B, C, D, E, F, DATA, ID_ANGAJAT) VALUES (?,?,?,?,?,?,?,?)");
                        st.setString(1, record.get(0));
                        st.setString(2, record.get(1));
                        st.setString(3, record.get(2));
                        st.setString(4, record.get(3));
                        st.setString(5, record.get(4));
                        st.setString(6, record.get(5));
                        st.setDate(7, (java.sql.Date) a);
                        st.setInt(8, Integer.parseInt(record.get(record.size() - 1)));
                    }


                    else if(record.size() == 9){ // 7 intrebari
                        st = conn.prepareStatement("INSERT INTO " + finalTableName + " (A, B, C, D, E, F, G, DATA, ID_ANGAJAT) VALUES (?,?,?,?,?,?,?,?,?)");
                        st.setString(1, record.get(0));
                        st.setString(2, record.get(1));
                        st.setString(3, record.get(2));
                        st.setString(4, record.get(3));
                        st.setString(5, record.get(4));
                        st.setString(6, record.get(5));
                        st.setString(7, record.get(6));
                        st.setDate(8, (java.sql.Date) a);
                        st.setInt(9, Integer.parseInt(record.get(record.size() - 1)));
                    }

                    st.executeUpdate();

                    REZ[0] =  true;
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    REZ[0] = false;
                }

            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return  false;
        }

        return REZ[0];
    }


    // Se foloseste in ComputeResultsActivity
    // Pentru sondajul dat prin nume (pollTitle) se aduc toate inregistrarile din baza de date
    // Se pun intr-o lista de liste inlantuite
    // Fiecare element din aceasta lista va fi o lista, fiecare lista contine toate valorile de pe o anumita coloana
    // Ex: l.get(0) va fi o lista care contine toate raspunsurile la intrebarea 1 din sondaj

    public static List<List<String>> getPollRecords(String pollTitle){
        final List<List<String>>[] l = new List[]{new LinkedList<>()};
        String tableName = CreatePoll2Activity.titleToTableName(pollTitle);

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    String query = "SELECT * FROM " + tableName;
                    PreparedStatement st = conn.prepareStatement(query);

                    ResultSet rez = st.executeQuery();
                    ResultSetMetaData rsmd = rez.getMetaData();
                    int n = rsmd.getColumnCount(); // numarul de coloane de la Result Set - poate varia de la tabel la tabel
                    n = n - 3; // toate tabelele au cel coloanele ID,DATA, ID_ANGAJAT, momentan ne intereseaza doar coloanele cu date despre sondaj
                    for(int i=1;i<=n;i++){
                        List<String> a = new LinkedList<>(); // pentru fiecare coloana creaza o lista
                        l[0].add(a);
                    }

                    while(rez.next()){
                        for(int i=1;i<=n;i++)
                            l[0].get(i-1).add(rez.getString(i+1)); // in lista de pe pozitia i-1 adauga valoarea in set de pe coloana i+1
                        // incepem de la +1 deoarece pe prima pozitie este ID
                    }
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    l[0] = null;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return l[0];

    }

    // Asemanator cu metoda getPollRecords, dar acolo se luau doar raspunsurile la intrebari
    // Acum se iau toate coloanele din tabel si se returneaza o lista care contine elemente de List<String>
    // Chiar daca din DB se vor lua coloane de tip int (sau ceva diferit de String), pentru aceste date se va face conversia la String
    public static List<List<String>> getPollCompleteRecords(String pollTitle){

        final List<List<String>>[] l = new List[]{new LinkedList<>()};

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                String tableName = CreatePoll2Activity.titleToTableName(pollTitle);
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    String query = "SELECT * FROM " + tableName;
                    PreparedStatement st = conn.prepareStatement(query);

                    ResultSet rez = st.executeQuery();
                    ResultSetMetaData rsmd = rez.getMetaData();
                    int n = rsmd.getColumnCount(); // numarul de coloane de la Result Set - poate varia de la tabel la tabel
                    for(int i=1;i<=n;i++){
                        List<String> a = new LinkedList<>(); // pentru fiecare coloana creaza o lista
                        l[0].add(a);
                    }

                    while(rez.next()){
                        l[0].get(0).add(String.valueOf(rez.getInt(1))); // pe prima coloana e valoare numerica
                        int t = n - 2;
                        for(int i=2;i<=t;i++) // aici sunt valori String
                            l[0].get(i-1).add(rez.getString(i));
                        String data = String.valueOf(rez.getDate(n-1));
                        String[] split = data.split("-");
                        data = split[2] + "-" + split[1] + "-" + split[0];
                        //System.out.println(data);
                        l[0].get(n-2).add(data); // pe pentultima este valoare Date
                        l[0].get(n-1).add(String.valueOf(rez.getInt(n))); // pe ultima este valoarea numerica
                    }
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    l[0] = null;
                }

            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return l[0];
    }


    // Se foloseste in WorkProgressActivity
    // Extrage din baza de date ID -ul si numele tuturor utilizatorilor cu ACCOUNTTYPE = "ANGAJAT"
    // Rezultatul este returnat sub forma unui TreeMap (Map cu cheile ordonate)
    // In TreeMap - ul rezultat, cheia va fi numele angajatului iar valoarea va fi ID -ul angajatului
    public static TreeMap<String,Integer> getEmployeeInfo(){
        final TreeMap<String, Integer>[] map = new TreeMap[]{new TreeMap<>()};

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    PreparedStatement st = conn.prepareStatement("SELECT ID, USERNAME FROM UTILIZATORI WHERE ACCOUNTTYPE = ?");
                    st.setString(1, "ANGAJAT");
                    ResultSet rez = st.executeQuery();
                    while(rez.next()){
                        map[0].put(rez.getString(2), rez.getInt(1));
                    }
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    map[0] =  null;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return map[0];
    }

    // Se foloseste in WorkProgressActivity
    // Calculeaza efectiv cate inregistrari a trimis fiecare angajat
    // Se foloseste de o data primita (luna, an) si de ID-ul angajatului
    // Cauta in toate tabelele sondajelor linii care corespund cu datele de mai sus (id, luna, an)
    // Se returneaza o lista cu String care contine 2 elemente:
    //      primul contine informatii despre numarul total de inregistrari trimise de angajat
    //      al doilea contine informatie detaliata - cat a trimis pentru fiecare sondaj
    //      (unde a trimis, pentru sondajele unde nu a trimis nu are rost sa afisam 0)
    // Forma rezultat: rez[0] = empName + " a colectat " +  nrInregistrari + " inregistrari in" + originalDate
    //                 rez[1] += numeSondaj1 + nrInregistrari1 + " inregistrari\n"
    // Primeste ca parametrii data (luna, an), ID-ul angajatului,
    // numele angajatului (nu cauta folosind numele, foloseste numele pt a forma un string rezultat)
    // precum si originalDate (ex. :"Mai 2021"), acest parametru se va folosi pentru a forma un string rezultat
    // In cazul in care sunt probleme cu conectarea la baza de date va returna un null
    public static List<String> computeEmployeeWork(List<Integer> date, Integer id, String empName, String originalDate){
        final List<String>[] l = new List[]{new LinkedList<>()};
        int luna = date.get(0);
        int an = date.get(1);

        final int[] totalSum = {0}; // suma (pontaj) total

        final String[] firstString = {""}; // primul String din lista rezultat
        StringBuilder secondString = new StringBuilder(); // al doilea String lista rezultat
        secondString.append("");

        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(url,dbUser,dbPasswd);
                    // ia toate sondajele (doar numele)
                    PreparedStatement st = conn.prepareStatement("SELECT NUME FROM SONDAJE");
                    List<String> numeSondaje = new LinkedList<>();
                    ResultSet rez = st.executeQuery();
                    while(rez.next()){
                        numeSondaje.add(rez.getString(1));
                    }
                    st.close();
                    rez.close();

                    // Pentru fiecare sondaj, vezi catre intregistrari a trimis angajatul cu ID-ul id,
                    // in luna - anul identificate prin luna, an
                    for(String i : numeSondaje){
                        String tableName = CreatePoll2Activity.titleToTableName(i);
                        PreparedStatement st2 = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName + " WHERE MONTH(DATA) = ? AND YEAR(DATA) = ? AND ID_ANGAJAT = ?");
                        st2.setInt(1, luna);
                        st2.setInt(2, an);
                        st2.setInt(3, id);
                        ResultSet rez2 = st2.executeQuery();
                        rez2.next();
                        int n = rez2.getInt(1);
                        if(n > 0){
                            totalSum[0] = totalSum[0] + n;
                            secondString.append(i + ": " + n + " inregistrari\n");
                        }
                        st2.close();
                        rez2.close();
                    }

                    if(totalSum[0] == 0)
                        firstString[0] = empName + " a colectat " + totalSum[0] + " inregistrari in " + originalDate;
                    else
                        firstString[0] = empName + " a colectat " + totalSum[0] + " inregistrari in " + originalDate + ":" ;

                    l[0].add(firstString[0]);
                    l[0].add(secondString.toString());


                    conn.close();
                } catch (SQLException e) {
                    System.out.println("FAIL");
                    e.printStackTrace();
                    l[0] = null;
                }
            }});
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return l[0];
    }
}