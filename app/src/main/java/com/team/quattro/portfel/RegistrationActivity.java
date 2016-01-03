package com.team.quattro.portfel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class RegistrationActivity extends ActionBarActivity {

    private Button btnRegister;
    private EditText login;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        btnRegister =(Button) findViewById(R.id.btnRegister);
        login = (EditText) findViewById(R.id.editTextLogin);
        password = (EditText) findViewById(R.id.editTextPassw);
        btnRegister.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strLogin = login.getText().toString();
                String strPassword = password.getText().toString();
                new UserRegisterTask(strLogin, strPassword).execute();
            }
        });

    }


    public void Alert (String title, String errorMessage, final Integer status)
    {
        new AlertDialog.Builder(RegistrationActivity.this)
                .setTitle(title)
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (status == 0) {
                            Intent intentLogin = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intentLogin);
                            finish();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public ParserStatus parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

        int eventType = parser.getEventType();
        ParserStatus result = new ParserStatus();

        while( eventType!= XmlPullParser.END_DOCUMENT) {
            String name = null;

            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    if ( name.equals("status")) {
                        result.status = parser.nextText();
                        break;
                    }
                    if ( name.equals("login")) {
                        result.login = parser.nextText();
                        break;
                    }
                    if ( name.equals("restKey")) {
                        result.restKey = parser.nextText();
                        break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
        return result;
    }

    public ParserStatus sendAndParseHTTP (String urlString, EnumClass.noYes authentication, String restKey)
    {
        InputStream in = null;
        ParserStatus parserStatusVO = null;
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
            parserStatusVO = parseXML(parser);

            in.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            urlConnection.disconnect();
        }
        return parserStatusVO;
    }
    public class UserRegisterTask extends AsyncTask<Void, Void, ParserStatus> {

        private final String mLogin;
        private final String mPassword;

        UserRegisterTask(String login, String password) {
            mLogin = login;
            mPassword = password;
        }

        @Override
        protected ParserStatus doInBackground(Void... params) {

            String serverAddress = getResources().getString(R.string.serverAddress);
            List <String> status = null;
            EnumClass.noYes authentication=null;
            //create user
            String urlString = serverAddress + "userServices/createUser?l=" + mLogin + "&p=" + mPassword;
            ParserStatus parserStatusVO = sendAndParseHTTP(urlString, authentication.NIE,null);
            String restKey = parserStatusVO.restKey;
            //create wallets assigned to user
            if (parserStatusVO != null)
                if (parserStatusVO.status.equals("0")) {
                    urlString = serverAddress + "walletService/createWallet?l=" + mLogin + "&typeCoin=" + "1";
                    parserStatusVO = sendAndParseHTTP(urlString, authentication.TAK, restKey);
                    urlString = serverAddress + "walletService/createWallet?l=" + mLogin + "&typeCoin=" + "2";
                    parserStatusVO = sendAndParseHTTP(urlString, authentication.TAK, restKey);
                    urlString = serverAddress + "walletService/createWallet?l=" + mLogin + "&typeCoin=" + "3";
                    parserStatusVO = sendAndParseHTTP(urlString, authentication.TAK, restKey);
            }
            return parserStatusVO;
        }


        @Override
        protected void onPostExecute(ParserStatus parserStatus) {

            //showProgress(false);
            if (parserStatus != null)
            {
                if (parserStatus.status.equals("0")) {
                Alert("Registration user", "User created. You will be redirected to login.",0 );
            } else
                Alert("Registration user", "The user cannot be created. Try enter other login.",1 );
            }
            else
                Alert("Registration user", "The user cannot be created. Try enter other login.",1 );
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
