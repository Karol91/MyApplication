package com.team.quattro.portfel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;



public class StartMenu extends ActionBarActivity {

    Button btnLogin;
    Button btnRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
        btnLogin = (Button) findViewById(R.id.imgBtnLogin);
        btnRegistration =(Button) findViewById(R.id.imgBtnRegistration);
        btnLogin.setOnClickListener(myOnlyhandler);
        btnRegistration.setOnClickListener(myOnlyhandler);
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.imgBtnLogin:
                    Intent intentLogin = new Intent(StartMenu.this,LoginActivity.class);
                    startActivity(intentLogin);
                    break;
                case R.id.imgBtnRegistration:
                    // it was the second button
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_menu, menu);
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
