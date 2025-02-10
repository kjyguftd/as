package io.github.junkfood.heal

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import io.github.junkfood.heal.ui.HomeEntry
import io.github.junkfood.heal.util.PreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //calls the onCreat method of the superclass(Activity) to perform the drfault initialization
        super.onCreate(savedInstanceState)
        //coroutine builder that blocks the current thread until its code block completes
        runBlocking {
            //set the application's locale based on the language configuration retrieved from PreferenceUtil.getLanguageCongiuration()
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(PreferenceUtil.getLanguageConfiguration()))
        }
        //control whether the window's decor view should fit system windows
        //TODO: setting it to false allows the app to fraw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //set a listener to handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        runBlocking {
           //if Android version is less than 33, set language again
            if (Build.VERSION.SDK_INT < 33)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(
                        PreferenceUtil.getLanguageConfiguration()
                    )
                )
        }


        setContent {
            HomeEntry()
        }
    }
//Android activity lifecycle
    //called when activity id becoming visible to users
    public override fun onStart() {
        super.onStart()
    }
    //called when activity is starting to interact with users
    public override fun onResume() {
        super.onResume()
    }
    //called when activity is no longer visible to users
    public override fun onStop() {
        super.onStop()
    }
    //called before activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
    }

    //define methods and properties belong to the class itself
    companion object {

        //set application' locale based on local string
        fun setLanguage(locale: String) {
            if (locale.isEmpty()) return
            //applicationScope is a CoroutineScope associated with the application
            //launch is a coroutine builder that start a new coroutine w/o blocking the current thread
            //Dispatchers.Main specifies the coroutine should run on the main thread, the main thread is responsible for update UI
            App.applicationScope.launch(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale))
            }
        }
    }
}

