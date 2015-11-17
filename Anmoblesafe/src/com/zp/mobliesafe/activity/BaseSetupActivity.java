package com.zp.mobliesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

/**
 * 设置引导页的基类1展示 不需要在清单文件中注册，
 * 
 * @author none1
 *
 */
public abstract class BaseSetupActivity extends Activity {

	private GestureDetector mDetector;
	
	public SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			// 监听手势滑动事件
			/**
			 * e1表示滑动的起点，e2表示滑动的终点 velovityX表示水平速度 velovityY表示垂直速度
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				// 纵向滑动幅度是否过大，过大的话不允许切换界面
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
					Toast.makeText(BaseSetupActivity.this, "不能这样滑哦", Toast.LENGTH_SHORT).show();
					return true;
				}
				// 判断滑动是否过慢
				if (Math.abs(velocityX) < 150) {
					Toast.makeText(BaseSetupActivity.this, "滑的有点慢~", Toast.LENGTH_SHORT).show();

				}
				// 向右划，上一页
				if (e2.getRawX() - e1.getRawX() > 100) {
					showPreviousPage();
					return true;
				}
				// 向左划动，下一页
				if (e1.getRawX() - e2.getRawX() > 100) {
					showNextPage();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	/**
	 * 展示上一页
	 */
	public abstract void showPreviousPage();

	/**
	 * 展示下一页
	 */
	public abstract void showNextPage();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);// 委托手势识别器处理触摸事件
		return super.onTouchEvent(event);
	}

	/**
	 * button的点击事件，跳转到下一页
	 * 
	 * @param view
	 */
	public void next(View view) {
		showNextPage();
	}

	/**
	 * 跳转到上一页
	 * 
	 * @param view
	 */
	public void previous(View view) {
		showPreviousPage();
	}

}
