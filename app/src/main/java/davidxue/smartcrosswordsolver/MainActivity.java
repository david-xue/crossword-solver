package davidxue.smartcrosswordsolver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import static android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity implements OnClickListener, TextWatcher {

    private TextView mPatternTextView;
    private TextView mLengthTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPatternTextView = (TextView) findViewById(R.id.pattern_textView);
        mLengthTextView = (TextView) findViewById(R.id.length_textView);
        mPatternTextView.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v != null && v.getId() == R.id.search_button) {
            TextView hintTextView = (TextView) findViewById(R.id.hint_textView);
            String hintText = hintTextView.getText().toString();
            String pattern = mPatternTextView.getText().toString();
            if (pattern.length() < 13 && pattern.matches("[a-zA-Z\\?]*")) {
                String lengthStr = mLengthTextView.getText().toString();
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("hint", hintText);
                intent.putExtra("pattern", pattern);
                intent.putExtra("length", lengthStr);
                startActivity(intent);

            } else {
                //TODO: user error
            }
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
        if (s.length() == 0) {
            mLengthTextView.setEnabled(true);
        } else {
            mLengthTextView.setEnabled(false);
            mLengthTextView.setText(s.length());
        }
    }

}

