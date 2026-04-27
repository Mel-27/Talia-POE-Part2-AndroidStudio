package com.gia.poe_demo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Displays a receipt photo attached to an expense entry.
 * Receives the file path via Intent extra "photoPath".
 *
 * References:
 * Used for getting data from the Intent using getIntent() / intent extras:
 * WWaldo (2011). Access intent after starting new activity android. Stack Overflow. Available at:
 * https://stackoverflow.com/questions/6163921/access-intent-after-starting-new-activity-android.
 * [Accessed 27 Apr. 2026]
 *
 * Used for loading an image from a file path using BitmapFactory.decodeFile():
 * nelzkie (2012). Bitmapfactory example. Stack Overflow. Available at:
 * https://stackoverflow.com/questions/11182714/bitmapfactory-example.
 * [Accessed 27 Apr. 2026]
 *
 */

class ReceiptViewActivity : AppCompatActivity() {

    private val TAG = "ReceiptViewActivity"

    // Explains retrieving data passed through Intent extras (StackOverflow, 2011)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_view)

        val photoPath = intent.getStringExtra("photoPath")
        Log.d(TAG, "Received photoPath: $photoPath")

        // Shows decoding an image file into a Bitmap (StackOverflow, 2012)
        if (!photoPath.isNullOrEmpty()) {
            try {
                val bitmap = BitmapFactory.decodeFile(photoPath)
                findViewById<ImageView>(R.id.ivReceipt).setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load receipt image: ${e.message}", e)
            }
        }

        // Back button
        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }
    }
}