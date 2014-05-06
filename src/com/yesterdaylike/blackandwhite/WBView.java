package com.yesterdaylike.blackandwhite;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WBView extends View {

	private String TAG = "WBView";

	private final int SIZE = 4;
	private final int STEPS = 16;
	private int DELAYMILLIS = 20;
	private final int START_TEXT_SIZE = 54;

	private int[] mValue = new int[50];
	private int[] mTouchEach = new int[50];
	private int[] mPositionsH;
	private int[] mPositionsW;
	private int mStepIntervalH;
	private int cellHeight;			//小方块的高度
	private int cellWidth;  		//小方块的宽度

	private boolean mAnimation = false;
	private boolean mInitPosition = false;
	private boolean mStart = false;

	private int mCount;
	private int mTouchCount;

	private Random mRandom;
	private Paint paint;
	private Paint textPaint;

	private SoundPool mSoundPool;
	private int mSound;
	private int mSoundOver;

	private Handler handler = new Handler();

	private Runnable runnable= new Runnable() {
		public void run() {  
			handler.postDelayed(this, DELAYMILLIS);
			WBView.this.postInvalidate();
			mCount++;
		}  
	};

	public WBView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		mSound = mSoundPool.load(context, R.raw.effect_tick, 1); 
		mSoundOver = mSoundPool.load(context, R.raw.keypress_spacebar, 1); 
		initPaint();
		setup();
	}

	int TileColor[] = {
			Color.BLACK,
			0xff080808,
			0xff101010,
			0xff181818,
			0xff202020,
			0xff282828,
			0xff303030,
			0xff383838,
			0xff404040,
			0xff484848,
			0xff505050,
			0xff585858,
			0xff606060,
			0xff686868,
			0xff707070,
			0xff787878,
			0xff808080,
			Color.GRAY,
	};

	private void initPaint(){
		paint=new Paint();
		paint.setAntiAlias(true); 
		paint.setDither(true);
		paint.setColor(Color.BLACK);

		textPaint=new Paint();
		textPaint.setTextSize(START_TEXT_SIZE);
		textPaint.setColor(Color.WHITE);
	}

	private void initPositionlist(){
		cellHeight = getHeight() / SIZE;//小方块的高度
		cellWidth = getWidth() / SIZE;  //小方块的宽度

		//Log.i(TAG, "getHeight:"+getHeight()+", getWidth:"+getWidth());
		//Log.i(TAG, "cellHeight:"+cellHeight+", cellWidth:"+cellWidth);

		mPositionsH = new int[SIZE+2];
		mPositionsW = new int[SIZE+1];

		for(int i = 0; i <= SIZE; i++){
			mPositionsH[i] = cellHeight *( SIZE - i );
			mPositionsW[i] = cellWidth * i;
		}
		mPositionsH[ SIZE+1 ] = -cellHeight;

		mStepIntervalH = cellHeight / STEPS;
	}

	private void setup(){
		mCount = 0;
		mTouchCount = 0;
		for (int i = 0; i <= SIZE; i++) {
			mValue[i] = getRandomPosition();
		}
		
		for (int i = 0; i < mTouchEach.length; i++) {
			mTouchEach[i] = 0;
		}
		mAnimation = false;
		mStart = true;
	}

	void restart(){
		setup();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if( !mInitPosition ){
			initPositionlist();
			mInitPosition = true;
		}

		int length = mCount / STEPS;
		int step = mCount % STEPS;
		int intervalH = step * mStepIntervalH;
		int left, top, right, bottom;

		for (int h = 0; h <= SIZE; h++) {
			int index  = ( h + length ) % mValue.length;
			int w = mValue[ index ];
			int change = mTouchEach[index];

			left = mPositionsW[w]+(change/2);
			top = mPositionsH[h+1]+intervalH+1+change;
			right = mPositionsW[w+1]-(change/2);
			bottom = mPositionsH[h]+intervalH-change;
			
			int color = TileColor[change/4];
			Log.i("zhengwenhui", "change: "+change+", change/4: "+change/4+", color: "+color);
			paint.setColor(color);
			canvas.drawRect(left, top, right, bottom, paint);
			
			if( change > 0 ){
				mTouchEach[index]++;
			}

			if( length == 0 && h == 0  ){
				String valueStr = "开始";
				int positionWText = left + ( cellWidth >> 1 );
				int positionHText = top + ( cellHeight >> 1 );

				float halfWidthText = textPaint.measureText(valueStr) /2;
				float halfHeightText = textPaint.getTextSize() /2 ;

				canvas.drawText(valueStr, positionWText - halfWidthText, positionHText + halfHeightText, textPaint);
			}
		}

		//计数
		String str = String.valueOf(mTouchCount);
		float halfWidthText = textPaint.measureText(str) /2;
		float halfHeightText = textPaint.getTextSize() /2 ;
		canvas.drawText(str, cellWidth * 2 - halfWidthText, 30 + halfHeightText, textPaint);

		if(length > mTouchCount){
			//没有点击
			checkPath( -1 );
		}

		if( step == ( STEPS - 1 ) ){
			int next = ( SIZE+length+1 ) % mValue.length;
			mValue[ next ] = getRandomPosition();
			mTouchEach[ next ] = 0;
		}
	}

	private int getRandomPosition(){
		if( null == mRandom ){
			mRandom = new Random();
		}
		return mRandom.nextInt(SIZE);
	}

	public boolean isAnimation(){
		return mAnimation;
	}

	public void checkPath(float x){
		if(!mStart){
			return;
		}

		boolean eq = false;
		if( x >= 0 ){
			int path = 0;
			for (; path < mPositionsW.length - 1 ; path++) {
				if(x <= mPositionsW[path+1] ){
					break;
				}
			}
			//Log.i(TAG, "mTouchCount:"+mTouchCount+", path:"+path+", mValue:"+mValue[ mTouchCount ]);
			int index =  mTouchCount % mValue.length;
			eq =  path == mValue[ index ];
			mTouchEach[ index ] = 1;
			mTouchCount++;
			
			if( mTouchCount % 12 == 0){
				DELAYMILLIS--;
			}
		}

		if(!eq){
			mSoundPool.play(mSoundOver, 1, 1, 0, 0, 1);
			if(!mAnimation){
				mTouchCount--;
				return;
			}
			mAnimation = false;
			handler.removeCallbacks(runnable);
			mStart = false;
			//可以重新开始
		}

		else {
			mSoundPool.play(mSound, 1, 1, 0, 0, 1);
			if( !mAnimation ){
				handler.post(runnable);
				mAnimation = true;
			}
		}
	}
}