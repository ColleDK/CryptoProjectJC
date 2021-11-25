package com.example.cryptoprojectjetpackcompose

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

// https://github.com/wasabeef/kotlin-mvvm/blob/master/app/src/main/kotlin/jp/wasabeef/util/SingleLiveEvent.kt
class Resource<T>: MutableLiveData<T>(){
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer<T>{ t ->
            if (mPending.compareAndSet(true, false)){
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(value: T) {
        mPending.set(true)
        super.setValue(value)
    }
}