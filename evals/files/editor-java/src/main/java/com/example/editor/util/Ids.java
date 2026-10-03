package com.example.editor.util;

import java.util.concurrent.atomic.AtomicLong;

public final class Ids {
    private static final AtomicLong NEXT = new AtomicLong(1);

    private Ids() { }

    public static String next(String prefix) { return prefix + "-" + NEXT.getAndIncrement(); }
}
