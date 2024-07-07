# Add project specific ProGuard rules here.
# ...

# Remove all logging calls in release builds
# Remove debug logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
}

# Remove verbose logs
-assumenosideeffects class android.util.Log {
    public static *** v(...);
}

# Remove info logs
-assumenosideeffects class android.util.Log {
    public static *** i(...);
}

# Remove warning logs
-assumenosideeffects class android.util.Log {
    public static *** w(...);
}

# Remove error logs
-assumenosideeffects class android.util.Log {
    public static *** e(...);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.example.findmypet.data.*
-keepclassmembers class com.example.findmypet.data.model.Post {
    public <init>();
}
-keepclassmembers class com.example.findmypet.data.model.Message {
    public <init>();
}
-keepclassmembers class com.example.findmypet.data.model.DisplayConversation {
    public <init>();
}
-keepclassmembers class com.example.findmypet.data.model.Conversation {
    public <init>();
}
-keep class com.example.findmypet.data.model.User{
    public *;
}

-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn com.google.ads.**
# Application classes that will be serialized/deserialized over Gson
-keep class org.bouncycastle.jsse.** { *; }
-keep class org.conscrypt.** { *; }
-keep class org.openjsse.** { *; }

-dontwarn cryptix.**
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response
# Keep Google Play services authentication-related classes and methods
-keep class com.google.android.gms.** { *; }

# Keep all classes and methods related to Firebase authentication
-keep class com.google.firebase.auth.** { *; }

# Keep any custom authentication-related classes and methods
-keep class com.example.findmypet.auth.** { *; }


# For Authentication
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.auth.internal.** { *; }
# For Cloud Messaging
-keep class com.google.firebase.messaging.** { *; }
# For Cloud Firestore
-keep class com.google.firebase.firestore.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class org.apache.http.** { *; }
-keepnames class com.google.firebase.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**

# For Firebase Storage
-keep class com.google.firebase.storage.** { *; }
-keep class com.google.firebase.storage.internal.** { *; }
-keep class com.google.firebase.storage.network.** { *; }
-keep class com.google.firebase.storage.network.connection.** { *; }
-keep class com.google.firebase.storage.network.connection.internal.** { *; }
-keep class com.google.firebase.storage.network.connection.internal.HttpURLConnectionFactoryImpl$** { *; }
-keep class com.google.firebase.storage.network.connection.internal.HttpURLConnectionFactoryImpl$* { *; }
-keep class com.google.firebase.storage.network.connection.internal.HttpURLConnectionWrapper$** { *; }
-keep class com.google.firebase.storage.network.connection.internal.HttpURLConnectionWrapper$* { *; }
