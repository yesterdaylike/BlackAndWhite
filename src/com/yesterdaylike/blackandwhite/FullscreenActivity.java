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
			gameRestart();//下一关
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
				// 如果 AppUpdateInfo 为 null 或它的 url 属性为 null，则可以判断为没有新版本。
				//Toast.makeText(this, "当前版本已经是最新版", Toast.LENGTH_SHORT).show();
				return;
			}

			// 这里简单示例使用一个对话框来显示更新信息
			new AlertDialog.Builder(this)
			.setTitle("发现新版本")
			.setMessage(updateInfo.getUpdateTips()) // 这里是版本更新信息
			.setNegativeButton("马上升级",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(updateInfo.getUrl()) );
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					FullscreenActivity.this.startActivity(intent);
					// ps：这里示例点击“马上升级”按钮之后简单地调用系统浏览器进行新版本的下载，
					// 但强烈建议开发者实现自己的下载管理流程，这样可以获得更好的用户体验。
				}
			})
			.setPositiveButton("下次再说",
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
