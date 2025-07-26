package com.fongmi.android.tv.api.loader;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.utils.FlowLogger;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsLoader {

    private final ConcurrentHashMap<String, Spider> spiders;
    private String recent;
    private String currentFlowId;

    public JsLoader() {
        spiders = new ConcurrentHashMap<>();
    }

    public void clear() {
        for (Spider spider : spiders.values()) App.execute(spider::destroy);
        spiders.clear();
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public void setFlowId(String flowId) {
        this.currentFlowId = flowId;
    }

    public Spider getSpider(String key, String api, String ext, String jar) {
        try {
            if (spiders.containsKey(key)) return spiders.get(key);

            long startTime = System.currentTimeMillis();
            if (currentFlowId != null) {
                FlowLogger.logJsEngineInit(currentFlowId, key, api);
            }

            // 设置Module的流程ID，这样JavaScript模块下载时也能记录日志
            com.fongmi.quickjs.utils.Module.get().setFlowId(currentFlowId);

            Spider spider = new com.fongmi.quickjs.crawler.Spider(key, api, BaseLoader.get().dex(jar));
            if (spider instanceof com.fongmi.quickjs.crawler.Spider) {
                ((com.fongmi.quickjs.crawler.Spider) spider).setFlowId(currentFlowId);
            }

            spider.init(App.get(), ext);
            spiders.put(key, spider);

            if (currentFlowId != null) {
                long duration = System.currentTimeMillis() - startTime;
                FlowLogger.logJsEngineInitSuccess(currentFlowId, key, duration);
            }

            return spider;
        } catch (Throwable e) {
            if (currentFlowId != null) {
                FlowLogger.logJsEngineInitError(currentFlowId, key, e);
            }
            e.printStackTrace();
            return new SpiderNull();
        }
    }

    public Object[] proxyInvoke(Map<String, String> params) {
        try {
            if (!params.containsKey("siteKey")) return spiders.get(recent).proxyLocal(params);
            return BaseLoader.get().getSpider(params).proxyLocal(params);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
