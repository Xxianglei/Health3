package com.example.heath.view;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.heath.R;

/**
 * create by feijie.xfj
 * 2015-11-22
 * */
public class RingView extends View {

	/**
	 * 整个View的宽高
	 * */
	private int mTotalHeight;

    /**
	 * 心跳线的总宽度 -- 圆环的宽度
	 * */
	private int mHeartBeatWidth;

    /**
	 * 圆环使用
	 * */
	private Paint mRingPaint;

	/**
	 * 圆环动画使用 -- 与mRingPaint唯一不同得方在于颜色
	 * */
	private Paint mRingAnimPaint;

	/**
	 * 圆环大小 矩形
	 * */
	private RectF mRectf;

	private Context mContext;

	/**
	 * 圆环 宽度
	 * */
	private final int mHeartPaintWidth = 50;

	/**
	 * 圆环动画开始时 画弧的偏移量
	 * */
	private int mAnimAngle = -1;

	/**
	 * 心跳线 Y轴坐标
	 * */
	private float[] mOriginalYPositon;

	/**
	 * 心跳线 Y轴坐标 -- 默认坐标
	 * */
	private float [] mDefaultYPostion;
	// y = Asin(w*x)+Y

    /**
	 * canvas抗锯齿开启需要
	 * */
	private DrawFilter mDrawFilter;

	/**
	 * 正弦曲线偏移量
	 * */
	private volatile int mOffset=0;

    /**
	 * 将SPEED转换为实际速度
	 * */
	private int mOffsetSpeed;

	/**
	 * 绘制心率线Paint
	 * */
	private Paint mHeartBeatPaint;

	/**
	 * 绘制心率线path的Paint -- 优化
	 * */
	private Paint mHeartBeatPathPaint;

	Path path = new Path();

	private void init() {
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if (!isInEditMode()) {
			// 造成错误的代码段
			mRingPaint.setColor(mContext.getResources().getColor(R.color.heart_default));
		}
		mRingPaint.setStrokeWidth(mHeartPaintWidth);
		mRingPaint.setStyle(Style.STROKE);
		mRingAnimPaint = new Paint(mRingPaint);
		mRingAnimPaint.setColor(Color.WHITE);


		//初始化心跳曲线
		mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		/*
	  心跳线条速度
	  */
        int SPEED = 5;
        mOffsetSpeed = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SPEED, mContext.getResources().getDisplayMetrics());
		mHeartBeatPaint  =new Paint(Paint.ANTI_ALIAS_FLAG);
		mHeartBeatPaint.setStrokeWidth(5);
		//mHeartBeatPaint.setStyle(Style.STROKE);
		if (!isInEditMode()) {
			mHeartBeatPaint.setColor(mContext.getResources().getColor(R.color.heartbeat));
		}

