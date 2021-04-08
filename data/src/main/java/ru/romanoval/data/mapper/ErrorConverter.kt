package ru.romanoval.data.mapper

import okhttp3.ResponseBody
import retrofit2.Retrofit
import ru.romanoval.domain.model.restful.Error

class ErrorConverter(
    private val retrofit: Retrofit
) {
    fun convertError(errorBody: ResponseBody?): String? {
        errorBody?.let { responseBody ->
            val error = retrofit
                .responseBodyConverter<Error>(Error::class.java, arrayOf())
                .convert(responseBody)

            error?.let {
                return "error ${it.code} - ${it.message}"
            }
        }

        return null
    }
}