package com.example.memori.ui.createDeck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

data class CreateDeckUiState(
    val error: String = ""
)

@HiltViewModel
class CreateDeckViewModel @Inject constructor(
    private val db: MemoriDB,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val deckDao = db.deckDao()
    private val _uiState = MutableStateFlow(CreateDeckUiState())
    val uiState: StateFlow<CreateDeckUiState> = _uiState
    val allDecks = deckDao.getAllFlow()

    fun createDeck(name: String, newCardLimit: Int, parentId: Long?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiState.value = CreateDeckUiState(error = "卡组名称不能为空")
                return@launch
            }
            val deck = Deck(
                name = name,
                newCount = 0,
                reviewCount = 0,
                newCardLimit = newCardLimit,
                parentId = parentId
            )
            deckDao.insert(deck)
            _uiState.value = CreateDeckUiState()
            onSuccess()
        }
    }
}