		mHeartBeatPathPaint = new Paint(mHeartBeatPaint);
		mHeartBeatPathPaint.setStrokeWidth(5);
		mHeartBeatPathPaint.setStyle(Style.STROKE);
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mTotalHeight = h;
        int mTotalWidth = w;
		mHeartBeatWidth = w - mHeartPaintWidth*2-40; //内圆宽度
		mFirstFrameOffset  =mHeartBeatWidth-1;
		/*
	  振幅
	  */
        float amplitudeA = (mTotalHeight - 2 * mHeartPaintWidth) / 4;
		mOriginalYPositon = new float[mHeartBeatWidth];//正弦曲线 Y坐标
		mDefaultYPostion = new float[mHeartBeatWidth];
		Arrays.fill(mOriginalYPositon, 0);
		Arrays.fill(mDefaultYPostion, -1);
		// 周期定位总宽度的1/4
		/*
	  sin函数 周期因子
	  */
        float mPeriodFraction = (float) (Math.PI * 2 / mHeartBeatWidth * 3);
		for (int i =  mHeartBeatWidth/3*2; i < mHeartBeatWidth; i++) {
			/*
	  期初的偏移量
	  */
            int OFFSET_Y = 0;
            mOriginalYPositon[i] = (float) (amplitudeA * Math.sin(mPeriodFraction * i) + OFFSET_Y);
		}
		/*
	  圆环的中心点 -- 画圆环和旋转画布时需要使用
	  */
        int x = w / 2;
        int y = h / 2;
		/*
	  圆环半径 根据view的宽度计算
	  */
        int mRadius = w / 2 - mHeartPaintWidth / 2;
		mRectf = new RectF(x - mRadius, y - mRadius, x + mRadius, y + mRadius);
	}

	private void resetPath(){
		path.reset();
		path.moveTo(mHeartPaintWidth+20, mTotalHeight/2-mOriginalYPositon[mOffset]);
		int interval = mHeartBeatWidth - mOffset;
		for(int i=mOffset+1,j=mHeartPaintWidth+20;i<mHeartBeatWidth;i++,j++){
			path.lineTo(j, mTotalHeight/2-mOriginalYPositon[i]);
		}
		for(int i=0,j=interval+mHeartPaintWidth+20;i<mOffset;i++,j++){
			path.lineTo(j, mTotalHeight/2-mOriginalYPositon[i]);
		}

	}

	private void resetPath1(){
		path.reset();
		path.moveTo(mHeartPaintWidth+20, mTotalHeight/2-mOriginalYPositon[mOffset]);
		int interval = mHeartBeatWidth - mOffset;
		//先找到全0的部分
		int index = -1;
		for(int i=mOffset+1;i<mHeartBeatWidth;i++){
			if(mOriginalYPositon[i]==0){
				index = i;
			}else{
				break;
			}
		}
		if(index!=-1){
			path.lineTo(mHeartPaintWidth+20+(index-mOffset+1), mTotalHeight/2);
			for(int i=index+1,j=mHeartPaintWidth+20+(index-mOffset+2);i<mHeartBeatWidth;i++,j++){
				path.lineTo(j, mTotalHeight/2-mOriginalYPositon[i]);
			}
		}else{
			for(int i=mOffset+1,j=mHeartPaintWidth+20;i<mHeartBeatWidth;i++,j++)
				path.lineTo(j, mTotalHeight/2-mOriginalYPositon[i]);
		}
		//查找最后全为0的index
		index = -1;
		for(int i =0;i<mOffset;i++){
			if(mOriginalYPositon[i]==0)
				index = i;
			else
				break;
		}
		if(index !=-1){
			//修正视觉偏移量
			path.lineTo(mHeartPaintWidth+20+(mHeartBeatWidth-mOffset), mTotalHeight/2);
			path.lineTo(interval+mHeartPaintWidth+20+index, mTotalHeight/2);
			for(int i=index+1,j=interval+mHeartPaintWidth+20+index+1;i<mOffset;i++,j++){
				path.lineTo(j, mTotalHeight/2-mOriginalYPositon[i]);
			}
		}else{
			for(int i=0,j=interval+mHeartPaintWidth+20;i<mOffset;i++,j++)
				path.lineTo(j, mTotalHeight/2-mOriginalYPositon[i]);
		}

	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(mDrawFilter);//在canvas上抗锯齿
		for (int i = 0; i < 360; i += 3) {
			canvas.drawArc(mRectf, i, 1, false, mRingPaint);
		}
		if (mAnimAngle != -1) {// 如果开启了动画
			for (int i = -90; i < mAnimAngle-90; i += 3) {
				canvas.drawArc(mRectf, i, 1, false, mRingAnimPaint);
			}
		}
		//canvas.restoreToCount(level);
		if(StartHeartBeatAnmiFlag){
			resetPath1(); //或者resetPath ，这个优化效果一般
			canvas.drawPath(path, mHeartBeatPathPaint);;
			canvas.drawCircle(mHeartBeatWidth+20+mHeartPaintWidth, mTotalHeight/2-mOriginalYPositon[mOffset], 10, mHeartBeatPaint);
		}


		if(StartFirstFrameFlag){
			for(int i=0,j=mHeartPaintWidth+20;i<mHeartBeatWidth;i++,j++){
				if(mDefaultYPostion[i]==-1)
					continue;
				else
					canvas.drawPoint(j, mTotalHeight/2-mDefaultYPostion[i], mHeartBeatPaint);
			}
		}

	}






	/*---------------------------------动画-----------------------------------------*/
	private volatile boolean StartHeartBeatAnmiFlag = false;

	private volatile boolean StartFirstFrameFlag = false;

	private int mFirstFrameOffset = 0;

	private volatile boolean StopHeartBeatAnmiFlag = false;

	/**
	 * 开启心跳动画
	 * */
	private void startHeartBeatAnmi(){
		StartFirstFrameFlag = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				while (mFirstFrameOffset > 0) {
					System.arraycopy(mOriginalYPositon,0,mDefaultYPostion, mFirstFrameOffset,mOriginalYPositon.length - mFirstFrameOffset );
					mFirstFrameOffset--;
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						;
					}
					postInvalidate();
				}
				StartFirstFrameFlag = false;
				StartHeartBeatAnmiFlag = true;
				startSecondFrameAnmi();
			}
		}).start();
	}
	/**
	 * 循环心跳图
	 * */
	private void startSecondFrameAnmi(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!StopHeartBeatAnmiFlag) {
					mOffset += mOffsetSpeed;
					if(mOffset>=mHeartBeatWidth)
						mOffset = 0;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					postInvalidate();
				}
			}
		}).start();
	}

	/**
	 * 开启圆环动画
	 * */
	private void startRingAnim() {
		mAnimAngle = 0;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (mAnimAngle < 360) {
					mAnimAngle++;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					postInvalidate();
				}
				mAnimAngle = -1;   // 停止动画
				stopAnim();
			}
		}).start();
	}


	public void stopAnim(){
		StopHeartBeatAnmiFlag = true;
		StartHeartBeatAnmiFlag = false;

	}

	public void startAnim(){
		startRingAnim();
		startHeartBeatAnmi();
	}

	/*---------------------------------动画  end------------------------------------*/

	/*---------------------------------构造函数-----------------------------------*/
	public RingView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public RingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public RingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}

}
