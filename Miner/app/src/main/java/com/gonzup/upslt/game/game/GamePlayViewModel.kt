package com.gonzup.upslt.game.game

import androidx.lifecycle.*
import com.gonzup.upslt.game.game.adapter.GamePlayItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamePlayViewModel : ViewModel() {
    private val _bid = MutableLiveData<Long>(0)
    val bid: LiveData<Long> = _bid
    private val _bidState = MutableLiveData<Boolean>(false)
    val bidState: LiveData<Boolean> = _bidState
    private val repository = GamePlayRepository()
    private val _coefficient = MutableLiveData(1.0)
    var coefficient: LiveData<Double> = _coefficient
    private val _list = MutableLiveData<MutableList<GamePlayItem>>()
    val list: LiveData<MutableList<GamePlayItem>> = _list
    private val _difficulty = MutableLiveData<Boolean?>(false)
    val difficulty: LiveData<Boolean?> = _difficulty

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = repository.generateList(difficulty.value)
            _list.postValue(newList)
        }
    }

    fun generateList() {
        viewModelScope.launch(Dispatchers.Default) {
            _list.postValue(repository.generateList(difficulty.value))
        }
    }

    fun changeBidValue(value: Long) {
        _bid.postValue(value)
    }

    fun increaseCoefficient() {
        val increaseBy = when (difficulty.value) {
            true -> 1.1
            false -> 1.2
            null -> 1.3
        }
        _coefficient.postValue(_coefficient.value?.times(increaseBy))
    }

    fun changeDifficulty(value: Boolean?) {
        _difficulty.postValue(value)
    }

    fun jackpot() {
        val increaseBy = when (difficulty.value) {
            true -> 1.0
            false -> 2.0
            null -> 3.0
        }
        _coefficient.postValue(_coefficient.value!!.plus(increaseBy))
    }

    fun endGame(position: Int) {
        val lastList = _list.value!!
        lastList[position].isCurrent = true
        lastList.map {
            it.isFinished = true
        }
        _list.postValue(lastList)
    }

    fun openCard(position: Int) {
        _list.value!![position].isOpened = true
    }

    fun changeBidState() {
        _bidState.postValue(_bidState.value!!.not())
    }
}