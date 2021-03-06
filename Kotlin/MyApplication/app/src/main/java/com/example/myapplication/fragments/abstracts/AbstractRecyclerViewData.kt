package com.example.myapplication.fragments.abstracts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable

abstract  class AbstractRecyclerViewData<T>() {

    val dataList: MutableLiveData<MutableList<T>> = MutableLiveData()
    var disposable: Disposable? = null
    init {
        dataList.value = ArrayList()
    }
    abstract fun checkForNews()

}