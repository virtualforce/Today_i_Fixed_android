# Today_i_Fixed_Listener issue in fragments frame.

# Problem :

### On Fragment transaction previous views listeners are working from the different fragment
the actual scenario is in Jomo App when we transit fragment and from home fragment and suddenly move to the different fragmengt then in stack latest fragment frame updated but when views are empty from that current fragment then click on any where in the screen previous fragment listener's are working


# How you fix it :
you just need to set listener on their current fragment root layout 

# Solution :
android:clickable="true"