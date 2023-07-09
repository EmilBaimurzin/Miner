package com.gonzup.upslt.game.game

import com.gonzup.upslt.game.game.adapter.GamePlayItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import java.util.*

class GamePlayRepository {
    suspend fun generateList(difficulty: Boolean?): MutableList<GamePlayItem> {
        var list = mutableListOf<GamePlayItem>()
        repeat(30) {
            list.add(GamePlayItem(true))
        }
        when (difficulty) {
            true -> {
                list = pasteInRandomSpot(list, 5, false)
                list = pasteInRandomSpot(list, 2, null)
            }
            false -> {
                list = pasteInRandomSpot(list, 10, false)
                list = pasteInRandomSpot(list, 2, null)
            }
            null -> {
                list = pasteInRandomSpot(list, 15, false)
                list = pasteInRandomSpot(list, 2, null)
            }
        }
        return list
    }

    private suspend fun pasteInRandomSpot(
        list: MutableList<GamePlayItem>,
        times: Int,
        value: Boolean?
    ): MutableList<GamePlayItem> {
        repeat(times) {
            getRandomIndex(list).retry { true }.collect {
                list[it].value = value
            }
        }
        return list
    }

    private fun getRandomIndex(list: MutableList<GamePlayItem>): Flow<Int> {
        return flow {
            val randomInt = Random().nextInt(30 - 0) + 0
            if (list[randomInt].value == true) {
                emit(randomInt)
            } else {
                throw Exception()
            }
        }
    }
}


