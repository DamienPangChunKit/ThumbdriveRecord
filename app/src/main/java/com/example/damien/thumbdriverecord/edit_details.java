package com.example.damien.thumbdriverecord;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class edit_details extends AppCompatActivity {
    TextInputLayout layout_employeeID;
    TextInputLayout layout_employeeName;
    TextInputLayout layout_borrowDate;
    TextInputLayout layout_returnDate;
    TextInputLayout layout_remark;
    EditText ETFinalEmployeeID;
    EditText ETFinalEmployeeName;
    EditText ETFinalBorrowDate;
    EditText ETFinalReturnDate;
    EditText ETFinalRemark;
    ImageView backButton;

    private String finalID;
    private String finalEmployeeID;
    private String finalEmployeeName;
    private String finalBorrowDate;
    private String finalReturnDate;
    private String finalRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);

        layout_employeeID = findViewById(R.id.textInputEmployeeID);
        layout_employeeName = findViewById(R.id.textInputEmployeeName);
        layout_borrowDate = findViewById(R.id.textInputBorrowDate);
        layout_returnDate = findViewById(R.id.textInputReturnDate);
        layout_remark = findViewById(R.id.textInputRemark);
        ETFinalEmployeeID = findViewById(R.id.etEmployeeID);
        ETFinalEmployeeName = findViewById(R.id.etEmployeeName);
        ETFinalBorrowDate = findViewById(R.id.etBorrowDate);
        ETFinalReturnDate = findViewById(R.id.etReturnDate);
        ETFinalRemark = findViewById(R.id.etRemark);
        backButton = findViewById(R.id.btnBack);

        Intent i = getIntent();
        finalID = i.getStringExtra("finalID");
        finalEmployeeID = i.getStringExtra("finalEmployeeID");
        finalEmployeeName = i.getStringExtra("finalEmployeeName");
        finalBorrowDate = i.getStringExtra("finalBorrowDate");
        finalReturnDate = i.getStringExtra("finalReturnDate");
        finalRemark = i.getStringExtra("finalRemark");

        ETFinalEmployeeID.setText(finalEmployeeID);
        ETFinalEmployeeName.setText(finalEmployeeName);
        ETFinalBorrowDate.setText(finalBorrowDate);
        ETFinalReturnDate.setText(finalReturnDate);
        ETFinalRemark.setText(finalRemark);

        ETFinalBorrowDate.setInputType(InputType.TYPE_NULL);
        ETFinalBorrowDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ETFinalBorrowDate.clearFocus();
                if (!hasFocus){
                    return;
                }
                showBorrowDateDialog(ETFinalBorrowDate);
            }
        });

        ETFinalReturnDate.setInputType(InputType.TYPE_NULL);
        ETFinalReturnDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ETFinalReturnDate.clearFocus();
                if (!hasFocus){
                    return;
                }
                showReturnDateDialog(ETFinalReturnDate);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateEmployeeID() {
        String empIDInput = layout_employeeID.getEditText().getText().toString().trim();

        if (empIDInput.isEmpty()){
            layout_employeeID.setError("This field cannot be empty!");
            return false;
        } else {
            layout_employeeID.setError(null);
            return true;
        }
    }

    private boolean validateEmployeeName() {
        String empNameInput = layout_employeeName.getEditText().getText().toString().trim();

        if (empNameInput.isEmpty()){
            layout_employeeName.setError("This field cannot be empty!");
            return false;
        } else {
            layout_employeeName.setError(null);
            return true;
        }
    }

    private boolean validateBorrowDate() {
        String borrowDateInput = layout_borrowDate.getEditText().getText().toString().trim();

        if (borrowDateInput.isEmpty()){
            layout_borrowDate.setError("This field cannot be empty!");
            return false;
        } else {
            layout_borrowDate.setError(null);
            return true;
        }
    }

    public void btnSaveDetails_onClicked(View view) {
        if (!validateEmployeeID() | !validateEmployeeName() | !validateBorrowDate()){
            return;
        } else {
            String getEmployeeID = layout_employeeID.getEditText().getText().toString().trim();
            String getEmployeeName = layout_employeeName.getEditText().getText().toString().trim();
            String getBorrowDate = layout_borrowDate.getEditText().getText().toString().trim();
            String getReturnDate = layout_returnDate.getEditText().getText().toString().trim();
            String getRemarkEmployee = layout_remark.getEditText().getText().toString().trim();

            if (getReturnDate.isEmpty()){
                getReturnDate = null;
            }

            if (getRemarkEmployee.isEmpty()){
                getRemarkEmployee = null;
            }

            Background bg = new Background();
            bg.execute(getEmployeeID, getEmployeeName, getBorrowDate, getReturnDate, getRemarkEmployee, finalID);
        }
    }

    private void showBorrowDateDialog(final EditText ETFinalBorrowDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(calendar.getTime());
                String returnDate = layout_returnDate.getEditText().getText().toString();

                try {
                    if (simpleDateFormat.parse(returnDate).before(simpleDateFormat.parse(date))){
                        ETFinalReturnDate.setText("");
                        layout_returnDate.setError("Remove due to borrow date selected is later than return date!");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ETFinalBorrowDate.setText(date);
                layout_borrowDate.setError(null);
            }
        };
        new DatePickerDialog(edit_details.this, dateSetListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
    }

    private void showReturnDateDialog(final EditText ETFinalReturnDate){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(calendar.getTime());

                String borrowDate = layout_borrowDate.getEditText().getText().toString();
                try {
                    if (simpleDateFormat.parse(borrowDate).before(simpleDateFormat.parse(date)) || simpleDateFormat.parse(borrowDate).equals(simpleDateFormat.parse(date))){
                        ETFinalReturnDate.setText(date);
                        layout_returnDate.setError(null);
                    } else{
                        ETFinalReturnDate.setText("");
                        layout_returnDate.setError("Return date must be later than borrow date!");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        new DatePickerDialog(edit_details.this, dateSetListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

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
                Toast.makeText(edit_details.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(edit_details.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
            progressDialog.show();
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            conn = connectDB();
            ResultSet result = null;

            if (conn == null) {
                return null;
            }
            try {
                String query = "UPDATE borrow_list SET employee_id = ?, employee_name = ?, borrow_date = ?, return_date = ?, remark = ? WHERE id = ?";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(strings[0]));
                stmt.setString(2, strings[1]);
                stmt.setString(3, strings[2]);
                stmt.setString(4, strings[3]);
                stmt.setString(5, strings[4]);
                stmt.setInt(6, Integer.parseInt(strings[5]));
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
