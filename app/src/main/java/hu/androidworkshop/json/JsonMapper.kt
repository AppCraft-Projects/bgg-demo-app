package hu.androidworkshop.json

import com.google.gson.Gson

interface JsonMapperInterface {
    fun <T> deserialize(input: String, clazz: Class<T>) : T
}

class JsonMapper(private val gson: Gson) : JsonMapperInterface {
    override fun <T> deserialize(input: String, clazz: Class<T>): T = gson.fromJson(input, clazz)
}