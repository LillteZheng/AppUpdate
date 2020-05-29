-dontwarn com.zhengsr.zdwon_lib.**
-keep public class com.zhengsr.zdwon_lib.bean.*
-keep public class com.zhengsr.zdwon_lib.callback.*



#第三方类
#okhttp3.x
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**


# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions


#----------- rxjava rxandroid----------------
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
-dontnote rx.internal.util.PlatformDependent




-dontwarn android.net.http.**
-keep class android.net.http.** { *;}


#fastjson

-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }

