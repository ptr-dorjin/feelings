package feelings.guide.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import feelings.guide.R
import feelings.guide.ui.answer.AnswerScreen
import feelings.guide.ui.log.LogScreen
import feelings.guide.ui.questions.QuestionListScreen
import feelings.guide.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeelingsGuideApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerViewModel: DrawerViewModel = hiltViewModel()
    val (answerCount, questionCount) = drawerViewModel.summary.collectAsState().value
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route

    val appSnackbarViewModel: AppSnackbarViewModel = hiltViewModel()
    val appSnackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    LaunchedEffect(Unit) {
        appSnackbarViewModel.snackbarEventBus.events.collect { messageRes ->
            appSnackbarHostState.showSnackbar(resources.getString(messageRes))
        }
    }

    fun navigateTopLevel(route: String) {
        scope.launch { drawerState.close() }
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute == Routes.QUESTIONS,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp),
                )
                Text(
                    text = stringResource(
                        R.string.nav_drawer_summary,
                        pluralStringResource(R.plurals.answers_count, answerCount, answerCount),
                        pluralStringResource(R.plurals.questions_count, questionCount, questionCount),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 24.dp, top = 4.dp, bottom = 16.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_drawer_item_questions)) },
                    icon = { Icon(Icons.Outlined.Article, contentDescription = null) },
                    selected = currentRoute == Routes.QUESTIONS,
                    onClick = { navigateTopLevel(Routes.QUESTIONS) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_drawer_item_full_log)) },
                    icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                    selected = currentRoute == Routes.LOG_FULL,
                    onClick = { navigateTopLevel(Routes.LOG_FULL) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_drawer_item_settings)) },
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    selected = currentRoute == Routes.SETTINGS,
                    onClick = { navigateTopLevel(Routes.SETTINGS) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        },
    ) {
        // contentWindowInsets = 0: each screen's own Scaffold already applies system bar insets;
        // this outer one exists only to host a single app-wide SnackbarHost above them.
        Scaffold(
            snackbarHost = { SnackbarHost(appSnackbarHostState) },
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.QUESTIONS,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.QUESTIONS) {
                QuestionListScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateToAnswer = { questionId -> navController.navigate(Routes.answer(questionId)) },
                    onNavigateToLog = { questionId -> navController.navigate(Routes.logQuestion(questionId)) },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                )
            }
            composable(
                route = Routes.ANSWER,
                arguments = listOf(
                    androidx.navigation.navArgument("questionId") { type = androidx.navigation.NavType.LongType },
                    androidx.navigation.navArgument("answerId") {
                        type = androidx.navigation.NavType.LongType
                        defaultValue = -1L
                    },
                ),
            ) {
                AnswerScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.LOG_FULL) {
                LogScreen(
                    onBack = { navController.popBackStack() },
                    onEditAnswer = { questionId, answerId -> navController.navigate(Routes.answer(questionId, answerId)) },
                )
            }
            composable(
                route = Routes.LOG_QUESTION,
                arguments = listOf(androidx.navigation.navArgument("questionId") { type = androidx.navigation.NavType.LongType }),
            ) {
                LogScreen(
                    onBack = { navController.popBackStack() },
                    onEditAnswer = { questionId, answerId -> navController.navigate(Routes.answer(questionId, answerId)) },
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
        }
    }
}
