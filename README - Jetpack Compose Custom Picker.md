# Custom Number Picker in Jetpack Compose

# Problem :

### Number Picker in Jetpack Compose. Used for hours, minutes, text or any kind of string that you want to pick using a Picker in Jetpack Compose

# How you fix it :
Creates and Alert Popup and Place a custom number picker in it using LazyColumn.

# Solution :

# Picker Code:
```
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Picker(
    items: List<String>,
    state: PickerState = rememberPickerState(),
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
) {

    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = Integer.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex = listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val itemHeightPixels = remember { mutableStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.value)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }

    Box(modifier = modifier) {

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(listScrollCount) { index ->
                Text(
                    text = getItem(index),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixels.value = size.height }
                        .then(textModifier)
                )
            }
        }

        Divider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * visibleItemsMiddle)
        )

        Divider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * (visibleItemsMiddle + 1))
        )

    }

}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }
```

# Picker State:
```

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
}
```

# Picker in Alert Dialog:
```
package com.insofdev.sc.passenger.naylam.utils.HourPicker

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.insofdev.sc.passenger.naylam.R
import com.insofdev.sc.passenger.naylam.ui.ui_elements.theme.Shapes
import com.insofdev.sc.passenger.naylam.ui.ui_elements.theme.primaryColor
import com.insofdev.sc.passenger.naylam.ui.ui_elements.theme.primaryColorGrey
import com.insofdev.sc.passenger.naylam.ui.ui_elements.theme.primaryColorWhite

@ExperimentalMaterialApi
@Composable
fun PickerAlertPopup(
    title: String,
    hoursList: List<Int>,
    clickOk: (hour: Int) -> Unit,
    dialogState: Boolean,
    onDismissRequest: (dialogState: Boolean) -> Unit
) {
    var selectedHour by remember {
        mutableStateOf(0)
    }

    if (dialogState) {
        AlertDialog(onDismissRequest = { onDismissRequest(dialogState) },
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 200, easing = LinearOutSlowInEasing
                    )
                )
                .padding(20.dp),
            shape = Shapes.large,
            backgroundColor = primaryColorWhite,
            title = null,
            text = null,
            properties = DialogProperties(
                dismissOnBackPress = false, dismissOnClickOutside = false
            ),
            buttons = {
                Column(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Divider(
                        modifier = Modifier.padding(
                            start = 10.dp, end = 10.dp, bottom = 25.dp, top = 10.dp
                        ), color = primaryColorGrey, thickness = 1.dp
                    )


                    selectedHour = HourPicker(items = hoursList)


//                    Spacer(modifier = Modifier.height(60.dp))

//                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Button(
                            onClick = {
                                clickOk(selectedHour)
                                onDismissRequest(dialogState)
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(65.dp)
                                .padding(start = 50.dp, end = 50.dp, top = 20.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.ok),
                                color = primaryColorWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
//                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

            })
    }
}

@Composable
fun HourPicker(items: List<Int>): Int {

    val values = remember { items.map { it.toString() } }
    val valuesPickerState = rememberPickerState()

    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {

//            val units = remember { listOf("hours") }
//            val unitsPickerState = rememberPickerState()

//            Text(text = "Example Picker", modifier = Modifier.padding(top = 16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Picker(
                    state = valuesPickerState,
                    items = values,
                    visibleItemsCount = 3,
                    modifier = Modifier.weight(0.3f),
                    textModifier = Modifier.padding(8.dp),
                    textStyle = TextStyle(fontSize = 32.sp)
                )
                /*Picker(
                    state = unitsPickerState,
                    items = units,
                    visibleItemsCount = 3,
                    modifier = Modifier.weight(0.7f),
                    textModifier = Modifier.padding(8.dp),
                    textStyle = TextStyle(fontSize = 32.sp)
                )*/
            }
        }
    }

    return if (valuesPickerState.selectedItem.isNotEmpty()) valuesPickerState.selectedItem.toInt() else 0
}
```
