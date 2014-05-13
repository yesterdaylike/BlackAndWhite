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
import android.widget.Toast;

public class FullscreenActivity extends Activity implements ActionInterface, CheckAppUpdateCallBack{

	private WBView mWBView;
	private GameOverView mGameOverView;
	//private LinearLayout mAdLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AdManager.getInstance(this).init("a66256a694d087c4", "11db039c8f408310", false);
		SpotManager.getInstance(this).loadSpotAds();

		AdManager.getInstance(this).asyncCheckAppUpdate(this);
		AdManager.getInstance(this).setUserDataCollect(true);

		setContentView(R.layout.activity_fullscreen);

		mGameOverView = (GameOverView)findViewById(R.id.game_over_view);
		mGameOverView.setActionInterface(FullscreenActivity.this);
		mGameOverView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
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

	@Override
	public void gameOver() {
		// TODO Auto-generated method stub
		mGameOverView.getBestScore();

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
