# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#---------------------------------1.实体类---------------------------------

-keep class com.lzy.okhttpdemo.Bean.** { *; }

#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------
#okgo
#-dontwarn com.lzy.okgo.**
#-keep class com.lzy.okgo.**{*;}

#okrx
#-dontwarn com.lzy.okrx.**
#-keep class com.lzy.okrx.**{*;}

#okserver
#-dontwarn com.lzy.okserver.**
#-keep class com.lzy.okserver.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

-keepattributes SourceFile,LineNumberTable
-keep class com.parse.*{ *; }
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**
-keepclasseswithmembernames class * {
    native <methods>;
}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#imagepicker
-dontwarn com.lzy.imagepicker.**
-keep class com.lzy.imagepicker.**{*;}

#imagepicker
-dontwarn com.lzy.widget.**
-keep class com.lzy.widget.**{*;}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.**{ *; }
-keep class com.google.gson.examples.android.model.**{ *; }
-keep class com.google.gson.**{ *;}

#eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
-keepclassmembers class ** {
    public void onEvent*(**);
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#log4j
#-libraryjars log4j-1.2.17.jar
-dontwarn org.apache.log4j.**
-keep class  org.apache.log4j.** { *;}

#------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------

#-keepclasseswithmembers class com.demo.login.bean.ui.MainActivity$JSInterface {
#      <methods>;
#}

#------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------


#------------------------------------------------------------------------

#---------------------------------基本指令区-------------------------------
#代码混淆的压缩比例，值在0-7之间
-optimizationpasses 5
#混淆后类名都为小写
-dontusemixedcaseclassnames
#指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
#指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
#不做预校验的操作
-dontpreverify
#生成原类名和混淆后的类名的映射文件
-verbose
-printmapping proguardMapping.txt
#指定混淆是采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#不混淆Annotation
-keepattributes *Annotation*,InnerClasses
#不混淆泛型
-keepattributes Signature
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#------------------------------------------------------------------------

#-keep class XXXX   保留类名不变，也就是类名不混淆，而类中的成员名不保证。当然也可以是继承XXX类的所有类名不混淆
#-keepclasseswithmembers class XXXX 保留类名和成员名,当然也可以是类中特定方法

#---------------------------------默认保留区-------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.**
-keep public class * extends android.app.Fragment
-dontwarn android.support.**
-keep class android.support.** {*;}

#自定义控件不要混淆
-keep public class * extends android.view.View {*;}
#adapter不能混淆
-keep public class * extends android.widget.BaseAdapter {*;}
#CusorAdapter不混淆
-keep public class * extends android.widget.CusorAdapter{*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {*;}

-keep class **.R$* {*;}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------