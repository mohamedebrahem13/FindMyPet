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
