## Problem
   How to use Image Picker with Camera and Gallery in Jetpack Compose Android.

## How you fix it
   To use Image picker we you can use following code below to pick Image from Gallery or take Photo from Camera:
## Solution
   You need to right Activity for result launchers in your composable functions instead of writing them in your Activity. For this Google gives you            rememberLauncherForActivity for composable functions.

### Must include this code in your Android Manifest file for Image Picker
```
       <provider
            android:name=".utils.ComposeFileProvider"
            android:authorities="com.vf.naylam_customer.fileprovider"
            android:grantUriPermissions="true"
            android:exported="false">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />
        </provider>
```

### Provider Paths file in your res/xml folder of your project
```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <files-path
        name="my_images"
        path="images/" />
    <cache-path
        name="my_images"
        path="images/" />
</paths>
```


### Code for your Composable Function

```
// Image state for taking photo from Camera
    var hasImage by remember {
        mutableStateOf(false)
    }

    // Image Uri state Picking Image from Gallery
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

//remember launcher for activity result for Image pick from Gallery
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            //received image uri for picked image
            hasImage = uri != null
            imageUri = uri

            //use your picked image uri where you want
            imageUri?.path?.let { Log.d("image uri", it) }


        })

//remember launcher for activity result for Take photo from Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success

            if (hasImage) {
                //use your picked image uri where you want
                imageUri?.path?.let { Log.d("image uri", it) }
            }
        })

    //usage of these launchers on your button clicks or where you want to launch them for picking image
    //To pick image from Gallery simply use this code
    imagePicker.launch("image/*")


    //Before using this launcher you must need to get run time camera permissions first that are described in the link described below:

    //Take photo from Camera
    val uri = ComposeFileProvider.getImageUri(context)
    imageUri = uri
    cameraLauncher.launch(uri)
```

### Compose Runtime Permissions
[Github]("https://github.com/talha46/Today_i_Fixed_android/blob/main/README%20-%20Android%20Runtime%20Permissions%20Jetpack%20Compose.md")
