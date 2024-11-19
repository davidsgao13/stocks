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
-keep class **.BuildConfig { *; }

# Keep all Activities
-keep public class * extends android.app.Activity {
    public <init>();
}

# Keep all Services
-keep public class * extends android.app.Service {
    public <init>();
}

# Keep all BroadcastReceivers
-keep public class * extends android.content.BroadcastReceiver {
    public <init>();
}

# Keep all ContentProviders
-keep public class * extends android.content.ContentProvider {
    public <init>();
}

# Preserve Model Classes
-keep class com.example.stocks.domain.model.** { *; }

# Preserve Reflection and Annotations
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class androidx.room.** { *; }
-keep @androidx.room.** class * { *; }

# Preserve Dagger components and generated code
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.** class * { *; }

# Strip Debug Logs
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}