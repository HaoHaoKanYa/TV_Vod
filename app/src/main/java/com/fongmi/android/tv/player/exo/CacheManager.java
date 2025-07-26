package com.fongmi.android.tv.player.exo;

import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.NoOpCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.utils.FlowLogger;
import com.github.catvod.utils.Path;

public class CacheManager {

    private SimpleCache cache;
    private String currentFlowId;

    private static class Loader {
        static volatile CacheManager INSTANCE = new CacheManager();
    }

    public static CacheManager get() {
        return Loader.INSTANCE;
    }

    public void setFlowId(String flowId) {
        this.currentFlowId = flowId;
    }

    public Cache getCache() {
        if (cache == null) create();
        return cache;
    }

    private void create() {
        if (currentFlowId != null) {
            FlowLogger.logCacheRead(currentFlowId, "ExoCache", "VIDEO_CACHE");
        }
        cache = new SimpleCache(Path.exo(), new NoOpCacheEvictor(), new StandaloneDatabaseProvider(App.get()));
        if (currentFlowId != null) {
            FlowLogger.logCacheWrite(currentFlowId, "ExoCache", "VIDEO_CACHE", 0);
        }
    }
}

