package feelings.guide.ui.answer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import feelings.guide.R
import feelings.guide.data.CODE_FEELINGS
import feelings.guide.feelings.FeelingsGroup
import feelings.guide.feelings.loadFeelingsGroups

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerScreen(
    onBack: () -> Unit,
    viewModel: AnswerViewModel = hiltViewModel(),
) {
    val question by viewModel.question.collectAsState()
    val answerText by viewModel.answerText.collectAsState()
    val context = LocalContext.current
    val isFeelings = question?.code == CODE_FEELINGS
    val feelingsGroups = remember(context, isFeelings) { if (isFeelings) loadFeelingsGroups(context) else emptyList() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(question?.id, isFeelings) {
        if (question != null && !isFeelings) {
            runCatching { focusRequester.requestFocus() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_answer_activity)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.save(onBack) }) {
                        Text(stringResource(R.string.btn_save))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = question?.text.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("answerQuestionHeadline"),
            )
            OutlinedTextField(
                value = answerText,
                onValueChange = viewModel::onAnswerTextChanged,
                placeholder = { Text(stringResource(R.string.hint_answer_text)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                minLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .focusRequester(focusRequester)
                    .testTag("answerTextField"),
            )

            if (isFeelings) {
                FeelingsDictionary(
                    groups = feelingsGroups,
                    onWordClick = viewModel::appendFeelingWord,
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Text(
                    text = "${answerText.length}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FeelingsDictionary(
    groups: List<FeelingsGroup>,
    onWordClick: (String) -> Unit,
) {
    val totalWords = groups.sumOf { it.count }
    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    Column(modifier = Modifier.padding(top = 24.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.feelings_dictionary_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = pluralStringResource(R.plurals.feelings_words_count, totalWords, totalWords) +
                    " · " + pluralStringResource(R.plurals.feelings_groups_count, groups.size, groups.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        groups.forEach { group ->
            val isExpanded = expanded[group.label] ?: false
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clickable { expanded[group.label] = !isExpanded }
                    .testTag("feelingsGroup_${group.label}"),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = group.label, style = MaterialTheme.typography.titleSmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${group.count}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    Icon(
                        if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = null,
                    )
                }
            }
            if (isExpanded) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    group.words.forEach { word ->
                        AssistChip(
                            onClick = { onWordClick(word) },
                            label = { Text(word) },
                            modifier = Modifier.testTag("feelingWord_$word"),
                        )
                    }
                }
            }
        }
    }
}
