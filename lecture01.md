# Mobile App Development

## Toast in Android

- A toast provides a simple feedback about an operation in a small popup.
- Only fills the amount of space required for the message -- the current activity remains visible & interactive.

## Application Architecture

- Android application consists of one or more of the following classifications:
  - **Activities**: an application that has visible UI is implemented with an activity.
    - When a user selects an application from the home screen or app. launcher an activity is started
  - **Services**: a service should be used for any application that needs to persist for a long time, such as a network monitor or update-checking application.

### Content Providers

Play the role of the database servers.

- Used to manage access to persisted data, such as SQLite databases
- No need for content provider in simple applications
- Necessary in large applications that make data available to multiple activities or applications

### Broadcast Receivers

An android application may be launched to process an element of data or response to an event, such as the receipt of a text message

### MVC, MVVM, MVI

Android applications can be structured using the **Model-View-Controller** (MVC) architectural pattern. While more modern patterns like **Model-View-ViewModel** (MVVM) and **Model-View-Intent** (MVI) are now more commonly recommended and utilized in contemporary Android development.

#### MVC

          → MODEL -- updates → VIEW -- sees →  USER
        ↑                                           ↓
             ← manipulates -- CONTROLLER ←  uses --  

#### MVVM

            View 1                                  View n
GUI Object `1` - GUI Object `n`         GUI Object `1` - GUI Object `n`
     ↑                  ↑                     ↑                  ↑
          data binding                           data binding
     ↓                  ↓                     ↓                  ↓
         ViewModel 1                             ViewModel n
 Property     -  Property                 Property     -  Property
 Object 1        Object n                 Object 1        Object n
      ↑                  ↑                     ↑                  ↑
      ↓                  ↓       Model         ↓                  ↓
Data Object 1 - Data Object n             Data Object 1 - Data Object n

#### MVI

          → MODEL -- load(state) → VIEW -- display(State) →
        ↑                                                   ↓
            ←  manipulates -- INTENT  ← action --   USER  ←

### Activities

#### Activity

Is an application component that provides a screen with which users can interact in order to do something, such as dial the phone, take a photo, send an email, or view a map.
    Each activity is given a window in which to draw its user interface.

**To create an activity, you must *create a subclass of Activity***:

```java
public class Activity101Activity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
```

##### Performing Application Tasks with Activities

The android **Activity** class (`android.app.Activity`)

                    Startup/Splash
                       Activity
                           ↓
                        Main Menu 
                        Activity
                    ↙      ↓      ↘
         High Scores    Game Play    Help/About
          Activity       Activity     Activity

## The Lifecycle of an Android Activity

Android applications can be **multi-process**, and the Android OS allows **multiple applications** to run concurrently

When an activity transitions into and out of the different states, it is notified through various *callback* or *life cycle methods*

All of the callback methods are hooks that you can override to do appropriate work when the state of your activity changes:

```java
public class MyActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState);
    protected void onStart();
    protected void onRestart();
    protected void onResume();
    protected void onPause();
    protected void onStop();
    protected void onDestory();
}
```

### Lifecycle methods

- **Initializing** Static Activity Data in `onCreate()`
  - any setup, such as **layout** and **data binding**, in the `onCreate()` method. This includes calls to `setContentView()` method
- **Initializing and Retrieving** Activity Data in `onResume()`
  - Activity becomes the **foreground process**, the `onResume()` method is called
  - **start audio, video, and animations** here
- **Stopping, Saving, and Releasing** Activity Data in `onPause()`
  - Activity becomes the **background process**
  - **stop any audio, video** and animations it started in the `onResume()` method
  - **clean up** and release any resources

### Saving Activity State

- Under low-memory conditions, the Android OS can kill the process for any Activity that has been **paused** or **stopped**
- Activity state is saved into a **bundle** object, assuming the Activity implements and uses `onSaveInstanceState()` for custom data
  - some view data is automatically saved
- **Destroy** Static Activity Data in `onDestroy()`
  - When an Activity is being destroyed, the `onDestroy()` method is called
- When an Activity is **paused** or **stopped** the state of the activity is retained
- If your activity is destroyed and recreated, the state of the layout is restored to its previous state with no code required by you (although, the activity might have more state information that you'd like to restore)

## Application Anatomy

- An android application, along with a file called *AndroidManifest.xml* is deployed to the device
  - **AndroidManifest.xml** contains the necessary configuration information to properly install it to the device.
  - It includes:
    - The required class names
    - types of events the application is able to process
    - required permissions the application needs to run
  - **declarative security** helps reduce the likelihood that rogue application can cause damage on your device

## Defining Your Application Using the Android Manifest File

- The Android application manifest is specifically formatted XML file that must accompany each Android application.
- This file contains important information about the application's identity:
  - Application's name
  - Version information
  - Application configuration information