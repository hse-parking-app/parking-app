package com.hse.parkingapp.ui.buildings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hse.parkingapp.R
import com.hse.parkingapp.model.Building

@Composable
fun BuildingsScreen(
    modifier: Modifier = Modifier,
    buildingsState: BuildingsState = BuildingsState(),
    handleEvent: (BuildingsEvent) -> Unit = {  }
) {
    Column(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Header()
        BuildingsColumn(
            buildingsList = buildingsState.buildingList,
            onBuildingClick = { building ->
                handleEvent(BuildingsEvent.OnBuildingClick(building))
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Continue(
            isLoading = buildingsState.isLoading,
            onContinueClick = { handleEvent(BuildingsEvent.OnContinueClick) }
        )
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
        text = stringResource(id = R.string.choose_building),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun BuildingsColumn(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    buildingsList: List<Building> = listOf(),
    onBuildingClick: (Building) -> Unit = {  }
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = listState,
    ) {
        items(buildingsList) { building ->
            BuildingButton(
                building = building,
                onBuildingClick = onBuildingClick
            )
        }
    }
}

@Composable
fun BuildingButton(
    modifier: Modifier = Modifier,
    building: Building = Building(),
    onBuildingClick: (Building) -> Unit = {  }
) {
    val buttonColor by animateColorAsState(
        if (building.isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
    )

    val textColor by animateColorAsState(
        if (building.isSelected && isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.onSurface
    )

    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onBuildingClick(building) },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = building.name,
                style = MaterialTheme.typography.displayMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = building.address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun Continue(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onContinueClick: () -> Unit = {  }
) {
    Button(
        onClick = onContinueClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.then(Modifier.size(32.dp))
            )
        } else {
            Text(
                text = stringResource(id = R.string.continue_),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
