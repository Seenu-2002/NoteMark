package com.seenu.dev.android.notemark.presentation.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.data.User
import com.seenu.dev.android.notemark.presentation.UiState
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme
import com.seenu.dev.android.notemark.presentation.theme.onboardingBackground
import com.seenu.dev.android.notemark.presentation.util.DeviceConfiguration
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Preview(
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun OnboardingScreen(
    openApp: (User) -> Unit = {},
    onLogin: () -> Unit = {},
    onGetStarted: () -> Unit = {},
) {
    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)
    val viewModel: OnboardingViewModel = koinViewModel()

    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (isUserLoggedIn is UiState.Empty) {
            Timber.d("Checking if user is logged in")
            viewModel.checkIsUserLoggedIn()
        }
    }

    when (val loginStatus = isUserLoggedIn) {
        is UiState.Empty, is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        is UiState.Success -> {
            LaunchedEffect(Unit) {
                val user = loginStatus.data
                openApp(user)
            }
            return
        }

        is UiState.Error -> {
            Timber.d("Login check failed :: ${loginStatus.message}")
        }
    }

    when (DeviceConfiguration.fromWindowSizeClass(windowSizeClass)) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            ConstraintLayout(Modifier.fillMaxSize()) {
                val (image, content) = createRefs()
                OnboardingImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(image) {
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                            top.linkTo(parent.top)
                            bottom.linkTo(content.top, margin = (-16).dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                OnboardingContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = shape
                        )
                        .clip(shape)
                        .constrainAs(content) {
                            width = Dimension.fillToConstraints
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, margin = 16.dp)
                        },
                    onLogin = onLogin,
                    onGetStarted = onGetStarted
                )
            }
        }

        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Row(
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.onboardingBackground)
            ) {
                OnboardingImage(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1F),
                    contentScale = ContentScale.Crop
                )
                val shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                OnboardingContent(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = shape
                        )
                        .clip(shape)
                        .weight(1F)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        else -> {
            ConstraintLayout(
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.onboardingBackground)
            ) {
                val (image, content) = createRefs()
                OnboardingImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .constrainAs(image) {
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                            top.linkTo(parent.top)
                            bottom.linkTo(content.top, margin = (-16).dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                OnboardingContent(
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(.9F)
                        .padding(horizontal = 24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = shape
                        )
                        .clip(shape)
                        .constrainAs(content) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 12.dp),
                    onLogin = onLogin,
                    onGetStarted = onGetStarted
                )
            }
        }
    }
}

@Preview
@Composable
fun OnboardingImage(
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.login_bg),
        contentDescription = "Onboarding Image",
        contentScale = contentScale
    )
}

@Composable
fun OnboardingContent(
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    onLogin: () -> Unit = {},
    onGetStarted: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {

        Text(
            text = stringResource(R.string.onboarding_header),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp),
            textAlign = textAlign,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(R.string.login_message),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
            textAlign = textAlign,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val buttonShape = RoundedCornerShape(8.dp)
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = buttonShape
        ) {
            Text(
                text = stringResource(R.string.onboarding_get_started),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            shape = buttonShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.login_btn),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}

@Preview
@Composable
private fun OnboardingContentPreview() {
    NoteMarkTheme {
        val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        OnboardingContent(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceContainerLowest, shape = shape)
                .clip(shape)
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    NoteMarkTheme { OnboardingScreen() }
}