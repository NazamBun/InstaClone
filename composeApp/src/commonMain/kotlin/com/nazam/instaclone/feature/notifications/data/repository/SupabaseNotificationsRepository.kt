package com.nazam.instaclone.feature.notifications.data.repository

import com.nazam.instaclone.feature.notifications.domain.model.AppNotification
import com.nazam.instaclone.feature.notifications.domain.model.NotificationTarget
import com.nazam.instaclone.feature.notifications.domain.model.NotificationTargetType
import com.nazam.instaclone.feature.notifications.domain.repository.NotificationsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class SupabaseNotificationsRepository(
    private val client: SupabaseClient,
    private val json: Json
) : NotificationsRepository {

    private companion object {
        const val TABLE = "notifications"
    }

    private val notifications = MutableStateFlow<List<AppNotification>>(emptyList())

    override fun observeNotifications(): StateFlow<List<AppNotification>> = notifications

    override suspend fun refresh() {
        val user = client.auth.currentUserOrNull()
        if (user == null) {
            notifications.value = emptyList()
            return
        }

        runCatching {
            val response = client.postgrest[TABLE].select {
                order(column = "created_at", order = Order.DESCENDING)
            }
            decode(response.data).map(::toDomain)
        }.onSuccess { items ->
            notifications.value = items
        }
    }

    override suspend fun markAsRead(notificationId: String) {
        val user = client.auth.currentUserOrNull() ?: return

        runCatching {
            client.postgrest[TABLE].update(
                UpdateNotificationReadDto(isRead = true)
            ) {
                filter {
                    eq("id", notificationId)
                    eq("recipient_user_id", user.id)
                }
            }
        }.onSuccess {
            notifications.update { items ->
                items.map { item ->
                    if (item.id == notificationId) item.copy(isRead = true) else item
                }
            }
        }
    }

    private fun decode(raw: String): List<NotificationDto> {
        return json.decodeFromString(ListSerializer(NotificationDto.serializer()), raw)
    }

    private fun toDomain(dto: NotificationDto): AppNotification {
        return AppNotification(
            id = dto.id,
            emoji = dto.emoji,
            title = dto.title,
            body = dto.body,
            time = dto.createdAt.substringBefore(".").replace("T", " "),
            isRead = dto.isRead,
            target = NotificationTarget(
                type = dto.type.toDomainType(),
                postId = dto.postId,
                authorId = dto.authorId,
                openCommentsOnOpen = dto.openCommentsOnOpen
            )
        )
    }
}

private fun String.toDomainType(): NotificationTargetType {
    return when (this) {
        "HOME_FEED" -> NotificationTargetType.HOME_FEED
        "EXPLORE_FEED" -> NotificationTargetType.EXPLORE_FEED
        "POST" -> NotificationTargetType.POST
        "PROFILE" -> NotificationTargetType.PROFILE
        else -> NotificationTargetType.HOME_FEED
    }
}

@Serializable
private data class NotificationDto(
    val id: String,
    val emoji: String,
    val title: String,
    val body: String,
    val type: String,
    @SerialName("post_id") val postId: String? = null,
    @SerialName("author_id") val authorId: String? = null,
    @SerialName("open_comments_on_open") val openCommentsOnOpen: Boolean = false,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String
)

@Serializable
private data class UpdateNotificationReadDto(
    @SerialName("is_read") val isRead: Boolean
)
