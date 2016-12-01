# cordova_corsswalk_android
在普通的android工程里集成cordova、corsswalk，并自定义cordova插件，修改拍照插件等等...

内容：

	1、集成cordova，使用cordova提供的拍照插件，并修改其返回结果，自定义拨打电话插件
	2、为了提高html渲染性能集成corsswalk
	
	如果不需要corsswalk请在下面文件删除下面的内容
	
	congig.xml
	
    <preference name="webView" value="org.crosswalk.engine.XWalkWebViewEngine" />
    <preference default="20+" name="xwalkVersion" />
    <preference default="xwalk_core_library_canary:17+" name="xwalkLiteVersion" />
    <preference default="--disable-pull-to-refresh-effect" name="xwalkCommandLine" />
    <preference default="embedded" name="xwalkMode" />
    <preference default="true" name="xwalkMultipleApk" />
    <preference default="16" name="android-minSdkVersion" />
    <preference name="xwalkVersion" value="20+" />
    <preference name="xwalkCommandLine" value="--disable-pull-to-refresh-effect" />
    <preference name="xwalkMode" value="embedded" />
    <preference name="xwalkMultipleApk" value="true" />
    <preference name="android-minSdkVersion" value="16" />
	
	app->build.gradle
	
	 //使用corssWallk的webView 网页的渲染速度更快，兼容性好，但是包的体积会变大
    compile 'org.xwalk:xwalk_core_library:21.51.546.6'