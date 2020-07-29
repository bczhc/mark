package com.mycompany.test;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.text.*;
import android.text.style.*;
import android.graphics.*;
import java.util.*;
import java.util.regex.*;

public class MainActivity extends Activity 
{
	
	private TextWatcher tw = null;
	private Thread t = null;
	private boolean checked = false;
	private Handler handler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		handler = new Handler();
		final EditText textET = findViewById(R.id.s_et);
		final EditText patternET = findViewById(R.id.pattern_et);
		tw = new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
			{
				// TODO: Implement this method
			}

			@Override
			public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
			{
				if (t != null) t.interrupt();
				t = new Thread() {
					@Override
					public void run() {
						if (Thread.currentThread().isInterrupted()) return;
						String pattern = patternET.getText().toString();
						String text = textET.getText().toString();
						if (Thread.currentThread().isInterrupted()) return;
						mark(textET, find(text, pattern), text);
					}
				};
				t.start();
			}
			
			@Override
			public void afterTextChanged(Editable p1)
			{
			}
		};
		textET.addTextChangedListener(tw);
		patternET.addTextChangedListener(tw);	
		CheckBox cb = findViewById(R.id.regex_cb);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton p1, boolean checked)
				{
					MainActivity.this.checked = checked;
				}
			});
    }
	
	private void mark(final EditText et, List<Range> ranges, String text) {
		int len = ranges.size();
		final SpannableStringBuilder ssb = new SpannableStringBuilder(text);
		for (int i = 0; i < len; ++i) {
			if (Thread.currentThread().isInterrupted()) return;
			Range range = ranges.get(i);
			BackgroundColorSpan bcs = new BackgroundColorSpan(Color.RED);
			ssb.setSpan(bcs, range.start, range.end + 1, SpannableStringBuilder. SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (Thread.currentThread().isInterrupted()) return;
		handler.post(new Runnable(){
				@Override
				public void run()
				{
					int cursorStart = et.getSelectionStart();
					et.removeTextChangedListener(tw);
					if (Thread.currentThread().isInterrupted()) {
						et.addTextChangedListener(tw);
						return;
					}
					if (Thread.currentThread().isInterrupted()) return;
					et.setText(ssb);
					et.setSelection(cursorStart);
					et.addTextChangedListener(tw);
				}
			});
	}
	
	private class Range {
		private int start;
		private int end;
		
		private Range(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
	
	private List<Range> find(String text, String pattern) {
		List<Range> list = new ArrayList<>();
		if (checked) {
			try {
				Pattern regexPattern = Pattern.compile(pattern);
				Matcher matcher = regexPattern.matcher(text);
				while (matcher.find()) {
					list.add(new Range(matcher.start(), matcher.end() - 1));
				}
			} catch (Exception ignored) {
			}
			return list;
		}
		int lastIndex = -1;
		int len = pattern.length();
		if (pattern.isEmpty()) return list;
		while ((lastIndex = text.indexOf(pattern, lastIndex + 1)) != -1) {
			list.add(new Range(lastIndex, lastIndex + len - 1));
		}
		return list;
	}
}
