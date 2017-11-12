package hu.androidworkshop.network

import okhttp3.*
import java.io.File
import java.io.IOException

class ImageUploader {

    private val httpClient: OkHttpClient

    constructor(httpClient: OkHttpClient) {
        this.httpClient = httpClient
    }


    fun uploadImage(image: File, url: String, callback: Callback) {
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", image.name, RequestBody.create(MEDIA_TYPE_JPG, image))
                .build()
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        httpClient
                .newCall(request)
                .enqueue(callback)
    }

    companion object {
        val MEDIA_TYPE_JPG = MediaType.parse("image/jpg")
    }
}