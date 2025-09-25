package com.seenu.dev.android.notemark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.seenu.dev.android.notemark.presentation.login.LoginScreen
import com.seenu.dev.android.notemark.presentation.note_detail.NoteDetailScreen
import com.seenu.dev.android.notemark.presentation.notes_list.NotesListScreen
import com.seenu.dev.android.notemark.presentation.onboarding.OnboardingScreen
import com.seenu.dev.android.notemark.presentation.route.Screen
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NoteMarkTheme {
                NavHost(
                    modifier = Modifier,
                    navController = navController,
//                    startDestination = Screen.Onboarding
                    startDestination = Screen.NotesList("Seenu nan")
                ) {
                    composable<Screen.Login> {
                        ScreenContainer(
                            showDarkIcons = false
                        ) {
                            LoginScreen(onLogin = { user ->
                                navController.navigate(Screen.NotesList(user.userName)) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            }, onRegister = {
//                                navController.navigate(Screen.Register)
                            })
                        }
                    }
                    composable<Screen.Onboarding> {
                        SetStatusBarIconColor(showDarkIcons = true)
                        OnboardingScreen(
                            onLogin = { navController.navigate(Screen.Login) },
                            onGetStarted = { navController.navigate(Screen.Register) }
                        )
                    }
                    composable<Screen.NotesList> {
                        val userName = it.toRoute<Screen.NotesList>().userName
                        ScreenContainer(
                            showDarkIcons = true
                        ) {
                            NotesListScreen(userName, openNote = { noteId ->
                                navController.navigate(Screen.NoteDetail(noteId))
                            })
                        }
                    }
                    composable<Screen.NoteDetail> {
                        val noteId = it.toRoute<Screen.NoteDetail>().noteId
                        ScreenContainer(
                            showDarkIcons = true
                        ) {
                            NoteDetailScreen(noteId, onBack = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ScreenContainer(
    showDarkIcons: Boolean = true,
    content: @Composable () -> Unit
) {
    SetStatusBarIconColor(showDarkIcons)
    content()
}

@Composable
fun SetStatusBarIconColor(showDarkIcons: Boolean) {
    val view = LocalView.current
    val activity = LocalActivity.current
    val windowInsetsController = activity?.window?.let {
        WindowCompat.getInsetsController(it, view)
    }

    LaunchedEffect(showDarkIcons) {
        windowInsetsController?.isAppearanceLightStatusBars = showDarkIcons
    }
}