package feelings.guide.ui.nav

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import feelings.guide.util.SnackbarEventBus
import javax.inject.Inject

@HiltViewModel
class AppSnackbarViewModel @Inject constructor(
    val snackbarEventBus: SnackbarEventBus,
) : ViewModel()
