package com.ysr.learn.gugudan.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CentralViewModel : ViewModel() {

    var table: MutableLiveData<Int> = MutableLiveData()

    fun tableIndex(t: Int) {
        table.value = t
    }

    var quiz: MutableLiveData<Int> = MutableLiveData()

    fun quizIndex(q: Int) {
        quiz.value = q
    }

    var digit:MutableLiveData<Int> = MutableLiveData()

    fun inputDigit(d: Int) {
        digit.value = d
    }
}