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
import kotlinx.android.synthetic.main.activity_main.*
import com.jonathas.eloi.osrswiki.service.FloatingViewService

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private val SYSTEM_ALERT_WINDOW_PERMISSION = 2084
    val preferenceWiki = 0
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences()

        buttons()

        radioGroup()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
    }

    private fun radioGroup() {
        rgWikis.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val sharedPrefEdit = getPreferences(Context.MODE_PRIVATE) ?: null

            val radio : RadioButton = this.findViewById(checkedId)

            if (radio == rbWiki) {
                with (sharedPrefEdit!!.edit()) {
                    putInt(getString(R.string.preference_file_key), 0)
                    apply()
                }
                url = "https://oldschool.runescape.wiki/"
            }

            if (radio ==rbFandom) {
                with (sharedPrefEdit!!.edit()) {
                    putInt(getString(R.string.preference_file_key), 1)
                    apply()
                }
                url = "https://oldschoolrunescape.fandom.com/wiki/Old_School_RuneScape_Wiki"
            }
        })
    }

    private fun buttons() {
        var widgetButton = findViewById<Button>(R.id.btCreateWidget)
        widgetButton.setOnClickListener(this)

        var configurationButton = findViewById<Button>(R.id.btConfiguration)
        configurationButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 0)
            }
        }
    }

    private fun sharedPreferences() {
        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (!sharedPref.contains(getString(R.string.preference_file_key))){
            val sharedPrefEdit = getPreferences(Context.MODE_PRIVATE) ?: null
            with (sharedPrefEdit!!.edit()) {
                putInt(getString(R.string.preference_file_key), 0)
                apply()
            }
        }

        if (sharedPref.getInt(getString(R.string.preference_file_key), preferenceWiki) == 0) {
            url = "https://oldschool.runescape.wiki/"
            rbWiki.isChecked = true
            rbFandom.isChecked = false
        } else {
            url = "https://oldschoolrunescape.fandom.com/wiki/Old_School_RuneScape_Wiki"
            rbWiki.isChecked = false
            rbFandom.isChecked = true
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
        intent2.putExtra("url", url)

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
