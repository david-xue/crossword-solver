package davidxue.smartcrosswordsolver;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

import static android.view.View.OnClickListener;


public class MainActivity extends RoboActionBarActivity implements OnClickListener, TextWatcher {

    @InjectView(R.id.hint_edittext)
    private EditText mHintEditText;
    @InjectView(R.id.pattern_edittext)
    private EditText mPatternEditText;
    @InjectView(R.id.length_edittext)
    private EditText mLengthEditText;
    @InjectView(R.id.search_button)
    private Button mSearchButton;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPatternEditText.addTextChangedListener(this);
        mSearchButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onClick(View v) {
        if (v != null && v.getId() == R.id.search_button) {
            String hintText = mHintEditText.getText().toString();
            String pattern = mPatternEditText.getText().toString();
            String lengthStr = mLengthEditText.getText().toString();
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
            if ((hintText.isEmpty()) && (lengthStr.isEmpty())) {
                mToast.setText(R.string.enter_a_search_parameter);
            } else if (lengthStr.equals("0")) {
                mToast.setText("A word must have one or more letter");
            } else if (pattern.length() > 12) {
                mToast.setText("The pattern is too long");
            } else if (!pattern.matches("[a-zA-Z\\?]*")) {
                mToast.setText("The pattern must contain only ? and letters");
            } else {
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("hint", hintText);
                intent.putExtra("pattern", pattern);
                intent.putExtra("length", lengthStr);
                startActivity(intent);
                return;
            }
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mLengthEditText.setText(Integer.toString(s.length()));
        if (s.length() == 0) {
            mLengthEditText.setEnabled(true);
        } else {
            mLengthEditText.setEnabled(false);
        }
    }

}

