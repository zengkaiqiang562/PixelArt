package com.project_m1142.app.base.view.holder;

public interface LifecycleObservable<T extends LifecycleObserver> {
    void addLifecycleObserver(T callback);
    void removeLifecycleObserver(T callback);
}
