package com.example.collegeroamer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var navigateButton: ImageButton
    private lateinit var currentLocationSpinner: Spinner
    private lateinit var destinationSpinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateButton = findViewById(R.id.navigateButton)
        currentLocationSpinner = findViewById(R.id.currentLocationSpinner)
        destinationSpinner = findViewById(R.id.destinationSpinner)

        navigateButton.setOnClickListener {
            val startingLocation = currentLocationSpinner.selectedItem.toString()
            val destination = destinationSpinner.selectedItem.toString()

            val intent = Intent(this, NavigationDisplayActivity::class.java)
            intent.putExtra("starting_location", startingLocation)
            intent.putExtra("destination", destination)
            startActivity(intent)
        }
    }
}