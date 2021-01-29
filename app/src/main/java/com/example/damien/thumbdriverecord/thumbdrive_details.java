package com.example.damien.thumbdriverecord;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class thumbdrive_details extends AppCompatActivity {
    RecyclerView thumbDriveDetailsRV;
    thumbDriveDetailsAdapter mAdapter;

    TextView tvThumbdriveType;
    ImageView backButton;
    TableLayout mTableLayout;

    private int thumbdriveID;
    private String thumbdriveName;

    public static final int REQUEST_CODE3 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbdrive_details);

        backButton = findViewById(R.id.btnBack);
        tvThumbdriveType = findViewById(R.id.tvThumndriveType);
        mTableLayout = findViewById(R.id.layout_table);

        Intent i = getIntent();
        thumbdriveID = i.getIntExtra("thumbdriveID", -1);
        thumbdriveName = i.getStringExtra("thumbdriveName");

        tvThumbdriveType.setText(thumbdriveName);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Background bg = new Background(Background.FETCH_DETAILS);
        thumbDriveDetailsRV = (RecyclerView) findViewById(R.id.thumbdriveDetailsRecycler);
        mAdapter = new thumbDriveDetailsAdapter(bg);
        thumbDriveDetailsRV.setAdapter(mAdapter);
        thumbDriveDetailsRV.setLayoutManager(new LinearLayoutManager(thumbdrive_details.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE3){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt;
        private int method;

        public static final int FETCH_DETAILS = 1;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            conn = connectDB();
            ResultSet result = null;

            if (conn == null) {
                return null;
            }
            try {
                String query;
                switch(method){
                    case FETCH_DETAILS:
                        query = "SELECT id, employee_id, employee_name, borrow_date, return_date, remark FROM borrow_list WHERE thumbdrive_type = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, thumbdriveName);
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

    private class thumbDriveDetailsAdapter extends RecyclerView.Adapter<thumbDriveDetailsAdapter.thumbdriveDetailsHolder>{
        private LayoutInflater mInflater;
        private int itemCount;
        private Background bg;
        private ResultSet result;


        public thumbDriveDetailsAdapter(Background bg){
            this.bg = bg;
            updateResultSet();
            mInflater = LayoutInflater.from(thumbdrive_details.this);
        }

        class thumbdriveDetailsHolder extends RecyclerView.ViewHolder{
            TextView tvThumbdriveDetailsID;
            TextView tvThumbdriveDetailsName;
            TextView tvthumbdriveDetailsDate;
            TableLayout mTableLayout;

            final thumbDriveDetailsAdapter mAdapter;

            public thumbdriveDetailsHolder(@NonNull View itemView, thumbDriveDetailsAdapter adapter){
                super(itemView);
                tvThumbdriveDetailsID = (TextView) itemView.findViewById(R.id.tvthumbdriveDetailsID);
                tvThumbdriveDetailsName = (TextView) itemView.findViewById(R.id.tvthumbdriveDetailsEmployeeName);
                tvthumbdriveDetailsDate = (TextView) itemView.findViewById(R.id.tvthumbdriveDetailsDate);
                mTableLayout = (TableLayout) itemView.findViewById(R.id.thumbdrive_details_layout_table);

                this.mAdapter = adapter;
            }
        }

        @NonNull
        @Override
        public thumbdriveDetailsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(R.layout.thumbdrive_details_row, viewGroup, false);
            return new thumbdriveDetailsHolder(mItemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull thumbdriveDetailsHolder thumbdriveDetailsHolder, int position) {
            try {
                result.first();
                for (int i = 0; i < position; i++) {
                    result.next();
                }

//                if (result.next()){
//                    mTableLayout.setVisibility(View.INVISIBLE);
//                }

                final String detailsID = result.getString(1);
                final String detailsEmployeeID = result.getString(2);
                final String detailsEmployeeName = result.getString(3);
                final String detailsBorrowDate = result.getString(4);
                final String detailsReturnDate = result.getString(5);
                final String detailsRemark = result.getString(6);

                thumbdriveDetailsHolder.tvThumbdriveDetailsID.setText(detailsID);
                thumbdriveDetailsHolder.tvThumbdriveDetailsName.setText(detailsEmployeeName);
                thumbdriveDetailsHolder.tvthumbdriveDetailsDate.setText(detailsBorrowDate);

                thumbdriveDetailsHolder.mTableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(thumbdrive_details.this, thumbdrive_final_details.class);
                        i.putExtra("detailsID", detailsID);
                        i.putExtra("detailsEmployeeID", detailsEmployeeID);
                        i.putExtra("detailsEmployeeName", detailsEmployeeName);
                        i.putExtra("detailsBorrowDate", detailsBorrowDate);
                        i.putExtra("detailsReturnDate", detailsReturnDate);
                        i.putExtra("detailsRemark", detailsRemark);
                        startActivityForResult(i, REQUEST_CODE3);
                    }
                });
            }
            catch (SQLException e) {
                Log.d("ERROR BIND VIEW", e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        private int getResultCount() {
            try {
                result.next();
                result.last();
                int count = result.getRow();
                result.first();
                return count;
            } catch (SQLException e) {

            }
            return 0;
        }

        public void updateResultSet() {
            try {
                bg.closeConn();
                bg = new Background(Background.FETCH_DETAILS);
                this.result = this.bg.execute().get();
                itemCount = getResultCount();
            } catch (ExecutionException e) {
                Log.e("ERROR EXECUTION", e.getMessage());
            } catch (InterruptedException e) {
                Log.e("ERROR INTERRUPTED", e.getMessage());
            }
        }
    }
}
