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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class add_list extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextInputLayout layout_employeeID;
    TextInputLayout layout_employeeName;
    TextInputLayout layout_borrowDate;
    TextInputLayout layout_returnDate;
    TextInputLayout layout_remark;
    TextInputLayout layout_type;
    EditText ETFinalBorrowDate;
    EditText ETFinalReturnDate;
    ImageView backButton;
    Spinner mSpinner;

    private int[] thumdriveID;
    private String[] thumbdriveName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        layout_employeeID = findViewById(R.id.textInputEmployeeID);
        layout_employeeName = findViewById(R.id.textInputEmployeeName);
        layout_borrowDate = findViewById(R.id.textInputBorrowDate);
        layout_returnDate = findViewById(R.id.textInputReturnDate);
        layout_remark = findViewById(R.id.textInputRemark);
        layout_type = findViewById(R.id.textInputThumbdriveType);
        ETFinalBorrowDate = findViewById(R.id.etBorrowDate);
        ETFinalReturnDate = findViewById(R.id.etReturnDate);
        backButton = findViewById(R.id.btnBack);
        mSpinner = findViewById(R.id.spinnerThumbdriveType);

        mSpinner.setOnItemSelectedListener(this);
        new Background(Background.FETCH_THUMBDRIVE).execute();

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
        new DatePickerDialog(add_list.this, dateSetListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
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
                    if (borrowDate.isEmpty()){
                        layout_returnDate.setError("Please select a date for borrow date first!");
                    } else if (simpleDateFormat.parse(borrowDate).before(simpleDateFormat.parse(date)) || simpleDateFormat.parse(borrowDate).equals(simpleDateFormat.parse(date))){
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
        new DatePickerDialog(add_list.this, dateSetListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, thumbdriveName[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nothing to do
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

    public void btnAddList_onClicked(View view) {
        if (!validateEmployeeID() | !validateEmployeeName() | !validateBorrowDate()){
            return;
        } else {
            String empIDInput = layout_employeeID.getEditText().getText().toString().trim();
            String empNameInput = layout_employeeName.getEditText().getText().toString().trim();
            String borrowDateInput = layout_borrowDate.getEditText().getText().toString().trim();
            String returnDateInput = layout_returnDate.getEditText().getText().toString().trim();
            String remarkInput = layout_remark.getEditText().getText().toString().trim();
            String driveTypeInput = mSpinner.getSelectedItem().toString().trim();

            Background bg = new Background(Background.INSERT_LIST);
            bg.execute(empIDInput, empNameInput, borrowDateInput, returnDateInput, remarkInput, driveTypeInput);
        }
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        public static final int FETCH_THUMBDRIVE = 30;
        public static final int INSERT_LIST = 31;

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
                    case FETCH_THUMBDRIVE:
                        result.last();
                        int totalRow = result.getRow();
                        result.first();
                        thumdriveID = new int[totalRow];
                        thumbdriveName = new String[totalRow];
                        for (int i = 0; i < totalRow; i++) {
                            thumdriveID[i] = result.getInt(1);
                            thumbdriveName[i] = result.getString(2);
                            result.next();
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, thumbdriveName);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinner.setAdapter(arrayAdapter);
                        break;

                    case INSERT_LIST:
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(add_list.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(add_list.this);
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
                String query = "";
                switch (this.method) {
                    case FETCH_THUMBDRIVE:
                        query = "SELECT id, name FROM thumbdrive_type";
                        stmt = conn.prepareStatement(query);
                        return stmt.executeQuery();

                    case INSERT_LIST:
                        query = "insert into borrow_list (employee_id, employee_name, borrow_date, " +
                                "return_date, remark, thumbdrive_type) values (?, ?, ?, ?, ?, ?)";
                        stmt = conn.prepareStatement(query);
                        stmt.setInt(1, Integer.parseInt(strings[0]));
                        stmt.setString(2, strings[1]);
                        stmt.setString(3, strings[2]);
                        stmt.setString(4, strings[3]);
                        stmt.setString(5, strings[4]);
                        stmt.setString(6, strings[5]);
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
