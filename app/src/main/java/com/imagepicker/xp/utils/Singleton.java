package com.imagepicker.xp.utils;

/**
 * 单例模式 T 为类, P为传参
 */
public abstract class Singleton<T, P> {
    private volatile T mInstance;

    public Singleton() {
    }

    protected abstract T create(P var1);

    public final T get(P p) {
        if (this.mInstance == null) {
            synchronized (this) {
                if (this.mInstance == null) {
                    this.mInstance = this.create(p);
                }
            }
        }
        return this.mInstance;
    }
}
