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
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.ui.categories.CategoriesViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.ExploreViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import com.nazam.instaclone.feature.profile.data.repository.ProfileRepositoryImpl
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyPostsUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyProfileUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UpdateMyProfileUseCase
import com.nazam.instaclone.feature.profile.presentation.ui.edit.EditProfileViewModel
import com.nazam.instaclone.feature.profile.presentation.viewmodel.ProfileViewModel
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {

    single { SupabaseClientProvider.client }
    single { Json { ignoreUnknownKeys = true } }
    single<AppDispatchers> { DefaultAppDispatchers() }
    single<ImageBytesReader> { DefaultImageBytesReader() }

    single<PostMediaRepository> {
        PostMediaRepositoryImpl(client = get(), bytesReader = get())
    }
    factory { UploadPostImageUseCase(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { LoginUseCase(get()) }
    factory { SignupUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    single<SessionManager> {
        DefaultSessionManager(
            dispatchers = get(),
            getCurrentUserUseCase = get()
        )
    }

    single<HomeRepository> { HomeRepositoryImpl(client = get(), json = get()) }
    factory { GetFeedUseCase(get()) }
    factory { VoteLeftUseCase(get()) }
    factory { VoteRightUseCase(get()) }
    factory { CreatePostUseCase(get()) }
    factory { GetCommentsUseCase(get()) }
    factory { AddCommentUseCase(get()) }

    single<ProfileRepository> { ProfileRepositoryImpl(client = get(), json = get()) }
    factory { GetMyProfileUseCase(get()) }
    factory { GetMyPostsUseCase(get()) }
    factory { UpdateMyProfileUseCase(get()) }

    factory {
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

    factory {
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
    single { ExploreViewModel() }

    factory {
        ProfileViewModel(
            dispatchers = get(),
            getCurrentUserUseCase = get(),
            getMyProfileUseCase = get(),
            getMyPostsUseCase = get(),
            logoutUseCase = get(),
            sessionManager = get()
        )
    }

    factory {
        EditProfileViewModel(
            dispatchers = get(),
            getCurrentUserUseCase = get(),
            getMyProfileUseCase = get(),
            updateMyProfileUseCase = get()
        )
    }
}
