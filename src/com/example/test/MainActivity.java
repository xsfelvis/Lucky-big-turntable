package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private luckPan mLucyPan;
	private ImageView mStartBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLucyPan = (luckPan) findViewById(R.id.id_luckPan);
		mStartBtn = (ImageView) findViewById(R.id.id_startbtn);
		mStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mLucyPan.isStart()) {
					/* mLucyPan.LuckyStart(); */
					mLucyPan.LuckyStart(1);
					mStartBtn.setImageResource(R.drawable.stop);
				} else {
					if (!mLucyPan.isShouldEnd()) {
						mLucyPan.LuckyEnd();
						mStartBtn.setImageResource(R.drawable.start);
					}
				}

			}
		});

	}

}
