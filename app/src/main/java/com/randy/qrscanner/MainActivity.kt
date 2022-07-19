package com.randy.qrscanner

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class MainActivity : AppCompatActivity() {
    private lateinit var buttonScan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        buttonScan = findViewById(R.id.btn_scan)
        buttonScan.setOnClickListener { scanQrCode() }
    }

    private fun scanQrCode() {
        val scanner = IntentIntegrator(this)
        scanner.setOrientationLocked(false)
        scanner.initiateScan()
    }

    private fun isUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }

    private fun showQrMssg(context: Context, contents: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("QR Code Message")
            .setMessage(contents)
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
            .setNegativeButton("Copy") { dialog, _ ->
                copyText(contents)
                Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
            }
            .create()
            .show()
    }

    private fun copyText(contents: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Text Copied", contents)
        clipboard.setPrimaryClip(clip)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult
        val contents: String?
        val context = this

        if (resultCode == Activity.RESULT_OK) {
            result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            contents = result?.contents

            if(contents != null) {
                if (isUrl(contents)) { openUrl(contents) }
                else { showQrMssg(context, contents) }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun openUrl(contents: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(contents)
        startActivity(intent)
    }
}