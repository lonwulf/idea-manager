package com.lonwulf.ideamanager.util

import androidx.compose.runtime.mutableStateOf

object LightModeManager{
    val isDark = mutableStateOf(false)

    fun toggleMode(){
        isDark.value = !isDark.value
    }
}