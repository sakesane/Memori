package com.example.memori.ui.card.topInfo

import androidx.lifecycle.ViewModel
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TopInfoViewModel @Inject constructor(
    private val db: MemoriDB,
) : ViewModel() {
    private val deckDao = db.deckDao()
    private val cardDao = db.cardDao()
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks
}