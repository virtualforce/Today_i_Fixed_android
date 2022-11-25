# Get a Bitmap of any View.

# Problem
How to use qr scanner camera preview in JetPack Compose 


# Environment
N/A


# How you fix it
Here is the complete guide to implement qr scanner in Jetpack compose with theme and premisson checker  as a composable function.

# Solution

### Step 1 
First of All you need to add these cameraX,zxing in gradle.app 

```
    // camera x
    implementation "androidx.camera:camera-camera2:1.3.0-alpha01"
    implementation "androidx.camera:camera-lifecycle:1.3.0-alpha01"
    implementation "androidx.camera:camera-view:1.3.0-alpha01"

    // zxing
    implementation 'com.google.zxing:core:3.3.3'
```

### Step 2
Add these permission in AndroidManifest file

```
    <uses-feature android:name="android.hardware.camera" />
    <uses-permission android:name="android.permission.CAMERA" />
```


### Step 3
Add this QRCodeAnalyzer class into Utils of your app

```
    class QRCodeAnalyzer(
    private val onQrCodeScanned: (result: String?) -> Unit
) : ImageAnalysis.Analyzer {

    companion object {
        private val SUPPORTED_IMAGE_FORMATS = listOf(ImageFormat.YUV_420_888, ImageFormat.YUV_422_888, ImageFormat.YUV_444_888)
    }

    override fun analyze(image: ImageProxy) {
        if (image.format in SUPPORTED_IMAGE_FORMATS) {
            val bytes = image.planes.first().buffer.toByteArray()
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)
                        )
                    )
                }.decode(binaryBitmap)
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image.close()
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }
}
```


### Step 4
Add this code to your composable where to want to use the scanner 

``` 
val context = LocalContext.current
var cameraPreviewState by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            cameraPreviewState = true
        }
    )
```

### Step 5
Add this composable for camera preview

``` 
@Composable
fun CameraPreview(
    hasCameraPermission: Boolean,
    context: Context,
    onCallBack: (String) -> Unit
) {

    var code by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColorBlack), verticalArrangement = Arrangement.Center
    ) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { context ->

                    val previewView = PreviewView(context)
                    val preview = androidx.camera.core.Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QRCodeAnalyzer { result ->
                            result?.let { code = it }
                            onCallBack(code)
                        }
                    )

                    try {
                        cameraProviderFuture.get().unbindAll()
                        cameraProviderFuture.get().bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return@AndroidView previewView
                },
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SCAN QR CODE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColorWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
```


### Step 6
Call the Camera preview where you want to perform the CTA

``` 
CameraPreview(
                hasCameraPermission = hasCameraPermission,
                context = context,
                onCallBack = {data->
                        // this will return the information of that qr code 
                        
                        // also hide the preview when you have done scanning 
                        cameraPreviewState = false
                     })
```