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
            .addQueryParameter("api_key", "6ccd72a2a8fc239b13f209408fc31c33")
            .addQueryParameter("language", "ru-RU")
            .build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
