package com.example.memori.ui.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.database.entity.Deck
import com.example.memori.ui.theme.wordNewGreen
import com.example.memori.ui.theme.reviewPurple
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memori.navigation.Routes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.memori.database.util.CustomNewDayTimeConverter
import java.time.format.DateTimeFormatter

@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("memori_prefs", Context.MODE_PRIVATE) }
    var expandedDecks by remember {
        mutableStateOf(
            prefs.getString("expanded_decks", "")
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.map { it.toLong() }
                ?.toSet() ?: setOf()
        )
    }

    // 每次 expandedDecks 变化时保存
    LaunchedEffect(expandedDecks) {
        prefs.edit().putString("expanded_decks", expandedDecks.joinToString(",")).apply()
    }

    // 直接响应式获取 decks
    val decks by viewModel.decks.collectAsState()

    // 计算所有叶子节点的单词数
    val leafDecks = decks.filter { deck -> decks.none { it.parentId == deck.deckId } }
    val wordCount = leafDecks.sumOf { (it.newCount ?: 0) + (it.reviewCount ?: 0) }

    // 新增：异步获取刷新时间
    var refreshDateTime by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val dateTime = CustomNewDayTimeConverter.getTodayRefreshDateTime(context)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        refreshDateTime = dateTime.format(formatter)
    }

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
                navController.navigate(Routes.CARD_WITH_ARG.replace("{deckId}", deckId.toString())) {
                    popUpTo(Routes.HOME)
                }
            }
        )
        // 新增：刷新时间提示
        if (refreshDateTime != null) {
            Text(
                text = "新的一天将在 $refreshDateTime 开始",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
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