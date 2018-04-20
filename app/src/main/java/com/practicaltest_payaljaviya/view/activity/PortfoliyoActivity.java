package com.practicaltest_payaljaviya.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practicaltest_payaljaviya.R;
import com.practicaltest_payaljaviya.common.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PortfoliyoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txto2Class)
    TextView txto2Class;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.txtonHi)
    TextView txtonHi;
    @BindView(R.id.txtswami)
    TextView txtswami;
    @BindView(R.id.liner_main)
    LinearLayout linerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfoliyo);
        ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    @OnClick({R.id.txto2Class, R.id.txtonHi, R.id.txtswami})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txto2Class:
                if (Utils.isConnectingToInternet(PortfoliyoActivity.this)) {
                    Intent intentO2 = new Intent(Intent.ACTION_VIEW, Uri.parse(txto2Class.getText().toString().trim()));
                    startActivity(intentO2);
                } else {
                    Snackbar snackbar = Snackbar.make(linerMain, "Oops!! No Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                break;
            case R.id.txtonHi:
                if (Utils.isConnectingToInternet(PortfoliyoActivity.this)) {
                    Intent intentOnHi = new Intent(Intent.ACTION_VIEW, Uri.parse(txtonHi.getText().toString().trim()));
                    startActivity(intentOnHi);
                } else {
                    Snackbar snackbar = Snackbar.make(linerMain, "Oops!! No Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                break;
            case R.id.txtswami:
                if (Utils.isConnectingToInternet(PortfoliyoActivity.this)) {
                    Intent intentSwami = new Intent(Intent.ACTION_VIEW, Uri.parse(txtswami.getText().toString().trim()));
                    startActivity(intentSwami);
                } else {
                    Snackbar snackbar = Snackbar.make(linerMain, "Oops!! No Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                break;
        }
    }
}
