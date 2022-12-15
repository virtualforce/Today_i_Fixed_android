## Problem
How To create a Chat Screen with Lazy Column and Lazy List State in Jetpack Compose.

## How you fix it
Complete Chat Screen Code with proper chat bubbles alongside with image messages is written below:

## Solution

```

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Messages(
    navController: NavController?,
    dashboardViewModel: DashboardViewModel,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var messageTyped by remember { mutableStateOf("") }
    var focusState by remember { mutableStateOf(false) }
    var msgState by remember { mutableStateOf(false) }

    val scrollToBottom =
        scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == dashboardViewModel.messages.size - 2
    val lastItemIndex = dashboardViewModel.messages.size - 1

    Column(
        modifier = Modifier.fillMaxSize().background(
                color = primaryColorWhite,
            )
    ) {
        DynamicTopBar(title = "John Doe")
        Box(
            modifier = modifier.weight(1f)
        ) {

            val authorMe = "me"
            LazyColumn(
                reverseLayout = false, state = scrollState,
                // Add content padding so that the content can be scrolled (y-axis)
                // below the status bar + app bar
                // TODO: Get height from somewhere
                contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 90.dp))
                    .asPaddingValues(), modifier = Modifier.fillMaxSize()
            ) {
                for (index in 0 until dashboardViewModel.messages.size) {
                    val prevAuthor = dashboardViewModel.messages.getOrNull(index - 1)?.author
                    val nextAuthor = dashboardViewModel.messages.getOrNull(index + 1)?.author
                    val content = dashboardViewModel.messages[index]
                    val isFirstMessageByAuthor = prevAuthor != content.author
                    val isLastMessageByAuthor = nextAuthor != content.author

                    item {
                        Spacer(modifier = Modifier.padding(10.dp))
                    }

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

            LaunchedEffect(scrollState.layoutInfo.totalItemsCount) {
                scrollState.animateScrollToItem(scrollState.layoutInfo.totalItemsCount)
            }

        }

        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_camera),
                contentDescription = "Camera Image"
            )
            Spacer(
                modifier = Modifier.padding(5.dp)
            )
            OutlinedTextField(value = messageTyped,
                onValueChange = {
                    messageTyped = it
                },
                modifier = Modifier.weight(1f).onFocusChanged { focus ->
                        focusState = focus.isFocused
                    },
                label = {
                    Text(
                        text = if (focusState) "" else "Type Message…",
                    )
                },
                textStyle = Typography.body2.copy(color = primaryColorBlack),//TextStyle(color = primaryColorBlack, fontWeight = FontWeight.Normal, fontSize = 14.sp),
                maxLines = 5,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = primaryColorWhite,
                    focusedIndicatorColor = primaryColor,
                    unfocusedIndicatorColor = primaryColorLightGrey2
                )
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Image(painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "send button",
                modifier = Modifier.clickable {
                    if (messageTyped.isNotEmpty()) {
                        val msg =
                            Message(author = "me", content = messageTyped, timestamp = "10:02 PM")
                        dashboardViewModel.messages.add(msg)
                        msgState = true
                        messageTyped = ""
                    }
                })
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
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {

        if (isUserMe) {

            AuthorAndTextMessage(
                msg = msg,
                isUserMe = isUserMe,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                isLastMessageByAuthor = isLastMessageByAuthor,
                authorClicked = onAuthorClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

        } else {
            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.background(color = primaryColor, shape = CircleShape)
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
                                (newDiameter - currentWidth) / 2, (newDiameter - currentHeight) / 2
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

            AuthorAndTextMessage(
                msg = msg,
                isUserMe = isUserMe,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                isLastMessageByAuthor = isLastMessageByAuthor,
                authorClicked = onAuthorClick,
                modifier = Modifier.padding(end = 16.dp).weight(1f)
            )
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
        ChatItemBubble(msg, isUserMe, authorClicked = authorClicked)
    }
}

@Composable
private fun AuthorNameTimestamp(msg: Message) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.alignBy(LastBaseline)
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
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).height(16.dp)
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
        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun ChatItemBubble(
    message: Message, isUserMe: Boolean, authorClicked: (String) -> Unit
) {

    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.grey
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    if (isUserMe) {
        Column {
            if (message.content.isNotEmpty()) {
                Surface(
                    color = backgroundBubbleColor, shape = ChatBubbleShapeMe
                ) {
                    TextMessageView(
                        message = message, isUserMe = isUserMe, authorClicked = authorClicked
                    )
                }
            }

            message.image?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = backgroundBubbleColor, shape = ChatBubbleShapeMe
                ) {
                    AsyncImage(
                        model = it,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(3.dp)
                            .clip(ChatBubbleShapeMe),//.size(160.dp),
                        contentDescription = stringResource(id = R.string.additional_services)
                    )
                }
            }
        }
    } else {
        Column {
            if (message.content.isNotEmpty()) {
                Surface(
                    color = backgroundBubbleColor, shape = ChatBubbleShapeOther
                ) {
                    TextMessageView(
                        message = message, isUserMe = isUserMe, authorClicked = authorClicked
                    )
                }
            }

            message.image?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = backgroundBubbleColor, shape = ChatBubbleShapeOther
                ) {
                    AsyncImage(
                        model = it,
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
    message: Message, isUserMe: Boolean, authorClicked: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current


    val styledMessage = messageFormatter(
        text = message.content, primary = isUserMe
    )

    Column {

        ClickableText(text = styledMessage,
            style = Typography.caption.copy(color = if (isUserMe) primaryColorBlack else primaryColorWhite),
            modifier = Modifier.padding(16.dp),
            onClick = {
                styledMessage.getStringAnnotations(start = it, end = it).firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                            SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                            else -> Unit
                        }
                    }
            })

        Text(
            text = message.timestamp,
            color = if (isUserMe) primaryColorBlack else primaryColorWhite,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            textAlign = TextAlign.End
        )
    }
}

private val JumpToBottomThreshold = 56.dp

private fun ScrollState.atBottom(): Boolean = value == 0


```

