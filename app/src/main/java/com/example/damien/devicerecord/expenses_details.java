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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class expenses_details extends AppCompatActivity {
    TextView TVexpensesID;
    TextView TVexpenses;
    TextView TVexpensesYear;
    TextView TVexpensesMonth;
    TextView TVexpenses1;
    TextView TVexpenses2;
    TextView TVexpenses3;
    TextView TVexpensesOther;
    TextView TVexpensesTotal;
    TextView tvtvItem1;
    TextView tvtvItem2;
    TextView tvtvItem3;
    TextView tvtvOtherItem;
    ImageView backButton;
    ImageView addExpenses;

    private String expensesID;
    private String expenses;
    private String expensesItem1;
    private String expensesItem2;
    private String expensesItem3;
    private String expensesOtherItem;
    private String expensesMonth;
    private String expensesYear;
    private String expensesTotal;
    private String displayItem1;
    private String displayItem2;
    private String displayItem3;
    private String displayOtherItem;
    private String[] checkExpenses;
    private String[] checkMonth;
    private int[] checkYear;

    public static final int REQUEST_CODE15 = 15;
    public static final int REQUEST_CODE16 = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_details);

        TVexpensesID = findViewById(R.id.tvExpensesServiceID);
        TVexpenses = findViewById(R.id.tvExpenses);
        TVexpensesYear = findViewById(R.id.tvExpensesServiceYear);
        TVexpensesMonth = findViewById(R.id.tvExpensesServiceMonth);
        TVexpenses1 = findViewById(R.id.tvExpensesItem1);
        TVexpenses2 = findViewById(R.id.tvExpensesItem2);
        TVexpenses3 = findViewById(R.id.tvExpensesItem3);
        TVexpensesOther = findViewById(R.id.tvExpensesOtherItem);
        TVexpensesTotal = findViewById(R.id.tvExpensesTotal);
        tvtvItem1 = findViewById(R.id.tvtvitem1);
        tvtvItem2 = findViewById(R.id.tvtvitem2);
        tvtvItem3 = findViewById(R.id.tvtvitem3);
        tvtvOtherItem = findViewById(R.id.tvtvOtherItem);
        backButton = findViewById(R.id.btnBack);
        addExpenses = findViewById(R.id.imgAddService);

        Intent i = getIntent();
        expensesID = i.getStringExtra("expensesID");
        expenses = i.getStringExtra("expenses");
        expensesItem1 = i.getStringExtra("expensesItem1");
        expensesItem2 = i.getStringExtra("expensesItem2");
        expensesItem3 = i.getStringExtra("expensesItem3");
        expensesOtherItem = i.getStringExtra("expensesOtherItem");
        expensesMonth = i.getStringExtra("expensesMonth");
        expensesYear = i.getStringExtra("expensesYear");
        expensesTotal = i.getStringExtra("expensesTotal");
        displayItem1 = i.getStringExtra("displayItem1");
        displayItem2 = i.getStringExtra("displayItem2");
        displayItem3 = i.getStringExtra("displayItem3");
        displayOtherItem = i.getStringExtra("displayOtherItem");

        new Background(Background.FETCH_EXPENSES).execute();

        TVexpensesID.setText(expensesID + " ");
        TVexpenses.setText(expenses + " ");
        TVexpensesYear.setText(expensesYear + " ");
        TVexpensesMonth.setText(expensesMonth + " ");
        TVexpenses1.setText(expensesItem1 + " ");
        TVexpenses2.setText(expensesItem2 + " ");
        TVexpenses3.setText(expensesItem3 + " ");
        TVexpensesOther.setText(expensesOtherItem + " ");
        TVexpensesTotal.setText(expensesTotal + " ");
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

        addExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(expenses_details.this, add_expenses.class);
                startActivityForResult(i, REQUEST_CODE15);
            }
        });

    }

    public void btnEditExpenses_onClicked(View view) {
        Intent i = new Intent(expenses_details.this, edit_expenses.class);
        i.putExtra("expensesID", expensesID);
        i.putExtra("expenses", expenses);
        i.putExtra("expensesItem1", expensesItem1);
        i.putExtra("expensesItem2", expensesItem2);
        i.putExtra("expensesItem3", expensesItem3);
        i.putExtra("expensesOtherItem", expensesOtherItem);
        i.putExtra("expensesMonth", expensesMonth);
        i.putExtra("expensesYear", expensesYear);
        i.putExtra("displayItem1", displayItem1);
        i.putExtra("displayItem2", displayItem2);
        i.putExtra("displayItem3", displayItem3);
        i.putExtra("displayOtherItem", displayOtherItem);
        i.putExtra("checkExpenses", checkExpenses);
        i.putExtra("checkMonth", checkMonth);
        i.putExtra("checkYear", checkYear);
        startActivityForResult(i, REQUEST_CODE16);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE15 | requestCode == REQUEST_CODE16){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private final String LIBRARY = getString(R.string.db_library);
        private final String USERNAME = getString(R.string.db_username);
        private final String DB_NAME = getString(R.string.db_name);
        private final String PASSWORD = getString(R.string.db_password);
        private final String SERVER = getString(R.string.db_server);

        public static final int FETCH_EXPENSES = 30;

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
                    case FETCH_EXPENSES:
                        result.last();
                        int totalRow = result.getRow();
                        result.first();
                        checkExpenses = new String[totalRow];
                        checkMonth = new String[totalRow];
                        checkYear = new int[totalRow];

                        for (int i = 0; i < totalRow; i++) {
                            checkExpenses[i] = result.getString(1);
                            checkMonth[i] = result.getString(2);
                            checkYear[i] = result.getInt(3);
                            result.next();
                        }

                        break;
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(expenses_details.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(expenses_details.this);
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
                    case FETCH_EXPENSES:
                        query = "SELECT expenses, month, year FROM expenses";
                        stmt = conn.prepareStatement(query);
                        return stmt.executeQuery();
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
