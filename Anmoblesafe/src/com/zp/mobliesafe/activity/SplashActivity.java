package com.zp.mobliesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zp.mobliesafe.R;
import com.zp.mobliesafe.utils.StreamUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private static final int CODE_UPDATE_DIALOG = 0;
	private static final int CODE_URL_ERROR = 1;
	private static final int CODE_JSON_ERROR = 2;
	private static final int CODE_NET_ERROR = 3;
	private static final int CODE_ENTER_HOME = 4;

	// 服务器地址 使用的是电脑上的WIFI发出来的。
	private final String SERVER_URL = "http://10.0.2.2:8080";
	// 可以使用公网的IP地址,是我的花生壳
	
	
	private TextView tvVersion;
	private TextView tvDownload;
	private RelativeLayout rlRoot;

	private int mVersionCode;
	private String mVersionName;
	private String mVersionDes;
	private String mDownloadUri;

	private SharedPreferences mPref;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				// enterHome();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "url error",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "data error",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "net error",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvDownload = (TextView) findViewById(R.id.tv_download);
		tvVersion.setText("版本名:" + getVersionName());

		copyDB("address.db");// 在检查之前拷贝数据库

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			checkVerson();
		} else {
			// 延时两秒发送消息
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}
		// 渐变的动画
		AlphaAnimation anim = new AlphaAnimation(0.2f, 1);
		anim.setDuration(2000);
		rlRoot.startAnimation(anim);

	}

	// 拷贝数据库
	private void copyDB(String daName) {
		// 获取文件路径

		File destFile = new File(getFilesDir(), daName);
		// 判断是否已经存在
		if (!destFile.exists()) {
			FileOutputStream out = null;
			InputStream in = null;
			try {
				in = getAssets().open(daName);
				out = new FileOutputStream(destFile);
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) != -1) {

					out.write(buffer, 0, len);
				}

			} catch (IOException e) {

			} finally {
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 下载apk文件
	 */
	public void download() {

		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {

			tvDownload.setVisibility(View.VISIBLE);
			HttpUtils utils = new HttpUtils();
			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";
			utils.download(mDownloadUri, target, new RequestCallBack<File>() {
				// 文件的下载进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					System.out.println("下载进度:" + current + "/" + total);
					tvDownload.setText("下载进度:" + current * 100 / total + "%");
				}

				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					System.out.println("下载成功");
					// 启动安装
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					startActivityForResult(intent, 0);

				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(SplashActivity.this, "下载失败",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(SplashActivity.this, "你没有SD卡", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 进入主页面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	// checkVersion from server
	private void checkVerson() {
		final long startTime = System.currentTimeMillis();
		// 启动子线程异步加载
		new Thread() {

			@Override
			public void run() {
				Message message = new Message();
				HttpURLConnection conn = null;
				try {
					// http://192.168.56.251:8080/data.json
					// http://10.0.2.2:8080/data.json
					URL url = new URL(SERVER_URL + "/data.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();

					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.streamToString(inputStream);
						System.out.println("网络返回:" + result);
						// parse JSON
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mVersionDes = jo.getString("description");
						mDownloadUri = jo.getString("downloadUri");
						// System.out.println("mVersionName:" + mVersionCode);
						// System.out.println("getVersionCode(): " +
						// getVersionCode());
						if (mVersionCode > getVersionCode()) {// 判断是否有更新
							// 有更新就弹出一个升级对话框
							message.what = CODE_UPDATE_DIALOG;
						} else {
							message.what = CODE_ENTER_HOME;
						}
					}

				} catch (MalformedURLException e) {
					message.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					message.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					message.what = CODE_JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;
					if (timeUsed < 2000) {
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(message);
					if (conn != null) {
						conn.disconnect();
					}
				}
				super.run();
			}
		}.start();
	}

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("New Version :" + mVersionName);
		// builder.setCancelable(false);
		builder.setMessage(mVersionDes);
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("立即更新");
				download();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});
		// 设置取消的监听，
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		builder.show();
	}

	// get versionCode and versionName
	private String getVersionName() {

		String versionName = "";
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager
					.getPackageInfo(getPackageName(), 0);// 获取包的内容
			// int versionCode = packageInfo.versionCode;
			versionName = packageInfo.versionName;
			System.out.println("versionName = " + versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
	}

	private int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager
					.getPackageInfo(getPackageName(), 0);// 获取包的内容
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

}
