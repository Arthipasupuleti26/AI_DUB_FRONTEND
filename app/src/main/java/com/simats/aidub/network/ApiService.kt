package com.simats.aidub.network

import com.simats.aidub.model.LoginResponse
import com.simats.aidub.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {

        @POST("ai_dub/api/auth/login.php")
        fun login(
            @Body body: Map<String, String>
        ): Call<LoginResponse>

        @POST("ai_dub/api/auth/register.php")
        fun register(
            @Body body: Map<String, String>
        ): Call<RegisterResponse>

        @Multipart
        @POST("ai_dub/api/video/process.php")
        fun uploadAndProcessVideo(
            @Part video: MultipartBody.Part
        ): Call<ProcessVideoResponse>

        @Multipart
        @POST("ai_dub/api/audio/extract.php")
        fun extractAudio(
            @Part("project_id") projectId: okhttp3.RequestBody,
            @Part("video_path") videoPath: okhttp3.RequestBody
        ): Call<ExtractAudioResponse>

        @FormUrlEncoded
        @POST("ai_dub/api/audio/transcribe.php")
        fun transcribeAudio(
            @Field("project_id") projectId: String,
            @Field("audio_path") audioPath: String
        ): Call<TranscriptionResponse>


        @FormUrlEncoded
        @POST("ai_dub/api/audio/translate.php")
        fun translateText(
            @Field("project_id") projectId: String,
            @Field("telugu_text") teluguText: String
        ): Call<TranslationResponse>


        @FormUrlEncoded
        @POST("ai_dub/api/audio/detect_emotion.php")
        fun detectEmotion(
            @Field("project_id") projectId: String,
            @Field("english_text") englishText: String
        ): Call<EmotionResponse>



        @FormUrlEncoded
        @POST("ai_dub/api/audio/generate_voice.php")
        fun generateVoice(
            @Field("project_id") projectId: String,
            @Field("text") text: String,
            @Field("voice") voice: String,
            @Field("style") style: String
        ): Call<GenericResponse>

        @FormUrlEncoded
        @POST("ai_dub/api/audio/save_emotion.php")
        fun saveEmotion(
            @Field("project_id") projectId: String,
            @Field("happiness") happiness: Int,
            @Field("excitement") excitement: Int,
            @Field("sadness") sadness: Int
        ): Call<GenericResponse>

        @FormUrlEncoded
        @POST("ai_dub/api/audio/save_voice_tuning.php")
        fun saveVoiceTuning(
            @Field("project_id") projectId: String,
            @Field("speed") speed: Float,
            @Field("pitch") pitch: Float
        ): Call<GenericResponse>

        @POST("ai_dub/api/video/merge_video.php")
        fun mergeVideo(): Call<GenericResponse>

        @GET("ai_dub/api/notifications/list.php")
        fun getNotifications(
            @Query("user_id") userId: String
        ): Call<NotificationResponse>

    @FormUrlEncoded
    @POST("ai_dub/api/notifications/create.php")
    fun createNotification(
        @Field("user_id") userId: String,
        @Field("message") message: String
    ): Call<GenericResponse>

    }

