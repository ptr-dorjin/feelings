package feelings.guide.ui.log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import feelings.guide.R
import feelings.guide.data.AnswerEntity
import feelings.guide.export.LogExporter
import feelings.guide.util.EXPORT_FILE_NAME_FORMATTER
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    onBack: () -> Unit,
    onEditAnswer: (questionId: Long, answerId: Long) -> Unit,
    viewModel: LogViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val answerDeletedMessage = stringResource(R.string.msg_answer_deleted_success)
    val undoActionLabel = stringResource(R.string.snackbar_undo)

    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showClearDeletedDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) {
            scope.launch {
                val answers = viewModel.getAnswersForExport()
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    LogExporter().export(answers, out, context)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (viewModel.isFullLog) R.string.title_full_log_activity else R.string.title_answer_log_activity)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    if (uiState.rows.isNotEmpty()) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Outlined.MoreVert, contentDescription = stringResource(R.string.cd_more))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.btn_clear_log)) },
                                onClick = { showMenu = false; showClearDialog = true },
                            )
                            if (viewModel.isFullLog) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.btn_clear_log_deleted)) },
                                    onClick = { showMenu = false; showClearDeletedDialog = true },
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.btn_export_log)) },
                                onClick = {
                                    showMenu = false
                                    val suffix = if (viewModel.isFullLog) "full" else "${viewModel.questionId}"
                                    val name = "feelings-guide-log-$suffix-${LocalDateTime.now().format(EXPORT_FILE_NAME_FORMATTER)}.csv"
                                    exportLauncher.launch(name)
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (!viewModel.isFullLog && uiState.questionText != null) {
                LogHeader(questionText = uiState.questionText!!, answerCount = uiState.rows.size)
            }
            if (uiState.rows.isEmpty()) {
                Text(
                    text = stringResource(R.string.answer_log_empty_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp),
                )
            } else {
                val dateFormatter = remember(uiState.settings.dateFormatPattern) {
                    DateTimeFormatter.ofPattern(uiState.settings.dateFormatPattern)
                }
                val timeFormatter = remember(uiState.settings.timeFormatPattern) {
                    DateTimeFormatter.ofPattern(uiState.settings.timeFormatPattern)
                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("logRowsList"),
                ) {
                    items(uiState.rows, key = { it.answer.id }) { row ->
                        LogRowItem(
                            row = row,
                            showQuestion = viewModel.isFullLog,
                            dateFormatter = dateFormatter,
                            timeFormatter = timeFormatter,
                            onEdit = { onEditAnswer(row.answer.questionId, row.answer.id) },
                            onDelete = {
                                viewModel.deleteAnswer(row.answer)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = answerDeletedMessage,
                                        actionLabel = undoActionLabel,
                                        duration = SnackbarDuration.Long,
                                    )
                                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                        viewModel.undoDelete(row.answer)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        val titleRes = if (viewModel.isFullLog) R.string.title_confirm_clear_log_full_dialog else R.string.title_confirm_clear_log_by_question_dialog
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(titleRes)) },
            text = { Text(pluralStringResource(R.plurals.msg_confirm_clear_log, uiState.rows.size, uiState.rows.size)) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearLog(); showClearDialog = false }) {
                    Text(stringResource(R.string.btn_clear))
                }
            },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.btn_cancel)) } },
        )
    }

    if (showClearDeletedDialog) {
        AlertDialog(
            onDismissRequest = { showClearDeletedDialog = false },
            title = { Text(stringResource(R.string.title_confirm_clear_log_deleted_dialog)) },
            text = { },
            confirmButton = {
                TextButton(onClick = { viewModel.clearDeleted(); showClearDeletedDialog = false }) {
                    Text(stringResource(R.string.btn_clear))
                }
            },
            dismissButton = { TextButton(onClick = { showClearDeletedDialog = false }) { Text(stringResource(R.string.btn_cancel)) } },
        )
    }
}

@Composable
private fun LogHeader(questionText: String, answerCount: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = questionText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("logHeaderQuestionText"),
            )
            Text(
                text = stringResource(
                    R.string.log_by_question_summary,
                    pluralStringResource(R.plurals.answers_count, answerCount, answerCount),
                    stringResource(R.string.log_newest_first),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun LogRowItem(
    row: LogRow,
    showQuestion: Boolean,
    dateFormatter: DateTimeFormatter,
    timeFormatter: DateTimeFormatter,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        if (showQuestion) {
            Text(
                text = row.questionText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("logRowQuestionText"),
            )
        }
        Text(
            text = "${row.answer.dateTime.format(dateFormatter)} · ${row.answer.dateTime.format(timeFormatter)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = if (showQuestion) 4.dp else 0.dp)
                .testTag("logRowDateTime"),
        )
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = row.answer.answerText.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp),
            )
            IconButton(onClick = onEdit, modifier = Modifier.testTag("editAnswer_${row.answer.answerText}")) {
                Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.cd_edit))
            }
            IconButton(onClick = onDelete, modifier = Modifier.testTag("deleteAnswer_${row.answer.answerText}")) {
                Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.cd_delete))
            }
        }
    }
}
