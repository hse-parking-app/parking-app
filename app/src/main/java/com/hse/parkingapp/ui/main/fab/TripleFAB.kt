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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hse.parkingapp.R
import com.hse.parkingapp.utils.math.lerp

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
            contentDescription = "FAB Inner icon"
        )
    }
}

/**
 * Composable function representing a Triple FAB (Floating Action Button).
 * @param modifier The modifier for the Triple FAB.
 * @param fabState The mutable state representing the state of the Triple FAB.
 * @param firstIcon The icon of the first sub FAB button as a [Painter].
 * @param secondIcon The icon of the second sub FAB button as a [Painter].
 * @param thirdIcon The icon of the third sub FAB button as a [Painter].
 */
@Composable
fun TripleFAB(
    modifier: Modifier = Modifier,
    fabState: MutableState<FabState> = remember { mutableStateOf(FabState.COLLAPSED) },
    firstIcon: Painter = painterResource(id = R.drawable.building_icon_24),
    secondIcon: Painter = painterResource(id = R.drawable.car_icon_24),
    thirdIcon: Painter = painterResource(id = R.drawable.baseline_exit_to_app_24),
    onCarClick: () -> Unit = { },
    onExitClick: () -> Unit = { },
    onBuildingClick: () -> Unit = { },
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
    val shrank: Dp by stateTransition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "shrinking"
    ) { state ->
        if (state == FabState.EXPANDED) 40.dp else 56.dp
    }
    val fromZeroToOneFloat: Float by stateTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 50) }, label = "fromZeroToOneFloat"
    ) { state ->
        if (state == FabState.EXPANDED) 1f else 0f
    }
    val positioningZeroToSide: Dp by stateTransition.animateDp(
        transitionSpec = { tween(durationMillis = 50) }, label = "positioningZeroToSide"
    ) { state ->
        if (state == FabState.EXPANDED) (-105).dp else 0.dp
    }
    val positioningZeroToCorner: Dp by stateTransition.animateDp(
        transitionSpec = { tween(durationMillis = 50) }, label = "positioningZeroToCorner"
    ) { state ->
        if (state == FabState.EXPANDED) (-76).dp else 0.dp
    }

    // List of FAB items
    val items = listOf(
        FabItem(firstIcon) { onBuildingClick() },
        FabItem(secondIcon) { onCarClick() },
        FabItem(thirdIcon) { onExitClick() },
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
                    painter = createMenuCloseAnimatedVectorPainter(
                        fabState.value,
                        MaterialTheme.colorScheme.onSurface
                    ),
                    contentDescription = "FAB Main icon",
                )
            }
        }
    }
}

// Menu icon expressed in vector primitives
object MenuIcon {
    val firstLine = listOf(
        PathNode.MoveTo(120f, 320f),
        PathNode.LineTo(120f, 240f),
        PathNode.LineTo(840f, 240f),
        PathNode.LineTo(840f, 320f),
        PathNode.LineTo(120f, 320f),
    )

    val secondLine = listOf(
        PathNode.MoveTo(120f, 520f),
        PathNode.LineTo(120f, 440f),
        PathNode.LineTo(840f, 440f),
        PathNode.LineTo(840f, 520f),
        PathNode.LineTo(120f, 520f),
    )

    val thirdLine = listOf(
        PathNode.MoveTo(120f, 720f),
        PathNode.LineTo(120f, 640f),
        PathNode.LineTo(840f, 640f),
        PathNode.LineTo(840f, 720f),
        PathNode.LineTo(120f, 720f),
    )
}

@Composable
fun createMenuCloseAnimatedVectorPainter(state: FabState, contentColor: Color): Painter {
    return rememberVectorPainter(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = false,
    ) { _, _ ->
        val transition = updateTransition(targetState = state, label = "MenuToClose")

        val stiffness = 800f
        val rotationQuarter by transition.animateFloat(
            label = "rotationQuarter",
            transitionSpec = { spring(stiffness = stiffness) }
        ) { state ->
            if (state == FabState.EXPANDED) 45f else 0f
        }

        val rotationHalf by transition.animateFloat(
            label = "rotationHalf",
            transitionSpec = { spring(stiffness = stiffness) }
        ) { state ->
            if (state == FabState.EXPANDED) 90f else 0f
        }

        val fraction by transition.animateFloat(
            label = "Fraction",
            transitionSpec = { spring(stiffness = stiffness) }
        ) { state ->
            if (state == FabState.EXPANDED) 1f else 0f
        }

        val fromFirstToSecond = lerp(MenuIcon.firstLine, MenuIcon.secondLine, fraction)
        val fromThirdToSecond = lerp(MenuIcon.thirdLine, MenuIcon.secondLine, fraction)

        Group(
            name = "GroupMenuIcon",
            rotation = rotationQuarter,
            translationX = 0.0f,
            translationY = 0.0f,
            pivotX = 480.0f,
            pivotY = 480.0f,
        ) {
            Path(
                pathData = fromFirstToSecond,
                fill = SolidColor(contentColor),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            )
            Group(
                name = "GroupSecondLineRotation",
                rotation = rotationHalf,
                translationX = 0.0f,
                translationY = 0.0f,
                pivotX = 480.0f,
                pivotY = 480.0f,
            ) {
                Path(
                    pathData = MenuIcon.secondLine,
                    fill = SolidColor(contentColor),
                    strokeLineWidth = 2.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = PathFillType.NonZero
                )
            }
            Path(
                pathData = fromThirdToSecond,
                fill = SolidColor(contentColor),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            )
        }
    }
}
