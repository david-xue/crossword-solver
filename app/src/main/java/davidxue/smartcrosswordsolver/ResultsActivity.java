package davidxue.smartcrosswordsolver;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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


public class ResultsActivity extends ActionBarActivity {

    private String mHint, mPattern, mLength;
    private ArrayList<String> mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
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
            Uri getUri = new Uri.Builder()
                    .scheme("http")
                    .authority("dictionary.reference.com")
                    .path("crossword/index.html")
                    .appendQueryParameter("query", mHint)
                    .appendQueryParameter("type", "answer")
                    .appendQueryParameter("n", mLength)
                    .appendQueryParameter("pattern", mPattern)
                    .build();
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString;
            try {
                response = httpclient.execute(new HttpGet(String.valueOf(getUri))));
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
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Pattern p = Pattern.compile("Look up \"([a-zA-Z]+)\"");
            Matcher m = p.matcher(result);
            while (m.find()) {
                String res = m.group(1);
                mResultList.add(res);
            }
        }

    }

}
