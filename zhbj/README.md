## 智慧北京新闻客户端 ##

#### App动态图 ####

![https://github.com/you-big-big/MyApp/blob/master/zhbj/gif/whole.gif](https://github.com/you-big-big/MyApp/blob/master/zhbj/gif/whole.gif)

### 功能简介  ###

> 一款实时报导新闻实事的即时新闻APP。在方便用户浏览新闻的同时，还可以分享给其他的好友，通过QQ，微信，QQ空间，邮件等等方式。准备了多个页面，如中国新闻，国际新闻，体育新闻，生活新闻，军事新闻，等等页面。同时也照顾了对字体有要求的用户，能调整字体。


## 大致过程 ##

**Splash页面**

图片演示：
![https://github.com/you-big-big/MyApp/blob/master/zhbj/gif/splash.gif](https://github.com/you-big-big/MyApp/blob/master/zhbj/gif/splash.gif)

> 继承ActionBarActivity会报错，所以使用Activity
public class SplashActivity extends Activity

> 在 activity中修改它的主题，不要在Application中修改
> 在Application中修改主题，会让所有的标题栏都消失
	
	<activity
            android:name=".SplashActivity"
            android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen"

> 主要的动画代码

 设置动画及监听,这里应该很好的一个设计模式，onStart的时候可以做网络数据的获取，基本组件的初始化，等等，onEnd可以跳转页面，也是很好的方法。

	private void startAnim(){

        //动画集合
        AnimationSet set = new AnimationSet(false);

        RotateAnimation rotate = new RotateAnimation(0, 360, 
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setFillAfter(true);//保持动画状态

        ScaleAnimation scale = new ScaleAnimation
                (0, 1, 0, 1, 
                Animation.RELATIVE_TO_SELF, 0.5f, 
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);
        scale.setFillAfter(true);//保持动画状态

        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(1000);
        alpha.setFillAfter(true);//保持动画状态

        set.addAnimation(rotate);
        set.addAnimation(scale);
        set.addAnimation(alpha);

        //设置动画监听
        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //跳转到新手引导页
                startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                finish();
            }
        });

        rlRoot.startAnimation(set);
    }


**引导页面**

图片演示：
![https://github.com/you-big-big/MyApp/blob/master/zhbj/gif/guide.gif](https://github.com/you-big-big/MyApp/blob/master/zhbj/gif/guide.gif)

> 使用ViewPager,创建一个新的GuideActivity，继承Activity
> 找到对应的布局文件


	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        vpGuide = (ViewPager) findViewById(R.id.vp_guide);
        initViews();
        vpGuide.setAdapter(new GuideAdapter());
    }

	初始化ImageView
	private static final int[] mImageIds = 
            new int[]{R.drawable.guide_1,
                    R.drawable.guide_2,
                    R.drawable.guide_3
    };
	private void initViews(){
        mImageViews = new ArrayList<ImageView>();
        //初始化引导页的页面
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(mImageIds[i]);
            mImageViews.add(imageView);
        }

    }

	创建一个GuideAdapter extends PagerAdapter
	比较重要的4个方法重写
	@Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(mImageViews.get(position));

            return mImageViews.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
            //这里，不能有super，不然会报错
        }
    }

> 给Button的点击事件更改界面，一个是图片，一个字体颜色,在button中调用的代码
> 
> 
	android:textColor="@drawable/btn_guide_text_selector"
    android:background="@drawable/btn_guide_selectot"

> 小点点的动画制作

> 在activity_guide.xml中。


> 用一个RelativeLayout包裹LinearLayout


	<RelativeLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="40dp" >

        <LinearLayout
            android:id="@+id/ll_point_group"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal" >
        </LinearLayout>

        <View
            android:layout_width="10dp"
            android:layout_height="10dp"
            android:background="@drawable/shape_point_red" />
    </RelativeLayout>

> 在GuideActivity中
private LinearLayout llPiontGroup;// 引导小点的父控件
GuideActivity中的onCreate方法中
llPiontGroup = (LinearLayout) findViewById(R.id.ll_point_group);

	initViews()中
	// 初始化引导页的小圆点
        for (int i = 0; i < mImageIds.length; i++) {
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);

            if (i > 0) {
                params.leftMargin = 10;
            }

> 将两个小圆点画出来
>drawable中
>xml 文件设置为selector

>调用


 	    android:background="@drawable/shape_point_red"
     	point.setBackgroundResource(R.drawable.shape_point_gray);


