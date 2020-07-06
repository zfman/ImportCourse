# ImportCourse
适配平台是什么？简单来说，它是一组接口以及封装好的一些组件，使用它可以快速解析各个高校的课程，提供的功能有以下几点：

- 根据学校名称或教务类型获取教务url、课程解析函数（Js）等相关信息
- 获取页面源码
- 通过Js解析出课程集合
- 申请适配（上传源码）

### 申请appkey

请将你的应用程序包名发送到邮箱`1193600556@qq.com`，我会在一天内将appkey回复给你，接入完毕后请务必告知我，我会将你加入联盟支持的列表中。

### 引入依赖库 [![]([![](https://jitpack.io/v/zfman/ImportCourse.svg)](https://jitpack.io/#zfman/ImportCourse))

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency

```gradle
	dependencies {
	        implementation 'com.github.zfman:ImportCourse:v2.0.0'
	}
```

### 鉴权

在MyApplication中进行初始化：
```java
	public class MyApplication extends Application {
		@Override
		public void onCreate() {
			super.onCreate();
			AdapterLibManager.init("申请的appkey","埋点前缀");
      
      StatService.registerActivityLifecycleCallbacks(this);
        StatManager.register(new IStatSendCallback() {
            @Override
            public void sendKVEvent(Context context, String eventId, Map<String, String> params) {
                Properties properties=new Properties();
                if(params!=null){
                    for(Map.Entry<String,String> entry:params.entrySet()){
                        if(entry!=null){
                            if(entry.getKey()==null||entry.getValue()==null){
                                Log.d(TAG, "sendKVEvent: key="+entry.getKey()+"; v="+entry.getValue()+";id="+eventId);
                            }else{
                                properties.setProperty(entry.getKey(),entry.getValue());
                            }
                        }
                    }
                }
                StatService.trackCustomKVEvent(getApplicationContext(),eventId,properties);
            }

            @Override
            public void reportMultiAccount(Context context, StatManager.AccountType type, String v) {
                StatMultiAccount.AccountType thisType=null;
                if(type==StatManager.AccountType.OPEN_QQ){
                    thisType=StatMultiAccount.AccountType.OPEN_QQ;
                }else if(type==StatManager.AccountType.OPEN_WEIXIN){
                    thisType=StatMultiAccount.AccountType.OPEN_WEIXIN;
                }else if(type==StatManager.AccountType.GUEST_MODE){
                    thisType=StatMultiAccount.AccountType.GUEST_MODE;
                }else if(type==StatManager.AccountType.CUSTOM){
                    thisType=StatMultiAccount.AccountType.CUSTOM;
                }else{
                    thisType=StatMultiAccount.AccountType.GUEST_MODE;
                }
                StatMultiAccount account = new StatMultiAccount(
                        thisType, v);
                long time = System.currentTimeMillis() / 1000;
                // 登陆时间，单秒为秒
                account.setLastTimeSec(time);
                StatService.reportMultiAccount(context, account);
            }
        });
    }
		}
	}
```

设置MyApplication：
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.zhuangfei.adapterlibdemo">

    <application
        android:name=".MyApplication">
        <!--省略其他内容-->
    </application>

</manifest>
```

### 搜索页面

搜索页面是课程适配的入口，只要前往搜索页，然后在本页面接收返回的数据即可

```java
    public static final int REQUEST_CODE=1;
```

```java
    Intent intent=new Intent(this, SearchSchoolActivity.class);
    startActivityForResult(intent,REQUEST_CODE);
```


默认情况下进入搜索页面是空的，当然也可以设置一个默认的关键字，进入搜索页面后立即请求，示例如下:

```java
	Intent intent=new Intent(MainActivity.this, SearchSchoolActivity.class);
	intent.putExtra(SearchSchoolActivity.EXTRA_SEARCH_KEY,"河南理工大学");
	startActivityForResult(intent,REQUEST_CODE);
```

**接收解析的结果**

- `ParseManager`是解析管理类，可以判断是否解析成功以及取出解析结果
- `ParseResult`是本平台提供的课程实体类

接收结果示例如下：

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==SearchSchoolActivity.RESULT_CODE){
            if(ParseManager.isSuccess()&&ParseManager.getData()!=null){
                List<ParseResult> result=ParseManager.getData();
                String str="";
                for(ParseResult item:result){
                    str+=item.getName()+"\n";
                }
                Toast.makeText(MainActivity.this,str, Toast.LENGTH_SHORT).show();
            }
        }
    }
```

有任何问题可以联系开发者邮箱`1193600556@qq.com`
