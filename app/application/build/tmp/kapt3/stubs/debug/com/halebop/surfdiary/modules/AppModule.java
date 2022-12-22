package com.halebop.surfdiary.modules;

import java.lang.System;

@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tH\u0007J*\u0010\n\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\u000b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0004H\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/halebop/surfdiary/modules/AppModule;", "", "()V", "provideNOAADataSource", "Lcom/halebop/surfdiary/NOAADataSource;", "sqlDriver", "Lcom/squareup/sqldelight/android/AndroidSqliteDriver;", "provideSqlDriver", "app", "Landroid/app/Application;", "provideStationStore", "Lcom/dropbox/android/external/store4/Store;", "", "", "Lcom/halebop/web_types/Station;", "factory", "Lcom/halebop/network/NetworkServicesFactory;", "dataStore", "application_debug"})
@dagger.Module()
public final class AppModule {
    
    public AppModule() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @javax.inject.Singleton()
    @dagger.Provides()
    public final com.squareup.sqldelight.android.AndroidSqliteDriver provideSqlDriver(@org.jetbrains.annotations.NotNull()
    android.app.Application app) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @javax.inject.Singleton()
    @dagger.Provides()
    public final com.halebop.surfdiary.NOAADataSource provideNOAADataSource(@org.jetbrains.annotations.NotNull()
    com.squareup.sqldelight.android.AndroidSqliteDriver sqlDriver) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @javax.inject.Singleton()
    @dagger.Provides()
    public final com.dropbox.android.external.store4.Store<java.lang.Long, java.util.List<com.halebop.web_types.Station>> provideStationStore(@org.jetbrains.annotations.NotNull()
    com.halebop.network.NetworkServicesFactory factory, @org.jetbrains.annotations.NotNull()
    com.halebop.surfdiary.NOAADataSource dataStore) {
        return null;
    }
}