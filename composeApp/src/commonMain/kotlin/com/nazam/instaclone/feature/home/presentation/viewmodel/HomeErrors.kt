package com.nazam.instaclone.feature.home.presentation.viewmodel

internal fun Throwable.isAuthRequired(): Boolean =
    this is IllegalStateException && this.message == "AUTH_REQUIRED"

internal fun Throwable.toUiMessage(defaultMessage: String): String =
    this.message?.takeIf { it.isNotBlank() } ?: defaultMessage