package com.nazam.instaclone.feature.profile.presentation.navigation

import com.nazam.instaclone.core.navigation.Screen

object ProfileTargetStore {
    private var userId: String? = null
    private var emailFallback: String? = null
    private var returnScreen: Screen? = null

    fun open(
        userId: String,
        emailFallback: String,
        returnScreen: Screen
    ) {
        this.userId = userId
        this.emailFallback = emailFallback
        this.returnScreen = returnScreen
    }

    fun openSelf() {
        userId = null
        emailFallback = null
        returnScreen = null
    }

    fun getUserId(): String? = userId
    fun getEmailFallback(): String? = emailFallback
    fun getReturnScreen(): Screen? = returnScreen
}
