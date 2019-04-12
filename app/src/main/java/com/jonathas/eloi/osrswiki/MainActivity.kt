package com.jonathas.eloi.osrswiki

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.jonathas.eloi.osrswiki.service.FloatingViewService

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private val SYSTEM_ALERT_WINDOW_PERMISSION = 2084

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }

        var widgetButton = findViewById<Button>(R.id.buttonCreateWidget)
        widgetButton.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun askPermission() {
        var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()))
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION)
    }


    override fun onClick(v: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            startService(Intent(this@MainActivity, FloatingViewService::class.java))
            finish()
        }
        else if (Settings.canDrawOverlays(this))
        {
            startService(Intent(this@MainActivity, FloatingViewService::class.java))
            finish()
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            askPermission()
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show()
        }
    }
}
