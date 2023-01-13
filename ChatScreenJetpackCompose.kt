package com.vf.naylam_customer.ui.chat

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.vf.naylam_customer.R
import com.vf.naylam_customer.data.model.Message
import com.vf.naylam_customer.ui.dashboard.DashboardViewModel
import com.vf.naylam_customer.ui.my_trips.screens.MyTripCard
import com.vf.naylam_customer.ui.ui_elements.theme.*
import com.vf.naylam_customer.utils.DynamicTopBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private val ChatBubbleShapeMe =
    RoundedCornerShape(topEnd = 4.dp, topStart = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
private val ChatBubbleShapeOther =
    RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)

/*
@Composable
fun MessageScreen() {

//    val messages = remember { mutableStateListOf<ChatMessage>() }
//    val sdf = remember { SimpleDateFormat("hh:mm", Locale.ROOT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffFBE9E7))
    ) {
        DynamicTopBar(title = "Driver")
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(state = rememberLazyListState()) {
            items(5) {
                MessageBubble(true)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(driver: Boolean) {
    Row(modifier = Modifier.padding(horizontal = 20.dp)) {
        if (driver) {
            Image(painter = painterResource(id = R.drawable.persons), contentDescription = "Driver Image", modifier = Modifier.padding(10.dp))
            Text(text = "ABC", modifier = Modifier.weight(1f))
        } else {
            Text(text = "ABC", modifier = Modifier.weight(1f))
            Image(painter = painterResource(id = R.drawable.persons), contentDescription = "Pessenger Image", modifier = Modifier.padding(10.dp))
        }

    }
}
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Messages(
    navController: NavController?,
    dashboardViewModel: DashboardViewModel,
//    messages: ArrayList<Message>,
//    scrollState: LazyListState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var messageTyped by remember { mutableStateOf("") }
    var focusState by remember { mutableStateOf(false) }
    var msgState by remember { mutableStateOf(false) }
//    var msgS = dashboardViewModel.messages

    val scrollToBottom = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == dashboardViewModel.messages.size - 2
    val lastItemIndex = dashboardViewModel.messages.size - 1
//    var scroolOffset by remember { mutableStateOf(1) }
    /*var msgs by remember {
        mutableStateOf(messages)
    }*/

//    msgs.addAll(messages)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = primaryColorWhite,
//                    shape = RoundedCornerShape(10.dp)
            )
