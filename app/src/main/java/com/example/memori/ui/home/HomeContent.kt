package com.example.memori.ui.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.database.entity.Deck
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memori.navigation.Routes
import com.example.memori.database.util.CustomNewDayTimeConverter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.format.DateTimeFormatter

@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 主题
    val refreshTextColor = MaterialTheme.colorScheme.onSurfaceVariant

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
    val darkTheme = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()

    // 每次 expandedDecks 变化时保存
    LaunchedEffect(expandedDecks) {
        prefs.edit().putString("expanded_decks", expandedDecks.joinToString(",")).apply()
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            navigationBarContrastEnforced = false,
            darkIcons = !darkTheme
        )
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
        val formatter = DateTimeFormatter.ofPattern("yyyy / MM / dd  HH : mm")
        refreshDateTime = dateTime.format(formatter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        LearnProgressText(wordCount)

        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
        if (decks.isEmpty()) {
                Text(
                    text = "暂无卡组，请先创建卡组或导入卡组",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
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
                fontWeight = FontWeight.Light,
                color = refreshTextColor
            )
        }
    }
}

@Composable
fun LearnProgressText(wordCount: Int) {
    Text(
        text = "还有 $wordCount 张卡片待学习",
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
    val deckFontSize = 16.sp
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
                fontSize = deckFontSize
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
                    fontSize = deckFontSize,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "复 ${deck.reviewCount}",
                    fontSize = deckFontSize,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp)
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