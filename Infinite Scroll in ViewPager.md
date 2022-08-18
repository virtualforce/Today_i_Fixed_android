# Today_i_Fixed  Bi-Direction Infinite Scrolling.

# Problem :

### ViewPager2 does not providing infinite scrolling
we normally use RecyclerView Adapters for organizing the list data on the Viewpager2 for the case of sliding views.  <br />
This can only make you slide from the very first view till the last view and that’s it.  <br />
You can’t scroll backward to the last view from the first view and forward from the last view to the first view. This problem can make it cumbersome for the user to scroll through views on the ViewPager2.


# How you fix it :
First, add a fake first view after the actual last view and also add a fake last view before the actual first view. <br />
When the user is on fake last view, replace it with the actual last view and when the user is on the fake first view, replace it with the actual first view.  <br />

# Solution :

## Adapter Class
Make a new list of Sample class items out of the original list passed into the adapter class. It appends the last item of the original list at the very beginning and then adds the original list, followed by the first item of the original list.
```private val newList: List<Sample> =
        listOf(originalList.last()) + originalList + listOf(originalList.first())
        
        override fun onBindViewHolder(holder: InfiniteRecyclerViewHolder, position: Int) {
        holder.bind(newList[position])
    }
     override fun getItemCount(): Int {
        return newList.size
    }
```

## Activity
 Here, the first important thing is to make the infiniteViewPager’s current item to position 1, as position 0 is taken by the fake last item.  <br />

```
   infiniteViewPager.currentItem = 1
   onInfinitePageChangeCallback(list.size + 2)
```

 The second and the most important thing is adding an infiniteViewPager’s registerOnPageChangeCallback for performing our second workaround required for infinite scrolling. <br />
 The second and the most important thing is adding an infiniteViewPager’s registerOnPageChangeCallback for performing our second workaround required for infinite scrolling. <br />

 onPageScrollStateChanged() get notified when ever scroll state changes from
 ```ViewPager2.SCROLL_STATE_DRAGGING ``` to ```ViewPager2.SCROLL_STATE_SETTLING``` to ```ViewPager2.SCROLL_STATE_IDLE```. <br />
 When the state after dragging, settles, and became idle, if the current item of the ViewPager is listsize-1 (fake last item), set the new current item to 1 (actual first item). And similarly, if the current item of the ViewPager is 0 (fake first item), set the new current item to listsize-2 (actual last item). <br />
 if you have used a pageIndicatorView for indicating the pages on which the ViewPager is currently on, update it in onPageSelected()  method as shown below.

```
    private fun onInfinitePageChangeCallback(listSize: Int) {
        infiniteViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (infiniteViewPager.currentItem) {
                        listSize - 1 -> infiniteViewPager.setCurrentItem(1, false)
                        0 -> infiniteViewPager.setCurrentItem(listSize - 2, false)
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position != 0 && position != listSize - 1) {
                    // pageIndicatorView.setSelected(position-1)
                }
            }
        })
    }
```