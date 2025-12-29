package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel KMP "pur" :
 * - pas de androidx.lifecycle.ViewModel
 * - pas de code Android/iOS
 * - 1 seul uiState
 */
class HomeViewModel(
    private val getFeedUseCase: GetFeedUseCase,
    private val voteLeftUseCase: VoteLeftUseCase,
    private val voteRightUseCase: VoteRightUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) {

    // internal -> accessible par nos fichiers helpers (même module) mais pas par l’UI
    internal val job = SupervisorJob()
    internal val scope = CoroutineScope(job + Dispatchers.Main)

    internal val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        refreshSession()
        loadFeed()
    }

    /** Vérifie la session et met à jour l’état utilisateur */
    fun refreshSession() {
        scope.launch {
            val user = withContext(Dispatchers.Default) { getCurrentUserUseCase.execute() }
            _uiState.update {
                it.copy(
                    isLoggedIn = user != null,
                    currentUserId = user?.id,
                    currentUserEmail = user?.email,
                    currentUserDisplayName = user?.displayName
                )
            }
        }
    }

    /** Charge le feed */
    fun loadFeed() = loadFeedInternal(getFeedUseCase)

    /** Handler UI : vote à gauche */
    fun voteLeft(postId: String) = voteInternal(
        postId = postId,
        isLeft = true,
        voteLeftUseCase = voteLeftUseCase,
        voteRightUseCase = voteRightUseCase
    )

    /** Handler UI : vote à droite */
    fun voteRight(postId: String) = voteInternal(
        postId = postId,
        isLeft = false,
        voteLeftUseCase = voteLeftUseCase,
        voteRightUseCase = voteRightUseCase
    )

    /** Si pas connecté et clic créer post => dialog */
    fun onCreatePostClicked() {
        if (uiState.value.isLoggedIn) return
        showAuthRequiredDialogInternal("Tu dois être connecté pour créer un post.")
    }

    /** Ouvre les commentaires et charge la liste */
    fun openComments(postId: String) = openCommentsInternal(postId, getCommentsUseCase)

    /** Ferme les commentaires */
    fun closeComments() = closeCommentsInternal()

    /** Texte du commentaire (max 500) */
    fun onNewCommentChange(value: String) {
        _uiState.update { it.copy(newCommentText = value.take(500)) }
    }

    /** Clic envoyer commentaire */
    fun onSendCommentClicked() = sendCommentInternal(addCommentUseCase)

    /** Si l’utilisateur veut écrire mais il n’est pas connecté */
    fun onCommentInputRequested() {
        if (!uiState.value.isLoggedIn) {
            showAuthRequiredDialogInternal("Tu dois te connecter ou créer un compte pour commenter.")
        }
    }

    /** Déconnexion */
    fun logout() = logoutInternal(logoutUseCase)

    /** Ferme la popup */
    fun consumeDialog() {
        _uiState.update {
            it.copy(
                dialogMessage = null,
                dialogConfirmLabel = null,
                dialogSecondaryLabel = null,
                dialogShouldOpenLogin = false,
                dialogShouldOpenSignup = false
            )
        }
    }

    /** Stoppe les coroutines */
    fun clear() {
        job.cancel()
    }
}