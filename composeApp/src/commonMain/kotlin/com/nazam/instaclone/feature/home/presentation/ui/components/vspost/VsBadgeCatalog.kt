package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_badge_vs_classic
import instaclone.composeapp.generated.resources.create_post_badge_vs_combat
import instaclone.composeapp.generated.resources.create_post_badge_vs_futuristic
import instaclone.composeapp.generated.resources.create_post_badge_vs_premium
import instaclone.composeapp.generated.resources.vs2
import instaclone.composeapp.generated.resources.vs_1
import instaclone.composeapp.generated.resources.vsfuturistic
import instaclone.composeapp.generated.resources.vsor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class VsBadgeOption(
    val id: String,
    val titleRes: StringResource,
    val drawableRes: DrawableResource
)

object VsBadgeCatalog {

    val options = listOf(
        VsBadgeOption("vs_1", Res.string.create_post_badge_vs_combat, Res.drawable.vs_1),
        VsBadgeOption("vs2", Res.string.create_post_badge_vs_classic, Res.drawable.vs2),
        VsBadgeOption("vsfuturistic", Res.string.create_post_badge_vs_futuristic, Res.drawable.vsfuturistic),
        VsBadgeOption("vsor", Res.string.create_post_badge_vs_premium, Res.drawable.vsor)
    )

    fun findById(id: String): VsBadgeOption? {
        return options.firstOrNull { it.id == id }
    }
}
