package com.yesterdaylike.blackandwhite;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class WBView extends TextView {

	private String TAG = "WBView";

	private Paint paint;			//方块画笔

	private int mStepIntervalH;
	private final int STEPS = 8;
	private final int SIZE = 4;

	private boolean mAnimation = false;
	private boolean mInitPosition = false;
	private int count = 0;
	
	private int[] value;

	private Random mRandom;

	int[] mPositionsH;
	int[] mPositionsW;

	private Handler handler = new Handler();

	private Runnable runnable= new Runnable() {
		public void run() {  
			handler.postDelayed(this, 1000);
			WBView.this.postInvalidate();
			count++;
			if(count>96){
				handler.removeCallbacks(runnable);
				mAnimation = false;
			}
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

		Log.i(TAG, "cellHeight:"+cellHeight+", cellWidth:"+cellWidth);

		mPositionsH = new int[SIZE+1];
		mPositionsW = new int[SIZE+1];

		for(int i = 0; i <= SIZE; i++){
			mPositionsH[i] = cellHeight * i;
			mPositionsW[i] = cellWidth * i;
		}
		
		mStepIntervalH = cellHeight / STEPS;
		value = new int[20];
		
		for (int i = 0; i < value.length; i++) {
			value[i] = getRandomPosition();
		}

		/*handler.post(runnable);
		handler.removeCallbacks(runnable);*/
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if( !mInitPosition ){
			initPositionlist();
			mInitPosition = true;
		}

		int length = count / 8;
		int step = count % 8;
		int intervalH = step * mStepIntervalH;
		Log.e(TAG, "count:"+count+", length:"+length+", step:"+step+", intervalH:"+intervalH);
		
		for (int h = 0; h <= SIZE; h++) {
			int w = value[ h + length ];
			Log.i(TAG, "h:"+h+", w:"+w);
			
			int left = mPositionsW[w];
			int top = mPositionsH[h]+intervalH;
			int right = mPositionsW[w+1];
			int bottom = mPositionsH[h+1]+intervalH;
			Log.v(TAG, "left:"+left+", top:"+top+", right:"+right+", bottom:"+bottom);
			canvas.drawRect(mPositionsW[w], mPositionsH[h]+intervalH, mPositionsW[w+1], mPositionsH[h+1]+intervalH, paint);
		}
		
		/*if( ( count % 8 ) == 7 ){
			for (int i = 0; i < SIZE; i++) {
				value[i] = value[i+1];
			}
			value[SIZE] = getRandomPosition();
		}*/
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
}