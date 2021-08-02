package ru.vladder2312.filmcatalog.data

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Перехватчик запросов
 */
class QueryInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url.newBuilder()
            .addQueryParameter("api_key", ConstantsAPI.API_KEY)
            .addQueryParameter("language", ConstantsAPI.LOCALE)
            .build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
