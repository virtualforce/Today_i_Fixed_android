# No Supported surface combination is found for camera device in JetPack Compose.

# Problem
No Supported surface combination is found for camera device - id : 1 May be attempting to bind too many cases.


# Environment
N/A


# How you fix it
 you need to release the previous views before binding the latest life cycle.

# Solution
To fix this issue:
No supported surface combination is found for camera device - Id : 0. May be attempting to bind too many use cases.
you need to release the previous views before binding the latest life cycle.
unbindAll() before bindToLifecycleCamera

Add this line before bind to LifecycleCamera

```
     cameraProviderFuture.get().unbindAll()
```
