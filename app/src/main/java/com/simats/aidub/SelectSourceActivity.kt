package com.simats.aidub

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SelectSourceActivity : AppCompatActivity() {

    private lateinit var cameraVideoUri: Uri

    private val selectVideoLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { openUpload(it) }
        }

    private val recordVideoLauncher =
        registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success) {
                openUpload(cameraVideoUri)
            } else {
                Toast.makeText(this, "Recording cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { openUpload(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_source)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener { finish() }

        findViewById<ConstraintLayout>(R.id.btn_gallery).setOnClickListener {
            selectVideoLauncher.launch("video/*")
        }

        // ✅ UPDATED: permission-safe camera opening
        findViewById<ConstraintLayout>(R.id.btn_camera).setOnClickListener {
            openCameraWithPermission()
        }

        findViewById<ConstraintLayout>(R.id.btn_drive).setOnClickListener {
            pickDocumentLauncher.launch(arrayOf("video/*"))
        }
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.TITLE, "RecordedVideo")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        }

        cameraVideoUri = contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        )!!

        recordVideoLauncher.launch(cameraVideoUri)
    }

    private fun openCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                101
            )
        }
    }

    // ✅ REQUIRED: handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            ) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to record video",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openUpload(uri: Uri) {
        startActivity(
            Intent(this, UploadProgressActivity::class.java)
                .putExtra("VIDEO_URI", uri.toString())
        )
    }
}
