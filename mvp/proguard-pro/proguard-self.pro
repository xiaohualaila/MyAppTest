# ------------------------------- 自定义区 -------------------------------


-keepattributes Signature
-keepattributes *Annotation*
-keep class cn.com.library.** { *; }
-keep interface cn.com.library.** { *; }
-dontwarn cn.com.library.**

# ------------------------------- 自定义区 end -------------------------------
