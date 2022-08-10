# Today_i_Fixed Include Vs ViewStub.

# Problem :

### If You want to lazily Inflate Views at runtime
Sometimes while developing an application, we have encountered a situation like based on some logic, we have to render some new view in the layout that is rarely used. A general approach for this is to make View visibility GONE/VISIBLE.
So always inflating this rarely used view in a layout, is not a good idea and also it impacts the rendering of the layout.


# How you fix it :
Use ViewStub rather Then Include A ViewStub is an invisible, zero-sized View that can be used to lazily inflate layout resources at runtime. It is a dumb and lightweight view. It does not participate nor draws anything in the layout.
Use ViewStub When you want to inflate view lazily i.e on-demand or at runtime based on some logic.
Use ViewStub When you don't want to render rarely used UI in layout always.
ViewStub will be loaded only when you actually need it i.e when you set ViewStub visibility to true or call inflate() then only it will draw on UI.

# Solution :

<ViewStub
android:id="@+id/viewStub"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_marginTop="100dp"
android:inflatedId="@+id/inflatedviewsub"
android:layout="@layout/custom_viewstub" />

id: ViewStub defined can be found using the id.
inflatedId: This attribute is used to set the id of the inflated View.
layout: This attribute is used to supply an identifier for the layout resource to inflate.

//declare
private lateinit var mViewStub: ViewStub

//init

multiStopViewStub = findViewById(R.id.dropOffMultiAddressGroup)
val inflatedView = mViewStub.inflate()

//Now using inflatedView, we can find ids of items inside inflate view.

When inflate() is invoked, the ViewStub is replaced by the inflated View and the inflated View is returned to its view hierarchy.