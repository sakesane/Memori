package com.example.memori.model

data class Deck(
    val id: Int,
    val name: String,
    val newCount: Int,
    val reviewCount: Int,
    val subDecks: List<Deck> = emptyList()
)