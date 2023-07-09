package com.gonzup.upslt.game.rps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gonzup.upslt.helpful.random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RPSViewModel : ViewModel() {
    private val _scores = MutableLiveData(0 to 0)
    val scores: LiveData<Pair<Int, Int>> = _scores

    private val _gesture = MutableLiveData(Pair(1 , 1 random 3))
    val gesture: LiveData<Pair<Int, Int>> = _gesture

    private val _timer = MutableLiveData<Int>(0)
    val timer: LiveData<Int> = _timer

    private val _isOpened = MutableLiveData<Boolean>(false)
    val isOpened: LiveData<Boolean> = _isOpened

    fun setGesture(gestureIndex: Int) {
        _gesture.value = gestureIndex to _gesture.value!!.second
    }

    init {
        startRound()
    }

    fun checkWin(): Boolean? {
        val gestures = _gesture.value!!
        return when {
            gestures.first == 1 && gestures.second == 1 -> null
            gestures.first == 1 && gestures.second == 2 -> false
            gestures.first == 1 && gestures.second == 3 -> true
            gestures.first == 2 && gestures.second == 1 -> true
            gestures.first == 2 && gestures.second == 2 -> null
            gestures.first == 2 && gestures.second == 3 -> false
            gestures.first == 3 && gestures.second == 1 -> false
            gestures.first == 3 && gestures.second == 2 -> true
            gestures.first == 3 && gestures.second == 3 -> null
            else -> null
        }
    }

    private fun startRound() {
        viewModelScope.launch {
            _gesture.postValue(1 to (1 random 3))
            _isOpened.postValue(false)
            _timer.postValue(8)
            startTimer()
                .onCompletion {
                _isOpened.postValue(true)
                val scoresValue = _scores.value!!
                when (checkWin()) {
                    true -> _scores.postValue(scoresValue.first + 1 to scoresValue.second)
                    false -> _scores.postValue(scoresValue.first to scoresValue.second + 1)
                    null -> {

                    }
                }
                delay(2000)
                startRound()
            }
                .collect()
        }
    }

    private fun startTimer(): Flow<Int> {
        return flow {
            repeat(8) {
                delay(1000)
                _timer.postValue(_timer.value!! - 1 )
                emit(_timer.value!!)
            }
        }.flowOn(Dispatchers.Default)
    }
}