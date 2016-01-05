package com.team.quattro.portfel;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HistoryOperationActivity extends ActionBarActivity {

    Wallets wallet = null;
    User userVO =null;
    String dateStart = null;
    String dateEnd = null;
    private ListView listViewHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_operation);

        listViewHistory = (ListView) findViewById(R.id.listViewHist);
        Intent intent = getIntent();
        userVO = (User) getIntent().getSerializableExtra("user");
        wallet = (Wallets) getIntent().getSerializableExtra("wallet");
        dateStart = (String) getIntent().getSerializableExtra("date_start");
        dateEnd = (String) getIntent().getSerializableExtra("date_end");
        if (!dateStart.isEmpty() && !dateEnd.isEmpty() && userVO!=null && wallet!=null) {
            new HistoryOperationTask(userVO.login).execute();
        }
    }



    public class HistoryOperationTask extends AsyncTask<Void, Void, List<HistoryOperation>> {

        private final String mLogin;


        HistoryOperationTask(String login) {
            mLogin = login;
        }

        @Override
        protected List<HistoryOperation> doInBackground(Void... params) {

            String serverAddress = getResources().getString(R.string.serverAddress);
            List<HistoryOperation> historyOperationList = null;
            EnumClass.noYes authentication=null;
            Integer typeCoin = wallet.typeCoinId;

            String urlString = serverAddress + "historyOperationService/historyOperations?l=" + mLogin + "&typeCoin=" + typeCoin + "&from="+ dateStart + "&to=" + dateEnd;

            String restKey = userVO.restKey;
            historyOperationList = sendAndParseHTTP(urlString, authentication.TAK,restKey);
            return historyOperationList;
        }




        @Override
        protected void onPostExecute(List<HistoryOperation> historyOperations) {

            if (historyOperations!=null) {
                String historyVO = null;
                List<Map<String, String>> historyList = new ArrayList<Map<String, String>>();
                for (int i = 0; i < historyOperations.size(); i++) {
                     historyVO = historyOperations.get(i).getTypeOperationName() + ", Value: "+ historyOperations.get(i).getValue()+ " Date: "+ historyOperations.get(i).getDate();
                            historyList.add(createHistory("history", historyVO));

                }

                SimpleAdapter adapter = new SimpleAdapter(HistoryOperationActivity.this, historyList, android.R.layout.simple_list_item_1, new String[]{"history"}, new int[]{android.R.id.text1});
                listViewHistory.setAdapter(adapter);
            }
        }
    }



    private HashMap<String, String> createHistory(String key, String name) {
        HashMap<String, String> history = new HashMap<String, String>();
        history.put(key, name);

        return history;
    }


    public List<HistoryOperation> parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

        int eventType = parser.getEventType();
        List<HistoryOperation> result = new ArrayList<HistoryOperation>();
        Boolean operation = false;
        Boolean addResult = false;
        HistoryOperation historyOperation = new HistoryOperation();
        while( eventType!= XmlPullParser.END_DOCUMENT) {
            String name = null;
            operation = false;
            addResult =false;
            historyOperation = new HistoryOperation();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("operation"))
                            operation = true;
                        while (operation) {

                            if (name.equals("date")) {
                                historyOperation.setDate(parser.nextText());
                            }
                            else if (name.equals("typeOperationName")) {
                                historyOperation.setTypeOperationName(parser.nextText());
                            }
                            else if (name.equals("userLogin")) {
                                historyOperation.setUserLogin(parser.nextText());
                            }
                            else if (name.equals("value")) {
                                historyOperation.setValue(parser.nextText());
                            }
                            else if (name.equals("walletCode")) {
                                historyOperation.setWalletCode(parser.nextText());
                                operation =false;
                                addResult=true;
                            }
                            eventType = parser.next();
                            name = parser.getName();
                        }
                        if (addResult)
                            result.add(historyOperation);
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
            eventType = parser.next();
            }

        return result;
    }

    public List<HistoryOperation> sendAndParseHTTP (String urlString, EnumClass.noYes authentication, String restKey)
    {
        InputStream in = null;
        List<HistoryOperation> parserHistoryOperationVO = null;

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
            parserHistoryOperationVO = parseXML(parser);

            in.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            urlConnection.disconnect();
        }
        return parserHistoryOperationVO;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history_operation, menu);
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
        Intent intentPortfel = new Intent(HistoryOperationActivity.this, wallet1.class);
        intentPortfel.putExtra("user",userVO);
        intentPortfel.putExtra("wallet",wallet);
        startActivity(intentPortfel);
        finish();
    }
}
