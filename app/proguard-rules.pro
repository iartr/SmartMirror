
# ==============================================
#
# ВАЖНО!!!
# ОБЯЗАТЕЛЬНОЕ ПРАВИЛО!!!
# ПРИ ДОБАВЛЕНИИ НОВЫХ RULES НЕОБХОДИМО ОСТАВИТЬ КОММЕНТАРИЙ И ПРИЛОЖИТЬ ЗАДАЧУ!!!

# Полезные ссылки:
# https://www.zacsweers.dev/android-proguard-rules/
# www.guardsquare.com/en/products/proguard/manual/examples
# https://www.guardsquare.com/en/products/proguard/manual/usage
#
# ПАМЯТКА:
# C/M/F - class/method/fileds
# [M] - отличие от другого языка(Kotlin/Java)
#
# Kotlin:
# -keep class com.example.** { *; } # Все C/M/F в пакете
# -keep class com.example.** # Все C[M] в пакете
# -keep class com.example.** { public void set*(...); } # Все C[M] в пакете + F, где выполнено условие
#
# -keepclassmembers class com.example.** { public void set*(...); } # Только условие
# -keepclassmembers class com.example.** - # Ничего
#
# -keepclasseswithmembers class com.example.** { one(); } - Все C/M/F, где выполнено хоть 1 условие
# -keepclasseswithmembers class com.example.** # Все C[M] в пакете
#
# Java:
# -keep class com.example.** { *; } # Все C/M/F в пакете
# -keep class com.example.** # Все С в пакете
# -keep class com.example.** { public void set*(...); } # Все С в пакете + M/F, где выполнено условие
#
# -keepclassmembers class com.example.** { public void set*(...); } # Только условие
# -keepclassmembers class com.example.** - # Ничего
#
# -keepclasseswithmembers class com.example.** { one(); } - Все C/M/F где выполняется условие
# -keepclasseswithmembers class com.example.** # Все С в пакете
#
# $ - один символ
# * - любое символов до package separator
# ** - любое количество пакетов/типов кроме float/array
# *** - Любое количество пакетов/типов и т.д.
# ... - любое количество параметров
#
# ВАЖНО!!!
# ОБЯЗАТЕЛЬНОЕ ПРАВИЛО!!!
# ПРИ ДОБАВЛЕНИИ НОВЫХ RULES НЕОБХОДИМО ОСТАВИТЬ КОММЕНТАРИЙ И ПРИЛОЖИТЬ ЗАДАЧУ!!!
# ==============================================


# ==============================================
# Может использоваться для Reflection
# ==============================================
-keepattributes *Annotation*,Signature,SourceFile,LineNumberTable


# ==============================================
# PARCELABLE/SERIALIZABLE
# Многие классы кладутся в persistence, так что нужно их сохранять
#
# Убрали -keepnames class * implements java.io.Serializable, т.к. попадает куча классов, которые не являются serializable
# ==============================================
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepnames class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
    public <init>(...);
}

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepnames public class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ==============================================
# GOOGLE PLAY SERVICES
# ==============================================
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService


# ==============================================
# Для хранения нативных методов, т.к. в JNI имена прописываются насильно
# http://proguard.sourceforge.net/manual/examples.html#native
# ==============================================
-keepclasseswithmembers class * {
    native <methods>;
}


# ==============================================
# Некоторые анимации могут использовать рефлексию у View, ex: ObjectAnimator.ofInt(view, "scaleX")
# ==============================================
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}


# ==============================================
# ANDROID SPECIFIC
# ==============================================
-keepnames public class * extends android.app.Activity
-keepnames public class * extends android.app.Application
-keepnames public class * extends android.app.Service
-keepnames public class * extends android.content.BroadcastReceiver
-keepnames public class * extends android.content.ContentProvider
-keepnames public class * extends android.app.backup.BackupAgentHelper
-keepnames public class * extends android.preference.Preference

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclasseswithmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
# Web обращатся к методам по настоящим именам
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
# VkUiFragment has hidden unsued methods
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void openFileChooser(...);
}

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification
-dontnote android.net.http.*


