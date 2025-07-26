package com.fongmi.android.tv.server.process;

import android.text.TextUtils;

import com.fongmi.android.tv.server.Nano;
import com.fongmi.android.tv.server.impl.Process;
import com.fongmi.android.tv.utils.FlowLogger;
import com.github.catvod.utils.Prefers;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class Cache implements Process {

    @Override
    public boolean isRequest(NanoHTTPD.IHTTPSession session, String url) {
        return url.startsWith("/cache");
    }

    private String getKey(String rule, String key) {
        return "cache_" + (TextUtils.isEmpty(rule) ? "" : rule + "_") + key;
    }

    @Override
    public NanoHTTPD.Response doResponse(NanoHTTPD.IHTTPSession session, String url, Map<String, String> files) {
        Map<String, String> params = session.getParms();
        String action = params.get("do");
        String rule = params.get("rule");
        String key = params.get("key");
        String cacheKey = getKey(rule, key);
        String flowId = "CACHE_" + System.currentTimeMillis() % 10000;

        if ("get".equals(action)) {
            String value = Prefers.getString(cacheKey);
            if (!TextUtils.isEmpty(value)) {
                FlowLogger.logCacheHit(flowId, cacheKey, "SERVER_CACHE", 0);
            } else {
                FlowLogger.logCacheMiss(flowId, cacheKey, "SERVER_CACHE");
            }
            return Nano.ok(value);
        }

        if ("set".equals(action)) {
            String value = params.get("value");
            Prefers.put(cacheKey, value);
            FlowLogger.logCacheWrite(flowId, cacheKey, "SERVER_CACHE", value != null ? value.length() : 0);
        }

        if ("del".equals(action)) {
            Prefers.remove(cacheKey);
            FlowLogger.logCacheClear(flowId, "SERVER_CACHE", 1);
        }

        return Nano.ok();
    }
}
