# Control System UI

## status & navigation bar

### dim：

```
View decorView = getActivity().getWindow().getDecorView();
int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
decorView.setSystemUiVisibility(uiOptions);
```
### reveal:

```
View decorView = getActivity().getWindow().getDecorView();
// Calling setSystemUiVisibility() with a value of 0 clears
// all flags.
decorView.setSystemUiVisibility(0);
```

### Hide the Status Bar on Android 4.1 and Higher

```
View decorView = getWindow().getDecorView();
// Hide the status bar.
int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
ActionBar actionBar = getActionBar();
actionBar.hide();
```
[Make Content Appear Behind the Status Bar](https://developer.android.com/training/system-ui/status.html)

### Hide the Navigation Bar

You can hide the navigation bar using the `SYSTEM_UI_FLAG_HIDE_NAVIGATION` flag. 

```
View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN;
decorView.setSystemUiVisibility(uiOptions);
```

## Using Immersive Full-Screen Mode
> since Android 4.4 API19

The flags `SYSTEM_UI_FLAG_IMMERSIVE` and `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` both provide an immersive experience, but with the differences in behavior.

* a reader page.suggest use: use the `IMMERSIVE` flag in conjunction with `SYSTEM_UI_FLAG_FULLSCREEN` and `SYSTEM_UI_FLAG_HIDE_NAVIGATION`.
* a truly immersive app.suggest use:use the `IMMERSIVE_STICKY` flag in conjunction with `SYSTEM_UI_FLAG_FULLSCREEN` and `SYSTEM_UI_FLAG_HIDE_NAVIGATION`.
* video player.suggest use:simply using `SYSTEM_UI_FLAG_FULLSCREEN` and `SYSTEM_UI_FLAG_HIDE_NAVIGATION` should be sufficient. Don't use the "immersive" flags in this case.


### Non-Sticky Immersion

```
// This snippet hides the system bars.
private void hideSystemUI() {
    // Set the IMMERSIVE flag.
    // Set the content to appear under the system bars so that the content
    // doesn't resize when the system bars hide and show.
    mDecorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE);
}

// This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
private void showSystemUI() {
    mDecorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
}
```

Sticky Immersion

```
@Override
public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
```
> Note: If you like the auto-hiding behavior of IMMERSIVE_STICKY but need to show your own UI controls as well, just use IMMERSIVE combined with Handler.postDelayed() or something similar to re-enter immersive mode after a few seconds.

### Responding to UI Visibility Changes

To get notified of system UI visibility changes, register an View.OnSystemUiVisibilityChangeListener to your view. This is typically the view you are using to control the navigation visibility.

For example, you could add this code to your activity's onCreate() method:

```
View decorView = getWindow().getDecorView();
decorView.setOnSystemUiVisibilityChangeListener
        (new View.OnSystemUiVisibilityChangeListener() {
    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        // Note that system bars will only be "visible" if none of the
        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            // TODO: The system bars are visible. Make any desired
            // adjustments to your UI, such as showing the action bar or
            // other navigational controls.
        } else {
            // TODO: The system bars are NOT visible. Make any desired
            // adjustments to your UI, such as hiding the action bar or
            // other navigational controls.
        }
    }
});
```



 
* `View.SYSTEM_UI_FLAG_VISIBLE`：显示状态栏，Activity不全屏显示(恢复到有状态的正常情况)。
* `View.INVISIBLE`：隐藏状态栏，同时Activity会伸展全屏显示。
* `View.SYSTEM_UI_FLAG_FULLSCREEN`：Activity全屏显示，且状态栏被隐藏覆盖掉。
* `View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN`：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
* `View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION`：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
* `View.SYSTEM_UI_LAYOUT_FLAGS`：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
* `View.SYSTEM_UI_FLAG_HIDE_NAVIGATION`：隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
* `View.SYSTEM_UI_FLAG_LOW_PROFILE`：状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。