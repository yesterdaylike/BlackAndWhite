package com.yesterdaylike.blackandwhite;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WBView extends View {

	private String TAG = "WBView";

	private Paint paint;			//方块画笔

	private int mStepIntervalH;
	private final int STEPS = 8;
	private final int SIZE = 4;
	private final int DELAYMILLIS = 50;

	private boolean mAnimation = false;
	private boolean mInitPosition = false;
	private int mCount = 0;
	private int[] mValue;
	private Random mRandom;
	private int mTouchCount = 0;

	int[] mPositionsH;
	int[] mPositionsW;

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
		paint=new Paint();
		paint.setAntiAlias(true); 
		paint.setDither(true);
		paint.setColor(Color.BLACK);

		handler.post(runnable);
	}

	private void initPositionlist(){
		int cellHeight = getHeight() / SIZE;//小方块的高度
		int cellWidth = getWidth() / SIZE;  //小方块的宽度

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
		mValue = new int[100];

		for (int i = 0; i <= SIZE; i++) {
			mValue[i] = getRandomPosition();
		}
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
		//Log.e(TAG, "count:"+mCount+", length:"+length+", step:"+step+", intervalH:"+intervalH);

		for (int h = 0; h <= SIZE; h++) {
			int w = mValue[ ( h + length ) % 100 ];
			//Log.i(TAG, "h:"+h+", w:"+w);

			int left = mPositionsW[w];
			int top = mPositionsH[h+1]+intervalH+1;
			int right = mPositionsW[w+1];
			int bottom = mPositionsH[h]+intervalH-1;
			//Log.v(TAG, "left:"+left+", top:"+top+", right:"+right+", bottom:"+bottom);
			canvas.drawRect(left, top, right, bottom, paint);
		}

		if( step == 7 ){
			mValue[ ( SIZE+length+1 ) % 100 ] = getRandomPosition();
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
		int path = 0;
		for (; path < mPositionsW.length - 1 ; path++) {
			if(x <= mPositionsW[path+1] ){
				break;
			}
		}
		
		Log.i(TAG, "mTouchCount:"+mTouchCount+", path:"+path+", mValue:"+mValue[ mTouchCount ]);
		boolean eq =  path == mValue[ mTouchCount++ ];
		if(!eq){
			handler.removeCallbacks(runnable);
			mAnimation = false;
		}
	}
}