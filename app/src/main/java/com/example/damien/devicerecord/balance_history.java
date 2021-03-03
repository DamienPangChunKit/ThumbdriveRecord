package com.example.damien.devicerecord;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class balance_history extends AppCompatActivity {
    RecyclerView balanceHistoryRecyclerView;
    BalanceHistoryAdapter mBalanceHistoryAdapter;

    ImageView backButton;

    private int id;

    public static final int REQUEST_CODE9 = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_history);

        balanceHistoryRecyclerView = findViewById(R.id.BalanceHistoryRecycler);
        backButton = findViewById(R.id.btnBack);

        Intent i = getIntent();
        id = i.getIntExtra(login.EXTRA_ID, -1);

        Background bg = new Background(Background.FETCH_BALANCE_HISTORY);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        balanceHistoryRecyclerView.setLayoutManager(layoutManager);
        mBalanceHistoryAdapter = new BalanceHistoryAdapter(bg);
        balanceHistoryRecyclerView.setAdapter(mBalanceHistoryAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        public static final int FETCH_BALANCE_HISTORY = 5;

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
                    case FETCH_BALANCE_HISTORY:
                        query = "SELECT id, employee_id, amount_add, amount_deduct, remark FROM balance_history WHERE account_id = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setInt(1, id);
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

    private class BalanceHistoryAdapter extends RecyclerView.Adapter<BalanceHistoryAdapter.balanceHistoryHolder>{
        private LayoutInflater mInflater;
        private int itemCount;
        private Background bg;
        private ResultSet result;


        public BalanceHistoryAdapter(Background bg){
            this.bg = bg;
            updateResultSet();
            mInflater = LayoutInflater.from(balance_history.this);
        }

        class balanceHistoryHolder extends RecyclerView.ViewHolder{
            TextView tvBalanceHistoryID;
            TextView tvBalanceHistoryEmployeeID;
            TextView tvBalanceHistoryRemark;
            TableLayout mTableLayout;

            final BalanceHistoryAdapter mAdapter;

            public balanceHistoryHolder(@NonNull View itemView, BalanceHistoryAdapter adapter){
                super(itemView);
                tvBalanceHistoryID = (TextView) itemView.findViewById(R.id.tvDeviceDetailsID); // because of using the same row item with device details
                tvBalanceHistoryEmployeeID = (TextView) itemView.findViewById(R.id.tvDeviceDetailsEmployeeName);
                tvBalanceHistoryRemark = (TextView) itemView.findViewById(R.id.tvDeviceDetailsDate); // not data but is remark
                mTableLayout = (TableLayout) itemView.findViewById(R.id.device_details_layout_table);

                this.mAdapter = adapter;
            }
        }

        @NonNull
        @Override
        public balanceHistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(R.layout.device_details_row, viewGroup, false);
            return new balanceHistoryHolder(mItemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull balanceHistoryHolder balanceHistoryHolder, int position) {
            try {
                result.first();
                for (int i = 0; i < position; i++) {
                    result.next();
                }

                final String balanceHistoryID = result.getString(1);
                final String balanceHistoryEmployeeID = result.getString(2);
                final String balanceHistoryAmountAdd = result.getString(3);
                final String balanceHistoryAmountDeduct = result.getString(4);
                final String balanceHistoryRemark = result.getString(5);

                balanceHistoryHolder.tvBalanceHistoryID.setText(balanceHistoryID);
                balanceHistoryHolder.tvBalanceHistoryEmployeeID.setText(balanceHistoryEmployeeID);
                balanceHistoryHolder.tvBalanceHistoryRemark.setText(balanceHistoryRemark);

                balanceHistoryHolder.mTableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(balance_history.this, balance_final_history.class);
                        i.putExtra("balanceHistoryID", balanceHistoryID);
                        i.putExtra("balanceHistoryEmployeeID", balanceHistoryEmployeeID);
                        i.putExtra("balanceHistoryAmountAdd", balanceHistoryAmountAdd);
                        i.putExtra("balanceHistoryAmountDeduct", balanceHistoryAmountDeduct);
                        i.putExtra("balanceHistoryRemark", balanceHistoryRemark);
                        startActivityForResult(i, REQUEST_CODE9);
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
                bg = new Background(Background.FETCH_BALANCE_HISTORY);
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
