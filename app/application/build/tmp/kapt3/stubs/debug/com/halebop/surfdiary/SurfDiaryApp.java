package com.halebop.surfdiary;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0016R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\u000b"}, d2 = {"Lcom/halebop/surfdiary/SurfDiaryApp;", "Landroid/app/Application;", "()V", "networkFlipperPlugin", "Lcom/facebook/flipper/plugins/network/NetworkFlipperPlugin;", "getNetworkFlipperPlugin", "()Lcom/facebook/flipper/plugins/network/NetworkFlipperPlugin;", "setNetworkFlipperPlugin", "(Lcom/facebook/flipper/plugins/network/NetworkFlipperPlugin;)V", "onCreate", "", "application_debug"})
@dagger.hilt.android.HiltAndroidApp()
public final class SurfDiaryApp extends android.app.Application {
    @javax.inject.Inject()
    public com.facebook.flipper.plugins.network.NetworkFlipperPlugin networkFlipperPlugin;
    
    public SurfDiaryApp() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.facebook.flipper.plugins.network.NetworkFlipperPlugin getNetworkFlipperPlugin() {
        return null;
    }
    
    public final void setNetworkFlipperPlugin(@org.jetbrains.annotations.NotNull()
    com.facebook.flipper.plugins.network.NetworkFlipperPlugin p0) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
}