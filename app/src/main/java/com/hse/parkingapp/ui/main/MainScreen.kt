package com.hse.parkingapp.ui.main

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hse.parkingapp.R
import com.hse.parkingapp.model.parking.Parking
import com.hse.parkingapp.model.canvas.Canvas
import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.day.DayDataState
import com.hse.parkingapp.model.spot.Spot
import com.hse.parkingapp.ui.beta.screens.components.material3.ModalBottomSheetLayout
import com.hse.parkingapp.ui.beta.screens.components.material3.ModalBottomSheetState
import com.hse.parkingapp.ui.beta.screens.components.material3.ModalBottomSheetValue
import com.hse.parkingapp.ui.beta.screens.components.material3.rememberModalBottomSheetState
import com.hse.parkingapp.ui.theme.ParkingAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    selectorState: SelectorState = SelectorState(),
    parking: Parking = Parking(),
    handleEvent: (event: SelectorEvent) -> Unit = {  },
    dayDataState: DayDataState = DayDataState()
) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        DateChooser(
            selectorState = selectorState,
            dayDataState = dayDataState
        )
        SpotCanvas(
            canvas = parking.levels[0].canvas,
            spots = parking.spots,
            onSpotClick = { spot ->
                handleEvent(SelectorEvent.SpotChanged(spot))
                if (spot.isFree) {
                    scope.launch { bottomSheetState.show() }
                }
            }
        )
    }
    BottomSheet(
        bottomSheetState = bottomSheetState,
        parkingNumber = selectorState.selectedSpot?.parkingNumber,
        onBookClick = {
            scope.launch { bottomSheetState.hide() }
            // TODO: perform booking operation with a server
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    parkingNumber: String? = "",
    onBookClick: () -> Unit = {  }
) {
    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = bottomSheetState,
        sheetContent = {
            Column(
                modifier = modifier
                    .padding(top = 24.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.booking),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    BottomSheetFeature(
                        feature = R.string.place,
                        content = {
                            Text(
                                text = parkingNumber ?: "",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                    Divider(color = Color(0x0C9299A2), thickness = 1.dp)
                    BottomSheetFeature(
                        feature = R.string.time_and_date,
                        content = {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    // TODO: change sample data here
                                    text = "с 10:00 до 19:00",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    // TODO: change sample data here
                                    text = "вторник, 2 декабря",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    )
                    Divider(color = Color(0x0C9299A2), thickness = 1.dp)
                    BottomSheetFeature(
                        feature = R.string.car,
                        content = {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "А001ВС 152",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Toyota Camry",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(top = 25.dp)
                        .height(56.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    onClick = onBookClick
                ) {
                    Text(
                        text = stringResource(id = R.string.book),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    ) {  }
}

@Composable
fun BottomSheetFeature(
    @StringRes feature: Int = R.string.place,
    content: @Composable () -> Unit = {  }
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = feature),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        content()
    }
}

@Composable
fun DateChooser(
    modifier: Modifier = Modifier,
    selectorState: SelectorState = SelectorState(),
    dayDataState: DayDataState = DayDataState()
) {
    val listState = rememberLazyListState()
    val monthName by remember {
        derivedStateOf {
            dayDataState.dayDataList[listState.firstVisibleItemIndex].monthName
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f),
        shadowElevation = 8.dp
    ) {
        Column {
            // TODO: create an animation for a text of current month
            Crossfade(targetState = monthName) { month ->
                when (month) {
                    dayDataState.getCurrentMonthName() -> MonthText(dayDataState.getCurrentMonthName())
                    dayDataState.getNextMonthName() -> MonthText(dayDataState.getNextMonthName())
                }
            }
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp)
            ) {
                items(dayDataState.dayDataList) { item ->
                    DayButton(
                        dayData = item,
                        onClickChanged = dayDataState::onItemSelected
                    )
                }
            }
            LazyRow(
                contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
            ) {
                item {
                    TimeButton(time = "9:00 - 18:00")
                }
                item {
                    TimeButton(time = "10:00 - 19:00")
                }
                item {
                    TimeButton(time = "11:00 - 20:00")
                }
            }
        }
    }
}

@Composable
fun MonthText(
    monthName: String = "January"
) {
    Text(
        text = monthName,
        modifier = Modifier.padding(
            top = 36.dp,
            start = 20.dp,
            bottom = 4.dp
        ),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun SpotCanvas(
    canvas: Canvas = Canvas(0, 0),
    spots: List<Spot> = emptyList(),
    onSpotClick: (spot: Spot) -> Unit = {  }
) {
    var animated by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scale = remember { Animatable(0.75f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(animated) {
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = FloatSpringSpec(0.5f, 40f)
        )
        animated = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scope.launch {
                        scale.snapTo((scale.value * zoom).coerceIn(0.5f, 2f))
                    }
                    offsetX = (offsetX + pan.x).coerceIn(
                        -canvas.width.toFloat() * scale.value,
                        canvas.width.toFloat() * scale.value
                    )
                    offsetY = (offsetY + pan.y).coerceIn(
                        -canvas.height.toFloat() * scale.value,
                        canvas.height.toFloat() * scale.value
                    )
                }
            }
    ) {
        Box(
            Modifier
                .requiredSize(width = canvas.width.dp, height = canvas.height.dp)
                .align(Alignment.Center)
                .offset {
                    IntOffset(
                        offsetX.roundToInt(),
                        offsetY.roundToInt()
                    )
                }
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
        ) {
            spots.forEach { spot ->
                SpotButton(
                    offsetX = spot.onCanvasCoords.x,
                    offsetY = spot.onCanvasCoords.y,
                    width = spot.canvas.width,
                    height = spot.canvas.height,
                    parkingNumber = spot.parkingNumber,
                    isAvailable = spot.isFree,
                    onClick = { onSpotClick(spot) }
                )
            }
        }
    }
}

@Composable
fun TimeButton(
    time: String = "9:00 - 18:00"
) {
    Button(
        onClick = { /*TODO*/ },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(4.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SpotButton(
    offsetX: Int = 0,
    offsetY: Int = 0,
    width: Int = 140,
    height: Int = 60,
    parkingNumber: String = "",
    isAvailable: Boolean = false,
    onClick: () -> Unit = {  },
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isReleased by remember { mutableStateOf(false) }  // ???
    val sizeScale by animateFloatAsState(targetValue = if (isPressed) 1.2f else 1f)
    val offsetXAnimated by animateIntAsState(
        targetValue = if (isReleased && !isAvailable) offsetX + 10 else offsetX,
        animationSpec = spring(
            dampingRatio = 0.3f,
            stiffness = 5000f
        )
    )
    val coroutineScope = rememberCoroutineScope()

    // It's a trash code, but it works fine! (for this moment)
    // I'm working on it!
    if (isPressed) {
        DisposableEffect(Unit) {
            onDispose {
                coroutineScope.launch {
                    isReleased = !isReleased // true
                    delay(50)
                    isReleased = !isReleased // false
                }
            }
        }
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isAvailable) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        modifier = Modifier
            .size(width = width.dp, height = height.dp)
            // TODO: to work with gestures a little bit more deeply
//            .pointerInput(Unit) {
//                Log.d("test123", "pressed")
//                detectTapGestures(
//                    onPress = {
//                        Log.d("test123", "pressed")
//                    },
//                    onTap = {
//                        Log.d("test123", "pressed")
//                        try {
//                            Log.d("test123", "pressed")
////                            isPressed = true
//                            // Start recording here
////                            awaitRelease()
//                        } finally {
//                            Log.d("test123", "released")
////                            isPressed = false
//                            // Stop recording here
//                        }
//                    }
//                )
//            }
            .offset(x = offsetXAnimated.dp, y = offsetY.dp)
            .graphicsLayer(
                scaleX = sizeScale,
                scaleY = sizeScale
            )
        ,
        interactionSource = interactionSource,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = parkingNumber,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DayButton(
    dayData: DayData = DayData(),
    onClickChanged: (DayData) -> Unit = {  }
) {
    Button(
        onClick = {
            if (!dayData.isSelected) {
                onClickChanged(dayData.copy(isSelected = true))
            }
        },
        modifier = Modifier
            .padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 4.dp,
                end = if (dayData.isLastDayOfMonth) 12.dp else 4.dp
            )
            .size(50.dp),
        border = if(dayData.isToday) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (dayData.isSelected)
                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = "${dayData.day}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ParkingAppTheme {
        MainScreen()
    }
}