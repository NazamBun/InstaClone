package com.nazam.instaclone.core.di

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.dispatchers.DefaultAppDispatchers
import com.nazam.instaclone.core.media.DefaultImageBytesReader
import com.nazam.instaclone.core.media.ImageBytesReader
import com.nazam.instaclone.core.session.DefaultSessionManager
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.data.repository.AuthRepositoryImpl
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.auth.presentation.viewmodel.LoginViewModel
import com.nazam.instaclone.feature.auth.presentation.viewmodel.SignupViewModel
import com.nazam.instaclone.feature.home.data.repository.HomeRepositoryImpl
import com.nazam.instaclone.feature.home.data.repository.PostMediaRepositoryImpl
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import com.nazam.instaclone.feature.home.domain.repository.PostMediaRepository
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetExplorePostsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetPostByIdUseCase
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.ui.categories.CategoriesViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.ExploreViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import com.nazam.instaclone.feature.notifications.data.repository.SupabaseNotificationsRepository
import com.nazam.instaclone.feature.notifications.domain.repository.NotificationsRepository
import com.nazam.instaclone.feature.notifications.domain.usecase.MarkNotificationReadUseCase
import com.nazam.instaclone.feature.notifications.domain.usecase.ObserveNotificationsUseCase
import com.nazam.instaclone.feature.notifications.domain.usecase.RefreshNotificationsUseCase
import com.nazam.instaclone.feature.notifications.presentation.handler.NotificationsActionHandler
import com.nazam.instaclone.feature.notifications.presentation.badge.NotificationsBadgeStore
import com.nazam.instaclone.feature.notifications.presentation.viewmodel.NotificationsViewModel
import com.nazam.instaclone.feature.permissions.data.repository.PermissionsRepositoryImpl
import com.nazam.instaclone.feature.permissions.domain.repository.PermissionsRepository
import com.nazam.instaclone.feature.permissions.domain.usecase.GetPostPermissionUseCase
import com.nazam.instaclone.feature.profile.data.repository.ProfileRepositoryImpl
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository
import com.nazam.instaclone.feature.profile.domain.usecase.FollowUserUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetFollowersCountUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetFollowingCountUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyPostsUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyProfileUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.IsFollowingUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UnfollowUserUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UpdateAvatarUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UpdateMyProfileUseCase
import com.nazam.instaclone.feature.profile.presentation.ui.edit.EditProfileViewModel
import com.nazam.instaclone.feature.profile.presentation.viewmodel.ProfileViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ---------- Core ----------
    single { SupabaseClientProvider.client }
    single { Json { ignoreUnknownKeys = true } }
    single<AppDispatchers> { DefaultAppDispatchers() }
    single<ImageBytesReader> { DefaultImageBytesReader() }

    // ---------- Permissions ----------
    single<PermissionsRepository> { PermissionsRepositoryImpl(client = get(), json = get()) }
    factory { GetPostPermissionUseCase(get()) }

    // ---------- Auth ----------
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { LoginUseCase(get()) }
    factory { SignupUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    // ---------- Session (dépend de Auth + Permissions) ----------
    single<SessionManager> {
        DefaultSessionManager(
            dispatchers = get(),
            getCurrentUserUseCase = get(),
            getPostPermissionUseCase = get()
        )
    }

    // ---------- Post media ----------
    single<PostMediaRepository> { PostMediaRepositoryImpl(client = get(), bytesReader = get()) }
    factory { UploadPostImageUseCase(get()) }

    // ---------- Home ----------
    single<HomeRepository> { HomeRepositoryImpl(client = get(), json = get()) }
    factory { GetFeedUseCase(get()) }
    factory { GetPostByIdUseCase(get()) }
    factory { GetExplorePostsUseCase(get()) }
    factory { VoteLeftUseCase(get()) }
    factory { VoteRightUseCase(get()) }
    factory { CreatePostUseCase(get()) }
    factory { GetCommentsUseCase(get()) }
    factory { AddCommentUseCase(get()) }

    // ---------- Notifications ----------
    single<NotificationsRepository> { SupabaseNotificationsRepository(client = get(), json = get()) }
    factory { ObserveNotificationsUseCase(get()) }
    factory { RefreshNotificationsUseCase(get()) }
    factory { MarkNotificationReadUseCase(get()) }
    factory { NotificationsViewModel(get(), get(), get()) }
    single { NotificationsBadgeStore(dispatchers = get(), observeNotificationsUseCase = get(), refreshNotificationsUseCase = get()) }
    factory { NotificationsActionHandler(get()) }

    // ---------- Profile ----------
    single<ProfileRepository> { ProfileRepositoryImpl(client = get(), json = get()) }
    factory { GetMyProfileUseCase(get()) }
    factory { GetMyPostsUseCase(get()) }
    factory { GetFollowersCountUseCase(get()) }
    factory { GetFollowingCountUseCase(get()) }
    factory { IsFollowingUseCase(get()) }
    factory { FollowUserUseCase(get()) }
    factory { UnfollowUserUseCase(get()) }
    factory { UpdateMyProfileUseCase(get()) }
    factory { UpdateAvatarUseCase(get()) }

    // ---------- ViewModels ----------
    viewModel {
        HomeViewModel(
            dispatchers = get(),
            getFeedUseCase = get(),
            voteLeftUseCase = get(),
            voteRightUseCase = get(),
            getCommentsUseCase = get(),
            addCommentUseCase = get(),
            getCurrentUserUseCase = get(),
            logoutUseCase = get(),
            sessionManager = get()
        )
    }
    viewModel {
        CreatePostViewModel(
            dispatchers = get(),
            uploadPostImageUseCase = get(),
            createPostUseCase = get(),
            getCurrentUserUseCase = get()
        )
    }
    factory {
        LoginViewModel(
            dispatchers = get(),
            loginUseCase = get(),
            getCurrentUserUseCase = get(),
            sessionManager = get()
        )
    }
    factory {
        SignupViewModel(
            dispatchers = get(),
            signupUseCase = get(),
            sessionManager = get()
        )
    }
    factory { CategoriesViewModel() }
    factory { ExploreViewModel(dispatchers = get(), getExplorePostsUseCase = get()) }
    viewModel {
        ProfileViewModel(
            dispatchers = get(),
            getCurrentUserUseCase = get(),
            getMyProfileUseCase = get(),
            getMyPostsUseCase = get(),
            getFollowersCountUseCase = get(),
            getFollowingCountUseCase = get(),
            isFollowingUseCase = get(),
            followUserUseCase = get(),
            unfollowUserUseCase = get(),
            logoutUseCase = get(),
            sessionManager = get(),
            uploadPostImageUseCase = get(),
            updateAvatarUseCase = get()
        )
    }
    viewModel {
        EditProfileViewModel(
            dispatchers = get(),
            getCurrentUserUseCase = get(),
            getMyProfileUseCase = get(),
            updateMyProfileUseCase = get()
        )
    }
}
