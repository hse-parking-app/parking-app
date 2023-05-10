package com.hse.parkingapp.ui.main

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
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
import com.hse.parkingapp.model.Canvas
import com.hse.parkingapp.model.Employee
import com.hse.parkingapp.model.Parking
import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.day.DayDataState
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.time.TimeData
import com.hse.parkingapp.model.time.TimeDataState
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
    dayDataState: DayDataState = DayDataState(),
    timeDataState: TimeDataState = TimeDataState(),
    employee: Employee = Employee()
) {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        DateChooser(
            dayDataState = dayDataState,
            onDayDataClick = { day ->
                handleEvent(SelectorEvent.DayChanged(day))
            },
            timeDataState = timeDataState,
            onTimeDataClick = { time ->
                handleEvent(SelectorEvent.TimeChanged(time))
            }
        )
        SpotCanvas(
            canvas = parking.level.canvas,
            spots = parking.spots,
            onSpotClick = { spot ->
                handleEvent(SelectorEvent.SpotChanged(spot))
                if (spot.isFree) {
                    scope.launch { bottomSheetState.show() }
                }
            }
        )
    }
    if (employee.cars.isNotEmpty()) {
        BottomSheet(
            bottomSheetState = bottomSheetState,
            selectorState = selectorState,
            onBookClick = {
            scope.launch {
                handleEvent(SelectorEvent.SpotBooked)
                bottomSheetState.hide()}
            },
            employee = employee
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    selectorState: SelectorState = SelectorState(),
    onBookClick: () -> Unit = {  },
    employee: Employee = Employee()
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
                                text = selectorState.selectedSpot?.parkingNumber ?: "",
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
                                    text = selectorState.selectedTime.getPeriod(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = selectorState.selectedDay.toString(),
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
                                    text = employee.cars.first().registryNumber,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = employee.cars.first().model,
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
                    if (employee.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.then(Modifier.size(32.dp))
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.book),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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
    dayDataState: DayDataState = DayDataState(),
    onDayDataClick: (day: DayData) -> Unit = {  },
    timeDataState: TimeDataState = TimeDataState(),
    onTimeDataClick: (TimeData) -> Unit = {  }
) {
    val listState = rememberLazyListState()
    val monthName by remember {
        derivedStateOf {
            dayDataState.dayDataList[listState.firstVisibleItemIndex].month
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f),
        shadowElevation = 8.dp
    ) {
        Column {
            Crossfade(targetState = monthName) { month ->
                when (month) {
                    dayDataState.getCurrentMonthName() -> MonthText(dayDataState.getCurrentMonthName())
                    dayDataState.getNextMonthName() -> MonthText(dayDataState.getNextMonthName())
                }
            }
            DaysRow(
                listState = listState,
                daysList = dayDataState.dayDataList.toList(),
                onDayDataClick = { onDayDataClick(it) }
            )
            TimesRow(
                timesList = timeDataState.timeDataList.toList(),
                onTimeDataClick = { onTimeDataClick(it) }
            )
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
fun DaysRow(
    listState: LazyListState = rememberLazyListState(),
    daysList: List<DayData> = listOf(),
    onDayDataClick: (DayData) -> Unit = {  }
) {
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(start = 16.dp)
    ) {
        items(daysList) { day ->
            DayButton(
                dayData = day,
                onDayDataClick = { dayData -> onDayDataClick(dayData) }
            )
        }
    }
}

@Composable
fun TimesRow(
    listState: LazyListState = rememberLazyListState(),
    timesList: List<TimeData> = listOf(),
    onTimeDataClick: (TimeData) -> Unit = {  }
) {
    if (timesList.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "No available options to book for today.")
        }
    } else {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
        ) {
            items(timesList) { time ->
                TimeButton(
                    timeData = time,
                    onTimeDataClick = onTimeDataClick
                )
            }
        }
    }
}

@Composable
fun SpotCanvas(
    canvas: Canvas = Canvas(0, 0),
    spots: List<Spot> = emptyList(),
    onSpotClick: (spot: Spot) -> Unit = {  }
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scale = remember { Animatable(0.5f) }
    val scope = rememberCoroutineScope()

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
                    isAvailable = spot.isAvailable,
                    isFree = spot.isFree,
                    onClick = { if (spot.isFree && spot.isAvailable) onSpotClick(spot) }
                )
            }
        }
    }
}

@Composable
fun TimeButton(
    timeData: TimeData = TimeData(),
    onTimeDataClick: (TimeData) -> Unit = {  }
) {
    val buttonColor by animateColorAsState(
        if (timeData.isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )

    Button(
        onClick = { onTimeDataClick(timeData) },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(4.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(
            text = timeData.toString(),
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
    isAvailable: Boolean = true,
    isFree: Boolean = false,
    onClick: () -> Unit = {  },
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isReleased by remember { mutableStateOf(false) }  // ???
    val sizeScale by animateFloatAsState(targetValue = if (isPressed) 1.2f else 1f)
    val offsetXAnimated by animateIntAsState(
        targetValue = if (isReleased && !isAvailable && !isFree) offsetX + 10 else offsetX,
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
            containerColor = if (isFree && isAvailable) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        modifier = Modifier
            .size(width = width.dp, height = height.dp)
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
            text = if (isAvailable) parkingNumber else "",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DayButton(
    dayData: DayData = DayData(),
    onDayDataClick: (DayData) -> Unit = {  }
) {
    val buttonColor by animateColorAsState(
        if (dayData.isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )

    Button(
        onClick = { onDayDataClick(dayData) },
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
            containerColor = buttonColor,
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