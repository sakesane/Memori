package com.example.memoriuitest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memoriuitest.model.Deck
import com.example.memoriuitest.ui.theme.wordNewGreen
import com.example.memoriuitest.ui.theme.reviewPurple
import androidx.navigation.NavController

@Composable
fun MainContent(
    navController: NavController
) {
    // 示例数据
    val decks = remember {
        listOf(
            Deck(1, "卡组1", 5, 3, subDecks = listOf(
                Deck(11, "子卡组1A", 2, 1),
                Deck(12, "子卡组1B", 3, 2)
            )),
            Deck(2, "卡组2", 4, 0),
            Deck(3, "卡组3", 0, 5)
        )
    }
    var expandedDecks by remember { mutableStateOf(setOf<Int>()) }
    val wordCount = 15

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LearnProgressText(wordCount)
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
        DeckList(
            decks = decks,
            expandedDecks = expandedDecks,
            onToggleExpand = { id ->
                expandedDecks = if (id in expandedDecks) expandedDecks - id else expandedDecks + id
            },
            modifier = Modifier.weight(1f),
            onDeckClick = { deckId ->
                navController.navigate("card/$deckId")
            }
        )
    }
}

@Composable
fun LearnProgressText(wordCount: Int) {
    Text(
        text = "还有 $wordCount 个词待学习",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun DeckList(
    decks: List<Deck>,
    expandedDecks: Set<Int>,
    onToggleExpand: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onDeckClick: ((Int) -> Unit)? = null
) {
    LazyColumn(modifier = modifier) {
        items(decks) { deck ->
            DeckItem(deck, expandedDecks, onToggleExpand, onDeckClick = onDeckClick)
        }
    }
}

@Composable
fun DeckItem(
    deck: Deck,
    expandedDecks: Set<Int>,
    onToggleExpand: (Int) -> Unit,
    indent: Int = 0,
    onDeckClick: ((Int) -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (indent * 16).dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .let { m ->
                    if (onDeckClick != null) m.clickable { onDeckClick(deck.id) } else m
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            if (deck.subDecks.isNotEmpty()) {
                IconButton(
                    onClick = { onToggleExpand(deck.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(if (deck.id in expandedDecks) "-" else "+")
                }
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
            Text(
                text = deck.name,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "新 ${deck.newCount}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.wordNewGreen
                )
                Text(
                    text = "复 ${deck.reviewCount}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.reviewPurple,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        if (deck.id in expandedDecks && deck.subDecks.isNotEmpty()) {
            deck.subDecks.forEach { subDeck ->
                DeckItem(subDeck, expandedDecks, onToggleExpand, indent = indent + 1, onDeckClick = onDeckClick)
            }
        }
    }
}