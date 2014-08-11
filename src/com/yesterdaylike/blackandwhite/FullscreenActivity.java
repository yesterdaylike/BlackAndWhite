package com.yesterdaylike.blackandwhite;

import net.youmi.android.AdManager;
import net.youmi.android.dev.AppUpdateInfo;
import net.youmi.android.dev.CheckAppUpdateCallBack;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FullscreenActivity extends Activity implements ActionInterface, CheckAppUpdateCallBack{

	private WBView mWBView;
	private RelativeLayout mGameOverView;
	private TextView mScoreTextView;
	//private LinearLayout mAdLayout;
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AdManager.getInstance(this).init("a66256a694d087c4", "11db039c8f408310", false);
		SpotManager.getInstance(this).loadSpotAds();

		AdManager.getInstance(this).asyncCheckAppUpdate(this);
		AdManager.getInstance(this).setUserDataCollect(true);

		setContentView(R.layout.activity_fullscreen);
		mType = getIntent().getIntExtra("POSITION", 0);

		mGameOverView = (RelativeLayout)findViewById(R.id.game_over_view);
		//mGameOverView.setActionInterface(FullscreenActivity.this);
		mScoreTextView = (TextView)mGameOverView.findViewById(R.id.score);


		mWBView = (WBView) findViewById(R.id.main_view);
		
		mWBView.mType = mType;
		
		mWBView.setActionInterface(FullscreenActivity.this);
		mWBView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//Log.v("ACTION_UP", "X:"+event.getX()+", Y:"+event.getY());
					mWBView.checkPath(event.getX());
					break;

				default:
					break;
				}

				return false;
			}
		});
	}

	public void onButtonClick(View view){
		switch (view.getId()) {
		case R.id.share:

			break;
		case R.id.retry:
			gameRestart();
			break;
		case R.id.nextpoint:
			gameRestart();//��һ��
			break;
		default:
			break;
		}
	}

	@Override
	public void gameOver() {
		// TODO Auto-generated method stub
		//String bs = mWBView.getBestScore();
		int cs = mWBView.getCurrentScore();
		mScoreTextView.setText(String.valueOf(cs));

		new Handler().postDelayed(new Runnable(){  
			public void run() {  
				//execute the task
				mGameOverView.setVisibility(View.VISIBLE);
				SpotManager.getInstance(FullscreenActivity.this).showSpotAds(FullscreenActivity.this);
			}  
		}, 1000);
	}

	@Override
	public void gameRestart() {
		// TODO Auto-generated method stub
		mWBView.restart();
		mGameOverView.setVisibility(View.INVISIBLE);
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

	@Override
	public void onCheckAppUpdateFinish(final AppUpdateInfo updateInfo) {
		// TODO Auto-generated method stub
		try {
			if (updateInfo == null || updateInfo.getUrl() == null) {
				// ��� AppUpdateInfo Ϊ null ������ url ����Ϊ null��������ж�Ϊû���°汾��
				//Toast.makeText(this, "��ǰ�汾�Ѿ������°�", Toast.LENGTH_SHORT).show();
				return;
			}

			// �����ʾ��ʹ��һ���Ի�������ʾ������Ϣ
			new AlertDialog.Builder(this)
			.setTitle("�����°汾")
			.setMessage(updateInfo.getUpdateTips()) // �����ǰ汾������Ϣ
			.setNegativeButton("��������",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(updateInfo.getUrl()) );
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					FullscreenActivity.this.startActivity(intent);
					// ps������ʾ�������������������ť֮��򵥵ص���ϵͳ����������°汾�����أ�
					// ��ǿ�ҽ��鿪����ʵ���Լ������ع������̣��������Ի�ø��õ��û����顣
				}
			})
			.setPositiveButton("�´���˵",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).create().show();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
