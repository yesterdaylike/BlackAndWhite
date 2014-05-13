package com.yesterdaylike.blackandwhite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class GameOverView extends View {

	private String TAG = "GameOverView";

	private final int START_TEXT_SIZE = 54;

	private int height;			//小方块的高度
	private int width;  		//小方块的宽度

	private boolean mInitPosition = false;

	private Paint paint;
	private Paint paintGray;
	private Paint paintText;

	private SoundPool mSoundPool;
	private int mSound;

	private ActionInterface mActionInterface; 
	private Context mContext;

	RectF bestscoreRect, historyRect, restartRect;
	RectF checkedRect;

	private HistoryDB historyDB;

	private Handler handler = new Handler();
	private int mCount = 0;

	private String best;

	private Runnable runnable= new Runnable() {
		public void run() {  
			if( mCount > 8){
				mCount = 0;
				handler.removeCallbacks(runnable);
			}else{
				mCount++;
				handler.postDelayed(this, WBView.DELAYMILLIS);
				GameOverView.this.postInvalidate();
			}
		}  
	};

	public void getBestScore(){
		best = historyDB.queryBestScore();
	}

	public GameOverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		mSound = mSoundPool.load(context, R.raw.effect_tick, 1); 
		mContext = context;
		initPaint();

		if( null == historyDB ){
			historyDB = new HistoryDB(mContext);
		}
	}

	private void initPaint(){
		paint=new Paint();
		paint.setAntiAlias(true); 
		paint.setDither(true);
		paint.setColor(Color.GREEN);

		paintGray=new Paint();
		paintGray.setAntiAlias(true); 
		paintGray.setDither(true);
		paintGray.setColor(Color.GRAY);

		paintText=new Paint();
		paintText.setTextSize(START_TEXT_SIZE);
		paintText.setColor(Color.WHITE);
	}

	private void initPositionlist(){
		height = getHeight();
		width = getWidth();
		bestscoreRect = new RectF(0, height/4, width, height/2-1);
		historyRect = new RectF(0, height/2, width/2-1, height);
		restartRect = new RectF(width/2, height/2, width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if( !mInitPosition ){
			initPositionlist();
			mInitPosition = true;
		}

		canvas.drawRect(bestscoreRect, paint);
		canvas.drawRect(historyRect, paint);
		canvas.drawRect(restartRect, paint);

		if( mCount > 0 && null != checkedRect ){
			int iw = 0;
			int ih = 0;
			if(bestscoreRect == checkedRect){
				iw = 8 - mCount;
				ih = ( 8 - mCount ) * 2;
			}else{
				iw = ( 8 - mCount ) * 2;
				ih = 8 - mCount;
			}
			canvas.drawRect(checkedRect.left+iw, checkedRect.top+ih,checkedRect.right-iw,checkedRect.bottom-ih,paintGray);
		}

		String bestScoreStr, historyStr, restartStr;
		bestScoreStr = mContext.getString(R.string.action_best_score)+" "+best;
		historyStr = mContext.getString(R.string.action_history);
		restartStr = mContext.getString(R.string.action_restart);

		float halfHeightText = paintText.getTextSize() /2 ;

		float halfWidthText = paintText.measureText(bestScoreStr) /2;
		canvas.drawText(bestScoreStr, width/2 - halfWidthText, height*3/8 + halfHeightText, paintText);

		halfWidthText = paintText.measureText(historyStr) /2;
		canvas.drawText(historyStr, width/4 - halfWidthText, height*3/4 + halfHeightText, paintText);

		halfWidthText = paintText.measureText(restartStr) /2;
		canvas.drawText(restartStr, width/4*3 - halfWidthText, height*3/4 + halfHeightText, paintText);
	}

	public void checkPath(float x, float y){

		handler.post(runnable);
		mCount = 0;

		if ( bestscoreRect.contains(x, y) ){
			checkedRect = bestscoreRect;
			mSoundPool.play(mSound, 1, 1, 0, 0, 1);
			mActionInterface.gameBestScore();
		}
		else if ( historyRect.contains(x, y) ){
			checkedRect = historyRect;
			mSoundPool.play(mSound, 1, 1, 0, 0, 1);
			mActionInterface.gameHistory();
		}
		else if ( restartRect.contains(x, y) ){
			checkedRect = restartRect;
			mSoundPool.play(mSound, 1, 1, 0, 0, 1);
			mActionInterface.gameRestart();
		}
		else{
			checkedRect = null;
		}
	}

	void setActionInterface(ActionInterface mAI){
		mActionInterface = mAI;
	}

	public void closeHistoryDB(){
		if( null != historyDB ){
			historyDB.close();
		}
	}
}