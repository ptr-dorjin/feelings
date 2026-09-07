package feelings.guide.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import feelings.guide.R
import feelings.guide.settings.DATE_FORMAT_OPTIONS
import feelings.guide.settings.TIME_FORMAT_OPTIONS
import feelings.guide.settings.ThemeMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsState()
    val hiddenCount by viewModel.hiddenBuiltInCount.collectAsState()
    var showRestoreDialog by remember { mutableStateOf(false) }
    val now = remember { LocalDateTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings_activity)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            SectionLabel(R.string.title_settings_category_interface)
            Text(stringResource(R.string.title_settings_theme), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 12.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(top = 8.dp)) {
                val options = listOf(
                    ThemeMode.DARK to R.string.settings_theme_dark,
                    ThemeMode.LIGHT to R.string.settings_theme_light,
                    ThemeMode.SYSTEM to R.string.settings_theme_system,
                )
                options.forEachIndexed { index, (mode, labelRes) ->
                    SegmentedButton(
                        selected = settings.themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    ) { Text(stringResource(labelRes)) }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            SectionLabel(R.string.title_settings_category_log)
            FormatDropdownRow(
                label = stringResource(R.string.title_settings_date_format),
                preview = now.format(runCatching { DateTimeFormatter.ofPattern(settings.dateFormatPattern) }.getOrDefault(DateTimeFormatter.ISO_DATE)),
                options = DATE_FORMAT_OPTIONS,
                selected = settings.dateFormatPattern,
                onSelect = viewModel::setDateFormatPattern,
                triggerTestTag = "dateFormatDropdown",
            )
            FormatDropdownRow(
                label = stringResource(R.string.title_settings_time_format),
                preview = now.format(runCatching { DateTimeFormatter.ofPattern(settings.timeFormatPattern) }.getOrDefault(DateTimeFormatter.ISO_TIME)),
                options = TIME_FORMAT_OPTIONS,
                selected = settings.timeFormatPattern,
                onSelect = viewModel::setTimeFormatPattern,
                triggerTestTag = "timeFormatDropdown",
                modifier = Modifier.padding(top = 16.dp),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            SectionLabel(R.string.title_settings_category_builtin_questions)
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Column {
                    Text(stringResource(R.string.title_settings_restore_built_in_questions), style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = if (hiddenCount == 0) stringResource(R.string.settings_built_in_all_visible)
                        else pluralStringResource(R.plurals.settings_built_in_hidden_count, hiddenCount, hiddenCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                OutlinedButton(
                    onClick = { showRestoreDialog = true },
                    modifier = Modifier.testTag("restoreTriggerButton"),
                ) { Text(stringResource(R.string.btn_restore)) }
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text(stringResource(R.string.title_confirm_restore_built_in_questions_dialog)) },
            text = { },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.restoreHiddenBuiltInQuestions(); showRestoreDialog = false },
                    modifier = Modifier.testTag("restoreConfirmButton"),
                ) {
                    Text(stringResource(R.string.btn_restore))
                }
            },
            dismissButton = { TextButton(onClick = { showRestoreDialog = false }) { Text(stringResource(R.string.btn_cancel)) } },
        )
    }
}

@Composable
private fun SectionLabel(labelRes: Int) {
    Text(
        text = stringResource(labelRes).uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormatDropdownRow(
    label: String,
    preview: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    triggerTestTag: String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(preview, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag(triggerTestTag),
        ) {
            Text(selected, modifier = Modifier.weight(1f, fill = true))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false },
                    modifier = Modifier.testTag("${triggerTestTag}Item_$option"),
                )
            }
        }
    }
}
