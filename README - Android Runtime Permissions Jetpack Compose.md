
## Problem
   How to get Runtime permissions on Android in Jetpack Compose.

## How you fix it
   To use runtime permission you first need to create remember permission state in code below and after that check permissions where you required. Here is    the sample code for getting camera permissions in Android using Jetpack Compose:
## Solution

   You have to use this native runtime permissions library from Google to use Permissions in Android with Jetpack Compose.
```
//Jetpack compose runtime permissions
implementation "com.google.accompanist:accompanist-permissions:0.28.0"
```

### Warning
    The permission APIs are currently experimental and they could change at any time. All of the APIs are marked with the @ExperimentalPermissionsApi           annotation.

### Your Composable Function Code Snippet
```
// Camera permission state
val cameraPermissionState = rememberPermissionState(
    android.Manifest.permission.CAMERA
)

var permissionErrorMessage by remember { mutableStateOf("") }
var confirmDialogState by remember { mutableStateOf(false) }

//check the permissions before getting access to camera or any other thing whose permissions you're requesting in your composable function
if (cameraPermissionState.status.isGranted) {
    //if permission is granted already do your code here
} else {

    permissionErrorMessage = if (cameraPermissionState.status.shouldShowRationale) {
        // If the user has denied the permission but the rationale can be shown,
        // then gently explain why the app requires this permission
        "The camera is important for this app. Please grant the permission."
    } else {
        // If it's the first time the user lands on this feature, or the user
        // doesn't want to be asked again for this permission, explain that the
        // permission is required
        "Camera permission required for this feature to be available. " + "Please grant the permission"
    }

    //show your dialog for requesting permission
    confirmDialogState = true
}
```
