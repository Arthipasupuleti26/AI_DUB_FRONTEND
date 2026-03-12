package com.simats.aidub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import org.json.JSONObject

class UploadProgressActivity : AppCompatActivity() {

    private var videoUri: Uri? = null
    private var isCancelled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_progress)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        videoUri = intent.getStringExtra("VIDEO_URI")?.let { Uri.parse(it) }

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvPercentage = findViewById<TextView>(R.id.tv_percentage)
        val tvFileName = findViewById<TextView>(R.id.tv_file_name)
        val tvFileSize = findViewById<TextView>(R.id.tv_file_size)

        findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            isCancelled = true
            finish()
        }

        videoUri?.let { uri ->
            val (name, size) = getFileDetails(uri)
            tvFileName.text = name
            tvFileSize.text = "${size / (1024 * 1024)} MB"
            startRealUpload(progressBar, tvPercentage)
        } ?: navigateToError()
    }

    private fun getFileDetails(uri: Uri): Pair<String, Long> {
        var name = "Unknown"
        var size = 0L

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                size = cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
            }
        }
        return name to size
    }

    private fun startRealUpload(
        progressBar: ProgressBar,
        tvPercentage: TextView
    ) {
        lifecycleScope.launch(Dispatchers.IO) {

            runOnUiThread {
                progressBar.progress = 10
                tvPercentage.text = "Uploading..."
            }

            val requestBody = object : okhttp3.RequestBody() {
                override fun contentType() = "video/mp4".toMediaType()

                override fun writeTo(sink: BufferedSink) {
                    contentResolver.openInputStream(videoUri!!)?.use { input ->
                        val buffer = ByteArray(8 * 1024)
                        var read: Int
                        while (true) {
                            read = input.read(buffer)
                            if (read == -1) break
                            sink.write(buffer, 0, read)
                        }
                    }
                }
            }

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "My First Project")
                .addFormDataPart("user_id", "1")
                .addFormDataPart("video", "upload.mp4", requestBody)
                .build()


            val request = Request.Builder()
                .url("https://1j7cp4fh-80.inc1.devtunnels.ms/ai_dub/api/video/upload.php")
                .post(body)
                .build()

            val client = OkHttpClient.Builder()
                .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            // ✅ ONLY ADDITION (REQUIRED)
            var response: okhttp3.Response? = null

            try {
                response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    navigateToError()
                    return@launch
                }

                runOnUiThread {
                    progressBar.progress = 100
                    tvPercentage.text = "Processing completed"
                }

            } catch (e: java.net.SocketTimeoutException) {
                runOnUiThread {
                    tvPercentage.text = "Server processing… please wait"
                }
            } catch (e: Exception) {
                navigateToError()
            }

            // ✅ NOW THIS COMPILES
            val bodyText = response?.body?.string()
            android.util.Log.e("UPLOAD", "HTTP ${response?.code} BODY: $bodyText")

            val json = JSONObject(bodyText ?: "{}")
            val projectId = json.optString("project_id", null)
            val serverVideoPath = json.optString("video_path", null)

            if (projectId == null || serverVideoPath == null) {
                navigateToError()
                return@launch
            }


            if (response != null && !response!!.isSuccessful) {
                navigateToError()
                return@launch
            }

            runOnUiThread {
                progressBar.progress = 100
                tvPercentage.text = "Processing completed"
            }

            runOnUiThread {
                AppNotifier.notifySuccess(
                    this@UploadProgressActivity,
                    "Video uploaded successfully 🎬"
                )
            }
            val intent = Intent(this@UploadProgressActivity, UploadCompleteActivity::class.java)
            intent.putExtra("VIDEO_URI", videoUri.toString())
            intent.putExtra("PROJECT_ID", projectId)
            intent.putExtra("VIDEO_SERVER_PATH", serverVideoPath)

            startActivity(intent)

            finish()
        }
    }

    private fun navigateToError() {
        startActivity(Intent(this, UploadFailedActivity::class.java))
        finish()
    }
}
