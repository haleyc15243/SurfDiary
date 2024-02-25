package com.halebop.surfdiary.modules

import android.app.Application
import android.content.Context
import com.dropbox.android.external.store4.Store
import com.halebop.location_services.LocationUtils
import com.halebop.network.NetworkServicesFactory
import com.halebop.surfdiary.DiaryDatabase
import com.halebop.surfdiary.LocationDatasource
import com.halebop.surfdiary.LocationDatasourceImpl
import com.halebop.surfdiary.NOAADataSource
import com.halebop.surfdiary.NOAADataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): AndroidSqliteDriver {
        return AndroidSqliteDriver(
            schema = DiaryDatabase.Schema,
            context = app,
            name = "diary.db"
        )
    }

    @Provides
    @Singleton
    fun provideNOAADataSource(
        sqlDriver: AndroidSqliteDriver
    ): NOAADataSource {
        return NOAADataSourceImpl(DiaryDatabase.invoke(sqlDriver).noaa_stationQueries)
    }

    @Provides
    @Singleton
    fun provideLocationDatasource(
        sqlDriver: AndroidSqliteDriver
    ): LocationDatasource {
        return LocationDatasourceImpl(DiaryDatabase.invoke(sqlDriver).locationQueries)
    }

    @Provides
    @Singleton
    fun provideStationStore(
        factory: NetworkServicesFactory,
        dataStore: NOAADataSource
    ): Store<Long, List<Station>> = factory.noaaStationsStore(dataStore)

    @Provides
    @Singleton
    fun provideLocationUtils(
        @ApplicationContext context: Context
    ) = LocationUtils(context)
}