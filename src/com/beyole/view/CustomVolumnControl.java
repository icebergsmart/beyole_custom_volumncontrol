package com.beyole.view;

import com.beyole.customvolumncontrol.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CustomVolumnControl extends View {

	// 第一圈颜色
	private int mFirstColor;
	// 第二圈颜色
	private int mSecondColor;
	// 圈的宽度
	private int mCircleWidth;
	// 画笔
	private Paint mPaint;
	// 当前进度
	private int mProgress = 3;
	// 中间的图片
	private Bitmap mImage;
	// 间隙
	private int mSplitSize;
	// 个数
	private int mCount;
	// 描述图片边界
	private Rect mRect;

	public CustomVolumnControl(Context context) {
		this(context, null);
	}

	public CustomVolumnControl(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomVolumnControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomVolumnControl, defStyle, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.CustomVolumnControl_firstColor:
				mFirstColor = array.getColor(attr, Color.CYAN);
				break;
			case R.styleable.CustomVolumnControl_secondColor:
				mSecondColor = array.getColor(attr, Color.BLACK);
				break;
			case R.styleable.CustomVolumnControl_dotCount:
				mCount = array.getInt(attr, 20);
				break;
			case R.styleable.CustomVolumnControl_circleWidth:
				mCircleWidth = array.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
				break;
			case R.styleable.CustomVolumnControl_splitSize:
				mSplitSize = array.getInt(attr, 20);
				break;
			case R.styleable.CustomVolumnControl_bg:
				mImage = BitmapFactory.decodeResource(getResources(), array.getResourceId(attr, 0));
				break;

			}
		}
		array.recycle();
		mPaint = new Paint();
		mRect = new Rect();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 消除锯齿
		mPaint.setAntiAlias(true);
		// 设置画笔宽度
		mPaint.setStrokeWidth(mCircleWidth);
		// 设置画笔断点形状为圆形
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		// 设置空心
		mPaint.setStyle(Paint.Style.STROKE);
		// 圆心
		int center = getWidth() / 2;
		// 半径
		int radius = center - mCircleWidth / 2;
		// 画出小块
		drawOval(canvas, center, radius);
		// 计算圆内切正方形的位置
		int realRadius = radius - mCircleWidth / 2;// 获得内圆半径
		mRect.left = mCircleWidth + (int) (realRadius - Math.sqrt(2) * 1.0f / 2 * realRadius);
		mRect.top = mCircleWidth + (int) (realRadius - Math.sqrt(2) * 1.0f / 2 * realRadius);
		mRect.bottom = (int) (mRect.left + Math.sqrt(2) * realRadius);
		mRect.right = (int) (mRect.left + Math.sqrt(2) * realRadius);
		// 如果图片比较小，则放到正中心
		if (mImage.getWidth() < Math.sqrt(2) * realRadius) {
			mRect.left = (int) (mRect.left + Math.sqrt(2) * 1.0f * realRadius / 2 - mImage.getWidth() / 2);
			mRect.top = (int) (mRect.top + Math.sqrt(2) * 1.0f * realRadius / 2 - mImage.getWidth() / 2);
			mRect.right = mRect.left + mImage.getWidth();
			mRect.bottom = mRect.top + mImage.getHeight();
		}
		canvas.drawBitmap(mImage, null, mRect, mPaint);
	}

	/**
	 * 根据参数画出每个小块
	 * 
	 * @param canvas
	 * @param center
	 * @param radius
	 */
	private void drawOval(Canvas canvas, int center, int radius) {
		// 根据个数和间隙算出每块所占的比率*360
		float itemSize = (360 * 1.0f - mCount * mSplitSize) / mCount;
		// 定义圆弧的形状及界限
		RectF rectF = new RectF(center - radius, center - radius, center + radius, center + radius);
		// 设置圆环的颜色
		mPaint.setColor(mFirstColor);
		for (int i = 0; i < mCount; i++) {
			canvas.drawArc(rectF, (itemSize + mSplitSize) * i, itemSize, false, mPaint);
		}
		// 设置进度圆环颜色
		mPaint.setColor(mSecondColor);
		for (int i = 0; i < mProgress; i++) {
			canvas.drawArc(rectF, (itemSize + mSplitSize) * i, itemSize, false, mPaint);
		}
	}

	/**
	 * 当前进度+1
	 */
	public void up() {
		mProgress++;
			postInvalidate();
	}

	/**
	 * 当前进度-1
	 */
	public void down() {
		mProgress--;
		postInvalidate();
	}

	// 记录手指的抬起和按下的位置，判断用户趋势
	private int xDown, xUp;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
			xUp = (int) event.getY();
			if (xUp > xDown&&mProgress>0) {
				down();
			} else if(xUp < xDown&&mProgress<mCount) {
				up();
			}
			break;

		}
		return true;
	}
}
