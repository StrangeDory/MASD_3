package com.example.masd_3

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.util.*


class Activity_GenerateQR : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr)

        val button_generate = findViewById<Button>(R.id.generate)
        button_generate.setOnClickListener(){
            val entered_text = findViewById<EditText>(R.id.text_field).text.toString()
            val encoder = QRGEncoder(entered_text, null, QRGContents.Type.TEXT, 800)
            findViewById<ImageView>(R.id.qr_code).setImageBitmap(encoder.bitmap)
        }

        val image_qr = findViewById<ImageView>(R.id.qr_code)

        findViewById<EditText>(R.id.text_field).addTextChangedListener {
            image_qr.setImageBitmap(null)
        }

        image_qr.setOnLongClickListener {
            val popupMenu = PopupMenu(this, image_qr)
            popupMenu.menuInflater.inflate(R.menu.item_menu_save, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val id = menuItem.itemId
                if (id == R.id.menu_save) {
                    if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        EasyPermissions.requestPermissions(
                            this,
                            "This app needs access to your gallery so you can save pictures.",
                            100,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    } else {
                        saveImageToStorage()
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

    private fun saveImageToStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, String.format("%d.jpg", System.currentTimeMillis()) + ".jpg")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "QR_Codes")
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            try {
                val outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri)!!)
                val bitmap = findViewById<ImageView>(R.id.qr_code).drawable.toBitmap()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToStorage()
            } else {
                Toast.makeText(this, "Gallery permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
