package com.yesterdaylike.blackandwhite;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class WBView extends View {

	private String TAG = "WBView";

	private final int SIZE = 4;
	private final int STEPS = 16;
	public static int DELAYMILLIS = 20;
	private int delaychange = 1;
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
	private Paint paintGray;
	private Paint paintText;

	private SoundPool mSoundPool;
	private int mSound;
	private int mSoundOver;

	private ActionInterface mActionInterface; 
	private HistoryDB historyDB;
	private Context mContext;
	private String mStartStr;

	private Bitmap bitmap;
	private Bitmap bitmapPressed;
	RectF dst;

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
		mContext = context;
		mStartStr = mContext.getString(R.string.start);
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		mSound = mSoundPool.load(context, R.raw.effect_tick, 1); 
		mSoundOver = mSoundPool.load(context, R.raw.keypress_spacebar, 1); 

		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.foot_keng);
		bitmapPressed = BitmapFactory.decodeResource(getResources(), R.drawable.foot_pressed);
		dst = new RectF();

		initPaint();
		setup();
	}

	public String getBestScore(){
		if( null == historyDB ){
			historyDB = new HistoryDB(mContext);
		}
		return historyDB.queryBestScore();
	}


	public int getCurrentScore(){
		return mTouchCount;
	}

	private void initPaint(){
		paint=new Paint();
		paint.setAntiAlias(true); 
		paint.setDither(true);
		paint.setColor(Color.BLACK);

		paintGray=new Paint();
		paintGray.setAntiAlias(true); 
		paintGray.setDither(true);
		paintGray.setColor(Color.GRAY);

		paintText=new Paint();
		paintText.setTextSize(START_TEXT_SIZE);
		paintText.setColor(Color.WHITE);
	}

	private void initPositionlist(){
		cellHeight = getHeight() / SIZE;//小方块的高度
		cellWidth = getWidth() / SIZE;  //小方块的宽度

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

	public void restart(){
		setup();
		DELAYMILLIS = 20;
		delaychange = 1;
		this.invalidate();
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

			left = mPositionsW[w];
			top = mPositionsH[h+1]+intervalH+1;
			right = mPositionsW[w+1];
			bottom = mPositionsH[h]+intervalH;

			//canvas.drawRect(left, top, right, bottom, paint);
			dst.set(left, top, right, bottom);
			canvas.drawBitmap(bitmap, null, dst, paint);

			if( change > 0 ){
				change = 16-change;
				if( change < 0 ){
					change = 0;
				}
				left += change/2;
				top += change;
				right -= change/2;
				bottom -= change;
				//canvas.drawRect(left, top, right, bottom, paintGray);
				dst.set(left, top, right, bottom);
				canvas.drawBitmap(bitmapPressed, null, dst, paint);
				mTouchEach[index]++;
			}

			if( length == 0 && h == 0  ){
				String valueStr = mStartStr;
				int positionWText = left + ( cellWidth >> 1 );
				int positionHText = top + ( cellHeight >> 1 );

				float halfWidthText = paintText.measureText(valueStr) /2;
				float halfHeightText = paintText.getTextSize() /2 ;

				canvas.drawText(valueStr, positionWText - halfWidthText, positionHText + halfHeightText, paintText);
			}
		}

		//计数
		String str = String.valueOf(mTouchCount);
		float halfWidthText = paintText.measureText(str) /2;
		float halfHeightText = paintText.getTextSize() /2 ;
		canvas.drawText(str, cellWidth * 2 - halfWidthText, 30 + halfHeightText, paintText);

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
		try{
			boolean eq = false;
			if( x >= 0 && mPositionsW != null ){
				int path = 0;
				for (; path < mPositionsW.length - 1 ; path++) {
					if(x <= mPositionsW[path+1] ){
						break;
					}
				}
				int index =  mTouchCount % mValue.length;
				eq =  path == mValue[ index ];
				mTouchEach[ index ] = 1;
				mTouchCount++;

				if( mTouchCount == delaychange * 8 ){
					DELAYMILLIS--;
					delaychange += (20 - DELAYMILLIS);
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
				if(x>0){
					mTouchCount--;
				}

				saveHistory();
				DELAYMILLIS = 20;
				delaychange = 1;
				mActionInterface.gameOver();
			}

			else {
				mSoundPool.play(mSound, 1, 1, 0, 0, 1);
				if( !mAnimation ){
					handler.post(runnable);
					mAnimation = true;
				}
			}
		}catch(Exception e){

		}
	}

	void setActionInterface(ActionInterface mAI){
		mActionInterface = mAI;
	}

	public void saveHistory(){
		if(mTouchCount<10){
			return;
		}

		if( null == historyDB ){
			historyDB = new HistoryDB(mContext);
		}
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		long timeIiMillis = calendar.getTimeInMillis();

		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String time = df.format(calendar.getTime());
		historyDB.add(month, day, time ,timeIiMillis, 0, mTouchCount, 0, 0);
	}

	public void closeHistoryDB(){
		if( null != historyDB ){
			historyDB.close();
		}
	}
}