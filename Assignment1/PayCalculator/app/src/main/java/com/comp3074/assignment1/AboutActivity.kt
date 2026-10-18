package com.comp3074.assignment1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Shows the back arrow in the ActionBar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Runs when the ActionBar back arrow is tapped.
     * finish() closes this screen, so Android shows the MainActivity that is already
     * underneath it on the back stack. No new MainActivity is created, and the user's
     * inputs and results are still there.
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
