package com.team.quattro.portfel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpStatus;

import java.net.HttpURLConnection;
import java.net.URL;


public class StartMenu extends ActionBarActivity {

    final Context context = this;
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
        new ConnectedToServer().execute();
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.imgBtnLogin:
                    Intent intentLogin = new Intent(StartMenu.this,LoginActivity.class);
                    startActivity(intentLogin);
                    break;
                case R.id.imgBtnRegistration:
                    Intent intentRegistration = new Intent(StartMenu.this,RegistrationActivity.class);
                    startActivity(intentRegistration);
                    break;
            }
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public class ConnectedToServer extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;

            try {
                URL myUrl = new URL(getResources().getString(R.string.serverAddress) + "typeCoinService/getCoin?name=test");
                urlConnection = (HttpURLConnection) myUrl.openConnection();
                Integer code = urlConnection.getResponseCode();
                if (code >= HttpStatus.SC_BAD_REQUEST)
                    return false;
                else
                    return true;

            } catch (Exception e) {
                return false;
            } finally {
                urlConnection.disconnect();
            }

        }
        @Override
        protected void onPostExecute(Boolean isConnected) {
            if (!isConnected)
                if(CheckInternet(context)) {
                    Alert("Connect Error to server", "Cannot connect to server");
                }
                else {
                    Alert("Connect Error", "Check Internet connection");

                }
        }
    }
    public void Alert (String title, String errorMessage)
    {
        new AlertDialog.Builder(StartMenu.this)
                .setTitle(title)
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static boolean CheckInternet(Context context)
    {
        ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi.isConnected() || mobile.isConnected();
    }

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