**小圆点跟随手指移动**

效果上图有

#### 主要代码如下 ####


		GuideActivity中initView方法中
		// 初始化引导页的小圆点
        for (int i = 0; i < mImageIds.length; i++) {
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);

            if (i > 0) {
                params.leftMargin = 10;
            }

            // 设置引导圆点的大小
            point.setLayoutParams(params);

            llPiontGroup.addView(point);// 将圆点添加给线性布局

        }
        // measure layout draw
        int width = llPiontGroup.getChildAt(1).getLeft() - llPiontGroup.getChildAt(0).getLeft();

        System.out.println("距离：" + width);
        // 获取视图树
        llPiontGroup.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            // 当layout执行结束后回调此方法
            @Override
            public void onGlobalLayout() {
                System.out.println("layout ");
                llPiontGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mPointWidth = llPiontGroup.getChildAt(1).getLeft() - llPiontGroup.getChildAt(0).getLeft();
                // System.out.println("距离：" + width);
            }
        });

	GuidePageListener中设置page滑动的时候滑动小圆点

	@Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int len = (int) (mPointWidth * positionOffset + position * mPointWidth);
            RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) viewRedPoint
                    .getLayoutParams();

            params.leftMargin = len;
            viewRedPoint.setLayoutParams(params);
        }




**导入slidemenu**

>首先，要导入的包的libs下的v4包要先删除
然后把library中的v4包复制过来，然后再add library

>首先确定你的SDK是新的。
其次接下来检查你的.xml文件，文件名不能大写。
如果xml文件太多 ，那么clean一下你的项目，这时候注意看Console的提示。
>
>Console会提示你xml文件错误在哪里
修改完xml文件之后
clean你的项目，再build你的项目
R.java会重新出现或更新

>又搞了一个晚上，这样不行啊。好拖节奏。下次要小心点，超过10分钟立马就换包。

**json解析Gson**

> 1.使用xUtils连接网络，使用gson来解析json

>前提需要一个类，它和json的子节点定义的名称一样
> 如需要这样的一个类来辅助Gson解析Json 比用JsonObject方便了很多
> 
> 


	/**
     * 解析网络数据
     */
    protected void parseData(String result) {
        Gson gson = new Gson();

        NewsData data = gson.fromJson(result, NewsData.class);
        System.out.println("解析结果：" + data);

    }

> 也可以把json转成对象

**将网络数据传递给侧边栏**

> 这里因为BaseFragment中获取到了activity，名字为 mActivity，根本原因是BaseFragment需要贴在MainActivity上的，所以使用getActivity方法，就可一获取到这个activity对象。

> 首先，在NewsCenterPager.class 中


	/**
     * 解析网络数据
     */
    protected void parseData(String result) {
        Gson gson = new Gson();

        NewsData data = gson.fromJson(result, NewsData.class);
        System.out.println("解析结果：" + data);


        //1 拿到MainActivity 对象，
        MainActivity mainUi = (MainActivity) mActivity;

        //3 从 2中的方法获取到 lefMenuFragment 这个侧边栏对象
        LeftMenuFragment lefMenuFragment = mainUi.getLefMenuFragment();
        lefMenuFragment.setMenuData(data);
    }

> MainActivity 中

	 // 获取侧边栏对象
    public LeftMenuFragment getLefMenuFragment() {
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment fragment = (LeftMenuFragment) fm
                .findFragmentByTag(FRAGMENT_LEFT_MENU);
        return fragment;
    }


> 在侧边栏类中接收这些数据


	/**
     * 获取数据
     */
    public void setMenuData(NewsData data) {
        System.out.println("侧边栏拿到数据：" + data);
    }



>其他不同类中获取数据的方式

>intent putExtra

>BroadCast

>startActivityForResult


**侧边栏选中文字，图片变化**

list_menu_item

下面

 		<TextView
        android:id="@+id/tv_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:drawableLeft="@drawable/btn_menu_selector"
        android:drawablePadding="5dp"
        android:text="新闻"
        android:enabled="false"
        android:textColor="@drawable/text_menu_selector"
        android:textSize="25sp" />

btn_menu_selector.xml

  	<item android:drawable="@drawable/menu_arr_select" android:state_enabled="true"></item>
    <item android:drawable="@drawable/menu_arr_normal"></item>

text_menu_selector.xml

    <item android:color="#f00" android:state_enabled="true"></item>
    <item android:color="@android:color/white"></item>


