package com.seenu.dev.android.notemark.presentation.notes_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF667eea),
            Color(0xFF764ba2)
        )
    ),
    contentColor: Color = Color.White,
    size: Dp = 48.dp,
    shape: Shape = CircleShape,
    elevation: Dp = 4.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (enabled) elevation else 0.dp,
                shape = shape,
                clip = false
            )
            .clip(shape = shape)
            .background(
                brush = if (enabled) gradient else Brush.linearGradient(
                    listOf(Color.Gray.copy(alpha = 0.3F), Color.Gray.copy(alpha = 0.3F))
                ),
                shape = shape
            ).clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                role = Role.Button,
                indication = ripple()
            ),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}