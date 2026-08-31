package feelings.guide.ui.questions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import feelings.guide.R
import feelings.guide.data.CODE_FEELINGS
import feelings.guide.data.QuestionEntity
import feelings.guide.data.QuestionWithStats
import feelings.guide.util.relativeTimeLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionListScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToAnswer: (Long) -> Unit,
    onNavigateToLog: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: QuestionListViewModel = hiltViewModel(),
) {
    val questions by viewModel.questions.collectAsState()

    var showCreateSheet by rememberSaveable { mutableStateOf(false) }
    var editingQuestion by remember { mutableStateOf<QuestionEntity?>(null) }
    var deletingQuestion by remember { mutableStateOf<QuestionWithStats?>(null) }
    var hidingQuestion by remember { mutableStateOf<QuestionEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_questions_screen)) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Outlined.Menu, contentDescription = stringResource(R.string.cd_open_drawer))
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.cd_settings))
                    }
                },
            )
        },
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.large,
            ) {
                Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.cd_add_question))
            }
        },
    ) { padding ->
        LazyColumn(
            // A floating action button floats above content and isn't accounted for by
            // Scaffold's own padding (unlike a bottomBar) — without extra clearance here, the
            // FAB visually and functionally overlaps the last card's right-aligned icons.
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .testTag("questionsList"),
        ) {
            items(questions, key = { it.question.id }) { item ->
                QuestionCard(
                    item = item,
                    onClick = { onNavigateToAnswer(item.question.id) },
                    onLogClick = { onNavigateToLog(item.question.id) },
                    onEditClick = { editingQuestion = item.question },
                    onDeleteClick = { deletingQuestion = item },
                    onHideClick = { hidingQuestion = item.question },
                )
            }
        }
    }

    if (showCreateSheet) {
        QuestionEditBottomSheet(
            question = null,
            onDismiss = { showCreateSheet = false },
            onSave = { text ->
                viewModel.createQuestion(text)
                showCreateSheet = false
            },
        )
    }

    editingQuestion?.let { question ->
        QuestionEditBottomSheet(
            question = question,
            onDismiss = { editingQuestion = null },
            onSave = { text ->
                viewModel.updateQuestion(question, text)
                editingQuestion = null
            },
        )
    }

    deletingQuestion?.let { item ->
        DeleteQuestionDialog(
            answerCount = item.answerCount,
            onDismiss = { deletingQuestion = null },
            onConfirm = { alsoDeleteAnswers ->
                viewModel.deleteQuestion(item.question, alsoDeleteAnswers)
                deletingQuestion = null
            },
        )
    }

    hidingQuestion?.let { question ->
        HideQuestionDialog(
            onDismiss = { hidingQuestion = null },
            onConfirm = {
                viewModel.hideQuestion(question)
                hidingQuestion = null
            },
        )
    }
}

@Composable
private fun QuestionCard(
    item: QuestionWithStats,
    onClick: () -> Unit,
    onLogClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onHideClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val question = item.question
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("questionCard_${question.text}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            QuestionKicker(question)
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            val meta = if (item.answerCount == 0) {
                pluralStringResource(R.plurals.answers_count, 0, 0)
            } else {
                stringResource(
                    R.string.card_meta_last_answer,
                    pluralStringResource(R.plurals.answers_count, item.answerCount, item.answerCount),
                    relativeTimeLabel(item.lastAnsweredAt!!),
                )
            }
            Text(
                text = meta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                IconButton(onClick = onLogClick, modifier = Modifier.testTag("questionLog_${question.text}")) {
                    Icon(Icons.Outlined.History, contentDescription = stringResource(R.string.cd_log))
                }
                if (question.isUser) {
                    IconButton(onClick = onEditClick, modifier = Modifier.testTag("questionEdit_${question.text}")) {
                        Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.cd_edit))
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.testTag("questionDelete_${question.text}")) {
                        Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.cd_delete))
                    }
                } else {
                    IconButton(onClick = onHideClick, modifier = Modifier.testTag("questionHide_${question.text}")) {
                        Icon(Icons.Outlined.VisibilityOff, contentDescription = stringResource(R.string.cd_hide))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionKicker(question: QuestionEntity) {
    if (question.isUser) return
    val icon = if (question.code == CODE_FEELINGS) Icons.Outlined.FavoriteBorder else Icons.Outlined.Lock
    val label = stringResource(
        if (question.code == CODE_FEELINGS) R.string.kicker_feelings_built_in else R.string.kicker_built_in
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionEditBottomSheet(
    question: QuestionEntity?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var text by rememberSaveable(question?.id) { mutableStateOf(question?.text ?: "") }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // ModalBottomSheet owns a dismiss animation tied to sheetState; removing this composable from
    // composition (by flipping the caller's boolean) before that animation finishes leaves its
    // Popup window behind as a "ghost" root, breaking any dialog opened afterwards. Animate the
    // hide first and only then let the caller drop the composable.
    fun dismiss(afterHidden: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) afterHidden()
        }
    }

    ModalBottomSheet(onDismissRequest = { dismiss(onDismiss) }, sheetState = sheetState) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(
                    if (question == null) R.string.title_new_question_sheet else R.string.title_edit_question_sheet
                ),
                style = MaterialTheme.typography.titleLarge,
            )
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(R.string.hint_question_text)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .testTag("questionSheetTextField"),
            )
            if (question == null) {
                Text(
                    text = stringResource(R.string.msg_question_answers_free_text_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
            ) {
                TextButton(onClick = { dismiss(onDismiss) }) { Text(stringResource(R.string.btn_cancel)) }
                OutlinedButton(
                    onClick = { if (text.isNotBlank()) dismiss { onSave(text) } },
                    enabled = text.isNotBlank(),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .testTag("questionSheetSaveButton"),
                ) { Text(stringResource(R.string.btn_save)) }
            }
        }
    }
}

@Composable
private fun DeleteQuestionDialog(
    answerCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (alsoDeleteAnswers: Boolean) -> Unit,
) {
    var alsoDeleteAnswers by rememberSaveable { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_confirm_delete_question_dialog)) },
        text = {
            Column {
                Text(stringResource(R.string.msg_confirm_delete_question_dialog))
                if (answerCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Checkbox(
                            checked = alsoDeleteAnswers,
                            onCheckedChange = { alsoDeleteAnswers = it },
                            modifier = Modifier.testTag("deleteQuestionCheckbox"),
                        )
                        Text(
                            text = stringResource(R.string.checkbox_also_delete_answers),
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(alsoDeleteAnswers) },
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) { Text(stringResource(R.string.btn_delete), fontWeight = FontWeight.Medium) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } },
    )
}

@Composable
private fun HideQuestionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_confirm_hide_question_dialog)) },
        text = { },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.btn_hide)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } },
    )
}
