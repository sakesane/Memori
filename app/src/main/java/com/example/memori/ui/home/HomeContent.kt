package com.example.memori.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.database.entity.Deck
import com.example.memori.ui.theme.wordNewGreen
import com.example.memori.ui.theme.reviewPurple
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memori.navigation.Routes

@Composable
fun HomeContent(
    navController: NavController,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val decks by viewModel.decks.collectAsState()
    var expandedDecks by remember { mutableStateOf(setOf<Long>()) }
    val wordCount = decks.sumOf { it.newCount + it.reviewCount }

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
                navController.navigate(Routes.CARD_WITH_ARG.replace("{deckId}", deckId.toString())){
                    popUpTo(Routes.HOME)
                }
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
    expandedDecks: Set<Long>,
    onToggleExpand: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onDeckClick: ((Long) -> Unit)? = null,
    parentId: Long? = null,
    indent: Int = 0
) {
    val children = decks.filter { it.parentId == parentId }
    Column(modifier = modifier) {
        children.forEach { deck ->
            DeckItem(
                deck = deck,
                decks = decks,
                expandedDecks = expandedDecks,
                onToggleExpand = onToggleExpand,
                onDeckClick = onDeckClick,
                indent = indent
            )
        }
    }
}

@Composable
fun DeckItem(
    deck: Deck,
    decks: List<Deck>,
    expandedDecks: Set<Long>,
    onToggleExpand: (Long) -> Unit,
    onDeckClick: ((Long) -> Unit)? = null,
    indent: Int = 0
) {
    val hasChildren = decks.any { it.parentId == deck.deckId }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (indent * 16).dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .let { m ->
                    if (onDeckClick != null) m.clickable { onDeckClick(deck.deckId) } else m
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            if (hasChildren) {
                IconButton(
                    onClick = { onToggleExpand(deck.deckId) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(if (deck.deckId in expandedDecks) "-" else "+")
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
        if (deck.deckId in expandedDecks && hasChildren) {
            DeckList(
                decks = decks,
                expandedDecks = expandedDecks,
                onToggleExpand = onToggleExpand,
                onDeckClick = onDeckClick,
                parentId = deck.deckId,
                indent = indent + 1
            )
        }
    }
}