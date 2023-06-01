package com.hse.parkingapp.ui.main

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hse.parkingapp.R
import com.hse.parkingapp.model.Employee
import com.hse.parkingapp.model.Parking
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.car.Car
import com.hse.parkingapp.model.car.CarsState
import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.day.DayDataState
import com.hse.parkingapp.model.level.LevelData
import com.hse.parkingapp.model.level.LevelDataState
import com.hse.parkingapp.model.reservation.Reservation
import com.hse.parkingapp.model.time.TimeData
import com.hse.parkingapp.model.time.TimeDataState
import com.hse.parkingapp.ui.beta.screens.components.material3.ModalBottomSheetLayout
import com.hse.parkingapp.ui.beta.screens.components.material3.ModalBottomSheetState
import com.hse.parkingapp.ui.beta.screens.components.material3.ModalBottomSheetValue
import com.hse.parkingapp.ui.beta.screens.components.material3.rememberModalBottomSheetState
import com.hse.parkingapp.ui.main.fab.FabState
import com.hse.parkingapp.ui.main.fab.TripleFAB
import com.hse.parkingapp.ui.theme.ParkingAppTheme
import com.hse.parkingapp.utils.constants.RussianLicensePlate
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
    handleEvent: (event: SelectorEvent) -> Unit = { },
    dayDataState: DayDataState = DayDataState(),
    timeDataState: TimeDataState = TimeDataState(),
    levelDataState: LevelDataState = LevelDataState(),
    carsState: CarsState = CarsState(),
    employee: Employee = Employee(),
    reservation: Reservation = Reservation(),
) {
    val bookingSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val carSelectionSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val carAdditionSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val fabState = remember { mutableStateOf(FabState.COLLAPSED) }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        if (parking.isEmpty) {
            TripleFAB(
                modifier = Modifier.align(Alignment.BottomEnd),
                fabState = fabState,
                onExitClick = { handleEvent(SelectorEvent.Exit) },
                onBuildingClick = { handleEvent(SelectorEvent.OpenBuildings) },
                onCarClick = {
                    fabState.value = FabState.COLLAPSED
                    scope.launch { carSelectionSheetState.show() }
                }
            )
        } else if (employee.reservation == null) {
            DateChooser(
                modifier = Modifier,
                dayDataState = dayDataState,
                onDayDataClick = { day ->
                    handleEvent(SelectorEvent.DayChanged(day))
                    fabState.value = FabState.COLLAPSED
                },
                timeDataState = timeDataState,
                onTimeDataClick = { time ->
                    handleEvent(SelectorEvent.TimeChanged(time))
                    fabState.value = FabState.COLLAPSED
                }
            )
            if (levelDataState.levelDataList.size > 1) {
                LevelPicker(
                    modifier = Modifier.align(Alignment.CenterStart),
                    levelsList = levelDataState.levelDataList,
                    onLevelClick = { level ->
                        handleEvent(SelectorEvent.LevelChanged(level))
                        fabState.value = FabState.COLLAPSED
                    }
                )
            }
            TripleFAB(
                modifier = Modifier.align(Alignment.BottomEnd),
                fabState = fabState,
                onExitClick = { handleEvent(SelectorEvent.Exit) },
                onBuildingClick = { handleEvent(SelectorEvent.OpenBuildings) },
                onCarClick = {
                    fabState.value = FabState.COLLAPSED
                    scope.launch {
                        handleEvent(SelectorEvent.OpenCars)
                        carSelectionSheetState.show()
                    }
                }
            )
        } else {
            ReservationInfo(
                modifier = Modifier.align(Alignment.BottomCenter),
                reservation = reservation,
                onCancelClick = { handleEvent(SelectorEvent.CancelReservation) }
            )
        }
        if (parking.isEmpty) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.empty_parking),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            SpotCanvas(
                parking = parking,
                onSpotClick = { spot ->
                    handleEvent(SelectorEvent.SpotChanged(spot))
                    if (spot.isFree) {
                        scope.launch { bookingSheetState.show() }
                    }
                },
                employee = employee,
                reservation = reservation,
                fabState = fabState
            )
        }
    }
    if (employee.selectedCar != null) {
        BookingSheet(
            bottomSheetState = bookingSheetState,
            selectorState = selectorState,
            onBookClick = {
                scope.launch {
                    handleEvent(SelectorEvent.SpotBooked)
                    bookingSheetState.hide()
                }
            },
            employee = employee
        )
    }

    CarSelectionSheet(
        selectionSheetState = carSelectionSheetState,
        additionSheetState = carAdditionSheetState,
        carsState = carsState,
        onCarClick = { car ->
            scope.launch { handleEvent(SelectorEvent.SelectCar(car)) }
        },
        deleteCar = { carId ->
            handleEvent(SelectorEvent.DeleteCar(carId))
        }
    )

    CarAdditionSheet(
        bottomSheetState = carAdditionSheetState,
        onAddClick = { model, registryNumber ->
            scope.launch {
                handleEvent(SelectorEvent.AddCar(model, registryNumber))
                carAdditionSheetState.hide()
            }
        },
        employee = employee
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BookingSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    selectorState: SelectorState = SelectorState(),
    onBookClick: () -> Unit = { },
    employee: Employee = Employee(),
) {
    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = bottomSheetState,
        sheetContent = {
            Column(
                modifier = modifier
                    .padding(top = 24.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
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
                    BookingSheetFeature(
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
                    BookingSheetFeature(
                        feature = R.string.time_and_date,
                        content = {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = selectorState.selectedTime.getHoursPeriod(),
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
                    BookingSheetFeature(
                        feature = R.string.car,
                        content = {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = employee.selectedCar?.registryNumber ?: "",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = employee.selectedCar?.model ?: "",
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
    ) { }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun CarSelectionSheet(
    modifier: Modifier = Modifier,
    selectionSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    additionSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    carsState: CarsState = CarsState(),
    onCarClick: (Car) -> Unit = { },
    deleteCar: (String) -> Unit = { },
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = selectionSheetState,
        sheetContent = {
            Column(
                modifier = modifier
                    .padding(top = 24.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = stringResource(id = R.string.cars),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (carsState.carsList.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_cars_available),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = carsState.carsList,
                            key = { car -> car.id }
                        ) { car ->
                            val currentItem by rememberUpdatedState(car)
                            val dismissThreshold = 0.25f
                            val currentFraction = remember { mutableStateOf(0f) }
                            val dismissState = rememberDismissState(
                                confirmValueChange = {
                                    if (currentFraction.value >= dismissThreshold &&
                                        currentFraction.value < 1.0f
                                    ) {
                                        deleteCar(currentItem.id)
                                        true
                                    } else {
                                        false
                                    }
                                },
                                positionalThreshold = { 72.dp.toPx() }
                            )

                            SwipeToDismiss(
                                modifier = Modifier
                                    .animateItemPlacement(),
                                state = dismissState,
                                directions = setOf(DismissDirection.StartToEnd),
                                background = {
                                    SwipeBackground(dismissState)
                                },
                                dismissContent = {
                                    CarCard(
                                        car = car,
                                        onCarClick = onCarClick,
                                        changeCurrentFraction = {
                                            currentFraction.value = dismissState.progress
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(56.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    onClick = { scope.launch { additionSheetState.show() } },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.add),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    ) { }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SwipeBackground(dismissState: DismissState) {
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1.2f
    )

    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_delete_24),
            tint = MaterialTheme.colorScheme.error,
            contentDescription = "Delete car",
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CarAdditionSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    onAddClick: (String, String) -> Unit = { _, _ -> },
    employee: Employee = Employee(),
) {
    var model by remember { mutableStateOf("") }
    var registryNumber by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val isValidNumber: (String) -> Boolean = {
        it.matches(RussianLicensePlate.plateRegex) && it.length in 1..25
    }

    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = bottomSheetState,
        sheetContent = {
            Column(
                modifier = modifier
                    .padding(top = 24.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = stringResource(id = R.string.new_car),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium
                )
                Column(
                    modifier = modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InputLine(
                        value = model,
                        onValueChanged = { model = it },
                        placeholder = stringResource(id = R.string.model)
                    )
                    InputLine(
                        value = registryNumber,
                        onValueChanged = {
                            isError = false
                            registryNumber = it.uppercase()
                        },
                        placeholder = stringResource(id = R.string.registry_number),
                        isError = isError,
                        supportingText = stringResource(id = R.string.invalid_number),
                        isLastField = true,
                    )
                }
                Button(
                    onClick = {
                        if (isValidNumber(registryNumber) && (if (registryNumber.length == 8)
                                RussianLicensePlate.region.contains(
                                    registryNumber.takeLast(2).toInt()
                                )
                            else
                                RussianLicensePlate.region.contains(
                                    registryNumber.takeLast(3).toInt()
                                ))
                        ) {
                            scope.launch {
                                onAddClick(model, registryNumber)

                                delay(500)
                                model = ""
                                registryNumber = ""
                            }
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (employee.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.then(Modifier.size(32.dp))
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.add),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    ) { }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputLine(
    value: String = "",
    onValueChanged: (String) -> Unit = { },
    placeholder: String = "",
    supportingText: String = "",
    isError: Boolean = false,
    isLastField: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = { onValueChanged(it) },
        isError = isError,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        keyboardOptions = if (!isLastField) {
            KeyboardOptions(imeAction = ImeAction.Next)
        } else {
            KeyboardOptions.Default
        },
        supportingText = {
            if (isError) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = supportingText,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
    )
}

@Composable
fun CarCard(
    modifier: Modifier = Modifier,
    car: Car = Car(),
    onCarClick: (Car) -> Unit = { },
    changeCurrentFraction: () -> Unit = { },
) {
    changeCurrentFraction()

    val buttonColor by animateColorAsState(
        if (car.isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )

    val textColor by animateColorAsState(
        if (car.isSelected && isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.onSurface
    )

    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onCarClick(car) },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = car.model,
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = car.registryNumber,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun BookingSheetFeature(
    @StringRes feature: Int = R.string.place,
    content: @Composable () -> Unit = { },
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
    onDayDataClick: (day: DayData) -> Unit = { },
    timeDataState: TimeDataState = TimeDataState(),
    onTimeDataClick: (TimeData) -> Unit = { },
) {
    val listState = rememberLazyListState()
    val monthName by remember {
        derivedStateOf {
            dayDataState.dayDataList[listState.firstVisibleItemIndex].monthStandalone
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
    monthName: String = "January",
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
    onDayDataClick: (DayData) -> Unit = { },
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
    onTimeDataClick: (TimeData) -> Unit = { },
) {
    if (timesList.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(43.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(id = R.string.no_place_to_reserve))
        }
    } else {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
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
    parking: Parking = Parking(),
    onSpotClick: (spot: Spot) -> Unit = { },
    employee: Employee = Employee(),
    reservation: Reservation = Reservation(),
    fabState: MutableState<FabState> = remember { mutableStateOf(FabState.COLLAPSED) },
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scale = remember { Animatable(0.5f) }
    val scope = rememberCoroutineScope()

    val alpha by animateFloatAsState(
        targetValue = if (parking.isLoading) 0f else 1f,
        animationSpec = FloatTweenSpec(duration = 250, easing = LinearOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scope.launch {
                        scale.snapTo((scale.value * zoom).coerceIn(0.5f, 2f))

                        offsetX = (offsetX + pan.x).coerceIn(
                            -parking.level.canvas.width.toFloat() * scale.value,
                            parking.level.canvas.width.toFloat() * scale.value
                        )
                        offsetY = (offsetY + pan.y).coerceIn(
                            -parking.level.canvas.height.toFloat() * scale.value,
                            parking.level.canvas.height.toFloat() * scale.value
                        )

                        if (fabState.value == FabState.EXPANDED) {
                            fabState.value = FabState.COLLAPSED
                        }
                    }
                }
            }
    ) {
        Box(
            Modifier
                .requiredSize(
                    width = parking.level.canvas.width.dp,
                    height = parking.level.canvas.height.dp
                )
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
            parking.spots.forEach { spot ->
                SpotButton(
                    offsetX = spot.onCanvasCoords.x,
                    offsetY = spot.onCanvasCoords.y,
                    width = spot.canvas.width,
                    height = spot.canvas.height,
                    parkingNumber = spot.parkingNumber,
                    isAvailable = spot.isAvailable,
                    isFree = spot.isFree,
                    isReserved = employee.reservation != null && spot.id == reservation.spot.id,
                    onClick = {
                        if (spot.isFree && spot.isAvailable) {
                            onSpotClick(spot)
                        }
                        fabState.value = FabState.COLLAPSED
                    }
                )
            }
        }
    }
}

@Composable
fun TimeButton(
    timeData: TimeData = TimeData(),
    onTimeDataClick: (TimeData) -> Unit = { },
) {
    val buttonColor by animateColorAsState(
        if (timeData.isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )

    val textColor by animateColorAsState(
        if (timeData.isSelected && isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.onSurface
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
            color = textColor
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
    onClick: () -> Unit = { },
    isReserved: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isReleased by remember { mutableStateOf(false) }
    val sizeScale by animateFloatAsState(targetValue = if (isPressed) 1.2f else 1f)
    val coroutineScope = rememberCoroutineScope()

    if (isPressed) {
        DisposableEffect(Unit) {
            onDispose {
                coroutineScope.launch {
                    isReleased = !isReleased
                    delay(50)
                    isReleased = !isReleased
                }
            }
        }
    }

    val spotColor by animateColorAsState(
        if (isFree && isAvailable) {
            MaterialTheme.colorScheme.tertiary
        } else if (isReserved) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.tertiaryContainer
        }
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = spotColor,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        modifier = Modifier
            .size(width = width.dp, height = height.dp)
            .offset(x = offsetX.dp, y = offsetY.dp)
            .graphicsLayer(
                scaleX = sizeScale,
                scaleY = sizeScale
            ),
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
    onDayDataClick: (DayData) -> Unit = { },
) {
    val buttonColor by animateColorAsState(
        if (dayData.isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )

    val textColor by animateColorAsState(
        if (dayData.isSelected && isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.onSurface
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
        border = if (dayData.isToday) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ) else null,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = "${dayData.day}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Composable function for displaying reservation information.
 * @param modifier The modifier for configuring the appearance and behavior of the composable. Default value is Modifier.
 */
@Composable
fun ReservationInfo(
    modifier: Modifier = Modifier,
    reservation: Reservation = Reservation(),
    onCancelClick: () -> Unit = { },
) {
    Card(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .zIndex(1f)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(id = R.string.has_reservation),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = reservation.time.getHoursPeriod(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = reservation.time.getDayInfo(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.surfaceTint
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                        text = reservation.spot.parkingNumber
                    )
                }
            }
            Button(
                onClick = onCancelClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = stringResource(id = R.string.cancel_reservation),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun LevelPicker(
    modifier: Modifier = Modifier,
    levelsList: List<LevelData> = listOf(),
    onLevelClick: (LevelData) -> Unit = { },
) {
    Card(
        modifier = modifier
            .alpha(0.85f)
            .padding(12.dp)
            .zIndex(1f),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        LazyColumn {
            items(levelsList) { level ->
                LevelButton(
                    level = level,
                    onLevelClick = onLevelClick
                )
            }
        }
    }
}

@Composable
fun LevelButton(
    modifier: Modifier = Modifier,
    level: LevelData = LevelData(),
    onLevelClick: (LevelData) -> Unit = { },
) {
    val levelColor by animateColorAsState(
        if (level.isSelected) MaterialTheme.colorScheme.onSecondaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )

    val levelNumberColor by animateColorAsState(
        if (level.isSelected && !isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.onSurface
    )

    Button(
        modifier = modifier.size(44.dp),
        onClick = { onLevelClick(level) },
        shape = RoundedCornerShape(0),
        colors = ButtonDefaults.buttonColors(
            containerColor = levelColor,
            contentColor = levelNumberColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = level.level.levelNumber
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
