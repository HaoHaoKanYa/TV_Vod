package com.fongmi.android.tv.player;

import com.fongmi.android.tv.utils.FlowLogger;

/**
 * 播放器日志跟踪工具类
 */
public class PlayerLogger {
    
    /**
     * 记录点播播放器创建
     */
    public static void logVodPlayerCreate(String flowId, String playerType, String url) {
        FlowLogger.logVodPlayerCreate(flowId, playerType, url);
    }
    
    /**
     * 记录点播播放开始
     */
    public static void logVodPlayerStart(String flowId, String url) {
        long startTime = System.currentTimeMillis();
        FlowLogger.logVodPlayerStart(flowId, url);
        // 可以在这里存储开始时间，用于后续计算播放耗时
        PlayerTimeTracker.recordStartTime(flowId, startTime);
    }
    
    /**
     * 记录点播播放成功
     */
    public static void logVodPlaySuccess(String flowId, String url) {
        long duration = PlayerTimeTracker.getElapsedTime(flowId);
        FlowLogger.logVodPlaySuccess(flowId, url, duration);
    }
    
    /**
     * 记录点播播放错误
     */
    public static void logVodPlayError(String flowId, String url, String errorCode, Throwable error) {
        FlowLogger.logVodPlayError(flowId, url, errorCode, error);
    }
    
    /**
     * 记录直播播放器创建
     */
    public static void logLivePlayerCreate(String flowId, String playerType, String url) {
        FlowLogger.logLivePlayerCreate(flowId, playerType, url);
    }
    
    /**
     * 记录直播播放开始
     */
    public static void logLivePlayerStart(String flowId, String channelName, String url) {
        long startTime = System.currentTimeMillis();
        FlowLogger.logLivePlayerStart(flowId, channelName, url);
        PlayerTimeTracker.recordStartTime(flowId, startTime);
    }
    
    /**
     * 记录直播播放成功
     */
    public static void logLivePlaySuccess(String flowId, String channelName, String url) {
        long duration = PlayerTimeTracker.getElapsedTime(flowId);
        FlowLogger.logLivePlaySuccess(flowId, channelName, url, duration);
    }
    
    /**
     * 记录直播播放错误
     */
    public static void logLivePlayError(String flowId, String channelName, String url, String errorCode, Throwable error) {
        FlowLogger.logLivePlayError(flowId, channelName, url, errorCode, error);
    }
    
    /**
     * 播放时间跟踪器
     */
    private static class PlayerTimeTracker {
        private static final java.util.Map<String, Long> startTimes = new java.util.concurrent.ConcurrentHashMap<>();
        
        public static void recordStartTime(String flowId, long startTime) {
            startTimes.put(flowId, startTime);
        }
        
        public static long getElapsedTime(String flowId) {
            Long startTime = startTimes.get(flowId);
            if (startTime != null) {
                long elapsed = System.currentTimeMillis() - startTime;
                startTimes.remove(flowId); // 清理已使用的记录
                return elapsed;
            }
            return 0;
        }
    }
}
