## How to add custom snackbar in jetpack compose?

## Problem
How to add custom snackbar in jetpack compose?

## Environment
- OS Version : Android 11+
- IDE Version : Android Studio

## How you fix it
Add custom snackbar replacing the native one with your own layout (background image, text, buttons)

## Solution

1. Create a Custom Composable for snackbar

```
@Composable
fun CustomSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    SnackbarHost(modifier = modifier,
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                backgroundColor = transparentWhiteColor
            )
            {
                Text(
                    modifier = Modifier.padding(
                        top = 20.dp,
                        bottom = 20.dp,
                        start = 20.dp,
                        end = 30.dp
                    ).background(Color.Transparent), text = snackbarData.message, style = Typography.body1
                )
            }
        })
}
```

2. Call the composable function in main layout file where you want to add the custom snackbar

```
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

scope.launch {
  snackbarHostState.showSnackbar("Please Select Date")
}

CustomSnackbar(
  modifier = Modifier.align(Alignment.BottomCenter),
  snackbarHostState = snackbarHostState
)
```

