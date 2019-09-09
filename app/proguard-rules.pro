# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 保留包：com.moos.media.entity 下面的类以及类成员
-keep public class com.mml.topwho.data.**
#保留枚举
-keepclassmembers enum * {
    **[] $VALUES;
    public *;
}
#保留Activity中View及其子类入参的方法，如: onClick(android.view.View)
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#保留序列化的类
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

#保留R文件的静态成员
-keepclassmembers class **.R$* {
    public static <fields>;
}
#Umeng
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class com.umeng.** {*;}