package com.example.notificationwebhookapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("NotificationWebhookPrefs", Context.MODE_PRIVATE)

        val webhookUrlEditText = findViewById<EditText>(R.id.webhookUrlEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Load saved webhook URL
        webhookUrlEditText.setText(sharedPreferences.getString("webhookUrl", ""))

        saveButton.setOnClickListener {
            val webhookUrl = webhookUrlEditText.text.toString()
            // Save webhook URL to SharedPreferences
            sharedPreferences.edit().putString("webhookUrl", webhookUrl).apply()
        }
    }
}