# ==============================================
# ANDROID X SPECIFIC
# ==============================================
# Understand the @Keep support annotation.
-keep @androidx.annotation.Keep class * { *; }

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# Because of bug after migrating to v28.0.0 support libs
-dontwarn androidx.**


# ==============================================
# GOOGLE SPECIFIC
# ==============================================
# Support design
-dontwarn com.google.android.**

# ANDC-7081
# https://issuetracker.google.com/issues/154315507
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
  <fields>;
}

-keep class com.google.android.gms.internal.** { *; }
-keep public class com.google.android.material.R$* { *; }
-keep class com.google.i18n.** { *; }
-keep class com.google.firebase.iid.FirebaseInstanceIdService { *; }
-keepclassmembers class com.google.firebase.database.GenericTypeIndicator { *; }
-keep class com.google.firebase.auth.FirebaseAuth
-keep class com.google.firebase.auth.FirebaseUser

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
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


# ==============================================
# JAVA/KOTLIN SPECIFIC
# ==============================================
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# ==============================================
# Libraries
# ==============================================
-dontwarn okio.**
-dontwarn com.googlecode.**
-dontwarn com.my.target.ads.**

# Crashlytics
-keep public class * extends java.lang.Exception
-dontwarn com.crashlytics.**

# Fresco
-dontwarn com.facebook.imagepipeline.bitmaps.DalvikBitmapFactory
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Facebook
-keep class com.facebook.imagepipeline.nativecode.** { *; }

# OkHttp
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-keep class okhttp3.internal.proxy.** { *; }
-keep class okhttp3.Interceptor
-keep class okhttp3.internal.http.RealInterceptorChain
# to use own thread factory
-keep class okhttp3.internal.concurrent.TaskRunner

# RX
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Facebook SDK
-dontwarn com.facebook.**
-keepnames class com.facebook.FacebookActivity
-keepnames class com.facebook.CustomTabActivity
-keep class com.facebook.soloader.** { *; }
-keep public class com.android.vending.billing.IInAppBillingService {
    public static com.android.vending.billing.IInAppBillingService asInterface(android.os.IBinder);
    public android.os.Bundle getSkuDetails(int, java.lang.String, java.lang.String, android.os.Bundle);
}

# LeakCanary
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Jsoap
-keep class org.jsoup.** { *; }

# ZXING
-keep class com.google.zxing.** { *; }

# Sentry
-keepattributes LineNumberTable,SourceFile
-dontwarn org.slf4j.**
-dontwarn javax.**
-keep class io.sentry.event.Event { *; }

# Countly
-keep class org.openudid.** { *; }
-keep class ly.count.android.sdk.** { *; }

# libvoip
-keep class ru.ok.** { *; }
-keep class ru.mail.** { *; }
-keep class org.webrtc.** { *; }
-keep class syswrap.logging.** { *; }

# sqlite-requery
-keep class io.requery.android.database.** { *; }

# Apache
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

# ZSTD ANDC-9309 для натива важны поля и методы
-keep class com.github.luben.zstd.** { *; }

# CALL-4403
# messagepack
# used by ok calls
# looks up MessageBuffer constructors w/ reflection
-keep class org.msgpack.core.buffer.** { *; }

# fresco ANDC-9255 fix gif crashes by reflection
-keep class com.facebook.fresco.animation.bitmap.BitmapAnimationBackend { *; }

# Huawei packages
-keep class com.huawei.hms.** { *; }
-keep class com.huawei.agconnect.**{*;}
-dontwarn com.huawei.agconnect.**
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep interface com.huawei.hms.analytics.type.HAEventType { *; }
-keep interface com.huawei.hms.analytics.type.HAParamType { *; }

-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}

-ignorewarnings
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes LineNumberTable

# for flipper
-keep class com.facebook.jni.** { *; }
-keep class com.facebook.flipper.** { *; }

# for mp4parser
-dontwarn com.coremedia.iso.boxes.*
-dontwarn com.googlecode.mp4parser.authoring.tracks.mjpeg.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.ttml.**

# ANDC-11264: keep icon launchers enums name
#-keep public enum com.vkontakte.android.icons.**{* ; }

# leak canary in beta
-keep class androidx.appcompat.view.WindowCallbackWrapper { *; }
