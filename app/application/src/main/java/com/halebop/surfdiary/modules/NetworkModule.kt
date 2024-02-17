package com.halebop.surfdiary.modules

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.halebop.network.NetworkServicesFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkServicesFactory(
        client: OkHttpClient,
        gson: Gson
    ) = NetworkServicesFactory(client, gson)

    @Provides
    @Singleton
    fun providesFlipperNetworkInterceptor(
        plugin: NetworkFlipperPlugin
    ) = FlipperOkhttpInterceptor(plugin)

    @Provides
    @Singleton
    fun providesFlipperNetworkPlugin() = NetworkFlipperPlugin()

    @Provides
    @Singleton
    fun providesOkHttpClient(
        flipperOkhttpInterceptor: FlipperOkhttpInterceptor
    ): OkHttpClient {
        val httpBuilder = OkHttpClient.Builder()
            .addInterceptor(flipperOkhttpInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)

        return httpBuilder
            .protocols(mutableListOf(Protocol.HTTP_1_1))
            .build()
    }

    @Provides
    @Singleton
    fun getGson(): Gson {
        return GsonBuilder().serializeNulls().setLenient().create()
    }
}