package com.example.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.core.authStateFlow
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.data.local.AppDatabase
import com.example.data.local.LocalProgressRepository
import com.example.data.remote.AiTeacherRepository
import com.example.data.remote.FirestoreSyncRepository
import com.example.ui.SslcStudyViewModel
import com.example.ui.screens.AiTeacherScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CoachProfileScreen
import com.example.ui.screens.FullMarksAcademyScreen
import com.example.ui.screens.KsmkeSyllabusScreen
import com.example.ui.screens.PyqIntelligenceScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

sealed class AppRoute(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Teacher : AppRoute("teacher", "Teach", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    data object Syllabus : AppRoute("syllabus", "KSMKE", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook)
    data object PyqBank : AppRoute("pyq_bank", "PYQs", Icons.Filled.Quiz, Icons.Outlined.Quiz)
    data object Academy : AppRoute("academy", "Academy", Icons.Filled.School, Icons.Outlined.School)
    data object Coach : AppRoute("coach", "Coach", Icons.Filled.Person, Icons.Outlined.Person)
}

private val navDestinations = listOf(
    AppRoute.Teacher,
    AppRoute.Syllabus,
    AppRoute.PyqBank,
    AppRoute.Academy,
    AppRoute.Coach
)

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = auth.currentUser)

    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            MainStudyWorkspace(
                currentUserId = user.uid,
                displayName = user.displayName ?: "SSLC Scholar"
            )
        }
    }
}

@Composable
private fun MainStudyWorkspace(
    currentUserId: String,
    displayName: String
) {
    val studyViewModel: SslcStudyViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val localDb = AppDatabase.getInstance(app)
                SslcStudyViewModel(
                    firestoreSync = FirestoreSyncRepository(db),
                    localProgress = LocalProgressRepository(localDb.studyProgressDao()),
                    aiTeacher = AiTeacherRepository(),
                    currentUserId = currentUserId,
                    initialDisplayName = displayName
                )
            }
        }
    )

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val navigateTo: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!useRail) {
                    NavigationBar {
                        navDestinations.forEach { dest ->
                            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTo(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag("nav_${dest.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail {
                        navDestinations.forEach { dest ->
                            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTo(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag("rail_${dest.route}")
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Teacher.route,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(AppRoute.Teacher.route) {
                        AiTeacherScreen(viewModel = studyViewModel)
                    }
                    composable(AppRoute.Syllabus.route) {
                        KsmkeSyllabusScreen(
                            viewModel = studyViewModel,
                            onAskGurujiForChapter = { chapterNum ->
                                val ch = KsmkeSyllabusRepository.getChapter(chapterNum)
                                studyViewModel.setActiveChapter(chapterNum)
                                studyViewModel.askTeacher(ch.starterQuestion)
                                navigateTo(AppRoute.Teacher.route)
                            },
                            onOpenChapterPaper = { chapterNum ->
                                studyViewModel.selectPaperChapter(chapterNum)
                                studyViewModel.setPyqPaperTabSelected(true)
                                navigateTo(AppRoute.PyqBank.route)
                            }
                        )
                    }
                    composable(AppRoute.PyqBank.route) {
                        PyqIntelligenceScreen(
                            viewModel = studyViewModel,
                            onPracticeInGuruji = { pyq ->
                                studyViewModel.setActiveChapter(pyq.chapterNumber)
                                studyViewModel.askTeacher("Teach me how to solve this ${pyq.marks}-mark ${pyq.yearLabel} question step by step: ${pyq.questionEn}")
                                navigateTo(AppRoute.Teacher.route)
                            }
                        )
                    }
                    composable(AppRoute.Academy.route) {
                        FullMarksAcademyScreen(viewModel = studyViewModel)
                    }
                    composable(AppRoute.Coach.route) {
                        CoachProfileScreen(
                            viewModel = studyViewModel,
                            studentName = displayName,
                            onSignedOut = {}
                        )
                    }
                }
            }
        }
    }
}
