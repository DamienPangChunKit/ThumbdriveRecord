package com.example.damien.devicerecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class add_expenses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextInputLayout layout_item1;
    TextInputLayout layout_item2;
    TextInputLayout layout_item3;
    TextInputLayout layout_otherItem;
    TextView item1;
    TextView item2;
    TextView item3;
    ImageView backButton;
    Spinner spinnerExpenses;
    Spinner spinnerMonth;
    Spinner spinnerYear;

    String[] expenses = {"Service", "Bill", "Part"};
    String[] year = {"2010", "2011", "2012", "2013", "2014", "2015",
            "2016", "2017", "2018", "2019", "2020",
            "2021", "2022", "2023", "2024", "2025",
            "2026", "2027", "2028", "2029", "2030",
            "2031", "2032", "2033", "2034", "2035",
            "2036", "2037", "2038", "2039", "2040"};

    String[] month = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        backButton = findViewById(R.id.btnBack);
        spinnerExpenses = findViewById(R.id.spinnerExpenses);
        spinnerMonth = findViewById(R.id.spinnerExpensesMonth);
        spinnerYear = findViewById(R.id.spinnerExpensesYear);
        item1 = findViewById(R.id.tvItem1);
        item2 = findViewById(R.id.tvItem2);
        item3 = findViewById(R.id.tvItem3);
        layout_item1 = findViewById(R.id.textInputItem1);
        layout_item2 = findViewById(R.id.textInputItem2);
        layout_item3 = findViewById(R.id.textInputItem3);
        layout_otherItem = findViewById(R.id.textInputOtherItem);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinnerExpenses.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, expenses);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenses.setAdapter(arrayAdapter);

        spinnerMonth.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, month);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(arrayAdapter2);

        spinnerYear.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter3 = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, year);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(arrayAdapter3);
    }

    public void btnAddNewExpenses_onClicked(View view) {
        String getExpenses = spinnerExpenses.getSelectedItem().toString().trim();
        String getItem1 = layout_item1.getEditText().getText().toString().trim();
        String getItem2 = layout_item2.getEditText().getText().toString().trim();
        String getItem3 = layout_item3.getEditText().getText().toString().trim();
        String getOtherItem = layout_otherItem.getEditText().getText().toString().trim();
        String getMonth = spinnerMonth.getSelectedItem().toString().trim();
        String getYear = spinnerYear.getSelectedItem().toString().trim();

        if (getItem1.isEmpty()){
            getItem1 = String.valueOf(0);
        }

        if (getItem2.isEmpty()){
            getItem2 = String.valueOf(0);
        }

        if (getItem3.isEmpty()){
            getItem3 = String.valueOf(0);
        }

        if (getOtherItem.isEmpty()){
            getOtherItem = String.valueOf(0);
        }

        Float totalAmt = Float.parseFloat(getItem1) + Float.parseFloat(getItem2) + Float.parseFloat(getItem3) + Float.parseFloat(getOtherItem);

        Background bg = new Background(Background.INSERT_LIST);
        bg.execute(getExpenses, getItem1, getItem2, getItem3, getOtherItem, getMonth, getYear, String.valueOf(totalAmt));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (expenses[position].equals("Service")){
            item1.setText("Toshiba ");
            item2.setText("Sujirox ");
            item3.setText("Sortinet ");
        } else if (expenses[position].equals("Bill")){
            item1.setText("TM ");
            item2.setText("Maxis ");
            item3.setText("Mykris ");
        } else if (expenses[position].equals("Part")){
            item1.setText("Power Supply ");
            item2.setText("Toner Cartridge ");
            item3.setText("Mouse, Keyboard ");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nothing to do
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private final String LIBRARY = getString(R.string.db_library);
        private final String USERNAME = getString(R.string.db_username);
        private final String DB_NAME = getString(R.string.db_name);
        private final String PASSWORD = getString(R.string.db_password);
        private final String SERVER = getString(R.string.db_server);

        public static final int INSERT_LIST = 310;

        private int method;
        private Connection conn;
        private PreparedStatement stmt;
        private ProgressDialog progressDialog;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }
        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);

            try {
                switch (this.method) {
                    case INSERT_LIST:
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(add_expenses.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            finally {
//                progressDialog.hide();
                try { result.close(); } catch (Exception e) { /* ignored */ }
                closeConn();
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(add_expenses.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
//            progressDialog.show();
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
                switch (this.method) {
                    case INSERT_LIST:
//                        query = "insert into expenses (expenses, item_1, item_2, item_3, other_item, month, year, total) values (?, ?, ?, ?, ?, ?, ?, ?)";

                        query = "INSERT into expenses (expenses, item_1, item_2, item_3, other_item, month, year, total) " +
                                "SELECT ?, ?, ?, ?, ?, ?, ?, ? " +
                                "FROM DUAL " +
                                "WHERE NOT EXISTS( " +
                                    "SELECT 1 " +
                                    "FROM expenses " +
                                    "WHERE expenses = ? AND month = ? AND year = ?" +
                                ") " +
                                "LIMIT 1";

                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, strings[0]);
                        stmt.setFloat(2, Float.parseFloat(strings[1]));
                        stmt.setFloat(3, Float.parseFloat(strings[2]));
                        stmt.setFloat(4, Float.parseFloat(strings[3]));
                        stmt.setFloat(5, Float.parseFloat(strings[4]));
                        stmt.setString(6, strings[5]);
                        stmt.setInt(7, Integer.parseInt(strings[6]));
                        stmt.setFloat(8, Float.parseFloat(strings[7]));
                        stmt.setString(9, strings[0]);
                        stmt.setString(10, strings[5]);
                        stmt.setInt(11, Integer.parseInt(strings[6]));
                        stmt.executeUpdate();
                }
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return result;
        }


        private Connection connectDB(){
            try {
                Class.forName(LIBRARY);
                return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DB_NAME, USERNAME, PASSWORD);
            } catch (Exception e) {
                Log.e("Error on Connection", e.getMessage());
                return null;
            }
        }

        public void closeConn() {
            try {
                stmt.close();
            } catch (Exception e) {
                /* ignored */
            }
            try {
                conn.close();
            } catch (Exception e) { /* ignored */ }
        }
    }
}
