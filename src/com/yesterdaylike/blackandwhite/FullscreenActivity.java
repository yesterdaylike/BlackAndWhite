package com.yesterdaylike.blackandwhite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class FullscreenActivity extends Activity implements ActionInterface{

	private WBView mWBView;
	private GameOverView mGameOverView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		mGameOverView = (GameOverView)findViewById(R.id.game_over_view);
		mGameOverView.setActionInterface(FullscreenActivity.this);
		mGameOverView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					Log.v("ACTION_UP", "X:"+event.getX()+", Y:"+event.getY());
					mGameOverView.checkPath(event.getX(), event.getY());
					break;

				default:
					break;
				}

				return false;
			}
		});
		
		mWBView = (WBView) findViewById(R.id.main_view);
		mWBView.setActionInterface(FullscreenActivity.this);
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

	@Override
	public void gameOver() {
		// TODO Auto-generated method stub
		mGameOverView.getBestScore();
		mGameOverView.setVisibility(View.VISIBLE);
	}

	@Override
	public void gameRestart() {
		// TODO Auto-generated method stub
		mWBView.restart();
		mGameOverView.setVisibility(View.GONE);
	}

	@Override
	public void gameHistory() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}

	@Override
	public void gameBestScore() {
		// TODO Auto-generated method stub
		
	}
}
