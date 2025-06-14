package com.example.memori.ui.insertCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Card
import com.example.memori.database.entity.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import java.time.LocalDateTime

data class InsertCardUiState(
    val error: String = "",
    val decks: List<Deck> = emptyList()
)

@HiltViewModel
class InsertCardViewModel @Inject constructor(
    private val db: MemoriDB,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val cardDao = db.cardDao()
    private val deckDao = db.deckDao()
    private val _uiState = MutableStateFlow(InsertCardUiState())
    val uiState: StateFlow<InsertCardUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(decks = deckDao.getAll())
        }
    }

    fun insertCard(
        deckId: Long,
        word: String,
        IPA: String,
        definition: String,
        exampleEN: String,
        exampleCN: String,
        mnemonic: String,
        add: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (word.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "单词不能为空")
                return@launch
            }
            val card = Card(
                deckId = deckId,
                word = word,
                IPA = IPA,
                definition = definition,
                exampleEN = exampleEN,
                exampleCN = exampleCN,
                mnemonic = mnemonic,
                add = add,
                due = LocalDateTime.now()
            )
            cardDao.insertCard(card)
            _uiState.value = _uiState.value.copy(error = "")
            onSuccess()
        }
    }
}