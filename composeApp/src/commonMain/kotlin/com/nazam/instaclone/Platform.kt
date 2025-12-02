package com.nazam.instaclone

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform