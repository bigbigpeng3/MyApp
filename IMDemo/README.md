## 随心聊即时通信软件

> 这是一款自己独立开发，使用openfire + smack + spark 等框架编写的一款即时通信软件。

### 功能简介 ###

> 1. 登陆，注册。
> 2. 点对点的聊天功能，相互添加好友。
> 3. 后台接受到其他用户发来的消息并使用通知和红色角标提示提示。
> 4. 聊天记录的保存。

#### 登陆 ####

![login](https://github.com/you-big-big/MyApp/blob/master/IMDemo/gif/login.gif)

#### 注册+添加好友 ####

![logout](https://github.com/you-big-big/MyApp/blob/master/IMDemo/gif/logout.gif)

#### 新信息提示使用通知 ####
![notification](https://github.com/you-big-big/MyApp/blob/master/IMDemo/gif/notification.gif)

#### 新信息提示使用角标 ####
![jiaobiao](https://github.com/you-big-big/MyApp/blob/master/IMDemo/gif/jiaobiao.gif)

## 大概过程 ##

**首先实现了登陆功能，并且在openfire上的用户头像在线**
> 
> 
	
 	 	public class MainActivity extends Activity
           
	 	implements OnClickListener, ConnectionListener

 	 	XMPPTCPConnectionConfiguration.Builder config = 

		XMPPTCPConnectionConfiguration
                            .builder();

                    config.setSecurityMode(
                            			ConnectionConfiguration.SecurityMode.disabled);

                    config.setUsernameAndPassword("test" + "@" + "10.0.2.2",
                            "test");

                    config.setServiceName("none1-pc");

                    config.setHost("10.0.2.2");

                    config.setPort(5222);
                    config.setDebuggerEnabled(true);
                    mConnection = new XMPPTCPConnection(config.build());
                    mConnection.setPacketReplyTimeout(5000);
                    mConnection.addConnectionListener(MainActivity.this);
                    try {
                        mConnection.connect();
                    } catch (SmackException | IOException | XMPPException e) {
                        e.printStackTrace();
                    }
	@Override
    public void connected(XMPPConnection arg0) {
        Log.e("ZP-XMPP", "connected");
        try {
            mConnection.login("test", "test");
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
    }

 **使用了spark来作为接收端，主要的发送代码为**
	
	public void sendMessage() {
        ChatManager chat = ChatManager.getInstanceFor(con);
        Chat ct = chat.createChat("test2@none1-pc", new ChatMessageListener() {
            @Override
            public void processMessage(Chat arg0, Message message) {
                System.out.println("Received message: " + message);
            }
        });
        try {
            ct.sendMessage("i am test !");
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

>测试不给ct对象设置监听收到消息试试。也行。那么最简短的发送代码为：

	public void sendMessage() {
        ChatManager chat = ChatManager.getInstanceFor(con);
        Chat ct = chat.createChat("test2@none1-pc");
        try {
            ct.sendMessage("i am test !");
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
	 }
>前面的代码可以监听到其他用户发送过来的代码。测试一下，这就的话，就是实现了接收到消息和发送消息了。
不行，设置了ChatMessageListener也不行，接收不到。找了半天在这个网址找到了解决方法。

[http://stackoverflow.com/questions/28295783/smack-4-1-processmessage-method-does-not-called](http://stackoverflow.com/questions/28295783/smack-4-1-processmessage-method-does-not-called "solve")

好了。解决了。。
代码和优化如下
	
	ChatManager cm = ChatManager.getInstanceFor(con);
        ct = cm.createChat("test2@none1-pc");

        cm.addChatListener(new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean b) {
                 chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, Message message) {
                              if (message.getType().equals(Message.Type.chat) || message.getType().equals(Message.Type.normal)) {
                                  if (!TextUtils.isEmpty(message.getFrom())  && !TextUtils.isEmpty(message.getBody())) {
                                      System.out.println("message.getFrom()"+message.getFrom());
                                      System.out.println("message.getBody()"+message.getBody());

                                  }
                              }
                        }
                    });
            }
        });

这样就接收到了从spark客户端发送过来的消息了。

 **获取好友列表**

	public void getPeople() {
        Collection<RosterEntry> rosters = Roster.getInstanceFor(con).getEntries();
        System.out.println("我的好友列表：=======================");
        for (RosterEntry rosterEntry : rosters) {
            System.out.print("name: " + rosterEntry.getName() + ",jid: "
                    + rosterEntry.getUser());
            System.out.println("");
        }
        System.out.println("我的好友列表：=======================");
    }

获取完成

 **Bug+优化**

先优化了登陆注册问题，然后是添加了SharedPrefences记录用户是否登陆。
如果网络不稳定，这个直接登陆的效果不是很好。这个做就显得有点愚蠢

决定先优化网络的请求和页面跳转的正确性

优化网络请求：暂时只知道单例模式，让连接只有一个。
async-http 可以去看看是否有我想要的东西,感觉不是的，先看看HM的代码，还有SplashActivity这个里面的登陆处理过程。看别人代码也很重要。
>1. 提高了con.setPacketReplyTimeout(10000);延时。感觉还行。

正确性，可以让客户端等待接收服务器的返回信息，然后再跳转页面。这里是使用主线程呢，还是使用子线程呢？这是个问题，还有就是服务器会返回数据么，这里的服务器是openfire，所以需要继续查找smack中的API，来实现这个功能。

>1. 看到了一个方法
isAuthenticated() 返回的是一个boolean值
在登陆和注册时 使用一个子线程来判断是否登陆成功，就可以成功地减少网络延迟所带来的问题了。
>2. 给Connection类设置一个public static boolean isConnected = false;
这样在验证成功的时候就可以把它设置为已经登陆过的状态了，在点击按钮的时候初始化con的时候，判断。

	public void connectServer() {

        if (Connection.isConnected) {
            Connection.closeConnect();
            Connection.isConnected = false;
        }
        if (con == null) {
            Connection.initConnection();
        }
    }

这样就貌似解决了登陆后，注册后 回到这个界面的时候不能重新登陆和注册了。


 **实现公网上的聊天功能**

	public static String HOST = "我的公网地址";//10.0.2.2 ....
    public static int PORT = 19488 ;//5222
	config.setHost(HOST);
	config.setPort(PORT);

使用花生壳2.4 内网映射。

登陆的时候也有延迟，需要多点击几下，怎么办？优化了网络请求，使用子线程请求。


 **把登陆，注册Activity变成fragment**


可以从HM，标签页app，视图页Viewpager中复习知识。

HM的SignInFra 使用的布局文件是fra_sign_in。
SignUpFra 是fra_sign_up

他们依附在哪一个Activity上面呢？
依附在LoginActivity上面，LoginActivity的初始界面只是一个FrameLayout。
第一个显示的是logoFrag。

在LoginActivity中

这样在替换界面的时候很好用
遇到了	

		currentFra = new LogoFrag();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contanier_login, currentFra,TAG_LOGO);
        //transaction.addToBackStack(currentTag);
        transaction.commit();
如果不把transaction.addToBackStack(currentTag);注释掉的话，在logofrag返回的时候，就会返回到空白的LoginActivity中，这样很明显体验不好。

在LogoFrag中是如何点击跳转页面的呢？。

    private void signIn() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((LoginActivity) activity).goToSignIn();
        }
    }
把Activity强转成为(LoginActivity) ，因为必须是这个Activity，然后调用里面的方法就行了。

那么登陆界面的Activity转换成为Fragment完成。


 **给HomeActivity添加Fragment**

>首先创建出需要的Fragment。
然后添加对应的Indicator，给每个Indicator添加对应的Fragment。

>在搞完之后它报错了
>
[http://www.tuicool.com/articles/qyIvAf](http://www.tuicool.com/articles/qyIvAf "http://www.tuicool.com/articles/qyIvAf")

1 需要替换V4包  我换了HM的v4，还有电影院的包，都不行。

2 @android:id/tabhost 改成 @+id/tabhost 还是不行，不过报错的时候没有no tab content framelayout found for id 了

3 去掉android.support.v4.app.FragmentTabHost内的TabWidget和FrameLayout.

4 一直在报错，实在是找不到解决方法了。网上也没有好的解决方法，那么就先使用以前的代码试试。


 	<android.support.v4.app.FragmentTabHost
        android:id="@+id/mytabhost"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" >

    </android.support.v4.app.FragmentTabHost>
mTabHost = (FragmentTabHost) findViewById(R.id.mytabhost);

这样就基本解决了这个问题

添加TABHOST方法就在HomeActivity里面了。


**后台接收消息**

> 用什么接收服务器发来的消息？如何把消息传给正确的用户？如何把消息直接显示在用户名称的下面？然后点击进入的时候，历史聊天记录还在？包括新的最新的消息。接收消息存储起来，而不是退出就没有了。又该如何实现？

> 这里使用什么来后台接收来自服务器的信息呢？
 Service？广播？
这里使用Service，给Service创建监听器就好了，接收所有的消息。

> 进入HomeActivity就开启服务监听所有发送来的消息，如果接收到了消息就创建一个提示，用户点击提示，就可以跳转到相应的聊天界面了。
成功！


>这里包含了监听以及页面的跳转到对应的chatActivity。实现了点击通知可以和对应的用户聊天。用户如果退出了当前的聊天界面，就让Service提醒有新的消息。


 **保存聊天记录**


>SQLite 的创建，从网络中的消息，保存。然后需要实时调取里面的聊天记录。

>保证用户每次打开就是最新的消息显示在最下方。

> 1. 什么时候创建对应的数据库？
> 2. 在用户登陆成功的时候，创建数据库。
> 3. 表里面需要哪些属性?content type user
> 4. 怎么添加对应的用户数据？给存入数据库的数据添加一个user 列 。只要是这个user，那么查找的时候就按照这个user查找到的就行了发送和接收的时候都记录存储在对应的表里
> 5. 怎么读取对应的用户数据？因为存储的时候已经给每一条信息添加了user这个属性，type，那么就可以直接加载到ListView中
> 6. 遍历表里面的数据，将读取到的数据加载到ListView中，即每次用户打开对应的聊天窗口，对应的聊天记录就应该显示在窗口中。


**未读的消息**

>从服务器发来的消息，在好友列表里设置未读小心的数量。

>需要对Msg这个类添加一个。。。


>结果还是没有处理完成.不知道这个未读的消息到底要加在哪个类里面，

> 1. 给Contacts这个类添加一个新变量private int unread
    提供get set 方法，并设置初始为0.

> 2. 然后在ContactFrag中getView方法中，判断unread的值。
    根据unread的值显示对应的TextView。

>3. 获取到unread的值，Service获取到发送用户名，然后遍历存储用户名的集合，找到发送的用户名。然后给它的unread增加。



**广播功能**


> 添加广播功能，可以接收到系统的联网情况,在genymotion模拟器上模拟的不是很好，真机上成功。

>// 关于广播接收的声明
    
	private IntentFilter intentFiler;
    private InternetConnectedReceiver myNetReceiver ;

	intentFiler = new IntentFilter();
        intentFiler.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        myNetReceiver = new InternetConnectedReceiver();
        registerReceiver(myNetReceiver, intentFiler);

	@Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myNetReceiver);
    }


> 需要在Mainfest中添加权限
 	`<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`







