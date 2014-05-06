package com.yesterdaylike.blackandwhite;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class FullscreenActivity extends Activity {

	private WBView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		view = (WBView) findViewById(R.id.main_view);
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					Log.v("ACTION_UP", "X:"+event.getX()+", Y:"+event.getY());
					view.checkPath(event.getX());
					break;

				default:
					break;
				}
				
				return false;
			}
		});
	}
}