//            .padding(10.dp)
    ) {
        DynamicTopBar(title = "Talha Naeem")
        Box(
            modifier = modifier
                .weight(1f)
        ) {

            val authorMe = "me"
            LazyColumn(
                reverseLayout = false,
                state = scrollState,
                // Add content padding so that the content can be scrolled (y-axis)
                // below the status bar + app bar
                // TODO: Get height from somewhere
                contentPadding =
                WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
                modifier = Modifier
//                .testTag(ConversationTestTag)
                    .fillMaxSize()
            ) {
//                var index = 0
                for (index in 0 until dashboardViewModel.messages.size) {
                    val prevAuthor = dashboardViewModel.messages.getOrNull(index - 1)?.author
                    val nextAuthor = dashboardViewModel.messages.getOrNull(index + 1)?.author
                    val content = dashboardViewModel.messages[index]
                    val isFirstMessageByAuthor = prevAuthor != content.author
                    val isLastMessageByAuthor = nextAuthor != content.author

                    // Hardcode day dividers for simplicity
                    /*if (index == messages.size - 1) {
                        item {
                            DayHeader("20 Aug")
                        }
                    } else if (index == 2) {
                        item {
                            DayHeader("Today")
                        }
                    } else {*/
                    item {
                        Spacer(modifier = Modifier.padding(10.dp))
                    }
//                    }

                    item {
                        Message(
                            onAuthorClick = { name -> navigateToProfile(name) },
                            msg = content,
                            isUserMe = content.author == authorMe,
                            isFirstMessageByAuthor = isFirstMessageByAuthor,
                            isLastMessageByAuthor = isLastMessageByAuthor
                        )
                    }
                }
            }
            // Jump to bottom button shows up when user scrolls past a threshold.
            // Convert to pixels:
            val jumpThreshold = with(LocalDensity.current) {
                JumpToBottomThreshold.toPx()
            }

            // Show the button if the first visible item is not the first one or if the offset is
            // greater than the threshold.
            /*val jumpToBottomButtonEnabled by remember {
                derivedStateOf {
                    scrollState.firstVisibleItemIndex != 0 ||
                            scrollState.firstVisibleItemScrollOffset > jumpThreshold
                }
            }*/

            /*LaunchedEffect(scrollToBottom, lastItemIndex) {
                if (scrollToBottom) {
                    scope.launch {
                        scrollState.animateScrollToItem(lastItemIndex)
                    }
                }
            }*/

            LaunchedEffect(scrollState.layoutInfo.totalItemsCount) {
                scrollState.animateScrollToItem(scrollState.layoutInfo.totalItemsCount)
            }


//            Log.d("layout info", scrollState.layoutInfo.totalItemsCount.toString())
//            Log.d("first visible index", scrollState.firstVisibleItemIndex.toString() + ",    scroll offset" + scrollState.firstVisibleItemScrollOffset)
//            scrollState.layoutInfo.totalItemsCount
        }

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_camera),
                contentDescription = "Camera Image"
            )
            Spacer(
                modifier = Modifier
                    .padding(5.dp)
            )
            OutlinedTextField(
                value = messageTyped,
                onValueChange = {
                    messageTyped = it
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focus ->
                        focusState = focus.isFocused
                    },
                label = {
                    Text(
                        text = if (focusState) "" else "Type Message…",
                    )
                },
                textStyle = Typography.body2.copy(color = primaryColorBlack),//TextStyle(color = primaryColorBlack, fontWeight = FontWeight.Normal, fontSize = 14.sp),
                maxLines = 5,
                colors = TextFieldDefaults.textFieldColors(containerColor = primaryColorWhite, focusedIndicatorColor = primaryColor, unfocusedIndicatorColor = primaryColorLightGrey2)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "send button",
                modifier = Modifier.clickable {
                    if (messageTyped.isNotEmpty()) {
                        val msg =
                            Message(author = "me", content = messageTyped, timestamp = "10:02 PM")
                        dashboardViewModel.messages.add(msg)
                        msgState = true
                        messageTyped = ""
                    }
                }
            )
        }

        Spacer(modifier = Modifier.padding(5.dp))
    }
}

