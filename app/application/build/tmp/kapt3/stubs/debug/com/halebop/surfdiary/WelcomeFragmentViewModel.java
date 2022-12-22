package com.halebop.surfdiary;

import java.lang.System;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B!\b\u0007\u0012\u0018\u0010\u0002\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0003\u00a2\u0006\u0002\u0010\u0007R\u001d\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lcom/halebop/surfdiary/WelcomeFragmentViewModel;", "Landroidx/lifecycle/ViewModel;", "noaaDatastore", "Lcom/dropbox/android/external/store4/Store;", "", "", "Lcom/halebop/web_types/Station;", "(Lcom/dropbox/android/external/store4/Store;)V", "stationsFlow", "Landroidx/lifecycle/LiveData;", "getStationsFlow", "()Landroidx/lifecycle/LiveData;", "application_debug"})
public final class WelcomeFragmentViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.halebop.web_types.Station>> stationsFlow = null;
    
    @javax.inject.Inject()
    public WelcomeFragmentViewModel(@org.jetbrains.annotations.NotNull()
    com.dropbox.android.external.store4.Store<java.lang.Long, java.util.List<com.halebop.web_types.Station>> noaaDatastore) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.halebop.web_types.Station>> getStationsFlow() {
        return null;
    }
}