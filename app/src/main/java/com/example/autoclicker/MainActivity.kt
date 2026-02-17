package com.example.autoclicker

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEnable = findViewById<Button>(R.id.btn_enable_service)
        val tvStatus = findViewById<TextView>(R.id.tv_status)

        btnEnable.setOnClickListener {
            // Abre los ajustes de accesibilidad para que el usuario active el servicio
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        if (isAccessibilityServiceEnabled()) {
            tvStatus.text = "Estado: ACTIVO"
            tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        } else {
            tvStatus.text = "Estado: INACTIVO (Debes activarlo en Ajustes)"
            tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedServiceName = "$packageName/${AutoClickerService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(expectedServiceName)
    }
}
