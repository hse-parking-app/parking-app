package com.hse.parkingapp.ui.main.fab

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hse.parkingapp.R

/**
 * Composable function that wraps a FloatingActionButton with custom styling.
 * @param onClick The click listener for the FloatingActionButton.
 * @param modifier The modifier for the FloatingActionButton.
 * @param painter The icon of the FloatingActionButton as a [Painter].
 */
@Composable
fun FABWrapper(
    onClick: () -> Unit,
    modifier: Modifier,
    painter: Painter,
) {
    FloatingActionButton(
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
        modifier = modifier,
    ) {
        Icon(
            painter = painter,
            contentDescription = ""
        )
    }
}

/**
 * Composable function representing a Triple FAB (Floating Action Button).
 * @param modifier The modifier for the Triple FAB.
 * @param fabState The mutable state representing the state of the Triple FAB.
 * @param mainIcon The icon of the main FAB button as a [Painter].
 * @param firstIcon The icon of the first sub FAB button as a [Painter].
 * @param secondIcon The icon of the second sub FAB button as a [Painter].
 * @param thirdIcon The icon of the third sub FAB button as a [Painter].
 */
@Composable
fun TripleFAB(
    modifier: Modifier = Modifier,
    fabState: MutableState<FabState> = remember { mutableStateOf(FabState.COLLAPSED) },
    mainIcon: Painter = painterResource(id = R.drawable.settings_icon_24),
    firstIcon: Painter = painterResource(id = R.drawable.building_icon_24),
    secondIcon: Painter = painterResource(id = R.drawable.car_icon_24),
    thirdIcon: Painter = painterResource(id = R.drawable.baseline_exit_to_app_24),
) {
    // State transitions for animation
    val stateTransition: Transition<FabState> =
        updateTransition(targetState = fabState.value, label = "")

    val stateChange: () -> Unit = {
        fabState.value =
            if (stateTransition.currentState == FabState.EXPANDED) {
                FabState.COLLAPSED
            } else {
                FabState.EXPANDED
            }
    }

    // Animation properties
    val rotate: Float by stateTransition.animateFloat(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "rotation"
    ) { state ->
        if (state == FabState.EXPANDED) 45f else 0f
    }
    val shrank: Dp by stateTransition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "shrinking"
    ) { state ->
        if (state == FabState.EXPANDED) 40.dp else 56.dp
    }
    val fromZeroToOneFloat: Float by stateTransition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 50)
        }, label = "fromZeroToOneFloat"
    ) { state ->
        if (state == FabState.EXPANDED) 1f else 0f
    }
    val positioningZeroToSide: Dp by stateTransition.animateDp(
        transitionSpec = {
            tween(durationMillis = 50)
        }, label = "positioningZeroToSide"
    ) { state ->
        if (state == FabState.EXPANDED) (-105).dp else 0.dp
    }
    val positioningZeroToCorner: Dp by stateTransition.animateDp(
        transitionSpec = {
            tween(durationMillis = 50)
        }, label = "positioningZeroToCorner"
    ) { state ->
        if (state == FabState.EXPANDED) (-76).dp else 0.dp
    }

    // List of FAB items
    val items = listOf(
        FabItem(firstIcon) { /* TODO */ },
        FabItem(secondIcon) { /* TODO */ },
        FabItem(thirdIcon) { /* TODO */ },
    )

    Box(
        modifier = modifier
            .padding(20.dp)
            .zIndex(2f),
        contentAlignment = Alignment.BottomEnd,
    ) {
        // Render sub FAB items
        FABWrapper(
            onClick = { items[0].clickable() },
            painter = items[0].icon,
            modifier = Modifier
                .absoluteOffset(y = animateDpAsState(targetValue = positioningZeroToSide).value)
                .size(56.dp)
                .alpha(animateFloatAsState(targetValue = fromZeroToOneFloat).value)
                .scale(animateFloatAsState(targetValue = fromZeroToOneFloat).value),
        )
        FABWrapper(
            onClick = { items[1].clickable() },
            painter = items[1].icon,
            modifier = Modifier
                .absoluteOffset(
                    x = animateDpAsState(targetValue = positioningZeroToCorner).value,
                    y = animateDpAsState(targetValue = positioningZeroToCorner).value
                )
                .size(56.dp)
                .alpha(animateFloatAsState(targetValue = fromZeroToOneFloat).value)
                .scale(animateFloatAsState(targetValue = fromZeroToOneFloat).value),
        )
        FABWrapper(
            onClick = { items[2].clickable() },
            painter = items[2].icon,
            modifier = Modifier
                .absoluteOffset(x = animateDpAsState(targetValue = positioningZeroToSide).value)
                .size(56.dp)
                .alpha(animateFloatAsState(targetValue = fromZeroToOneFloat).value)
                .scale(animateFloatAsState(targetValue = fromZeroToOneFloat).value),
        )

        // Render main TripleFAB item
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { stateChange() },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .size(shrank)
            ) {
                Icon(
                    painter = mainIcon,
                    contentDescription = "",
                    modifier = Modifier.rotate(animateFloatAsState(targetValue = rotate).value)
                )
            }
        }
    }
}
