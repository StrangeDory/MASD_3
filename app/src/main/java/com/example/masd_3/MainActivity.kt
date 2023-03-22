package com.example.masd_3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.widget.AppCompatButton
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.btnGenerateCode).setOnClickListener(){
            val intent = Intent(this, Activity_GenerateQR::class.java)
            startActivity(intent);
        }

        findViewById<AppCompatButton>(R.id.btnScan).setOnClickListener(){
            val intent = Intent(this, Activity_ScanQR::class.java)
            startActivity(intent);
        }
    }
}