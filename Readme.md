#FileManager
Android filemanager provides a user interface to manage files and folders.
##FEATURES
 * listing of all file from your internal and exteranal memory 
 * create folders and files
 * edit your .txt file
 * play your audio,video files
 * imageview
 
#How To Create Android FileManger
1. Create a new project in android studio <br>

2. Open AndroidManifest.xml and add permission (READ_INTERNAL_STORAGE, WRITE_INTERNAL_STORAGE,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE)

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.satish.filemanager">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_INTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_INTERNAL_STORAGE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_folder"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/MyMaterialTheme">
        <activity android:name=".activity.MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```
3. Adding Navigation Drawer.[Here](http://www.androidhive.info/2015/04/android-getting-started-with-material-design/) how to add navigation drawer to your app.
   
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/a.jpg)

4. create a custom listview with icons on your internal fragment.
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/b.jpg)


![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/c.jpg)
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/d.jpg)
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/e.jpg)
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/f.jpg)
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/g.jpg)
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/h.jpg)
![alt tag](https://raw.githubusercontent.com/satishtamada/FileManager/screenshots/app/src/main/ScreenShots/i.jpg)
