package com.seenu.dev.android.notemark.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun CustomIconButtonPreview() {
    CustomIconButton({}) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add Note",
            tint = Color.White
        )
    }
}

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = Color.Unspecified,
    rippleRadius: Dp = 40.dp,
    padding: Dp = 8.dp,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(size)
            .background(shape = shape, color = backgroundColor)
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    radius = rippleRadius,
                ),
                onClick = onClick
            )
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}