>在listView中。
使用的状态条件是  android:state_enabled="true" 通过这个来更改文字颜色，和图片颜色

**侧边栏点击显示相应的界面**

>新建一个基类
BaseMenuDtailPager

> 再写5个子类继承

>代码基本相同

	/**
	 * 菜单详情页-新闻
	 * 
	 *  @author none1
	 */
	public class NewsMenuDetailPager extends BaseMenuDtailPager {

    public NewsMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {

        TextView text = new TextView(mActivity);
        text.setText("菜单详情页-新闻");
        text.setTextColor(Color.RED);
        text.setTextSize(25);
        text.setGravity(Gravity.CENTER);

        return text;
    }
}

> 和点击事件绑定起来

> NewsCenterPager中

> private ArrayList<BaseMenuDtailPager> mPagers ;//准备4个菜单详情页的集合

	parseData方法中
	//准备4个菜单详情页
        mPagers = new ArrayList<BaseMenuDtailPager>();

        mPagers.add(new NewsMenuDetailPager(mActivity));
        mPagers.add(new TopicMenuDetailPager(mActivity));
        mPagers.add(new PhotoMenuDetailPager(mActivity));
        mPagers.add(new InteractMenuDetailPager(mActivity));

	设置当前显示的界面
	/**
     * 设置当前菜单详情页
     */
    public void setCurrentMenuDetailPager(int position)
    {
        //获取当前页面
        BaseMenuDtailPager pager = mPagers.get(position);
        flContent.removeAllViews();//清除之前的布局
        flContent.addView(pager.mRootView);//将布局文件设置给FramLayout

    }


**滑动事件处理**

>在滑动页面的时候，有可能滑出侧边栏

>如何解决呢

	/**
     * 事件分发,请求父控件及祖宗控件，不要拦截事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        // 是否拦截事件
        if (getCurrentItem() != 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {// 如果是第一个控件，需要让侧边栏显示,请求父控件，拦截
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        return super.dispatchTouchEvent(ev);
    }


> 这样就不会在滑动ViewPager的时候，父控件不会出来。在最左边的时候，把滑动事件传递给父控件

**使用XUtils中的BitmapUtils显示图片**

	class TopNewsAdapter extends PagerAdapter {

        private BitmapUtils utils;

        public TopNewsAdapter(){
            utils = new BitmapUtils(mActivity);
        }
> instantiateItem方法中，就可以实现把网络中获取的图片直接显示到ViewPager中了。

    TopNewsData topNewsData = mTabDetailData.data.topnews.get(position);
            utils.display(image, topNewsData.topimage);

> 实现了快速开发，而且不用担心app过大。里面都是调用的library中的API


**使用ViewPagerIndicator实现新闻位置指示器**

>在tab_detial_pager.xml文件中的RelativeLayout中添加

	<com.viewpagerindicator.CirclePageIndicator
                android:id="@+id/indicator"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_alignParentRight="true"
                android:padding="10dip"

                />

>然后在TabDetailPager中

	@ViewInject(R.id.indicator)
    private CirclePageIndicator mIndicator;//头条新闻位置指示器

> parseData方法中

	mViewPager.setAdapter(new TopNewsAdapter());
        mIndicator.setViewPager(mViewPager);
        mIndicator.setSnap(true);

> 在mViewPager之后设置ViewPager，setSnap。

> 修改样式 优化

	app:fillColor="#f00"
                app:pageColor="@android:color/darker_gray"
                app:radius="3dp"
                app:strokeWidth="0dp"
> parseData方法中

	mIndicator.setViewPager(mViewPager);
        mIndicator.setSnap(true);
        mIndicator.setOnPageChangeListener(this);
        mIndicator.onPageSelected(0);

**ListView+ViewPager一起滑动**

>让它们一起滑动

>首先把开始的ViewPager放在一个新的xml中

>然后在需要显示的activity中找到这个布局

>View headerView = View.inflate(MainActivity.this, R.layout.top_header, null);

>一开始是使用的findViewById。。找不到。。。
>listView.addHeaderView(headerView); 然后给listView设置>headerView就完成了添加ViewPager进入listView，跟随listView一起滑动


**下拉刷新**

> 在view中新建一个RefreshListView，替换掉之间的listView

> 再新建一个布局文件。refresh_header.xml

**ListView点击事件**

>因为有headerView的干扰，所以点击事件不正确
根据headerView的数量，减去相应的数量

	public class RefreshListView extends ListView implements OnScrollListener,
        android.widget.AdapterView.OnItemClickListener

> 首先这个类，需要实现android.widget.AdapterView.OnItemClickListener
这个接口

>然后重写已下的方法

	OnItemClickListener mItemClickListener;

    @Override
    public void setOnItemClickListener(
            android.widget.AdapterView.OnItemClickListener listener) {
        super.setOnItemClickListener(this);
        mItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(parent, view, position-getHeaderViewsCount(), id);
        }
    }
> 这样listView，就把相应的点击事件给换掉了


**item点击过后的显示**

>把点击过的item的编号记录在sp中。

>然后显示的时候判断用户是否已经读取过了。

>TabDetailPager

>lvList.setOnItemClickListener中
		//在本地记录已读状态
                String ids = PrefUtils.getString(mActivity, "read_ids", "");
                String readId = mNewsList.get(position).id;
                if (!ids.contains(readId)) {
                    ids = ids + readId +",";
                    PrefUtils.setString(mActivity, "read_ids", ids);
                }

                mNewsAdapter.notifyDataSetChanged();


> 然后在初始化Adapter的时候把颜色换掉

	@Override
        public View getView(int position, View convertView, ViewGroup parent) 
> 中个重写的方法中换颜色

	String ids = PrefUtils.getString(mActivity, "read_ids", "");
            if (ids.contains(getItem(position).id)) {
                holder.tvTitle.setTextColor(Color.GRAY);

            }else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }


> 优化，只更新单独的item

	/**
     * 改变阅读的状态
     */
    private void changeReadState(View view) {
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setTextColor(Color.GRAY);
    }

