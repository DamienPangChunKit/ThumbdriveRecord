package com.example.damien.devicerecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class edit_expenses extends AppCompatActivity {
    TextView TVexpensesID;
    TextView TVexpenses;
    EditText ETexpensesYear;
    EditText ETexpensesMonth;
    EditText ETexpenses1;
    EditText ETexpenses2;
    EditText ETexpenses3;
    EditText ETexpensesOther;
    TextView tvtvItem1;
    TextView tvtvItem2;
    TextView tvtvItem3;
    TextView tvtvOtherItem;
    ImageView backButton;

    private String expensesID;
    private String expenses;
    private String expensesItem1;
    private String expensesItem2;
    private String expensesItem3;
    private String expensesOtherItem;
    private String expensesMonth;
    private String expensesYear;
    private String displayItem1;
    private String displayItem2;
    private String displayItem3;
    private String displayOtherItem;
    private String[] checkExpenses;
    private String[] checkMonth;
    private int[] checkYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expenses);
        TVexpensesID = findViewById(R.id.tvExpensesServiceID);
        TVexpenses = findViewById(R.id.tvExpenses);
        ETexpensesYear = findViewById(R.id.etExpensesServiceYear);
        ETexpensesMonth = findViewById(R.id.etExpensesServiceMonth);
        ETexpenses1 = findViewById(R.id.etExpensesItem1);
        ETexpenses2 = findViewById(R.id.etExpensesItem2);
        ETexpenses3 = findViewById(R.id.etExpensesItem3);
        ETexpensesOther = findViewById(R.id.etExpensesOtherItem);
        tvtvItem1 = findViewById(R.id.tvtvitem1);
        tvtvItem2 = findViewById(R.id.tvtvitem2);
        tvtvItem3 = findViewById(R.id.tvtvitem3);
        tvtvOtherItem = findViewById(R.id.tvtvOtherItem);
        backButton = findViewById(R.id.btnBack);

        Intent i = getIntent();
        expensesID = i.getStringExtra("expensesID");
        expenses = i.getStringExtra("expenses");
        expensesItem1 = i.getStringExtra("expensesItem1");
        expensesItem2 = i.getStringExtra("expensesItem2");
        expensesItem3 = i.getStringExtra("expensesItem3");
        expensesOtherItem = i.getStringExtra("expensesOtherItem");
        expensesMonth = i.getStringExtra("expensesMonth");
        expensesYear = i.getStringExtra("expensesYear");
        displayItem1 = i.getStringExtra("displayItem1");
        displayItem2 = i.getStringExtra("displayItem2");
        displayItem3 = i.getStringExtra("displayItem3");
        displayOtherItem = i.getStringExtra("displayOtherItem");
        checkExpenses = i.getStringArrayExtra("checkExpenses");
        checkMonth = i.getStringArrayExtra("checkMonth");
        checkYear = i.getIntArrayExtra("checkYear");

        TVexpensesID.setText(expensesID + " ");
        TVexpenses.setText(expenses + " ");
        ETexpensesYear.setText(expensesYear + " ");
        ETexpensesMonth.setText(expensesMonth + " ");
        ETexpenses1.setText(expensesItem1 + " ");
        ETexpenses2.setText(expensesItem2 + " ");
        ETexpenses3.setText(expensesItem3 + " ");
        ETexpensesOther.setText(expensesOtherItem + " ");
        tvtvItem1.setText(" " + displayItem1 + " ");
        tvtvItem2.setText(" " + displayItem2 + " ");
        tvtvItem3.setText(" " + displayItem3 + " ");
        tvtvOtherItem.setText(" " + displayOtherItem + " ");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateYear(){
        String yearInput = ETexpensesYear.getText().toString().trim();

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        String todayDate = dateFormat.format(today);

        try {
            int num = Integer.parseInt(yearInput);
            Log.i("",num + "is a number");
        } catch (NumberFormatException e){
            Log.i("",yearInput + "is not a number");
            Toast.makeText(this, "Please input only number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (yearInput.isEmpty()){
            Toast.makeText(this, "Year cannot be blank!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(yearInput) < 2010) {
            Toast.makeText(this, "Year input was too old!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(yearInput) > Integer.parseInt(todayDate)) {
            Toast.makeText(this, "Year input was not reached yet!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMonth(){
        String monthInput = ETexpensesMonth.getText().toString().trim();

        if (monthInput.isEmpty()){
            Toast.makeText(this, "Month cannot be blank!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!monthInput.equals("January") & !monthInput.equals("February")
                & !monthInput.equals("March") & !monthInput.equals("April")
                & !monthInput.equals("May") & !monthInput.equals("June")
                & !monthInput.equals("July") & !monthInput.equals("August")
                & !monthInput.equals("September") & !monthInput.equals("October")
                & !monthInput.equals("November") & !monthInput.equals("December")
                & !monthInput.equals("1") & !monthInput.equals("01")
                & !monthInput.equals("2") & !monthInput.equals("02")
                & !monthInput.equals("3") & !monthInput.equals("03")
                & !monthInput.equals("4") & !monthInput.equals("04")
                & !monthInput.equals("5") & !monthInput.equals("05")
                & !monthInput.equals("6") & !monthInput.equals("06")
                & !monthInput.equals("7") & !monthInput.equals("07")
                & !monthInput.equals("8") & !monthInput.equals("08")
                & !monthInput.equals("9") & !monthInput.equals("09")
                & !monthInput.equals("10") & !monthInput.equals("10")
                & !monthInput.equals("11") & !monthInput.equals("11")
                & !monthInput.equals("12") & !monthInput.equals("12")){
            Toast.makeText(this, "Please input month properly!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateItem1(){
        String item1Input = ETexpenses1.getText().toString().trim();

        if (item1Input.isEmpty()){
            Toast.makeText(this, "Price cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Float num = Float.parseFloat(item1Input);
            Log.i("",num + "is a number");
        } catch (NumberFormatException e){
            Log.i("",item1Input + "is not a number");
            Toast.makeText(this, "Please input only number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Float.parseFloat(item1Input) < 0) {
            Toast.makeText(this, "Price cannot be negative!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateItem2(){
        String item2Input = ETexpenses2.getText().toString().trim();

        if (item2Input.isEmpty()){
            Toast.makeText(this, "Price cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Float num = Float.parseFloat(item2Input);
            Log.i("",num + "is a number");
        } catch (NumberFormatException e){
            Log.i("",item2Input + "is not a number");
            Toast.makeText(this, "Please input only number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Float.parseFloat(item2Input) < 0) {
            Toast.makeText(this, "Price cannot be negative!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateItem3(){
        String item3Input = ETexpenses3.getText().toString().trim();

        if (item3Input.isEmpty()){
            Toast.makeText(this, "Price cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Float num = Float.parseFloat(item3Input);
            Log.i("",num + "is a number");
        } catch (NumberFormatException e){
            Log.i("",item3Input + "is not a number");
            Toast.makeText(this, "Please input only number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Float.parseFloat(item3Input) < 0) {
            Toast.makeText(this, "Price cannot be negative!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateOtherItem(){
        String otherItemInput = ETexpensesOther.getText().toString().trim();

        if (otherItemInput.isEmpty()){
            Toast.makeText(this, "Price cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Float num = Float.parseFloat(otherItemInput);
            Log.i("",num + "is a number");
        } catch (NumberFormatException e){
            Log.i("",otherItemInput + "is not a number");
            Toast.makeText(this, "Please input only number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Float.parseFloat(otherItemInput) < 0) {
            Toast.makeText(this, "Price cannot be negative!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void btnSaveExpenses_onClicked(View view) {
        if (!validateYear() | !validateMonth() | !validateItem1() | !validateItem2() | !validateItem3() | !validateOtherItem()){
            return;
        } else {
            String getExpenses = TVexpenses.getText().toString().trim();
            String getYear = ETexpensesYear.getText().toString().trim();
            String getMonth = ETexpensesMonth.getText().toString().trim();
            String getItem1 = ETexpenses1.getText().toString().trim();
            String getItem2 = ETexpenses2.getText().toString().trim();
            String getItem3 = ETexpenses3.getText().toString().trim();
            String getOtherItem = ETexpensesOther.getText().toString().trim();

            if (getItem1.equals("")){
                getItem1 = String.valueOf(0);
            }

            if (getItem2.equals("")){
                getItem2 = String.valueOf(0);
            }

            if (getItem3.equals("")){
                getItem3 = String.valueOf(0);
            }

            if (getOtherItem.equals("")){
                getOtherItem = String.valueOf(0);
            }

            if (getMonth.equals("01") | getMonth.equals("1")){
                getMonth = "January";
            }

            if (getMonth.equals("02") | getMonth.equals("2")){
                getMonth = "February";
            }

            if (getMonth.equals("03") | getMonth.equals("3")){
                getMonth = "March";
            }

            if (getMonth.equals("04") | getMonth.equals("4")){
                getMonth = "April";
            }

            if (getMonth.equals("05") | getMonth.equals("5")){
                getMonth = "May";
            }

            if (getMonth.equals("06") | getMonth.equals("6")){
                getMonth = "June";
            }

            if (getMonth.equals("07") | getMonth.equals("7")){
                getMonth = "July";
            }

            if (getMonth.equals("08") | getMonth.equals("8")){
                getMonth = "August";
            }

            if (getMonth.equals("09") | getMonth.equals("9")){
                getMonth = "September";
            }

            if (getMonth.equals("10")){
                getMonth = "October";
            }

            if (getMonth.equals("11")){
                getMonth = "November";
            }

            if (getMonth.equals("12")){
                getMonth = "December";
            }

            if (getExpenses.equals(expenses) && getMonth.equals(expensesMonth) && getYear.equals(expensesYear)) {
                Float totalAmt = Float.parseFloat(getItem1) + Float.parseFloat(getItem2) + Float.parseFloat(getItem3) + Float.parseFloat(getOtherItem);

                Background bg = new Background();
                bg.execute(getItem1, getItem2, getItem3, getOtherItem, getMonth, getYear, String.valueOf(totalAmt), expensesID);

            } else {
                boolean pass = true;
                for (int i = 0; i < checkExpenses.length; i++ ) {

                    if (checkExpenses[i].equals(getExpenses) && checkMonth[i].equals(getMonth) && checkYear[i] == Integer.parseInt(getYear)) {
                        Toast.makeText(this, "Month and year selected of this expenses has been exist, please try another!", Toast.LENGTH_SHORT).show();
                        pass = false;
                        Log.e("Testing" + i, checkExpenses[i] + " and " + getExpenses + ", " + checkMonth[i] + " and " + getMonth + ", " + checkYear[i] + " and " + getYear);
                        break;
                    }
                }

                if (pass){
                    Float totalAmt = Float.parseFloat(getItem1) + Float.parseFloat(getItem2) + Float.parseFloat(getItem3) + Float.parseFloat(getOtherItem);

                    Background bg = new Background();
                    bg.execute(getItem1, getItem2, getItem3, getOtherItem, getMonth, getYear, String.valueOf(totalAmt), expensesID);
                }
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

        public Background() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);

            try {
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(edit_expenses.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            finally {
                progressDialog.hide();
                try { result.close(); } catch (Exception e) { /* ignored */ }
                closeConn();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(edit_expenses.this);
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
                String query = "UPDATE expenses SET item_1 = ?, item_2 = ?, item_3 = ?, other_item = ?, month = ?, year = ?, total = ? WHERE id = ?";
                stmt = conn.prepareStatement(query);
                stmt.setFloat(1, Float.parseFloat(strings[0]));
                stmt.setFloat(2, Float.parseFloat(strings[1]));
                stmt.setFloat(3, Float.parseFloat(strings[2]));
                stmt.setFloat(4, Float.parseFloat(strings[3]));
                stmt.setString(5, strings[4]);
                stmt.setInt(6, Integer.parseInt(strings[5]));
                stmt.setFloat(7, Float.parseFloat(strings[6]));
                stmt.setInt(8, Integer.parseInt(strings[7]));
                stmt.executeUpdate();
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
