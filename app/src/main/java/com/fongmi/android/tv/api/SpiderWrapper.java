package com.fongmi.android.tv.api;

import android.content.Context;

import com.fongmi.android.tv.utils.FlowLogger;
import com.github.catvod.crawler.Spider;

import java.util.HashMap;
import java.util.List;

/**
 * Spider包装类，用于添加日志跟踪
 */
public class SpiderWrapper extends Spider {
    
    private final Spider spider;
    private final String siteKey;
    private String flowId;
    
    public SpiderWrapper(Spider spider, String siteKey) {
        this.spider = spider;
        this.siteKey = siteKey;
    }
    
    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }
    
    @Override
    public void init(Context context) throws Exception {
        spider.init(context);
    }
    
    @Override
    public void init(Context context, String extend) throws Exception {
        spider.init(context, extend);
    }
    
    @Override
    public String homeContent(boolean filter) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            FlowLogger.logVod(flowId, FlowLogger.VodStage.HOME_CONTENT, FlowLogger.Level.INFO, 
                String.format("开始获取首页内容 [%s]", siteKey));
            
            String result = spider.homeContent(filter);
            long duration = System.currentTimeMillis() - startTime;
            
            FlowLogger.logVodHomeContent(flowId, siteKey, true, result);
            FlowLogger.logVod(flowId, FlowLogger.VodStage.HOME_CONTENT, FlowLogger.Level.INFO, 
                String.format("首页内容获取完成 [%s]，耗时: %dms", siteKey, duration));
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            FlowLogger.logVodHomeContent(flowId, siteKey, false, e.getMessage());
            FlowLogger.logVod(flowId, FlowLogger.VodStage.HOME_CONTENT, FlowLogger.Level.ERROR, 
                String.format("首页内容获取失败 [%s]，耗时: %dms", siteKey, duration), e);
            throw e;
        }
    }
    
    @Override
    public String homeVideoContent() throws Exception {
        return spider.homeVideoContent();
    }
    
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            FlowLogger.logVod(flowId, FlowLogger.VodStage.CATEGORY_CONTENT, FlowLogger.Level.INFO, 
                String.format("开始获取分类内容 [%s] tid:%s pg:%s", siteKey, tid, pg));
            
            String result = spider.categoryContent(tid, pg, filter, extend);
            long duration = System.currentTimeMillis() - startTime;
            
            FlowLogger.logVodCategoryContent(flowId, siteKey, tid, pg, true, result);
            FlowLogger.logVod(flowId, FlowLogger.VodStage.CATEGORY_CONTENT, FlowLogger.Level.INFO, 
                String.format("分类内容获取完成 [%s] tid:%s pg:%s，耗时: %dms", siteKey, tid, pg, duration));
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            FlowLogger.logVodCategoryContent(flowId, siteKey, tid, pg, false, e.getMessage());
            FlowLogger.logVod(flowId, FlowLogger.VodStage.CATEGORY_CONTENT, FlowLogger.Level.ERROR, 
                String.format("分类内容获取失败 [%s] tid:%s pg:%s，耗时: %dms", siteKey, tid, pg, duration), e);
            throw e;
        }
    }
    
    @Override
    public String detailContent(List<String> ids) throws Exception {
        long startTime = System.currentTimeMillis();
        String vodId = ids != null && !ids.isEmpty() ? ids.get(0) : "unknown";
        try {
            FlowLogger.logVod(flowId, FlowLogger.VodStage.DETAIL_CONTENT, FlowLogger.Level.INFO, 
                String.format("开始获取详情内容 [%s] vodId:%s", siteKey, vodId));
            
            String result = spider.detailContent(ids);
            long duration = System.currentTimeMillis() - startTime;
            
            FlowLogger.logVodDetailContent(flowId, siteKey, vodId, true, result);
            FlowLogger.logVod(flowId, FlowLogger.VodStage.DETAIL_CONTENT, FlowLogger.Level.INFO, 
                String.format("详情内容获取完成 [%s] vodId:%s，耗时: %dms", siteKey, vodId, duration));
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            FlowLogger.logVodDetailContent(flowId, siteKey, vodId, false, e.getMessage());
            FlowLogger.logVod(flowId, FlowLogger.VodStage.DETAIL_CONTENT, FlowLogger.Level.ERROR, 
                String.format("详情内容获取失败 [%s] vodId:%s，耗时: %dms", siteKey, vodId, duration), e);
            throw e;
        }
    }
    
    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            FlowLogger.logVod(flowId, "SEARCH_CONTENT", FlowLogger.Level.INFO, 
                String.format("开始搜索内容 [%s] key:%s quick:%s", siteKey, key, quick));
            
            String result = spider.searchContent(key, quick);
            long duration = System.currentTimeMillis() - startTime;
            
            FlowLogger.logVod(flowId, "SEARCH_CONTENT", FlowLogger.Level.INFO, 
                String.format("搜索内容完成 [%s] key:%s，耗时: %dms，结果长度: %d", 
                    siteKey, key, duration, result != null ? result.length() : 0));
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            FlowLogger.logVod(flowId, "SEARCH_CONTENT", FlowLogger.Level.ERROR, 
                String.format("搜索内容失败 [%s] key:%s，耗时: %dms", siteKey, key, duration), e);
            throw e;
        }
    }
    
    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            FlowLogger.logVod(flowId, "SEARCH_CONTENT", FlowLogger.Level.INFO, 
                String.format("开始搜索内容 [%s] key:%s quick:%s pg:%s", siteKey, key, quick, pg));
            
            String result = spider.searchContent(key, quick, pg);
            long duration = System.currentTimeMillis() - startTime;
            
            FlowLogger.logVod(flowId, "SEARCH_CONTENT", FlowLogger.Level.INFO, 
                String.format("搜索内容完成 [%s] key:%s pg:%s，耗时: %dms，结果长度: %d", 
                    siteKey, key, pg, duration, result != null ? result.length() : 0));
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            FlowLogger.logVod(flowId, "SEARCH_CONTENT", FlowLogger.Level.ERROR, 
                String.format("搜索内容失败 [%s] key:%s pg:%s，耗时: %dms", siteKey, key, pg, duration), e);
            throw e;
        }
    }
    
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            FlowLogger.logVod(flowId, FlowLogger.VodStage.PLAYER_CONTENT, FlowLogger.Level.INFO, 
                String.format("开始获取播放地址 [%s] flag:%s id:%s", siteKey, flag, id));
            
            String result = spider.playerContent(flag, id, vipFlags);
            long duration = System.currentTimeMillis() - startTime;
            
            FlowLogger.logVodPlayerContent(flowId, siteKey, flag, id, true, result);
            FlowLogger.logVod(flowId, FlowLogger.VodStage.PLAYER_CONTENT, FlowLogger.Level.INFO, 
                String.format("播放地址获取完成 [%s] flag:%s id:%s，耗时: %dms", siteKey, flag, id, duration));
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            FlowLogger.logVodPlayerContent(flowId, siteKey, flag, id, false, e.getMessage());
            FlowLogger.logVod(flowId, FlowLogger.VodStage.PLAYER_CONTENT, FlowLogger.Level.ERROR, 
                String.format("播放地址获取失败 [%s] flag:%s id:%s，耗时: %dms", siteKey, flag, id, duration), e);
            throw e;
        }
    }
    
    @Override
    public boolean isVideoFormat(String url) throws Exception {
        return spider.isVideoFormat(url);
    }
    
    @Override
    public boolean manualVideoCheck() throws Exception {
        return spider.manualVideoCheck();
    }

    @Override
    public String liveContent(String url) throws Exception {
        return spider.liveContent(url);
    }

    @Override
    public Object[] proxyLocal(java.util.Map<String, String> params) throws Exception {
        return spider.proxyLocal(params);
    }

    @Override
    public String action(String action) throws Exception {
        return spider.action(action);
    }

    @Override
    public void destroy() {
        spider.destroy();
    }
    
    // 获取原始Spider实例
    public Spider getOriginalSpider() {
        return spider;
    }
}