> 在lvList.setOnItemClickListener中

                changeReadState(view);// 局部界面刷新,这个view就是被点击的布局界面


**listView点击后展示页面**

> 如何判断是不是一个webView，在手机设置里面的开发者选项中有个布局显示。。。可以看到所有的布局

> 这里的是一个网页。

> 过程
> TabDetailPager中

> lvList.setOnItemClickListener
> 
	// 跳转新闻详情页
                Intent intent = new Intent();
                intent.setClass(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", mNewsList.get(position).url);
                mActivity.startActivity(intent);


> 新建一个类
 > NewsDetailActivity
>onCreate中


	mWebView = (WebView) findViewById(R.id.wv_web);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnShare = (ImageButton) findViewById(R.id.btn_share);
        btnSize = (ImageButton) findViewById(R.id.btn_size);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);// 表示支持js
        settings.setBuiltInZoomControls(true);// 显示放大缩小按钮
        settings.setUseWideViewPort(true);// 支持双击缩放
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);

> 还有一个布局文件
> 名字是activity_news_detail

**缓存**

> 可以把缓存放在文件中。

> 最简单的方法就是把json缓存到本地。。

> 而且会显示侧边栏里面的内容，不用从网络获取了。

> 这点比较好。

	public class CacheUtils {

    /**
     * 设置缓存
     */
    public static void setCache(String key ,String json,Context ctx) {
        PrefUtils.setString(ctx, key, json);
    }

    /**
     * 获取缓存
     */
    public static String getCache(String key,Context ctx) {
        return PrefUtils.getString(ctx, key, null);
    }
}

> NewsCenterPager
> initData() 
	String cache = CacheUtils.getCache(GlobalContants.CATEFORIES_URI, mActivity);

        if (!TextUtils.isEmpty(cache)) {
            parseData(cache);
        }else {
            getDataFromServer();
        }

	getDataFromServer()
	onSuccess

	//设置缓存
    CacheUtils.setCache(GlobalContants.CATEFORIES_URI,result, mActivity);


**图片的轮播**

> 使用

	if (mHandler == null) {
                mHandler = new Handler() {
                    public void handleMessage(android.os.Message msg) {

                        int currentItem = mViewPager.getCurrentItem();
                        if (currentItem < mTopnewsList.size() - 1) {
                            currentItem++;
                        }else {
                            currentItem = 0 ;
                        }
                        mViewPager.setCurrentItem(currentItem);

                        mHandler.sendEmptyMessageDelayed(0, 3000);
                    }
                };

                mHandler.sendEmptyMessageDelayed(0, 3000);
            }


>这里和上次不同的地方在于，这里的思路比较简单，把current的位置置零就能回到了第一页


> 优化，用户按住的时候，不能让图片继续轮播

	instantiateItem
	image.setOnTouchListener(new TopNewsTouchListener());

