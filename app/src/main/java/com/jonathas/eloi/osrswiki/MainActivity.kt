package com.jonathas.eloi.osrswiki

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.jonathas.eloi.osrswiki.service.FloatingViewService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private val SYSTEM_ALERT_WINDOW_PERMISSION = 2084
    val preferenceWiki = 0
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
    }

    private fun buttons() {
        var widgetButton = findViewById<Button>(btCreateWidget.id)
        widgetButton.setOnClickListener(this)

        var configurationButton = findViewById<Button>(btConfiguration.id)
        configurationButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun askPermission() {
        var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()))
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION)
    }


    override fun onClick(v: View) {

        var intent2 = Intent(this@MainActivity, FloatingViewService::class.java)
        intent2.putExtra("url", "https://oldschool.runescape.wiki/")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            startService(intent2)
            finish()
        }
        else if (Settings.canDrawOverlays(this))
        {
            startService(intent2)
            finish()
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            askPermission()
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show()
        }
    }
}
