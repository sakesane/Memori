package com.example.memori.ui.card.topInfo

import androidx.lifecycle.ViewModel
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TopInfoViewModel @Inject constructor(
    private val db: MemoriDB,
) : ViewModel() {
    private val deckDao = db.deckDao()
    private val cardDao = db.cardDao()

    fun deckFlow(deckId: Long): Flow<Deck?> {
        return deckDao.getDeckFlow(deckId)
    }

    // 递归获取所有子孙deckId
    private suspend fun getAllDescendantDeckIds(deckId: Long): List<Long> {
        val result = mutableListOf<Long>()
        suspend fun dfs(id: Long) {
            result.add(id)
            val children = deckDao.getChildren(id)
            for (child in children) {
                dfs(child.deckId)
            }
        }
        dfs(deckId)
        return result
    }

    // 查询该deck及所有子孙deck的new卡数量
    suspend fun getAllNewCount(deckId: Long): Int = withContext(Dispatchers.IO) {
        val allIds = getAllDescendantDeckIds(deckId)
        cardDao.getNewCardCountByDeckIds(allIds)
    }
}