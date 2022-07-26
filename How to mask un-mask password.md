
## Problem
  How to mask/un-mask password in through combination of TextInputLayout and com.google.android.material.textfield.TextInputEditText

## How you fix it
   We were doing password masking/un-masking through code. Now we can do this through XML properties of com.google.android.material.textfield.TextInputLayout

## Solution

```app:passwordToggleEnabled="true"
app:passwordToggleTint="@color/hbl_main_green"
app:endIconDrawable="@drawable/toggle_password_states"
```
		
Add above properties in `TextInputLayout` and you are good to go. (No Java or Kotlin code require)
		
		
Complete code of TextInputLayout for reference:
		
```		<com.google.android.material.textfield.TextInputLayout
        android:id="@+id/confirm_password_layout"
        style="@style/My.TextInputLayout.FilledBox.Padding"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="@dimen/margin_20"
        android:hint="@string/confirm_password"
        app:passwordToggleEnabled="true"
        app:passwordToggleTint="@color/hbl_main_green"
        app:endIconDrawable="@drawable/toggle_password_states">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/confirm_password_et"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/light_background"
            android:singleLine="true"
            android:textColor="@color/gray_text"
            android:textSize="@dimen/title"
            android:inputType="textPassword"/>

    </com.google.android.material.textfield.TextInputLayout>
```	
	
Selector file code for reference:
	
```	<?xml version="1.0" encoding="utf-8"?>
	<selector xmlns:android="http://schemas.android.com/apk/res/android">
		<item android:drawable="@drawable/ic_show_password_green" android:state_checked="true"/>
		<item android:drawable="@drawable/ic_hide_password_green"/>
	</selector>
```