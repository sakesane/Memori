package com.example.memori.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val db: MemoriDB
) : ViewModel() {
    private val deckDao = db.deckDao()
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks

    init {
        viewModelScope.launch {
            if (deckDao.getAll().isEmpty()) {
            deckDao.insert(Deck(name = "卡组0", newCount = 4, reviewCount = 1))
            val parentId0 = deckDao.getAll().last().deckId
            deckDao.insert(Deck(name = "卡组1", newCount = 3, reviewCount = 1, parentId = parentId0))
            val parentId1 = deckDao.getAll().last().deckId
            deckDao.insert(Deck(name = "卡组1-1", newCount = 2, reviewCount = 1, parentId = parentId1))
            deckDao.insert(Deck(name = "卡组1-2", newCount = 1, reviewCount = 0, parentId = parentId1))
            deckDao.insert(Deck(name = "卡组2", newCount = 4, reviewCount = 0, parentId = parentId0))
            deckDao.insert(Deck(name = "卡组3", newCount = 0, reviewCount = 5, parentId = parentId0))
            }
            _decks.value = deckDao.getAll()
        }
    }
}