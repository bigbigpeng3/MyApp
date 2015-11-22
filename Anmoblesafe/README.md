# 安心手机管家 #

### 功能简介  ###

>一款个人手机安全管家，绑定SIM卡，获取联系人信息，来去电归属
地，截取短信，监听开机，手机定位，播放报警音乐...主要保护用户的隐私信息，同时保护用户的手机。加强手机整体的安全。 

##  大致开发过程 ##

**获取版本号，版本名称**


    //get versionCode and versionName
     private String getVersionName() {
    
      String versionName = "";
      PackageManager packageManager = getPackageManager();
      try {
       PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);// 获取包的内容
       //int versionCode = packageInfo.versionCode;
       versionName = packageInfo.versionName;
       System.out.println("versionName = " + versionName);
      } catch (NameNotFoundException e) {
       e.printStackTrace();
      }
    
      return versionName;
     }


**获取网络信息与JSON解析**

>json数据：
{"versionName":"2.0","versionCode":2,"description":"系统优化，启动速度加快，界面美化","downloadUri":"http://www.baidu.com"}

> http://127.0.0.1:8080/data.json

>　//本机的IP
http://10.0.2.2:8080/data.json

>获取网络信息然后解析JSON
>步骤
>
>* 开启Tomcat服务器，能正确获取到服务器资源，记录地址
>* 新建一个URL 如下


    URL url = new URL("http://192.168.56.251:8080/data.json");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();//获取一个连接
    conn.setRequestMethod("GET");
    conn.setConnectTimeout(5000);
    conn.setReadTimeout(5000);
    conn.connect();//连接
    int responseCode = conn.getResponseCode();
    if (responseCode == 200) {
    InputStream inputStream = conn.getInputStream();
    String result = StreamUtils.streamToString(inputStream);
    System.out.println("网络返回:" + result);
    }

>* 新建一个StreamUtils类用来返回获取到的信息

    public static String streamToString(InputStream in) throws IOException{
      ByteArrayOutputStream out =new  ByteArrayOutputStream();
      int len = 0 ;
      byte[] buffer = new byte[1024];
      while((len = in.read(buffer))!=-1){
       out.write(buffer,0,len);
      }
      String result = out.toString();
      in.close();
      out.close();
      return result;
     }
>在子线程运行
到这里就能从服务器端获取到数据了。注意添加权限


>* 解析JSON


	JSONObject jo = new JSONObject(result);
      mVersionName = jo.getString("versionName");
      mVersionCode = jo.getInt("versionCode");
      mVersionDes = jo.getString("description");
      mDownloadUri= jo.getString("downloadUri");
      System.out.println("mVersionName:"+mVersionName);

**创建一个对话框提示**

	private void showUpdateDialog() {
  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
 	 builder.setTitle("New Version :" + mVersionName);
 	 builder.setMessage(mVersionDes);
 	 builder.setPositiveButton("立即更新", new OnClickListener() {
 	  @Override
	   public void onClick(DialogInterface dialog, int which) {
    System.out.println("立即更新");
    download();
  	 }
 	 });
	  builder.setNegativeButton("以后再说", new OnClickListener	() {

	   @Override
 	  public void onClick(DialogInterface dialog, int which) 	{
    enterHome();
	   }
	  });
  	builder.show();
 	}


**更新界面**

> 首先要说明的是子线程中不能更行UI

>这里使用到了Handler


	 private static final int CODE_UPDATE_DIALOG = 0;
	Message message = new Message();//创建一个Message对象
	//达到了特定的条件就给 message.waht赋值
      if (mVersionCode > getVersionCode()) {// 判断是否有更新
       // 有更新就弹出一个升级对话框
       message.what = CODE_UPDATE_DIALOG;

      }
	//发送Message并带上消息
	mHandler.sendMessage(message);
	//接收从子线程中来的消息，并刷新UI.
	private Handler mHandler = new Handler() {
 	 public void handleMessage(android.os.Message msg) {
 	  switch (msg.what) {
  	 case CODE_UPDATE_DIALOG:
    showUpdateDialog();
    break;
  	default:
    break;
 	  }
 	 }
 	};

**下载APK**

> 使用开源框架xUtils

> 1. 将xUtils的JAR包复制到libs里。

> 2. 代码如下


    public void download() {
     if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
       tvDownload.setVisibility(View.VISIBLE);
       HttpUtils utils = new HttpUtils();
       String target = Environment.getExternalStorageDirectory() + "/update.apk";
       utils.download(mDownloadUri, target, new RequestCallBack<File>() {
    // 文件的下载进度
    @Override
    public void onLoading(long total, long current, boolean isUploading) {
     super.onLoading(total, current, isUploading);
     System.out.println("下载进度:" + current + "/" + total);
     tvDownload.setText("下载进度:" + current*100/total+"%");
    }
    @Override
    public void onSuccess(ResponseInfo<File> arg0) {
     System.out.println("下载成功");
    }
    @Override
    public void onFailure(HttpException arg0, String arg1) {
     Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
    }
  	 });
 	 }else {
  	 Toast.makeText(SplashActivity.this, "你没有SD卡", Toast.LENGTH_SHORT).show();
 	 }
	 }
