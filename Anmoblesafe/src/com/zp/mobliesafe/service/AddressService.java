package com.zp.mobliesafe.service;

import com.zp.mobliesafe.R;
import com.zp.mobliesafe.db.dao.AddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 
 * @author none1
 *
 */
public class AddressService extends Service {

	private TelephonyManager tm;
	private MyListener listener;
	private OutCallReceiver receiver;
	private WindowManager mWM;
	private View view;
	private SharedPreferences mPref;

	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;
	private int winWidth;
	private int winHeight;

	@Override
	public void onCreate() {
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		// 监听打电话的状态
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		receiver = new OutCallReceiver();

		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);

		registerReceiver(receiver, filter);

	}

	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String number = getResultData();

			String address = AddressDao.getAddress(number);

			// Toast.makeText(context,address, Toast.LENGTH_LONG).show();
			showToast(address);
		}

	}

	class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("电话铃响");
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG).show();
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (mWM != null && view != null) {
					mWM.removeView(view);
					view = null;
				}
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	/**
	 * 自定义一个归属地浮窗
	 */
	private void showToast(String text) {

		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		winWidth = mWM.getDefaultDisplay().getWidth();
		winHeight = mWM.getDefaultDisplay().getHeight();

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		params.format = PixelFormat.TRANSLUCENT;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST
		// TYPE_TOAST是权限比较低级的一种
		// TYPE_PHONE电话窗口
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		// 将重心位置设置为左上方,也就是从左上方开始
		params.gravity = Gravity.LEFT + Gravity.TOP;

		params.setTitle("Toast");

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		// 设置浮窗的位置
		params.x = lastX;
		params.y = lastY;

		// view = new TextView(this);
		view = View.inflate(this, R.layout.toast_address, null);

		int[] bgs = new int[] { R.drawable.call_locate_white, R.drawable.call_locate_orange,
				R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int style = mPref.getInt("address_style", 2);

		view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

		TextView tvText = (TextView) view.findViewById(R.id.tv_number);
		tvText.setText(text);

		mWM.addView(view, params);// 将view添加在屏幕上(Window)

		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// System.out.println("onTouching...");
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

					params.x += dx;
					params.y += dy;

					//防止坐标偏移
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > winWidth - view.getWidth()) {
						params.x = winWidth - view.getWidth();
					}
					if (params.y > winHeight - view.getHeight()) {
						params.y = winHeight - view.getHeight();
					}

					mWM.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:

					// 记录坐标点
					Editor edit = mPref.edit();
					edit.putInt("lastX", params.x);
					edit.putInt("lastY", params.y);
					edit.commit();
					break;

				default:
					break;
				}
				return true;
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);// 注销广播
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

}
