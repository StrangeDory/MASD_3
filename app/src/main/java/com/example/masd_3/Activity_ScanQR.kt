package com.example.masd_3

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import pub.devrel.easypermissions.EasyPermissions


class Activity_ScanQR : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)

        if (hasCameraAccess()) {
            startScanning()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera so you can take pictures.",
                123,
                android.Manifest.permission.CAMERA
            )
        }

        val text_view_result = findViewById<TextView>(R.id.text_view_result)
        text_view_result.setOnLongClickListener {
            val popupMenu = PopupMenu(this, text_view_result)
            popupMenu.menuInflater.inflate(R.menu.item_menu_result_qr, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val id = menuItem.itemId
                if (id == R.id.menu_copy) {
                    val summary = text_view_result.text
                    val clipboardManager = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("", summary))
                }
                if (id == R.id.menu_browser) {
                    try {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(text_view_result.text as String?))
                        startActivity(browserIntent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Could not open in browser!", Toast.LENGTH_SHORT).show()
                    }
                }
                false
            }
            popupMenu.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenu)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
            false
        }
    }

    private fun startScanning() {
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                findViewById<TextView>(R.id.text_view_result).text = it.text
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
            findViewById<TextView>(R.id.text_view_result).text = ""
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner.startPreview()
            findViewById<TextView>(R.id.text_view_result).text = ""
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized){
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    private fun hasCameraAccess(): Boolean {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)
    }
}