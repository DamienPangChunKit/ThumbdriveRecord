package com.example.damien.devicerecord;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class balance_final_history extends AppCompatActivity {
    TextView TVBalanceHistoryID;
    TextView TVBalanceHistoryEmployeeID;
    TextView TVBalanceHistoryAmountAdd;
    TextView TVBalanceHistoryAmountDeduct;
    TextView TVBalanceHistoryRemark;
    ImageView backButton;

    private String balanceHistoryID;
    private String balanceHistoryEmployeeID;
    private String balanceHistoryAmountAdd;
    private String balanceHistoryAmountDeduct;
    private String balanceHistoryRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_final_history);

        TVBalanceHistoryID = findViewById(R.id.tvBalanceHistoryID);
        TVBalanceHistoryEmployeeID = findViewById(R.id.tvBalanceHistoryEmployeeID);
        TVBalanceHistoryAmountAdd = findViewById(R.id.tvBalanceHistoryAmountAdd);
        TVBalanceHistoryAmountDeduct = findViewById(R.id.tvBalanceHistoryAmountDeduct);
        TVBalanceHistoryRemark = findViewById(R.id.tvBalanceHistoryRemark);
        backButton = findViewById(R.id.btnBack);

        Intent i = getIntent();
        balanceHistoryID = i.getStringExtra("balanceHistoryID");
        balanceHistoryEmployeeID = i.getStringExtra("balanceHistoryEmployeeID");
        balanceHistoryAmountAdd = i.getStringExtra("balanceHistoryAmountAdd");
        balanceHistoryAmountDeduct = i.getStringExtra("balanceHistoryAmountDeduct");
        balanceHistoryRemark = i.getStringExtra("balanceHistoryRemark");

        TVBalanceHistoryID.setText(balanceHistoryID);
        TVBalanceHistoryEmployeeID.setText(balanceHistoryEmployeeID);
        TVBalanceHistoryRemark.setText(balanceHistoryRemark);

        if (balanceHistoryAmountAdd == null | balanceHistoryAmountAdd == ""){
            TVBalanceHistoryAmountAdd.setText(" - ");
            TVBalanceHistoryAmountDeduct.setText("RM " + balanceHistoryAmountDeduct);
        } else {
            TVBalanceHistoryAmountAdd.setText("RM " + balanceHistoryAmountAdd);
            TVBalanceHistoryAmountDeduct.setText(" - ");
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
