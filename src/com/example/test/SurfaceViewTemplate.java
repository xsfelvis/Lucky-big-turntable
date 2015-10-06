package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
/**
 * 
 * @author ELVIS
 *surfaceView 常用编写模式
 */
public class SurfaceViewTemplate extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder mHolder;
	private Canvas mCanvas;

	private Thread t;// 用于绘制的子线程
	private boolean isRunning; // 线程的控制开关

	public SurfaceViewTemplate(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public SurfaceViewTemplate(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHolder = getHolder();
		mHolder.addCallback(this); // 添加回调结构？？？

		setFocusable(true);// 可获得焦点
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);// 设置常亮

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		isRunning = true;
		t = new Thread(this);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		isRunning = false;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(isRunning){
			draw();//进行绘制
		}
	}

	private void draw() {
		//获取canvas
		try {
			mCanvas = mHolder.lockCanvas();
			if(mCanvas!=null){
				//draw 
			}
		} catch (Exception e) {
			/*// TODO Auto-generated catch block
			e.printStackTrace();*/
		}
		finally{
			//canvas 释放
			if(mCanvas!=null){
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
		
	}
	
}

