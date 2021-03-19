package com.example.damien.devicerecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class expenses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView imgService;
    ImageView imgBill;
    ImageView imgPart;
    ImageView backButton;
    Spinner spinYear;
    Spinner spinMonth;

    String[] year = {"2010", "2011", "2012", "2013", "2014", "2015",
            "2016", "2017", "2018", "2019", "2020",
            "2021", "2022", "2023", "2024", "2025",
            "2026", "2027", "2028", "2029", "2030",
            "2031", "2032", "2033", "2034", "2035",
            "2036", "2037", "2038", "2039", "2040"};

    String[] month = {"January", "February", "March", "April", "May", "June",
                      "July", "August", "September", "October", "November", "December"};

    public static final int REQUEST_CODE17 = 17;
    public static final int REQUEST_CODE18 = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        imgService = findViewById(R.id.imgService);
        imgBill = findViewById(R.id.imgBill);
        imgPart = findViewById(R.id.imgPart);
        backButton = findViewById(R.id.btnBack);
        spinYear = findViewById(R.id.spinnerYear);
        spinMonth = findViewById(R.id.spinnerMonth);

        spinYear.setOnItemSelectedListener(this);
        spinMonth.setOnItemSelectedListener(this);

        ArrayAdapter aaYear = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, year);
        aaYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYear.setAdapter(aaYear);

        ArrayAdapter aaMonth = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, month);
        aaMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonth.setAdapter(aaMonth);

        imgService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getYear = spinYear.getSelectedItem().toString().trim();
                String getMonth = spinMonth.getSelectedItem().toString().trim();
                String expenses = "Service";

                Background bg = new Background(Background.FETCH_EXPENSES);
                bg.execute(expenses, getMonth, getYear);
            }
        });

        imgBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getYear = spinYear.getSelectedItem().toString().trim();
                String getMonth = spinMonth.getSelectedItem().toString().trim();
                String expenses = "Bill";

                Background bg = new Background(Background.FETCH_EXPENSES);
                bg.execute(expenses, getMonth, getYear);
            }
        });

        imgPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getYear = spinYear.getSelectedItem().toString().trim();
                String getMonth = spinMonth.getSelectedItem().toString().trim();
                String expenses = "Part";

                Background bg = new Background(Background.FETCH_EXPENSES);
                bg.execute(expenses, getMonth, getYear);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void btnAddNewExpenses_onClicked(View view) {
        Intent i = new Intent(expenses.this, add_expenses.class);
        startActivityForResult(i, REQUEST_CODE17);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE17 | requestCode == REQUEST_CODE18){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Expenses added or modify successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private final String LIBRARY = getString(R.string.db_library);
        private final String USERNAME = getString(R.string.db_username);
        private final String DB_NAME = getString(R.string.db_name);
        private final String PASSWORD = getString(R.string.db_password);
        private final String SERVER = getString(R.string.db_server);

        private Connection conn;
        private PreparedStatement stmt;
        private ProgressDialog progressDialog;
        private int method;

        public static final int FETCH_EXPENSES = 1;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);

            try {
                ResultSet resultSet = (ResultSet) result;

                while (resultSet.next()){
                    String expensesID = result.getString(1);
                    String expenses = result.getString(2);
                    String expensesItem1 = result.getString(3);
                    String expensesItem2 = result.getString(4);
                    String expensesItem3 = result.getString(5);
                    String expensesOtherItem = result.getString(6);
                    String expensesMonth = result.getString(7);
                    String expensesYear = result.getString(8);
                    String expensesTotal = result.getString(9);

                    if (expensesItem1 == null | expensesItem1.equals("")) {
                        expensesItem1 = "0";
                    }

                    if (expensesItem2 == null | expensesItem1.equals("")) {
                        expensesItem2 = "0";
                    }

                    if (expensesItem3 == null | expensesItem1.equals("")) {
                        expensesItem3 = "0";
                    }

                    if (expensesOtherItem == null | expensesItem1.equals("")) {
                        expensesOtherItem = "0";
                    }

                    if (expensesTotal == null | expensesItem1.equals("")) {
                        expensesTotal = "0";
                    }

                    switch (this.method){
                        case FETCH_EXPENSES:
                            Intent i = new Intent(expenses.this, expenses_details.class);
                            i.putExtra("expensesID", expensesID);
                            i.putExtra("expenses", expenses);
                            i.putExtra("expensesItem1", expensesItem1);
                            i.putExtra("expensesItem2", expensesItem2);
                            i.putExtra("expensesItem3", expensesItem3);
                            i.putExtra("expensesOtherItem", expensesOtherItem);
                            i.putExtra("expensesMonth", expensesMonth);
                            i.putExtra("expensesYear", expensesYear);
                            i.putExtra("expensesTotal", expensesTotal);

                            if (expenses.equals("Service")){
                                i.putExtra("displayItem1", "Toshiba");
                                i.putExtra("displayItem2", "Sujirox");
                                i.putExtra("displayItem3", "Sortinet");
                            } else if (expenses.equals("Bill")) {
                                i.putExtra("displayItem1", "TM");
                                i.putExtra("displayItem2", "Maxis");
                                i.putExtra("displayItem3", "Mykris");
                            } else if (expenses.equals("Part")) {
                                i.putExtra("displayItem1", "Power supply");
                                i.putExtra("displayItem2", "Toner cartridge");
                                i.putExtra("displayItem3", "Mouse, Keyboard");
                            }
                            i.putExtra("displayOtherItem", "Other");
                            startActivityForResult(i, REQUEST_CODE18);
                    }
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(expenses.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(expenses.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            conn = connectDB();
            ResultSet result = null;

            if (conn == null) {
                return null;
            }
            try {
                String query = "";

                switch(this.method){
                    case FETCH_EXPENSES:
                        query = "SELECT id, expenses, item_1, item_2, item_3, other_item, month, year, total FROM expenses WHERE expenses = ? AND month = ? AND year = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, strings[0]);
                        stmt.setString(2, strings[1]);
                        stmt.setInt(3, Integer.parseInt(strings[2]));
                        result = stmt.executeQuery();
                        return result;
                }
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return null;
        }

        private Connection connectDB() {
            try {
                Class.forName(LIBRARY);
                return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DB_NAME, USERNAME, PASSWORD);
            }
            catch (Exception e) {
                Log.e("Error on Connection", e.getMessage());
                return null;
            }
        }

        public void closeConn () {
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
    }
}
