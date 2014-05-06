package com.yesterdaylike.blackandwhite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class FullscreenActivity extends Activity implements ActionInterface{

	private WBView mWBView;
	private Button mRestartButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		mRestartButton = (Button) findViewById(R.id.restart);
		mWBView = (WBView) findViewById(R.id.main_view);
		mWBView.setPrintInterface(FullscreenActivity.this);
		mWBView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					Log.v("ACTION_UP", "X:"+event.getX()+", Y:"+event.getY());
					mWBView.checkPath(event.getX());
					break;

				default:
					break;
				}
				
				return false;
			}
		});
	}
	
	public void onClickRestart(View view){
		mWBView.restart();
		mRestartButton.setVisibility(View.GONE);
		
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}

	@Override
	public void gameOver() {
		// TODO Auto-generated method stub
		mRestartButton.setVisibility(View.INVISIBLE);
	}
}
