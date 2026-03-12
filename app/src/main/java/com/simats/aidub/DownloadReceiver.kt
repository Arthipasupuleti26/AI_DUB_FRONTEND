package com.simats.aidub

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simats.aidub.model.GenericResponse
import com.simats.aidub.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {

            // ⭐ SAVE NOTIFICATION IN DATABASE
            ApiClient.apiService.createNotification(
                "1",
                "Video downloaded successfully 📥"
            ).enqueue(object : Callback<GenericResponse> {

                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {}

                override fun onFailure(
                    call: Call<GenericResponse>,
                    t: Throwable
                ) {}
            })

            // ⭐ SHOW SYSTEM NOTIFICATION
            val helper = NotificationHelper(context)
            helper.createNotificationChannel()
            helper.showNotification(
                "Download Completed",
                "Dubbed video saved to Downloads"
            )

            // ⭐ OPEN SUCCESS SCREEN
            val successIntent = Intent(context, ExportSuccessActivity::class.java)
            successIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            context.startActivity(successIntent)
        }
    }
}
