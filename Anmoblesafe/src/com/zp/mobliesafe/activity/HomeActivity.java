package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;
import com.zp.mobliesafe.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主页面
 * 
 * @author none1
 *
 */
public class HomeActivity extends Activity {

	private GridView gvHome;
	private String[] mItems = new String[]
			{"手机防盗","通讯卫士",
			"软件管理","进程管理",
			"流量统计","手机杀毒",
			"缓冲管理","高级工具",
			"设置中心"
			};
	private int[] mPics = new int[]{
		R.drawable.home_safe,
		R.drawable.home_callmsgsafe,
		R.drawable.home_apps,
		R.drawable.home_taskmanager,
		R.drawable.home_netmanager,
		R.drawable.home_trojan,
		R.drawable.home_sysoptimize,
		R.drawable.home_tools,
		R.drawable.home_settings
	};
	private EditText etPasswordConfirm;
	private EditText etPassword;
	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		
		gvHome = (GridView) findViewById(R.id.gv_home);
		gvHome.setAdapter(new HomeAdapter());
		gvHome.setOnItemClickListener(new OnItemClickListener() {
			//设置中心
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				switch (position) {
				case 0 :
					showPasswordDialog();
					break;
				case 1 :
					showPasswordDialog();
					break;
				case 7:
					startActivity(new Intent(HomeActivity.this,AToolsActivity.class));
					break;
				case 8:
					startActivity(new Intent(HomeActivity.this,SettingActivity.class));
					break;
				default:
					break;
				}
			}
		});
	}
	
	/**
	 * 显示密码弹窗
	 */
	protected void showPasswordDialog() {
		
		//判断是否设置密码
		
		String savePassword = mPref.getString("password", null);
		if (TextUtils.isEmpty(savePassword)) {
			showPasswordSetDailog();
		}else {
			//如果没有设置过，弹出设置密码的弹窗
			showPasswordInputDialog();
		}
		
	}

	/**
	 * 输入密码提示
	 */
	private void showPasswordInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		
		View view = View.inflate(this, R.layout.dialog_input_password,null);
		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		
		etPassword = (EditText) view.findViewById(R.id.et_password);
		
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = etPassword.getText().toString();
				
				if (!TextUtils.isEmpty(password)) {
					String comfirmPassword = mPref.getString("password", null);
					if (MD5Utils.encode(password).equals(comfirmPassword)) {
						//Toast.makeText(HomeActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						startActivity(new Intent(HomeActivity.this,LostFindActivity.class));
					}else {
						Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(HomeActivity.this, "输入框内容不能为空", Toast.LENGTH_SHORT).show();
				}
				
				
				
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		//dialog.setView(view);
		dialog.setView(view,0,0,0,0);
		dialog.show();
		
	}
	/**
	 * 输入密码和确认密码提示
	 */
	private void showPasswordSetDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		
		View view = View.inflate(this, R.layout.dailog_set_password,null);
		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		
		etPassword = (EditText) view.findViewById(R.id.et_password);
		etPasswordConfirm = (EditText) view.findViewById(R.id.et_password_confirm);
		
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = etPassword.getText().toString();
				String passwordConfirm = etPasswordConfirm.getText().toString();
				
				if (!TextUtils.isEmpty(password) && !passwordConfirm.isEmpty()) {
					if (password.equals(passwordConfirm)) {
						//Toast.makeText(HomeActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
						
						mPref.edit().putString("password",MD5Utils.encode(password)).commit();
						
						dialog.dismiss();
						startActivity(new Intent(HomeActivity.this,LostFindActivity.class));
					}else {
						Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
					}
					
				}else {
					Toast.makeText(HomeActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		//dialog.setView(view);
		dialog.setView(view,0,0,0,0);
		dialog.show();
	}

	class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mItems.length;
		}

		@Override
		public Object getItem(int position) {
			return mItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
			ImageView icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView item = (TextView) view.findViewById(R.id.tv_item);
			item.setText(mItems[position]);
			icon.setImageResource(mPics[position]);
			return view;
		}

	}

}
