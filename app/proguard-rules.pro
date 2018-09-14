# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/prog/Android/Sdk/tools/proguard/proguard-android.txt
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
-verbose

# -printmapping map.txt
# -dump class_files.txt
# -printseeds seeds.txt
# -dontobfuscate

-dontpreverify
-repackageclasses
-optimizations !code/simplification/arithmetic

# не трогаем наташек, сигнатуры и выбрасываемые исключения
-keepattributes *Annotation*, Signature, Exception
# добавляем номера строк в трасировку
-keepattributes SourceFile, LineNumberTable
# Не обфусцируем исключения
-keep public class * extends java.lang.Exception

# Support library
-keep class android.support.v7.widget.** { *; }
-dontwarn androidx.media.**

# Picasso
-dontwarn com.squareup.picasso.OkHttpDownloader

# Retrofit
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
#-dontwarn retrofit2.**

# OkHttp3
#-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn javax.annotation.**

# Joda-Time
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.** { *; }
-keep interface org.joda.time.** { *;}

# STOMP
-dontwarn ua.naiksoftware.stomp.**
#-keep class ua.naiksoftware.stomp.** { *; }
#-keep interface ua.naiksoftware.stomp.** { *;}

# ???
-dontwarn java8.util.**

# Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keep class com.google.android.gms.measurement.AppMeasurement { *; }
-keep class com.google.android.gms.measurement.AppMeasurement$OnEventListener { *; }