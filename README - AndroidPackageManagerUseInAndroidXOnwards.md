
## Problem
  How to use Android Package Manager for accessing other apps installed in the Android Device

## How you fix it
To use package manager in our Android project for latest AndroidX and onwards we first need write queries in our Android Manifest like below:
## Solution

```
	<queries>
           <package android:name="com.ktm.kualitee" />
    	</queries>
```
		
If we need to perform some actions with Intent using package manager then we also need to define that action in our queries section to like this.
Complete code is shared below
		
    Android Manifest code
```		    
   <queries>
        <!--        packages that you need to access using Android Package Manager-->
        <package android:name="com.ktm.kualitee" />
        <package android:name="com.microsoft.office.officehubrow" />
        <package android:name="com.microsoft.office.word" />
        <package android:name="com.google.android.apps.docs.editors.docs" />
        <package android:name="com.facebook.orca" />

        <!--        actions that you need to perform using package manager via Intent-->
        <intent>
            <action android:name="com.google.android.youtube.api.service.START" />
        </intent>
    </queries>

```	

Example Activity/Fragment complete code to get the installed app package in your Android device. If the package is not present
then we navigate the user to Google Play Store to install the app first before navigating to that app from his current running app.

```
        final String packageName = "com.ktm.kualitee";
        Intent shareIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (shareIntent != null) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType(type);
            shareIntent.setPackage(packageName);
            context.startActivity(shareIntent);
        } else {
            try {
                shareIntent = new Intent(Intent.ACTION_VIEW);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setData(Uri.parse("market://details?id=" + packageName));
                context.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }
```
	
For Example if you want get all defualt email apps packges from package manager then you need to write following code sinepet
	
  Android Manifest code
  ```
    <queries>
        <intent>
            <action android:name="android.intent.action.SEND"/>
            <data android:mimeType="*/*" />
        </intent>
    </queries>
  ```
  
  Activity/Fragment code
```	
	@NonNull
    	Intent getEmailWithAttachmentIntent(
        @NonNull final String[] emailAddresses,
        @NonNull final String emailSubjectLine,
        @NonNull final String emailBody,
        @NonNull final Uri attachmentUri) {

        final Intent result = getEmailIntent(emailAddresses, emailSubjectLine, emailBody);

        result.putExtra(Intent.EXTRA_STREAM, attachmentUri);

        return result;
    }

    @NonNull
    private List<ResolveInfo> getEmailWithAttachmentAppList() {
        final Intent queryIntent = getEmailWithAttachmentIntent(
                DUMMY_EMAIL_ADDRESSES, DUMMY_EMAIL_SUBJECT_LINE, DUMMY_EMAIL_BODY, DUMMY_EMAIL_URI);

        return packageManager.queryIntentActivities(
                queryIntent, PackageManager.MATCH_DEFAULT_ONLY);
    }
```
