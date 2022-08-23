# Today_i_Fixed  onCreateOptionsMenu deprecated.

# Problem :

onCreateOptionsMenu has been deprecated on 31 Api and above  <br />


# How you fix it :
onCreateOptionsMenu has been deprecated on 31 Api and above,I had done some research on it. <br />
I have created two extensions and I hope they will help you implement your menu in the new way, easily and clearly,that will help you not repeat a huge code <br />
# Solution :

## Step 1
Add dependencies in build.gradle(app level)
```
//implement the new activity dependency in you gradle
implementation 'androidx.activity:activity-ktx:1.4.0-alpha01'

// if you are using compose 
implementation 'androidx.activity:activity-compose:1.4.0-alpha01'
```

## step 2 
create extension functions and implementation into your activity or fragment<br />


###  Activity Extension 

```
   fun AppCompatActivity.bindMenu(
    @MenuRes menuRes: Int,
    onMenuItemSelected: (MenuItem) -> Unit
): MenuProvider{
    val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(menuRes, menu)
        }
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            onMenuItemSelected(menuItem)
            return true
        }
    }
    addMenuProvider(menuProvider)
    return menuProvider
}
```

#### Usage in Activity


```
 class MainActivity : AppCompatActivity(R.layout.activity_main) ){
    	setSupportActionBar(tb)
      val provider = bindMenu(R.menu.home_menu){
           /*
           * do what you want on your menu item click listener
           * */
       }
       // if you want to remove it in a specific action
       removeMenuProvider(provider)
}
```

###  Fragment Extension

```
  fun FragmentActivity.bindMenu(
    menuHost: MenuHost,
    @MenuRes menuRes: Int,
    lifecycleOwner: LifecycleOwner,
    onMenuItemSelected: (MenuItem) -> Unit
): MenuProvider{
    val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(menuRes, menu)
        }
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            onMenuItemSelected(menuItem)
            return true
        }
    }
    menuHost.addMenuProvider(menuProvider,lifecycleOwner, Lifecycle.State.RESUMED)
    return menuProvider
}

```

#### Usage in Fragment


```
 class HomeFragment : Fragment() {
  
  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val menuHost: MenuHost = requireActivity()
        val provider = requireActivity().bindMenu(
            menuHost = menuHost,
            menuRes = R.menu.home_menu,
            lifecycleOwner = viewLifecycleOwner) { menuItem ->
            /*
             * do what you want on your menu item click listener
             * */
        };
        // if you want to remove it in a specific action
        menuHost.removeMenuProvider(provider)

  }
```