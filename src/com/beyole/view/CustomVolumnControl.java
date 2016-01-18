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

	// ��һȦ��ɫ
	private int mFirstColor;
	// �ڶ�Ȧ��ɫ
	private int mSecondColor;
	// Ȧ�Ŀ��
	private int mCircleWidth;
	// ����
	private Paint mPaint;
	// ��ǰ����
	private int mProgress = 3;
	// �м��ͼƬ
	private Bitmap mImage;
	// ��϶
	private int mSplitSize;
	// ����
	private int mCount;
	// ����ͼƬ�߽�
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
		// �������
		mPaint.setAntiAlias(true);
		// ���û��ʿ��
		mPaint.setStrokeWidth(mCircleWidth);
		// ���û��ʶϵ���״ΪԲ��
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		// ���ÿ���
		mPaint.setStyle(Paint.Style.STROKE);
		// Բ��
		int center = getWidth() / 2;
		// �뾶
		int radius = center - mCircleWidth / 2;
		// ����С��
		drawOval(canvas, center, radius);
		// ����Բ���������ε�λ��
		int realRadius = radius - mCircleWidth / 2;// �����Բ�뾶
		mRect.left = mCircleWidth + (int) (realRadius - Math.sqrt(2) * 1.0f / 2 * realRadius);
		mRect.top = mCircleWidth + (int) (realRadius - Math.sqrt(2) * 1.0f / 2 * realRadius);
		mRect.bottom = (int) (mRect.left + Math.sqrt(2) * realRadius);
		mRect.right = (int) (mRect.left + Math.sqrt(2) * realRadius);
		// ���ͼƬ�Ƚ�С����ŵ�������
		if (mImage.getWidth() < Math.sqrt(2) * realRadius) {
			mRect.left = (int) (mRect.left + Math.sqrt(2) * 1.0f * realRadius / 2 - mImage.getWidth() / 2);
			mRect.top = (int) (mRect.top + Math.sqrt(2) * 1.0f * realRadius / 2 - mImage.getWidth() / 2);
			mRect.right = mRect.left + mImage.getWidth();
			mRect.bottom = mRect.top + mImage.getHeight();
		}
		canvas.drawBitmap(mImage, null, mRect, mPaint);
	}

	/**
	 * ���ݲ�������ÿ��С��
	 * 
	 * @param canvas
	 * @param center
	 * @param radius
	 */
	private void drawOval(Canvas canvas, int center, int radius) {
		// ���ݸ����ͼ�϶���ÿ����ռ�ı���*360
		float itemSize = (360 * 1.0f - mCount * mSplitSize) / mCount;
		// ����Բ������״������
		RectF rectF = new RectF(center - radius, center - radius, center + radius, center + radius);
		// ����Բ������ɫ
		mPaint.setColor(mFirstColor);
		for (int i = 0; i < mCount; i++) {
			canvas.drawArc(rectF, (itemSize + mSplitSize) * i, itemSize, false, mPaint);
		}
		// ���ý���Բ����ɫ
		mPaint.setColor(mSecondColor);
		for (int i = 0; i < mProgress; i++) {
			canvas.drawArc(rectF, (itemSize + mSplitSize) * i, itemSize, false, mPaint);
		}
	}

	/**
	 * ��ǰ����+1
	 */
	public void up() {
		mProgress++;
			postInvalidate();
	}

	/**
	 * ��ǰ����-1
	 */
	public void down() {
		mProgress--;
		postInvalidate();
	}

	// ��¼��ָ��̧��Ͱ��µ�λ�ã��ж��û�����
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
