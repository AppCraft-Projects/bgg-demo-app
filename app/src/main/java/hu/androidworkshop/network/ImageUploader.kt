package hu.androidworkshop.network

import okhttp3.*
import java.io.File

interface ImageUploaderInterface {
    fun uploadImage(image: File, url: String, callback: Callback)
}

class ImageUploader(private val httpClient: OkHttpClient, private val fileNameKey: String) : ImageUploaderInterface {

    override fun uploadImage(image: File, url: String, callback: Callback) {
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileNameKey, image.name, RequestBody.create(MEDIA_TYPE_JPG, image))
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