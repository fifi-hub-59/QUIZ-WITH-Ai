package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.SoundManager

/**
 * A sleek custom click modifier that scales the component down slightly on press
 * and automatically plays the click sound effect, providing high-fidelity game feel.
 */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    playSound: Boolean = true,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (enabled && isPressed) 0.93f else 1.0f,
        label = "bounce_click_scale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = null, // Uses sleek scale animation instead of heavy standard ripple
        onClick = {
            if (playSound) {
                SoundManager.playClick()
            }
            onClick()
        }
    )
}
