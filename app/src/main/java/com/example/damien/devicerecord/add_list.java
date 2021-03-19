package com.example.damien.devicerecord;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class add_list extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    TextInputLayout layout_employeeID;
    TextInputLayout layout_employeeName;
    TextInputLayout layout_borrowDate;
    TextInputLayout layout_returnDate;
    EditText ETFinalEmployeeID;
    EditText ETFinalBorrowDate;
    EditText ETFinalReturnDate;
    ImageView backButton;
    Button btnScan;
    Spinner mSpinner;

    private int[] deviceID;
    private String[] deviceName;

    public static final int REQUEST_CODE9 = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        layout_employeeID = findViewById(R.id.textInputEmployeeID);
        layout_employeeName = findViewById(R.id.textInputEmployeeName);
        layout_borrowDate = findViewById(R.id.textInputBorrowDate);
        layout_returnDate = findViewById(R.id.textInputReturnDate);
        ETFinalEmployeeID = findViewById(R.id.etDevice);
        ETFinalBorrowDate = findViewById(R.id.etDeviceRemark);
        ETFinalReturnDate = findViewById(R.id.etReturnDate);
        backButton = findViewById(R.id.btnBack);
        btnScan = findViewById(R.id.btnScan);
        mSpinner = findViewById(R.id.spinnerDevice);

        mSpinner.setOnItemSelectedListener(this);
        new Background(Background.FETCH_DEVICE).execute();

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(today);
        String tomorrowDate = dateFormat.format(tomorrow);

        ETFinalBorrowDate.setText("" + todayDate);
        ETFinalReturnDate.setText("" + tomorrowDate);

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

        btnScan.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v.getId()==R.id.btnScan){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE9){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanEmpID = scanningResult.getContents();
            //String scanEmpName = scanningResult.getFormatName();
            ETFinalEmployeeID.setText("" + scanEmpID);
            //ETFinalReturnDate.setText("" + scanEmpName);
        }
        else{
            ETFinalEmployeeID.setText("");
//            Toast.makeText(this, "No scan data received!", Toast.LENGTH_SHORT).show();
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
                        ETFinalReturnDate.setText(null);
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
                        ETFinalReturnDate.setText(null);
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
        Toast.makeText(this, deviceName[position], Toast.LENGTH_SHORT).show();
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

    public void btnNext_onClicked(View view) {
        if (!validateEmployeeID() | !validateEmployeeName() | !validateBorrowDate()){
            return;
        } else {
            String empIDInput = layout_employeeID.getEditText().getText().toString().trim();
            String empNameInput = layout_employeeName.getEditText().getText().toString().trim();
            String borrowDateInput = layout_borrowDate.getEditText().getText().toString().trim();
            String returnDateInput = layout_returnDate.getEditText().getText().toString().trim();
            String deviceInput = mSpinner.getSelectedItem().toString().trim();

            if (returnDateInput.isEmpty()){
                returnDateInput = null;
            }

            Intent i = new Intent(add_list.this, add_list_next.class);
            i.putExtra("empIDInput", empIDInput);
            i.putExtra("empNameInput", empNameInput);
            i.putExtra("borrowDateInput", borrowDateInput);
            i.putExtra("returnDateInput", returnDateInput);
            i.putExtra("deviceInput", deviceInput);
            startActivityForResult(i, REQUEST_CODE9);
        }
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private final String LIBRARY = getString(R.string.db_library);
        private final String USERNAME = getString(R.string.db_username);
        private final String DB_NAME = getString(R.string.db_name);
        private final String PASSWORD = getString(R.string.db_password);
        private final String SERVER = getString(R.string.db_server);

        public static final int FETCH_DEVICE = 30;

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
                    case FETCH_DEVICE:
                        result.last();
                        int totalRow = result.getRow();
                        result.first();
                        deviceID = new int[totalRow];
                        deviceName = new String[totalRow];
                        for (int i = 0; i < totalRow; i++) {
                            deviceID[i] = result.getInt(1);
                            deviceName[i] = result.getString(2);
                            result.next();
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, deviceName);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinner.setAdapter(arrayAdapter);
                        break;
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(add_list.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(add_list.this);
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
                    case FETCH_DEVICE:
                        query = "SELECT id, name FROM device";
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
