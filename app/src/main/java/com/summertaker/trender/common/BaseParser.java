package com.summertaker.trender.common;

public class BaseParser {

    public String TAG;

    public BaseParser() {
        TAG = "== " + this.getClass().getSimpleName();
    }
}