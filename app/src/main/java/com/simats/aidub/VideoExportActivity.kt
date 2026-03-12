package com.simats.aidub

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.aidub.network.ApiClient
import com.simats.aidub.model.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoExportActivity : AppCompatActivity() {

    private var streamUrl: String? = null
    private var downloadUrl: String? = null
    private lateinit var videoView: VideoView
    private lateinit var btnExport: Button
    private lateinit var fabDownload: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_export)

        // Android 13+ notification permission (required for DownloadManager)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        videoView = findViewById(R.id.video_view)
        btnExport = findViewById(R.id.btn_export)
        fabDownload = findViewById(R.id.fab_download)

        btnExport.setOnClickListener { generateDubbedVideo() }
        fabDownload.setOnClickListener { downloadVideo() }
    }

    // 🎬 CREATE FINAL VIDEO
    private fun generateDubbedVideo() {

        Toast.makeText(this,"Creating dubbed video...",Toast.LENGTH_LONG).show()

        ApiClient.apiService.mergeVideo()
            .enqueue(object : Callback<GenericResponse> {

                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {

                    val body = response.body()

                    if(response.isSuccessful && body?.success == true){

                        streamUrl = body.stream
                        downloadUrl = body.download

                        Toast.makeText(this@VideoExportActivity,"Dubbed video ready 🎉",Toast.LENGTH_LONG).show()

                        // ⭐ NEW — SAVE NOTIFICATION IN DATABASE
                        sendNotificationToServer("Your dubbed video is ready 🎬")

                        // ⭐ NEW — SHOW SYSTEM NOTIFICATION
                        showSystemNotification("Export Complete","Your dubbed video is ready")

                        playDubbedVideo()
                        showFloatingDownloadButton()
                    }

                    else {
                        Toast.makeText(this@VideoExportActivity,"Export failed",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@VideoExportActivity,t.message,Toast.LENGTH_LONG).show()
                }
            })
    }

    // ▶️ STREAM VIDEO
    private fun playDubbedVideo() {
        val uri = Uri.parse(streamUrl)
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { videoView.start() }
    }

    // ✨ SHOW FLOATING DOWNLOAD BUTTON WITH ANIMATION
    private fun showFloatingDownloadButton() {
        fabDownload.visibility = View.VISIBLE
        val anim = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.floating_pulse)
        fabDownload.startAnimation(anim)
    }

    // ⭐ SAVE NOTIFICATION IN DATABASE
    private fun sendNotificationToServer(message:String){

        val userId = "1"   // later replace with logged user id

        ApiClient.apiService.createNotification(userId,message)
            .enqueue(object: Callback<GenericResponse>{
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {}
                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {}
            })
    }

    // ⭐ SHOW SYSTEM NOTIFICATION
    private fun showSystemNotification(title:String, message:String){
        val helper = NotificationHelper(this)
        helper.createNotificationChannel()
        helper.showNotification(title,message)
    }

    // 📥 DOWNLOAD VIDEO + LISTEN FOR COMPLETION
    private fun downloadVideo() {

        val url = downloadUrl ?: return

        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("AI Dubbed Video")
        request.setDescription("Downloading video...")
        request.setMimeType("video/mp4")
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "AI_Dubbed_Video.mp4"
        )

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        Toast.makeText(this,"Downloading to Downloads 📥",Toast.LENGTH_LONG).show()
    }


}

