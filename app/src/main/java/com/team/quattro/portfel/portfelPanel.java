package com.team.quattro.portfel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class portfelPanel extends ActionBarActivity {

    Button btn1Wallet;
    Button btn2Wallet;
    Button btn3Wallet;
    TextView viewWelcome;
    User userVO =null;
    Wallets wallet1 = null;
    Wallets wallet2 = null;
    Wallets wallet3 = null;
    ArrayList<Wallets> walletsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfel_panel);
        viewWelcome = (TextView) findViewById(R.id.txtWelcome);
        btn1Wallet = (Button) findViewById(R.id.imgBtn1Wallet);
        btn2Wallet = (Button) findViewById(R.id.imgBtn2Wallet);
        btn3Wallet = (Button) findViewById(R.id.imgBtn3Wallet);
        btn1Wallet.setOnClickListener(buttonHandler);
        btn2Wallet.setOnClickListener(buttonHandler);
        btn3Wallet.setOnClickListener(buttonHandler);

        btn1Wallet.setEnabled(false);
        btn2Wallet.setEnabled(false);
        btn3Wallet.setEnabled(false);



        if (userVO==null)
        {
            Intent intent = getIntent();
            userVO = (User) getIntent().getSerializableExtra("user");

            viewWelcome.setText(getResources().getString(R.string.welcomeString)+ " " +userVO.login +", ");
            new getWallets().execute();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertLogout("Logout", "Do you want logout?");
        }
        return super.onKeyDown(keyCode, event);
        }


    View.OnClickListener buttonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intentWallet;
            switch(v.getId()) {
                case R.id.imgBtn1Wallet:
                    intentWallet = new Intent(portfelPanel.this,wallet1.class);
                    intentWallet.putExtra("user",userVO);
                    intentWallet.putExtra("wallet",wallet1);
                    startActivity(intentWallet);
                    finish();
                    break;
                case R.id.imgBtn2Wallet:
                    intentWallet = new Intent(portfelPanel.this,wallet1.class);
                    intentWallet.putExtra("user",userVO);
                    intentWallet.putExtra("wallet",wallet2);
                    startActivity(intentWallet);
                    finish();
                    break;
                case R.id.imgBtn3Wallet:
                    intentWallet = new Intent(portfelPanel.this,wallet1.class);
                    intentWallet.putExtra("user",userVO);
                    intentWallet.putExtra("wallet",wallet3);
                    startActivity(intentWallet);
                    finish();
                    break;
            }
        }
    };

    public void AlertLogout (String title, String errorMessage)
    {
        new AlertDialog.Builder(portfelPanel.this)
                .setTitle(title)
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentStartMenu = new Intent(portfelPanel.this, StartMenu.class);
                        startActivity(intentStartMenu);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public Wallets parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

        int eventType = parser.getEventType();
        Wallets wallet = new Wallets();
        while( eventType!= XmlPullParser.END_DOCUMENT) {
            String name = null;

            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    if ( name.equals("code")) {
                        wallet.code = parser.nextText();
                        break;
                    }
                    if ( name.equals("date")) {
                        wallet.date = parser.nextText();
                        break;
                    }
                    if ( name.equals("saldo")) {
                        wallet.saldo = Double.parseDouble(parser.nextText());
                        break;

                    }
                    if ( name.equals("type_coin_id")) {
                        wallet.typeCoinId = Integer.parseInt(parser.nextText());
                        break;
                    }
                    if ( name.equals("userLogin")) {
                        wallet.userLogin = parser.nextText();
                        break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
        return wallet;
    }

    public Wallets sendAndParseHTTP (String urlString, EnumClass.noYes authentication, String restKey)
    {
        InputStream in = null;
        Wallets walletsVO = null;
        HttpURLConnection urlConnection =null;
        try {

            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (authentication== EnumClass.noYes.TAK)
            {
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/xml");
                urlConnection.setRequestProperty("REST_KEY", restKey);
                urlConnection.setDoOutput(false);
            }
            Integer code =urlConnection.getResponseCode();
            if(code >= HttpStatus.SC_BAD_REQUEST)
                in = urlConnection.getErrorStream();
            else
                in = urlConnection.getInputStream();
        } catch (Exception e) {
            finish();

        }

        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            walletsVO = parseXML(parser);

            in.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            urlConnection.disconnect();
        }
        return walletsVO;
    }

    public class getWallets extends AsyncTask<Void, Void, ArrayList<Wallets> > {

        String restKey = userVO.restKey;
        String login = userVO.login;
        String serverAddress = getResources().getString(R.string.serverAddress);
        //String urlString = serverAddress + "walletService/getWallet?l=" + login+ "&typeCoin=1";
        InputStream in = null;
        EnumClass.noYes authentication = null;


        Wallets walletVO = null;
        String urlString = null;

        @Override
        protected ArrayList<Wallets> doInBackground(Void... params) {
            urlString = serverAddress + "walletService/getWallet?l=" + login+ "&typeCoin=1";
            wallet1 = sendAndParseHTTP(urlString, authentication.TAK, restKey);
            walletsList.add(wallet1);
            urlString = serverAddress + "walletService/getWallet?l=" + login+ "&typeCoin=2";
            wallet2 = sendAndParseHTTP(urlString, authentication.TAK, restKey);
            walletsList.add(wallet2);
            urlString = serverAddress + "walletService/getWallet?l=" + login+ "&typeCoin=3";
            wallet3 = sendAndParseHTTP(urlString, authentication.TAK, restKey);
            walletsList.add(wallet3);

            return walletsList;
        }
        protected void onPostExecute(ArrayList<Wallets> walletList) {
            btn1Wallet.setEnabled(true);
            btn2Wallet.setEnabled(true);
            btn3Wallet.setEnabled(true);
        }
        }

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
