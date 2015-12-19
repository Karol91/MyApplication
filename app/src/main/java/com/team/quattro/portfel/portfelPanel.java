package com.team.quattro.portfel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class portfelPanel extends ActionBarActivity {

    Button btn1Wallet;
    Button btn2Wallet;
    Button btn3Wallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfel_panel);
        btn1Wallet = (Button) findViewById(R.id.imgBtn1Wallet);
        btn2Wallet = (Button) findViewById(R.id.imgBtn2Wallet);
        btn3Wallet = (Button) findViewById(R.id.imgBtn3Wallet);
        btn1Wallet.setOnClickListener(myOnlyhandler);
        btn2Wallet.setOnClickListener(myOnlyhandler);
        btn3Wallet.setOnClickListener(myOnlyhandler);

    }
    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intentWallet;
            switch(v.getId()) {
                case R.id.imgBtn1Wallet:
                    intentWallet = new Intent(portfelPanel.this,wallet1.class);
                    startActivity(intentWallet);
                    break;
                case R.id.imgBtn2Wallet:
                    intentWallet = new Intent(portfelPanel.this,wallet2.class);
                    startActivity(intentWallet);
                    break;
                case R.id.imgBtn3Wallet:
                    intentWallet = new Intent(portfelPanel.this,wallet3.class);
                    startActivity(intentWallet);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portfel_panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
