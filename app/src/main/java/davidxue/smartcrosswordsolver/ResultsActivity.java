package davidxue.smartcrosswordsolver;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roboguice.activity.RoboActionBarActivity;


public class ResultsActivity extends RoboActionBarActivity {

    private String mHint, mPattern, mLength;
    private ArrayList<String> mResultList;
    private ArrayAdapter<String> mResultArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        mResultList = new ArrayList<>();
        mResultArrayAdapter = new ArrayAdapter<>(this, R.layout.result_list_item, R.id.result_list_item_textview, mResultList);
        ListView resultList = (ListView) findViewById(R.id.result_list);
        resultList.setAdapter(mResultArrayAdapter);
        Intent intent = getIntent();
        mHint = intent.getStringExtra("hint");
        mPattern = intent.getStringExtra("pattern");
        mLength = intent.getStringExtra("length");
        FetchResultsTask task = new FetchResultsTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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

    private class FetchResultsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... uri) {
            Uri.Builder uriBuilder = new Uri.Builder()
                    .scheme("http")
                    .authority("dictionary.reference.com")
                    .path("crossword/index.html")
                    .appendQueryParameter("type", "answer")
                    .appendQueryParameter("n", "10");
            if (!mHint.isEmpty()) {
                uriBuilder = uriBuilder.appendQueryParameter("query", mHint);
            }
            if (!mLength.isEmpty()) {
                uriBuilder = uriBuilder.appendQueryParameter("l", mLength);
            }
            if (!mPattern.isEmpty()) {
                uriBuilder = uriBuilder.appendQueryParameter("pattern", mPattern);

            }
            Uri getUri = uriBuilder.build();
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString;
            try {
                response = httpclient.execute(new HttpGet(String.valueOf(getUri)));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else{
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception e) {
                //TODO: what if network call fails
                responseString = "";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                finish();
            } else {
                Pattern p = Pattern.compile("Look up \"([a-zA-Z]+)\"");
                Matcher m = p.matcher(result);
                while (m.find()) {
                    String res = m.group(1);
                    mResultList.add(res);
                }
                mResultArrayAdapter.notifyDataSetChanged();
            }
        }
    }

}
