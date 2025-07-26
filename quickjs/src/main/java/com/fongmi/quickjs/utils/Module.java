package com.fongmi.quickjs.utils;

import android.net.Uri;

import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Asset;
import com.github.catvod.utils.Path;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class Module {

    private final ConcurrentHashMap<String, String> cache;
    private String currentFlowId;

    private static class Loader {
        static volatile Module INSTANCE = new Module();
    }

    public static Module get() {
        return Loader.INSTANCE;
    }

    public Module() {
        this.cache = new ConcurrentHashMap<>();
    }

    public void setFlowId(String flowId) {
        this.currentFlowId = flowId;
    }

    public String fetch(String name) {
        long startTime = System.currentTimeMillis();
        boolean fromCache = cache.containsKey(name);

        if (fromCache) {
            // 记录缓存命中
            if (currentFlowId != null && name.startsWith("http")) {
                try {
                    android.util.Log.i("VOD_FLOW", String.format("[%s] [FlowID:%s] [JS_MODULE_DOWNLOAD] JavaScript模块下载成功: %s，大小: %d bytes，缓存: 命中",
                        new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()),
                        currentFlowId, name, cache.get(name).length()));
                } catch (Exception e) {
                    // 忽略日志错误
                }
            }
            return cache.get(name);
        }

        String content = null;
        if (name.startsWith("http")) {
            content = request(name);
            cache.put(name, content);
        } else if (name.startsWith("assets")) {
            content = Asset.read(name);
            cache.put(name, content);
            if (currentFlowId != null) {
                try {
                    long duration = System.currentTimeMillis() - startTime;
                    android.util.Log.i("VOD_FLOW", String.format("[%s] [FlowID:%s] [JS_MODULE_LOAD] JavaScript模块加载成功 [%s]，耗时: %dms",
                        new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()),
                        currentFlowId, name, duration));
                } catch (Exception e) {
                    // 忽略日志错误
                }
            }
        } else if (name.startsWith("lib/")) {
            content = Asset.read("js/" + name);
            cache.put(name, content);
            if (currentFlowId != null) {
                try {
                    long duration = System.currentTimeMillis() - startTime;
                    android.util.Log.i("VOD_FLOW", String.format("[%s] [FlowID:%s] [JS_MODULE_LOAD] JavaScript模块加载成功 [%s]，耗时: %dms",
                        new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()),
                        currentFlowId, name, duration));
                } catch (Exception e) {
                    // 忽略日志错误
                }
            }
        }

        return cache.get(name);
    }

    private String request(String url) {
        long startTime = System.currentTimeMillis();
        try {
            if (currentFlowId != null) {
                try {
                    android.util.Log.i("VOD_FLOW", String.format("[%s] [FlowID:%s] [JS_MODULE_DOWNLOAD] 开始下载JavaScript模块: %s",
                        new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()),
                        currentFlowId, url));
                } catch (Exception ex) {
                    // 忽略日志错误
                }
            }

            Uri uri = Uri.parse(url);
            byte[] data = OkHttp.bytes(url);
            File file = Path.js(uri.getLastPathSegment());
            boolean cache = !"127.0.0.1".equals(uri.getHost());
            if (cache) new Thread(() -> Path.write(file, data)).start();
            String content = new String(data, StandardCharsets.UTF_8);

            if (currentFlowId != null) {
                try {
                    android.util.Log.i("VOD_FLOW", String.format("[%s] [FlowID:%s] [JS_MODULE_DOWNLOAD] JavaScript模块下载成功: %s，大小: %d bytes，缓存: 未命中",
                        new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()),
                        currentFlowId, url, content.length()));
                } catch (Exception ex) {
                    // 忽略日志错误
                }
            }

            return content;
        } catch (Exception e) {
            if (currentFlowId != null) {
                try {
                    android.util.Log.e("VOD_FLOW", String.format("[%s] [FlowID:%s] [JS_MODULE_DOWNLOAD] JavaScript模块下载失败: %s",
                        new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()),
                        currentFlowId, url), e);
                } catch (Exception ex) {
                    // 忽略日志错误
                }
            }
            return cache(url);
        }
    }

    private String cache(String url) {
        try {
            Uri uri = Uri.parse(url);
            File file = Path.js(uri.getLastPathSegment());
            return file.exists() ? Path.read(file) : "";
        } catch (Exception e) {
            return "";
        }
    }
}
