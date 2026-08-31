package feelings.guide.ui.nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import feelings.guide.data.QuestionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    questionRepository: QuestionRepository,
) : ViewModel() {
    val summary: StateFlow<Pair<Int, Int>> = questionRepository.observeSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0 to 0)
}
