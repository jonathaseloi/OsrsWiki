package com.jonathas.eloi.osrswiki

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.jonathas.eloi.osrswiki.service.FloatingViewService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private val systemAlertpermission = 2084
    private var url = "https://oldschool.runescape.wiki/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons()
        language()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
    }

    private fun buttons() {
        val widgetButton = findViewById<Button>(btCreateWidget.id)
        widgetButton.setOnClickListener(this)

        val configurationButton = findViewById<Button>(btConfiguration.id)
        configurationButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun askPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName"))
        startActivityForResult(intent, systemAlertpermission)
    }


    override fun onClick(v: View) {
        val intent2 = Intent(this@MainActivity, FloatingViewService::class.java)
        intent2.putExtra("url", url)

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || Settings.canDrawOverlays(this))
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

    private fun language() {
        val radioGroupLanguage: RadioGroup = findViewById(R.id.radioGroupLanguage)
        val radioButtonEng: RadioButton = findViewById(R.id.radioButtonEng)
        val radioButtonPtBr: RadioButton = findViewById(R.id.radioButtonPtBr)

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        when(sharedPref.getString("language", null)) {
            "pt-br" -> {
                radioButtonPtBr.isChecked = true
                url = "https://oldschool-runescape-wiki.translate.goog/?_x_tr_sl=en&_x_tr_tl=pt&_x_tr_hl=pt-BR&_x_tr_pto=sc"
            }
            else -> {
                radioButtonEng.isChecked = true
                url = "https://oldschool.runescape.wiki/"
            }
        }
        radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonEng -> {
                    url = "https://oldschool.runescape.wiki/"
                    saveLanguage(sharedPref, "eng")
                }

                R.id.radioButtonPtBr -> {
                    url = "https://oldschool-runescape-wiki.translate.goog/?_x_tr_sl=en&_x_tr_tl=pt&_x_tr_hl=pt-BR&_x_tr_pto=sc"
                    saveLanguage(sharedPref, "pt-br")
                }
            }
        }
    }

    private fun saveLanguage(sharedPref: SharedPreferences, language:String) {
        val editor = sharedPref.edit()

        editor.putString("language", language)
        editor.apply()
    }
}