## Message Formatter

```

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

// Regex containing the syntax tokens
val symbolPattern by lazy {
    Regex("""(https?://[^\s\t\n]+)|(`[^`]+`)|(@\w+)|(\*[\w]+\*)|(_[\w]+_)|(~[\w]+~)""")
}

// Accepted annotations for the ClickableTextWrapper
enum class SymbolAnnotationType {
    PERSON, LINK
}
typealias StringAnnotation = AnnotatedString.Range<String>
// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>


@Composable
fun messageFormatter(
    text: String,
    primary: Boolean
): AnnotatedString {
    val tokens = symbolPattern.findAll(text)

    return buildAnnotatedString {

        var cursorPosition = 0

        val codeSnippetBackground =
            if (primary) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.surface
            }

        for (token in tokens) {
            append(text.slice(cursorPosition until token.range.first))

            val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                matchResult = token,
                colorScheme = MaterialTheme.colorScheme,
                primary = primary,
                codeSnippetBackground = codeSnippetBackground
            )
            append(annotatedString)

            if (stringAnnotation != null) {
                val (item, start, end, tag) = stringAnnotation
                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }

            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }
    }
}

/**
 * Map regex matches found in a message with supported syntax symbols
 *
 * @param matchResult is a regex result matching our syntax symbols
 * @return pair of AnnotatedString with annotation (optional) used inside the ClickableText wrapper
 */
private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme,
    primary: Boolean,
    codeSnippetBackground: Color
): SymbolAnnotation {
    return when (matchResult.value.first()) {
        '@' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ),
            StringAnnotation(
                item = matchResult.value.substring(1),
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.PERSON.name
            )
        )
        '*' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('*'),
                spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
            ),
            null
        )
        '_' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('_'),
                spanStyle = SpanStyle(fontStyle = FontStyle.Italic)
            ),
            null
        )
        '~' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('~'),
                spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
            ),
            null
        )
        '`' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('`'),
                spanStyle = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    background = codeSnippetBackground,
                    baselineShift = BaselineShift(0.2f)
                )
            ),
            null
        )
        'h' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary
                )
            ),
            StringAnnotation(
                item = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.LINK.name
            )
        )
        else -> SymbolAnnotation(AnnotatedString(matchResult.value), null)
    }
}


```
