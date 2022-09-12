# Get a Bitmap of any View.

# Problem
How to get a bitmap of any view


# Environment
N/A


# How you fix it
You can use that to create an entire pdf page from a layout that turned into documentation and tutorials


# Solution
you can get a bitmap of any view by using this function

```
fun getBitmapFromView(view: View):Bitmap{
    val bitmap = Bitmap.createBitmap(
        view.width,
        view.height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    view.layout(
        view.left,
        view.top,
        view.right,
        view.bottom,
    )

    view.draw(canvas)

    return bitmap
}
```

