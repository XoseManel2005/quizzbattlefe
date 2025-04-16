package com.xose.quizzbattle.data

import android.content.Context
import com.xose.quizzbattle.util.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object ApiClient {
    private const val BASE_URL = "https://10.0.2.2:8443/"

    // Interceptor que añade el token al header
    private fun getAuthInterceptor(context: Context): Interceptor {
        val sessionManager = SessionManager(context)
        return Interceptor { chain ->
            val token = sessionManager.getAuthToken()
            val request: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }
    }

    // Cliente OkHttp que ignora el certificado SSL (desarrollo)
    private fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(getAuthInterceptor(context))
            .build()
    }

    // Retrofit creado con contexto para el token
    private fun create(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio con token incluido
    fun getGameService(context: Context): GameService {
        return create(context).create(GameService::class.java)
    }

    // Servicio con token incluido
    fun getClientService(context: Context): ApiService {
        return create(context).create(ApiService::class.java)
    }
}
