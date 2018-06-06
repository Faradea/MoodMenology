package com.macgavrina.moodmenology.logging;

import android.text.TextUtils;

public class Log {

    private static boolean mLoggingEnabled = false;
    private static final String LOG_TAG = "MoodMenology2";

    private Log() {

    }

    public static void setDebugLogging(boolean enabled) {
        mLoggingEnabled = enabled;
    }

    public static void d(String msg) {
        if (mLoggingEnabled) {
            android.util.Log.d(LOG_TAG, getLogPrefix() + msg);
        }
    }

    private static String getLogPrefix() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

}
