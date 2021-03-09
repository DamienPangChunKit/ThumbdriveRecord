package com.example.damien.devicerecord;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class manage_balance extends AppCompatActivity implements View.OnClickListener {
    RadioButton radioAdd;
    RadioButton radioDeduct;
    ImageView backButton;
    Button btnScan;
    EditText ETFinalEmployeeID;
    TextInputLayout layout_employeeID;
    TextInputLayout layout_amount;
    TextInputLayout layout_remark;

    private int id;
    private float totalBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_balance);

        radioAdd = findViewById(R.id.radioAdd);
        radioDeduct = findViewById(R.id.radioDeduct);
        backButton = findViewById(R.id.btnBack);
        btnScan = findViewById(R.id.btnScan);
        ETFinalEmployeeID = findViewById(R.id.etEmployeeID);
        layout_employeeID = findViewById(R.id.textInputEmployeeID);
        layout_amount = findViewById(R.id.textInputAmount);
        layout_remark = findViewById(R.id.textInputRemark);

        Intent a = getIntent();
        id = a.getIntExtra(login.EXTRA_ID, -1);
        totalBalance = a.getFloatExtra(login.EXTRA_BALANCE, 1);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnScan.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v.getId()==R.id.btnScan){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            ETFinalEmployeeID.setText("" + scanContent);
        }
        else{
            ETFinalEmployeeID.setText("");
//            Toast.makeText(this, "No scan data received!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRadioBtnClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radioAdd:
                if (checked)
                    Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();
                    break;
            case R.id.radioDeduct:
                if (checked)
                    Toast.makeText(this, "Deduct", Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    private boolean validateEmployeeID(){
        String empIDInput = layout_employeeID.getEditText().getText().toString().trim();

        if (empIDInput.isEmpty()){
            layout_employeeID.setError("This field cannot be empty!");
            return false;
        } else {
            layout_employeeID.setError(null);
            return true;
        }
    }

    private boolean validateAmount(){
        String amountInput = layout_amount.getEditText().getText().toString().trim();

        if (amountInput.isEmpty()){
            layout_amount.setError("This field cannot be empty!");
            return false;
        } else {
            layout_amount.setError(null);
            return true;
        }
    }

    private boolean validateMoney(){
        String amountInput = layout_amount.getEditText().getText().toString().trim();
        Float totalAmount = Float.parseFloat(amountInput);

        if (totalBalance < totalAmount && radioDeduct.isChecked()){
            Toast.makeText(this, "Please make sure you have enough money before manage balance!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRemark(){
        String remarkInput = layout_remark.getEditText().getText().toString().trim();

        if (remarkInput.isEmpty()){
            layout_remark.setError("This field cannot be empty!");
            return false;
        } else {
            layout_remark.setError(null);
            return true;
        }
    }

    public void btnConfirm_onClicked(View view) {
        if (!validateEmployeeID() || !validateAmount() || !validateMoney() || !validateRemark()){
            return;
        } else {
            openConfirmationDialog();
        }
    }

    private void openConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(manage_balance.this);
        builder.setTitle("Are you sure make the following changes?");
        builder.setMessage("Total balance will be change and cannot be undo once confirm!");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String empIDInput = layout_employeeID.getEditText().getText().toString().trim();
                String amountInput = layout_amount.getEditText().getText().toString().trim();
                String remarkInput = layout_remark.getEditText().getText().toString().trim();
                String addMoney = "";
                String deductMoney = "";
                Float amt = Float.parseFloat(amountInput);

                if (radioAdd.isChecked()){
                    totalBalance = totalBalance + amt;
                    addMoney = amountInput;
                    deductMoney = null;
                } else {
                    totalBalance = totalBalance - amt;
                    addMoney = null;
                    deductMoney = amountInput;
                }

                Background bg = new Background();
                bg.execute(empIDInput, addMoney, deductMoney, remarkInput, String.valueOf(totalBalance));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing to do here
            }
        });

        builder.setIcon(R.drawable.ic_payment);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt, stmt2;
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
                i.putExtra("TOTAL_BALANCE_AFTER_PAID", totalBalance);
                setResult(RESULT_OK, i);
                finish();
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(manage_balance.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(manage_balance.this);
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
                if (strings[1] == null){
                    String query = "INSERT into balance_history (employee_id, amount_deduct, remark, account_id) values (?, ?, ?, ?)";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, strings[0]);
                    stmt.setFloat(2, Float.parseFloat(strings[2]));
                    stmt.setString(3, strings[3]);
                    stmt.setInt(4, id);
                } else {
                    String query = "INSERT into balance_history (employee_id, amount_add, remark, account_id) values (?, ?, ?, ?)";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, strings[0]);
                    stmt.setFloat(2, Float.parseFloat(strings[1]));
                    stmt.setString(3, strings[3]);
                    stmt.setInt(4, id);
                }

                String query2 = "UPDATE account SET balance = ? WHERE id = ?";
                stmt2 = conn.prepareStatement(query2);
                stmt2.setFloat(1, Float.parseFloat(strings[4]));
                stmt2.setInt(2, id);

                stmt.executeUpdate();
                stmt2.executeUpdate();
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