>有一个奇怪的地方这里它可以更新UI，也就是TextView可以变化，它也没有设置Message。唯一能得出的结论就是它在主线程中运行的。//底层已经帮你封装好了

>这里还判断了有没有SD卡的存在。
Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)

>没有下载前，TextView是不显示的。
tvDownload.setVisibility(View.VISIBLE);
这行代码将它显示了


**下载好APK后自动提示安装**

>在xUtils框架下
在download方法里。


	@Override
    public void onSuccess(ResponseInfo<File> arg0) {
     System.out.println("下载成功");
     
     Intent intent = new Intent(Intent.ACTION_VIEW);
     intent.addCategory(Intent.CATEGORY_DEFAULT);
     intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
     startActivity(intent);
     
    }

**闪屏细节处理**

1 用户点击返回。这时就不会进入主界面了。
解决方法

one : builder.setCancelable(false);//不让用户点击返回，用户体验不好。不建议使用

two：//设置取消的监听，
  builder.setOnCancelListener(new OnCancelListener() {
   @Override
   public void onCancel(DialogInterface dialog) {
    enterHome();
   }
  });//用户体验更好
如图


2 安装时，用户点击取消。这时就卡在了Splash界面

one ： 在onSuccess方法中修改
startActivity(intent);
成
startActivityForResult(intent, 0);
需要重写onActivityResult方法
如图

在里面添加enterHome方法即可


3 样式比较丑
如何修改？
在manifest文件中修改成android:theme="@style/AppTheme" 
如图

隐藏标题栏
在style.xml文件中添加
<item name="android:windowNoTitle">true</item>
如图

getApplicationContext()不能当做this用
因为它没有token。


Activity(token),Context(没有token)
要获取context对象的话，优先使用Activity。


**主页面的开发**

> 过程

>layout文件里有一个TextView作为标题
>
>一个GridView作为放数据的容器

 	<GridView
        android:id="@+id/gv_home"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:numColumns="3"
        android:verticalSpacing="20dp" >
    </GridView>
numColumns要注意使用，指明是几列
GridView的使用方法和ListView差不多

1 创建GridView

private GridView gvHome;

2 findViewById

gvHome = (GridView) findViewById(R.id.gv_home);

并提前给它设置适配器，以免后面忘记写了

gvHome.setAdapter(new HomeAdapter());

3 写一个Adapter类

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
这里的getView方法和我看第一行代码的ListView的Adapter有点不一样


4 初始化数据这里是图片和文字，定义一个int数组，一字符串数组


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
    
在Adapter中就已经配置好了数据。


**Activity切换动画**

1 在startActivity(new Intent(this,Setup1Activity.class));后面
添加这行代码
overridePendingTransition(enterAnim, exitAnim);//分别代表了进入动画，和退出动画
2 创建动画
one：在res目录下创建新的文件夹anim


然后新建XML文件


选择translate，然后在File后面打上名称就好了。

经测试，发现在不同的目录下创建的XML文件不同
在drawable目录下时

明显不一样。

在tran_in.xml文件中代码如下
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="500"
    android:fromXDelta="100%p"
    android:toXDelta="0" >

</translate>

怎么理解呢？
下一步按钮：
从Setup1Activity到Setup2Activity
进入的动画是从100%p到0的。注意：0就是当前屏幕显示的页面
出去的动画就是从0到-100%p的。

返回按钮：
从Setup2Activity到Setup1Activity
进入的动画是从-100%p到0的。
出去的动画就是从0到100%p的。


**手势滑动切换Activity**

1 创建

    
    private GestureDetector mDetector;
    
    mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
       // 监听手势滑动事件
       /**
    * e1表示滑动的起点，e2表示滑动的终点 velovityX表示水平速度 velovityY表示垂直速度
    */
       @Override
       public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    // 向右划，上一页
    if (e2.getRawX() - e1.getRawX() > 100) {  //滑动的距离>100像素
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

>需要在当前的Activity中重写这个方法


    @Override
     public boolean onTouchEvent(MotionEvent event) {
      mDetector.onTouchEvent(event);// 委托手势识别器处理触摸事件
      return super.onTouchEvent(event);
     }
这行代码一定要写，不然没用


精简代码与优化一些设置
新建一个BaseSetupActivity继承Activity
1 把之前的重要代码复制过来

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


>其他的子类继承BaseSetupActivity，就要实现两个重要的方法。
这样就简化了代码。优化了滑动。斜着滑不允许，滑的过慢也不允许。
还可以把SharedPreference设置成public允许子类调用

