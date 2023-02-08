# scroll to bottom listener (end of list) in Jetpack Compose.

# Problem
How to use scroll to bottom listener (end of list) in Jetpack Compose


# Environment
N/A


# How you fix it
you can use rememberLazyListState() and compare with list counts.

# Solution
To fix this issue:
First add the above command as an extension (e.g., extensions.kt file):
```
    fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
```
Then use it in the following code:

```
     @Compose
fun List() {
  val scrollState = rememberLazyListState()

  LazyColumn(
    state = scrollState)
  ) {
     ...
  }

  // observer when reached end of list
  val endOfList by remember {
    derivedStateOf {
      scrollState.isScrolledToEnd()
    }
  }

  // act when end of list reached
  LaunchedEffect(endOfList) {
    // do your stuff
  }
}
```
