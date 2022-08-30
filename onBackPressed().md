# onBackPressed deprecated.

# Problem
onBackPressed() is now deprecated on API level 33+ <br />


# How you fix it
Alternatively, you can use: OnBackPressedDispatcher. <br />
So I have made a simple extension that will let you still use onBackPressed keyword and you will not miss it anymore. <br />
I hope they will help you implement your menu in the new way, easily and clearly,that will help you not repeat a huge code <br />

# Solution
Before we start, let’s talk about why there is such a change. You may know that starting from Android 10 the system provide gesture navigation feature. <br />
In a bit more detail, the system supports gestures like swiping left to right to navigate back ( just like IOS devices ).<br />
But this has led unexpected behaviour when it has been combined with horizontal swipes in applications.<br />
The users reported that they sometimes trigger system back while swiping horizontally in an application.<br />
The problem is that the android system cannot differentiate if the gesture is for system back or for application back navigation.<br />
In other words, It cannot tell if the application handles the gesture.<br />

## Step 1
Add dependencies in build.gradle(app level)
```
Make sure you have at least 'androidx.activity:activity-ktx:1.6.0-rc01' at your dependencies in Case of Activity Extention 
Otherwise 'androidx.activity:activity-ktx:1.0.0' is must for Fragment Only 

implementation 'androidx.activity:activity-ktx:1.6.0-rc01'
```

## Step 2 
create extension functions and implementation into your activity or fragment<br />


###  Activity Extension 

```
  fun AppCompatActivity.onBackPressed(isEnabled: Boolean, callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this,
        object : OnBackPressedCallback(isEnabled) {
            override fun handleOnBackPressed() {
                callback()
            }
        })
}

```

#### Usage in Activity


```
 class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
    	onBackPressed(true) {
		  // do what do you want when get back 
        }
    }

}
```

###  Fragment Extension

```
 fun FragmentActivity.onBackPressed(callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this,
     //default value is True 
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback()
            }
        }
 	)
}

```

#### Usage in Fragment


```
 class HomeFragment : Fragment() {
  
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		    requireActivity().onBackPressed {        
		  // do what do you want when get back 
		}
  }
}

```