@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val borderColor = if (isUserMe) {
        primaryColor
//        MaterialTheme.colorScheme.primary
    } else {
        primaryColorGrey
//        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        /*if (isLastMessageByAuthor) {
            // Avatar
            Image(
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(msg.author) })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
//                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                painter = painterResource(id = msg.authorImage),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            // Space under avatar
            Spacer(modifier = Modifier.width(74.dp))
        }*/
        if (isUserMe) {
            /*if (msg.image != null) {
                Column(modifier = Modifier
                    .padding(10.dp)
                    .background(color = primaryColorLightGrey2, shape = ChatBubbleShapeMe)
                    .fillMaxWidth()) {
//                    Spacer(modifier = Modifier.weight(1f))
                    AsyncImage(model = msg.image, contentDescription = "msg image", modifier = Modifier.fillMaxSize())
                }

            } else {*/
                AuthorAndTextMessage(
                    msg = msg,
                    isUserMe = isUserMe,
                    isFirstMessageByAuthor = isFirstMessageByAuthor,
                    isLastMessageByAuthor = isLastMessageByAuthor,
                    authorClicked = onAuthorClick,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
//                    .weight(1f)
                )
//            }
        } else {
            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(color = primaryColor, shape = CircleShape)
                    .layout { measurable, constraints ->
                        // Measure the composable
                        val placeable = measurable.measure(constraints)

                        //get the current max dimension to assign width=height
                        val currentHeight = placeable.height
                        val currentWidth = placeable.width
                        val newDiameter = maxOf(currentHeight, currentWidth)

                        //assign the dimension and the center position
                        layout(newDiameter, newDiameter) {
                            // Where the composable gets placed
                            placeable.placeRelative(
                                (newDiameter - currentWidth) / 2,
                                (newDiameter - currentHeight) / 2
                            )
                        }
                    }) {

                Text(
                    text = "DC",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.padding(10.dp),
                    fontSize = 17.sp
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
            /*if (msg.image != null) {
                Column(modifier = Modifier
                    .padding(10.dp)
                    .background(color = primaryColor, shape = ChatBubbleShapeOther).fillMaxWidth()) {
                    AsyncImage(model = msg.image, contentDescription = "msg image", modifier = Modifier.fillMaxSize())
                }

            } else {*/
                AuthorAndTextMessage(
                    msg = msg,
                    isUserMe = isUserMe,
                    isFirstMessageByAuthor = isFirstMessageByAuthor,
                    isLastMessageByAuthor = isLastMessageByAuthor,
                    authorClicked = onAuthorClick,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f)
                )
//            }
        }
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        /*if (isLastMessageByAuthor) {
            AuthorNameTimestamp(msg)
        }*/
        ChatItemBubble(msg, isUserMe, authorClicked = authorClicked)
        /*if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }*/
    }
}

@Composable
private fun AuthorNameTimestamp(msg: Message) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}


@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {

    val backgroundBubbleColor = if (isUserMe) {
        primaryColorLightGrey2
    } else {
        primaryColor
//        MaterialTheme.colorScheme.surfaceVariant
    }

    if (isUserMe) {
        Column {
            if (message.content.isNotEmpty()) {
                Surface(
                    color = backgroundBubbleColor,
                    shape = ChatBubbleShapeMe
                ) {
                    TextMessageView(
                        message = message,
                        isUserMe = isUserMe,
                        authorClicked = authorClicked
                    )
                }
            }

            message.image?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = backgroundBubbleColor,
                    shape = ChatBubbleShapeMe
                ) {
                    AsyncImage(model = it,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(3.dp).clip(ChatBubbleShapeMe),//.size(160.dp),
                        contentDescription = stringResource(id = R.string.additional_services)
                    )
                }
            }
        }
    } else {
        Column {
            if (message.content.isNotEmpty()) {
                Surface(
                    color = backgroundBubbleColor,
                    shape = ChatBubbleShapeOther
                ) {
                    TextMessageView(
                        message = message,
                        isUserMe = isUserMe,
                        authorClicked = authorClicked
                    )
                }
            }

            message.image?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = backgroundBubbleColor,
                    shape = ChatBubbleShapeOther
                ) {
                    AsyncImage(model = it,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(3.dp).clip(ChatBubbleShapeOther),
                        contentDescription = stringResource(id = R.string.additional_services)
                    )
                }
            }
        }
    }
}

@Composable
fun TextMessageView(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current


    val styledMessage = messageFormatter(
        text = message.content,
        primary = isUserMe
    )

    Column {

        Text(
            text = message.content,
            style = Typography.body2.copy(color = if (isUserMe) primaryColorBlack else primaryColorWhite),
            modifier = Modifier.padding(10.dp)
        )
        /*ClickableText(
            text = styledMessage,
            style = Typography.caption.copy(color = if (isUserMe) primaryColorBlack else primaryColorWhite),
            modifier = Modifier.padding(16.dp),
            onClick = {
                styledMessage
                    .getStringAnnotations(start = it, end = it)
                    .firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                            SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                            else -> Unit
                        }
                    }
            }
        )*/

        Text(text = message.timestamp, color = if (isUserMe) primaryColorBlack else primaryColorWhite, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), textAlign = TextAlign.End)
    }
}

private val JumpToBottomThreshold = 56.dp

private fun ScrollState.atBottom(): Boolean = value == 0
