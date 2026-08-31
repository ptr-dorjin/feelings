package feelings.guide.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A simple one-shot event bus for cross-screen snackbar messages (e.g. "answer saved" shown on
 * whichever screen navigation lands back on). Simpler and more robust than routing the message
 * through a NavBackStackEntry's SavedStateHandle, and works regardless of which screen is current
 * since it's collected once at the app root.
 */
@Singleton
class SnackbarEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val events: SharedFlow<Int> = _events.asSharedFlow()

    suspend fun emit(messageRes: Int) {
        _events.emit(messageRes)
    }
}
