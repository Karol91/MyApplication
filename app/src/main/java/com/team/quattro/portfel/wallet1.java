package com.team.quattro.portfel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class wallet1 extends ActionBarActivity {

    final Context context = this;
    private Button btnPayment;
    private Button btnPayoff;
    private Button btnOperationHistory;
    private Button btnExchange;
    private TextView textValue;
    private EditText valueInput;
    private RadioButton radioBtnEx1;
    private RadioButton radioBtnEx2;
    private DatePicker datePickerStart;
    private DatePicker datePickerEnd;

    User userVO =null;
    Double value;
    Wallets wallet = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet1);
        btnPayment = (Button) findViewById(R.id.imgBtnPayment);
        btnPayoff = (Button) findViewById(R.id.imgBtnWithdraw);
        btnOperationHistory = (Button) findViewById(R.id.imgBtnStory);
        btnExchange = (Button) findViewById(R.id.imgBtnExchange);
        textValue = (TextView) findViewById(R.id.txtViewBalance);
        btnPayment.setOnClickListener(buttonPaymentHandler);
        btnPayoff.setOnClickListener(buttonPayoffHandler);
        btnExchange.setOnClickListener(buttonExchangeHandler);
        btnOperationHistory.setOnClickListener(buttonOperationHistoryHandler);

        Intent intent = getIntent();
        userVO = (User) getIntent().getSerializableExtra("user");
        wallet = (Wallets) getIntent().getSerializableExtra("wallet");
        textValue.setText(getResources().getString(R.string.balanceAccount)+ ": " + String.format("%.2f",wallet.saldo) + " "+ typeWallets(wallet.typeCoinId));
        //setTittle();
    }

    private View.OnClickListener buttonPayoffHandler =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater li = LayoutInflater.from(context);
            View payoffView = li.inflate(R.layout.payoff_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder.setView(payoffView);
            valueInput = (EditText) payoffView
                    .findViewById(R.id.editTextValue);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    new PayoffTask(userVO.login).execute();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    };

    private View.OnClickListener buttonPaymentHandler =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater li = LayoutInflater.from(context);
            View paymentView = li.inflate(R.layout.payment_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder.setView(paymentView);
            valueInput = (EditText) paymentView
                    .findViewById(R.id.editTextValue);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    new PaymentTask(userVO.login).execute();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    };

    private View.OnClickListener buttonOperationHistoryHandler =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater li = LayoutInflater.from(context);
            View operationHistoryView = li.inflate(R.layout.history_operation_dialog, null);
            radioBtnEx1 = (RadioButton) operationHistoryView.findViewById(R.id.radioBtnExchange);
            radioBtnEx2 = (RadioButton) operationHistoryView.findViewById(R.id.radioBtnExchange2);
            datePickerStart = (DatePicker) operationHistoryView.findViewById(R.id.datePickerStart);
            datePickerEnd = (DatePicker) operationHistoryView.findViewById(R.id.datePickerEnd);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder.setView(operationHistoryView);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Show history",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    int dayStart = datePickerStart.getDayOfMonth();
                                    int monthStart = datePickerStart.getMonth();
                                    String yearStart = String.valueOf(datePickerStart.getYear());
                                    String dateStart = yearStart+"-"+checkDigit(monthStart+1)+"-"+checkDigit(dayStart);

                                    int dayEnd = datePickerEnd.getDayOfMonth();
                                    int  monthEnd = datePickerEnd.getMonth();
                                    String yearEnd = String.valueOf(datePickerEnd.getYear());
                                    String dateEnd = yearEnd+"-"+checkDigit(monthEnd+1)+"-"+checkDigit(dayEnd);

                                    Intent intentHistoryOperation = new Intent(wallet1.this,HistoryOperationActivity.class);
                                    if (!dateStart.isEmpty() && !dateEnd.isEmpty() && userVO!=null && wallet!=null) {
                                        intentHistoryOperation.putExtra("date_start", dateStart);
                                        intentHistoryOperation.putExtra("date_end", dateEnd);
                                        intentHistoryOperation.putExtra("user", userVO);
                                        intentHistoryOperation.putExtra("wallet", wallet);

                                        startActivity(intentHistoryOperation);
                                        finish();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    };

    private View.OnClickListener buttonExchangeHandler =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater li = LayoutInflater.from(context);
            View exchangeView = li.inflate(R.layout.exchange_dialog, null);
            radioBtnEx1 = (RadioButton) exchangeView.findViewById(R.id.radioBtnExchange);
            radioBtnEx2 = (RadioButton) exchangeView.findViewById(R.id.radioBtnExchange2);
            if (wallet.typeCoinId==1) {
                radioBtnEx1.setText("LiteCoin");
                radioBtnEx2.setText("Nubits");
            }
            if (wallet.typeCoinId==2) {
                radioBtnEx1.setText("BitCoin");
                radioBtnEx2.setText("Nubits");
            }
            if (wallet.typeCoinId==3) {
                radioBtnEx1.setText("BitCoin");
                radioBtnEx2.setText("LiteCoin");
            }
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder.setView(exchangeView);
            valueInput = (EditText) exchangeView
                    .findViewById(R.id.editTextValue);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    new ExchangeTask(userVO.login).execute();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    };

    public String checkDigit(int number)
    {
        return number<=9?"0"+number:String.valueOf(number);
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

    public class PaymentTask extends AsyncTask<Void, Void, ParserStatus> {

        private final String mLogin;


        PaymentTask(String login) {
            mLogin = login;
        }

        @Override
        protected ParserStatus doInBackground(Void... params) {

            String serverAddress = getResources().getString(R.string.serverAddress);
            List<String> status = null;
            EnumClass.noYes authentication=null;
            Integer typeCoin = wallet.typeCoinId;

            String valueStr =valueInput.getText().toString();
            if (valueStr ==null || valueStr.isEmpty())
                value=0.0;
            else
                value = Double.parseDouble(valueStr);


            String urlString = serverAddress + "operationService/payment?l=" + mLogin + "&typeCoin=" + typeCoin + "&value="+ value;
            ParserStatus parserStatusVO = null;
            String restKey = userVO.restKey;
            parserStatusVO = sendAndParseHTTP(urlString, authentication.TAK,restKey);
            return parserStatusVO;
        }


        @Override
        protected void onPostExecute(ParserStatus parserStatus) {

            //showProgress(false);
            if (parserStatus != null) {
                if (parserStatus.status.equals("0")) {
                    Toast.makeText(wallet1.this,"The payment was made",Toast.LENGTH_LONG).show();
                    wallet.saldo = wallet.saldo +value;
                    textValue.setText(getResources().getString(R.string.balanceAccount)+ ": " +String.format("%.2f",wallet.saldo) );
                }
                else
                    Toast.makeText(wallet1.this,"Something went wrong. Try again later.",Toast.LENGTH_LONG);
            }
        }
    }

    public class PayoffTask extends AsyncTask<Void, Void, ParserStatus> {

        private final String mLogin;


        PayoffTask(String login) {
            mLogin = login;
        }

        @Override
        protected ParserStatus doInBackground(Void... params) {

            String serverAddress = getResources().getString(R.string.serverAddress);
            List<String> status = null;
            EnumClass.noYes authentication=null;
            Integer typeCoin = wallet.typeCoinId;

            String valueStr =valueInput.getText().toString();
            if (valueStr ==null || valueStr.isEmpty())
                value=0.0;
            else
                value = Double.parseDouble(valueStr);


                String urlString = serverAddress + "operationService/payoff?l=" + mLogin + "&typeCoin=" + typeCoin + "&value=" + value;
                ParserStatus parserStatusVO = null;
                String restKey = userVO.restKey;
            if (wallet.saldo >= value) {
                parserStatusVO = sendAndParseHTTP(urlString, authentication.TAK, restKey);
            }
            else {
                parserStatusVO = new ParserStatus();
                parserStatusVO.status = "2";
            }
            return parserStatusVO;
        }


        @Override
        protected void onPostExecute(ParserStatus parserStatus) {

            //showProgress(false);
            if (parserStatus != null) {
                if (parserStatus.status.equals("0")) {
                    Toast.makeText(wallet1.this,"The payoff was made.",Toast.LENGTH_LONG).show();
                    wallet.saldo = wallet.saldo - value;
                    textValue.setText(getResources().getString(R.string.balanceAccount)+ ": " +String.format("%.2f",wallet.saldo) );
                }
                else if (parserStatus.status.equals("2"))
                    Toast.makeText(wallet1.this,"You don't have enough money to make this operation.",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(wallet1.this,"Something went wrong. Try again later.",Toast.LENGTH_LONG);
            }
        }
    }

    public class ExchangeTask extends AsyncTask<Void, Void, ParserStatus> {

        private final String mLogin;


        ExchangeTask(String login) {
            mLogin = login;
        }

        @Override
        protected ParserStatus doInBackground(Void... params) {

            String serverAddress = getResources().getString(R.string.serverAddress);
            List<String> status = null;
            EnumClass.noYes authentication=null;
            Integer typeCoin = wallet.typeCoinId;

            String valueStr =valueInput.getText().toString();
            if (valueStr ==null || valueStr.isEmpty())
                value=0.0;
            else
                value = Double.parseDouble(valueStr);

            Integer toCoin = whichChecked();
            String urlString = serverAddress + "operationService/exchange?l=" + mLogin + "&fromCoin=" + typeCoin + "&toCoin="+ toCoin +"&value=" + value;
            ParserStatus parserStatusVO = null;
            String restKey = userVO.restKey;
            if (wallet.saldo >= value) {
                parserStatusVO = sendAndParseHTTP(urlString, authentication.TAK, restKey);
            }
            else {
                parserStatusVO = new ParserStatus();
                parserStatusVO.status = "2";
            }
            return parserStatusVO;
        }


        @Override
        protected void onPostExecute(ParserStatus parserStatus) {

            if (parserStatus != null) {
                if (parserStatus.status.equals("0")) {
                    Toast.makeText(wallet1.this,"The exchange was made.",Toast.LENGTH_LONG).show();
                    wallet.saldo = wallet.saldo - value;
                    textValue.setText(getResources().getString(R.string.balanceAccount)+ ": " +String.format("%.2f",wallet.saldo) );
                }
                else if (parserStatus.status.equals("2"))
                    Toast.makeText(wallet1.this,"You don't have enough money to make this operation.",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(wallet1.this,"Something went wrong. Try again later.",Toast.LENGTH_LONG);
            }
        }
    }

    private Integer whichChecked ()
    {
        if (wallet.typeCoinId==1) {
            if (radioBtnEx1.isChecked())
                return 2;//LiteCoin
            else
            return 3; //Nubits
        }
        if (wallet.typeCoinId==2) {
            if (radioBtnEx1.isChecked())
                return 1;//BitCoin
            else
                return 3; //Nubits
        }
        if (wallet.typeCoinId==3) {
            if (radioBtnEx1.isChecked())
                return 1;//BitCoin
            else
                return 2; //LiteCoin
        }
        return 0;
    }

    private void setTittle() {

        switch (wallet.typeCoinId)
        {
            case 1: {
                getActionBar().setTitle("BitCoin");
                break;
            }
            case 2: {
                getActionBar().setTitle("LiteCoin");
                break;
            }
            case 3: {
                getActionBar().setTitle("Nubits");
                break;
            }

        }

    }
    private String typeWallets(int typeCoinId)
    {
        String typeWalletStr = null;
        switch (wallet.typeCoinId)
        {
            case 1: {
                typeWalletStr = "BitCoin";
                break;
            }
            case 2: {
                typeWalletStr ="LiteCoin";
                break;
            }
            case 3: {
                typeWalletStr ="Nubits";
                break;
            }

        }

        return typeWalletStr;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wallet1, menu);
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

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intentPortfel = new Intent(wallet1.this, portfelPanel.class);
        intentPortfel.putExtra("user",userVO);
        startActivity(intentPortfel);
        finish();
    }
}
