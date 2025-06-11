package com.example.memori.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.memori.navigation.AppNavGraph
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.memori.navigation.Routes
import com.example.memori.ui.bars.BottomNavBar
import com.example.memori.ui.bars.TopNavBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memori.ui.home.HomeViewModel
import java.io.File
import java.io.InputStream

fun getFileName(context: Context, uri: Uri): String {
    var name = "import"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0) {
                name = it.getString(idx)
            }
        }
    }
    return name
}

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun AppScaffold(navController: NavHostController) {

    // 主题
    val bgColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
    val contentColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    val darkTheme = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()
    
    LaunchedEffect(darkTheme) {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            navigationBarContrastEnforced = false,
            darkIcons = !darkTheme
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedIndex = when (currentRoute) {
        Routes.HOME -> 0
        Routes.SMART -> 1
        Routes.STATS -> 2
        else -> 0 // 默认主页面
    }
    val isCardScreen = currentRoute?.startsWith("card") == true

    val context = LocalContext.current
    val homeViewModel: HomeViewModel = hiltViewModel()

    // 文件选择器 launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "import.txt")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val fileName = getFileName(context, uri)
            val nameWithoutExt = fileName.substringBeforeLast('.')
            homeViewModel.importFromFile(tempFile.absolutePath, nameWithoutExt, true)
        }
    }

    if (isCardScreen) {
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavGraph(navController = navController)
        }
    } else {
        Scaffold(
            topBar = { TopNavBar() },
            bottomBar = { BottomNavBar(selectedIndex = selectedIndex, navController = navController) },
            floatingActionButton = {
                if (currentRoute == Routes.HOME) {
                    FloatingActionButton(
                        onClick = {
                            // 只允许选择txt文件
                            filePickerLauncher.launch("text/plain")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "导入卡组"
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(bgColor)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                    color = contentColor
                ) {
                    Crossfade(
                        targetState = currentRoute,
                        animationSpec = tween(durationMillis = 0)
                    ) { route ->
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}