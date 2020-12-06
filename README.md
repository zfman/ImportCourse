# ImportCourse
适配平台是什么？简单来说，它是一组接口以及封装好的一些组件，使用它可以快速解析各个高校的课程，提供的功能有以下几点：

- 根据学校名称或教务类型获取教务url、课程解析函数（Js）等相关信息
- 获取页面源码
- 通过Js解析出课程集合
- 申请适配（上传源码）

### 申请appkey

请将你的应用程序包名、应用签名发送到邮箱`1193600556@qq.com`，我会在一天内将appkey回复给你，接入完毕后请务必告知我，我会将你加入联盟支持的列表中。

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
	        implementation 'com.github.zfman.ImportCourse:importcourselib:2.2.3'
	}
```

### 鉴权

在MyApplication中进行初始化：
```java
	public class MyApplication extends Application {
		@Override
		public void onCreate() {
			super.onCreate();
			AdapterLibManager.init("申请的appkey");
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

### 教务导入页面

```java
    Intent intent=new Intent(MainActivity.this, AutoImportActivity.class);
    startActivityForResult(intent,100);
```

**接收解析的结果**

- `ParseManager`是解析管理类，可以判断是否解析成功以及取出解析结果
- `ParseResult`是本平台提供的课程实体类

接收结果示例如下：

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==AutoImportActivity.RESULT_CODE){
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
