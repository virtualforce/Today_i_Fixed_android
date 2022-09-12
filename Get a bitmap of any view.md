# Get a bitmap of any view.

# Problem :

How to get a bitmap of any view <br />

#Environment :

N/A <br />

# How you fix it :
You Can used that to create an entire pdf page from a layout that turned into documentation and tutorials <br />

# Solution :

you can get a bitmap of any view by using this function <br />

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

