package com.gonzup.upslt.game.game.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gonzup.upslt.helpful.random

class ChanceViewModel: ViewModel() {
    private val _list = MutableLiveData(generate())
    val list: LiveData<MutableList<Pair<Boolean, Boolean>>> = _list

    fun openCard(index: Int) {
        val newList = _list.value!!
        newList[index]  = true to newList[index].second
        _list.postValue(newList)
    }

    private fun generate(): MutableList<Pair<Boolean, Boolean>> {
        val list = mutableListOf(false to false, false to false, false to false)
        list[0 random 2] = false to true
        return list
    }
}