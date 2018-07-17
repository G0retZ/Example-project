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

# Picasso
-dontwarn com.squareup.okhttp.**

# OkHttp3 & Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.**

# Joda-Time
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**

# STOMP
-dontwarn ua.naiksoftware.stomp.**

# ???
-dontwarn java8.util.**

-printmapping map.txt
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt

-keepattributes Signature
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

#наши пакеты
# -adaptresourcefilenames **.ogg,**.xml,**.gif,**.jpg,**.png
-keep class !com.cargopull.executor_driver.**{*;}

#Даем уникальные имена методам в классе
-useuniqueclassmembernames

#добавляем номера строк в трасировку
-keepattributes LineNumberTable

#прогуард поломался временно отключил оптимизацию
#-dontoptimize
