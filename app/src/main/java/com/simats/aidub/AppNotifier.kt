package com.simats.aidub

import android.content.Context
import com.simats.aidub.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.simats.aidub.model.GenericResponse

object AppNotifier {

    fun notifySuccess(context: Context, message: String) {

        // ⭐ Save in DB (real notifications screen)
        ApiClient.apiService.createNotification("1", message)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {}

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {}
            })

        // ⭐ Show phone notification
        val helper = NotificationHelper(context)
        helper.createNotificationChannel()
        helper.showNotification("AI DUB Update", message)
    }
}
