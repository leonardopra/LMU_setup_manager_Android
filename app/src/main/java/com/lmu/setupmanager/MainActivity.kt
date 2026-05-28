package com.lmu.setupmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.lmu.setupmanager.navigation.AppNavigation
import com.lmu.setupmanager.ui.theme.LmuTheme
import com.lmu.setupmanager.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState by themeViewModel.themeState.collectAsStateWithLifecycle()
            LmuTheme(
                darkTheme = themeState.isDarkTheme,
                useSystemTheme = themeState.useSystemTheme
            ) {
                AppNavigation(themeViewModel = themeViewModel)
            }
        }
    }
}