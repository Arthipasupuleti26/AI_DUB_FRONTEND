package com.simats.aidub

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.aidub.network.ApiClient
import com.simats.aidub.model.NotificationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        container = findViewById(R.id.notifications_container)

        loadNotifications()

        findViewById<ImageView>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun loadNotifications() {

        // ⚠️ Replace with real logged user id later
        val userId = "1"

        ApiClient.apiService.getNotifications(userId)
            .enqueue(object : Callback<NotificationResponse> {

                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {

                    if(response.isSuccessful && response.body()?.success == true){

                        val list = response.body()!!.notifications

                        if(list.isEmpty()){
                            showEmptyMessage()
                            return
                        }

                        list.forEach {
                            addNotificationCard(it.message, it.is_read == 0)
                        }

                    } else {
                        Toast.makeText(this@NotificationsActivity,"Failed",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    Toast.makeText(this@NotificationsActivity,t.message,Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun addNotificationCard(message:String, isUnread:Boolean){

        val card = TextView(this)
        card.text = message
        card.textSize = 16f
        card.setPadding(30,30,30,30)

        if(isUnread){
            card.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
        }else{
            card.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0,0,0,20)

        card.layoutParams = params

        container.addView(card)
    }

    private fun showEmptyMessage(){
        val tv = TextView(this)
        tv.text = "No notifications yet 🔔"
        tv.textSize = 18f
        tv.setPadding(40,80,40,40)
        container.addView(tv)
    }
}
