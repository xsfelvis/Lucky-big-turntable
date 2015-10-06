package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class luckPan extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder mHolder;
	private Canvas mCanvas;

	private Thread t;// 用于绘制的子线程
	private boolean isRunning; // 线程的控制开关
	// 转盘信息
	private String[] mStrs = new String[] { "单反相机", "IPAD", "恭喜发财", "IPHONE",
			"美女蛇", "恭喜发财" };// 转盘文字
	private int[] mImgs = new int[] { R.drawable.danfan, R.drawable.ipad,
			R.drawable.f040, R.drawable.iphone, R.drawable.meizi,
			R.drawable.f040 };// 转盘图片
	private Bitmap[] mImgsBitmap;
	private int[] mColors = new int[] { 0xFFFFC300, 0xFFF17E01, 0xFFFFC300,
			0xFFF17E01, 0xFFFFC300, 0xFFF17E01 };// 盘块颜色
	private int mItemCount = 6;// 盘快数目
	// 盘块范围
	private RectF mRange = new RectF();
	// 盘块直径
	private int mRadius;
	// 绘制盘块的画笔
	private Paint mArcPaint;
	// 绘制文本的画笔
	private Paint mTextPaint;
	// 盘块的滚动速度
	private double mSpeed = 0;
	private volatile float mStartAngele = 0;
	// 是否点击了停止按钮
	private boolean isShouldEnd;
	// 转盘的中心位置
	private int mCenter;
	private int mPadding;// padding取四个padding的最小值，直接以paddingleft为准
	// 背景图
	private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.bg2);
	// 文字的大小
	private float mTextSize = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());// 转变为标准尺寸

	public luckPan(Context context) {
		this(context, null);
	}

	public luckPan(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHolder = getHolder();
		mHolder.addCallback(this); // 添加回调结构

		setFocusable(true);// 可获得焦点
		setFocusableInTouchMode(true);// 可以点击
		setKeepScreenOn(true);// 设置常亮

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mPadding = getPaddingLeft();
		// 直径
		mRadius = width - mPadding * 2;
		// 中心点
		mCenter = width / 2;
		setMeasuredDimension(width, width);// 用于实现自定义组件的大小
	};

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 初始化绘制盘块画笔
		mArcPaint = new Paint();
		mArcPaint.setAntiAlias(true);// 抗锯齿方法
		mArcPaint.setDither(true);// 防抖动
		// 初始化文本的画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(0xffffffff);// 设置颜色白色
		mTextPaint.setTextSize(mTextSize);// 设置大小
		// 圆弧的绘制范围
		mRange = new RectF(getPaddingLeft(), getPaddingLeft(), getPaddingLeft()
				+ mRadius, getPaddingLeft() + mRadius);
		// 初始化图片
		mImgsBitmap = new Bitmap[mItemCount];
		for (int i = 0; i < mItemCount; i++) {
			mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),
					mImgs[i]);
		}

		isRunning = true;
		t = new Thread(this);
		t.start();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		isRunning = false;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRunning) {
			long start = System.currentTimeMillis();
			draw();// 进行绘制重要！
			long end = System.currentTimeMillis();
			if (end - start < 50) {
				try {
					Thread.sleep(50 - (end - start));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		// 获取canvas
		try {
			mCanvas = mHolder.lockCanvas();
			if (mCanvas != null) {
				// 绘制背景
				drawBg();
				// 绘制盘块
				float tmpAngle = mStartAngele;
				float sweepAngle = (float) (360 / mItemCount);
				for (int i = 0; i < mItemCount; i++) {
					mArcPaint.setColor(mColors[i]);
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true,
							mArcPaint);// 绘制弧形（扇形）
					/**
					 * 参数说明： 第一个参数：矩形实例 第二个参数：弧形的起始角度 第三个参数：弧形的终止角度
					 * 第四个参数：是否绘制中心点
					 * ，若为真，起始点与终止点都会分别连接中心，从而形成封闭图形；如果为假，则起始点直接连接终止点，从而形成封闭图形
					 */
					// 绘制文本
					drawText(tmpAngle, sweepAngle, mStrs[i]);
					// 绘制图标
					drawIconn(tmpAngle, mImgsBitmap[i]);
					tmpAngle += sweepAngle;

				}
				mStartAngele += mSpeed;
				// 如果点击了停止
				if (isShouldEnd) {
					mSpeed -= 1;
				}
				if (mSpeed <= 0) {
					mSpeed = 0;
					isShouldEnd = false;
				}

			}
		} catch (Exception e) {
			/*
			 * // TODO Auto-generated catch block e.printStackTrace();
			 */
		} finally {
			// canvas 释放
			if (mCanvas != null) {
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}

	}

	// 表盘转动
	/* public void LuckyStart() */
	public void LuckyStart(int index) {
		/*
		 * mSpeed = 50; isShouldEnd = false;
		 */
		int angle = 360 / mItemCount;// 每一项的角度
		// 计算当前index的中奖范围
		// 1:150-210 ipad
		// 0:210-270 单反
		float from = 270 - (index + 1) * angle;
		float end = from + angle;
		// 设置停下来需要旋转的距离
		float targetFrom = 4 * 360 + from;
		float targetEnd = 4 * 360 + end;
		// v1-0 每次减一的，到targetFrom
		float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
		float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

		mSpeed = v1 + Math.random() * (v2 - v1);
		isShouldEnd = false;
	}

	public void LuckyEnd() {
		isShouldEnd = true;
		mStartAngele = 0;
	}

	// 转盘是否在旋转
	public boolean isStart() {
		return mSpeed != 0;
	}

	public boolean isShouldEnd() {
		return isShouldEnd;
	}

	// 绘制图片
	private void drawIconn(float tmpAngle, Bitmap bitmap) {
		// TODO Auto-generated method stub
		// 设置图片宽度为直径的1/2
		int imgWidth = mRadius / 8;
		float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);// 弧长
		int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
		int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));// 图片中心点位置
		// 确定图片
		Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
				/ 2, y + imgWidth / 2);
		mCanvas.drawBitmap(bitmap, null, rect, null);

	}

	// 绘制每个盘块的文本
	private void drawText(float tmpAngle, float sweepAngle, String string) {
		Path path = new Path();
		path.addArc(mRange, tmpAngle, sweepAngle); // 圆弧路径
		float textWidth = mTextPaint.measureText(string);
		// 利用水平偏移量让文字居中
		int hOffset = (int) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);
		// 垂直偏移量
		int vOffset = mRadius / 2 / 6;
		mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
	}

	private void drawBg() {
		// 绘制背景
		mCanvas.drawColor(0xffffffff);
		mCanvas.drawBitmap(mBgBitmap, null, new RectF(mPadding / 2,
				mPadding / 2, getMeasuredWidth() - mPadding / 2,
				getMeasuredHeight() - mPadding / 2), null);

	}

}
