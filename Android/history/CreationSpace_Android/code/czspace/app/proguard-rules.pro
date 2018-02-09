# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\program_install\android_sdk/tools/proguard/proguard-android.txt
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
#不压缩输入的类文件
-dontshrink
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
 -verbose
# 混淆时所采用的算法
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
#忽略警告
#-ignorewarnings

###-----------基本配置-不能被混淆的------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

#support.v4/v7包不混淆
-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
-keep interface android.support.v7.app.** { *; }
-dontwarn android.support.**    # 忽略警告

#保持注解继承类不混淆
-keep class * extends java.lang.annotation.Annotation {*;}
#保持Serializable实现类不被混淆
-keepnames class * implements java.io.Serializable
#保持Serializable不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
     public <fields>;
}

#保持枚举enum类不被混淆
-keepclassmembers enum * {
  public static **[] values();
 public static ** valueOf(java.lang.String);
}
#自定义组件不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
    public static final int *;
}

#Glide
-keepnames class om.lht.creationspace.clazz.LhtDiskCacheGlideModule

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}

-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

#apk 包内所有 class 的内部结构
#-dump class_files.txt
#未混淆的类和成员
#-printseeds seeds.txt
#列出从 apk 中删除的代码
#-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

#------------------- 三方
#网络库
-keep class org.apache.http.** { *; }
#-dontwarn org.apache.http.**

#async-http
-keep class com.loopj.android.http.** { *; }

#com.alibaba.fastjson
-keep class com.alibaba.fastjson.** { *; }

#jpush
-keep class cn.jpush.** { *; }

#liteorm
-keep class com.litesuits.orm.** { *; }

-keep class com.tencent.** { *; }

#photoview
-keep class uk.co.senab.photoview.** { *; }

-keep class com.sina.** { *; }

#fastjson 可以混淆也可以不混淆
-keep class javax.ws.rs.** { *; }
#-dontwarn com.alibaba.fastjson.**
#-keep class com.alibaba.fastjson.** { *; }

-keepattributes Signature