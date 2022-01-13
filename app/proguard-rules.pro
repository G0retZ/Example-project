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

# не трогаем энумку
-keepclassmembers enum com.cargopull.executor_driver.entity.** { *; }

# не трогаем наташек, сигнатуры и выбрасываемые исключения
-keepattributes *Annotation*, Signature, Exception
# добавляем номера строк в трасировку
-keepattributes SourceFile, LineNumberTable
# Не обфусцируем исключения
-keep public class * extends java.lang.Exception

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

# ???
-dontwarn java8.util.**

# Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keep class com.google.android.gms.measurement.AppMeasurement { *; }
-keep class com.google.android.gms.measurement.AppMeasurement$OnEventListener { *; }

# Gson
# For using GSON @Expose annotation
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.cargopull.executor_driver.backend.web.incoming.** { <fields>; }
-keep class com.cargopull.executor_driver.backend.web.outgoing.** { <fields>; }
# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken