package com.nazam.instaclone.feature.profile.presentation.navigation

object ProfileTargetStore {
    private var userId: String? = null
    private var emailFallback: String? = null

    fun open(userId: String, emailFallback: String) {
        this.userId = userId
        this.emailFallback = emailFallback
    }

    fun openSelf() {
        userId = null
        emailFallback = null
    }

    fun getUserId(): String? = userId
    fun getEmailFallback(): String? = emailFallback
}
