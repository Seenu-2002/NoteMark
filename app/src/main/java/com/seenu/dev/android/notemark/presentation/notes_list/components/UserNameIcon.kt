package com.seenu.dev.android.notemark.presentation.notes_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme
import com.seenu.dev.android.notemark.presentation.theme.SpaceGrotesk

class UserNameProvider(
    override val values: Sequence<String> = listOf(
        "John",
        "Jane Smith",
        "Charlie Max Black"
    ).asSequence()
) : PreviewParameterProvider<String>

@Preview
@Composable
private fun UserNameIconPreview(
    @PreviewParameter(UserNameProvider::class) name: String
) {
    NoteMarkTheme {
        UserNameIcon(
            modifier = Modifier.size(40.dp),
            name = name
        )
    }
}

@Composable
fun UserNameIcon(modifier: Modifier = Modifier, name: String) {
    val parts = name.split(" ")
    val initials = when (parts.size) {
        0 -> ""
        1 -> name.substring(0, 2).uppercase()
        else -> {
            (parts.first()[0].uppercase() + parts.last()[0].uppercase())
        }
    }

    Box(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp)
        ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}