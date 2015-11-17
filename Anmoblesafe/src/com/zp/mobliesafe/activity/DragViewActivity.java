package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 修改归属地显示位置
 * 
 * @author none1
 *
 */
public class DragViewActivity extends Activity {

	private TextView tvTop;
	private TextView tvButton;

	private ImageView ivDrag;

	private int startX;
	private int startY;
	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		tvTop = (TextView) findViewById(R.id.tv_top);
		tvButton = (TextView) findViewById(R.id.tv_bottom);
		ivDrag = (ImageView) findViewById(R.id.iv_drag);

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);

		// onMeasure测量view,onLayout安放位置,onDraw绘制
		// 这里不能用这个方法，因为还没有测量完成，就不能安放位置
		// ivDrag.layout(lastX, lastX, lastX + ivDrag.getWidth(), lastY +
		// ivDrag.getHeight());

		// 获取屏幕宽高
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winheight = getWindowManager().getDefaultDisplay().getHeight();

		if (lastY > winheight / 2) {// 让上边的显示，下边的隐藏
			tvTop.setVisibility(View.VISIBLE);
			tvButton.setVisibility(View.INVISIBLE);
		} else {
			tvTop.setVisibility(View.INVISIBLE);
			tvButton.setVisibility(View.VISIBLE);
		}

		RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) ivDrag
				.getLayoutParams();// 获取布局对象

		layoutParams.leftMargin = lastX;
		layoutParams.topMargin = lastY;
		ivDrag.setLayoutParams(layoutParams);// 重新设置位置

		final long[] mHits = new long[2];// 数组长度表示要点击的次数
		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 把图片居中
					ivDrag.layout(winWidth / 2 - ivDrag.getWidth() / 2, ivDrag.getTop(),
							winWidth / 2 + ivDrag.getWidth() / 2, ivDrag.getBottom());

				}
			}
		});

		ivDrag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					int dx = endX - startX;
					int dy = endY - startY;

					// 更新左上右下坐标距离
					int l = ivDrag.getLeft() + dx;
					int r = ivDrag.getRight() + dx;
					int t = ivDrag.getTop() + dy;
					int b = ivDrag.getBottom() + dy;

					// 判断是否超出了屏幕的范围
					if (l < 0 || r > winWidth || t < 0 || b > winheight - 20) {
						break;
					}

					if (t > winheight / 2) {// 让上边的显示，下边的隐藏
						tvTop.setVisibility(View.VISIBLE);
						tvButton.setVisibility(View.INVISIBLE);
					} else {
						tvTop.setVisibility(View.INVISIBLE);
						tvButton.setVisibility(View.VISIBLE);
					}
					// 更新界面
					ivDrag.layout(l, t, r, b);

					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// 记录坐标点
					Editor edit = mPref.edit();
					edit.putInt("lastX", ivDrag.getLeft());
					edit.putInt("lastY", ivDrag.getTop());
					edit.commit();
					break;

				default:
					break;
				}
				// 事件要往下传递 需要false 让click可以响应
				return false;
			}
		});
	}
}
