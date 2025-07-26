package com.fongmi.android.tv.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TV-Vod 流程日志跟踪工具类
 * 用于跟踪点播和直播系统的完整流程
 */
public class FlowLogger {

    private static final String TAG = "FlowLogger";
    private static final String VOD_TAG = "VOD_FLOW";
    private static final String LIVE_TAG = "LIVE_FLOW";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    // 流程ID生成器
    private static final AtomicLong flowIdGenerator = new AtomicLong(1);
    
    // 日志级别
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    // 流程阶段定义
    public static class VodStage {
        // 配置相关
        public static final String CONFIG_INPUT = "CONFIG_INPUT";
        public static final String CONFIG_DOWNLOAD = "CONFIG_DOWNLOAD";
        public static final String CONFIG_PARSE = "CONFIG_PARSE";
        public static final String SITE_INIT = "SITE_INIT";
        public static final String SPIDER_LOAD = "SPIDER_LOAD";

        // JavaScript引擎相关
        public static final String JS_ENGINE_INIT = "JS_ENGINE_INIT";
        public static final String JS_MODULE_DOWNLOAD = "JS_MODULE_DOWNLOAD";
        public static final String JS_MODULE_LOAD = "JS_MODULE_LOAD";
        public static final String JS_FUNCTION_CALL = "JS_FUNCTION_CALL";

        // 内容获取相关
        public static final String HOME_CONTENT = "HOME_CONTENT";
        public static final String CATEGORY_CONTENT = "CATEGORY_CONTENT";
        public static final String DETAIL_CONTENT = "DETAIL_CONTENT";
        public static final String PLAYER_CONTENT = "PLAYER_CONTENT";

        // 搜索相关
        public static final String SEARCH_OPEN = "SEARCH_OPEN";
        public static final String SEARCH_INPUT = "SEARCH_INPUT";
        public static final String SEARCH_EXECUTE = "SEARCH_EXECUTE";
        public static final String SEARCH_RESULT = "SEARCH_RESULT";
        public static final String SEARCH_SELECT = "SEARCH_SELECT";

        // 站点选择相关
        public static final String SITE_SELECTOR_OPEN = "SITE_SELECTOR_OPEN";
        public static final String SITE_SELECTOR_LOAD = "SITE_SELECTOR_LOAD";
        public static final String SITE_SELECT = "SITE_SELECT";
        public static final String SITE_CONFIRM = "SITE_CONFIRM";

        // 线路选择相关
        public static final String ROUTE_SELECTOR_OPEN = "ROUTE_SELECTOR_OPEN";
        public static final String ROUTE_SELECTOR_LOAD = "ROUTE_SELECTOR_LOAD";
        public static final String ROUTE_SELECT = "ROUTE_SELECT";
        public static final String ROUTE_CONFIRM = "ROUTE_CONFIRM";

        // 电影详情相关
        public static final String MOVIE_CLICK = "MOVIE_CLICK";
        public static final String MOVIE_DETAIL_OPEN = "MOVIE_DETAIL_OPEN";
        public static final String MOVIE_DETAIL_LOAD = "MOVIE_DETAIL_LOAD";
        public static final String MOVIE_EPISODES_LOAD = "MOVIE_EPISODES_LOAD";
        public static final String EPISODE_SELECT = "EPISODE_SELECT";

        // 播放相关
        public static final String PARSE_URL = "PARSE_URL";
        public static final String PLAYER_CREATE = "PLAYER_CREATE";
        public static final String PLAYER_START = "PLAYER_START";
        public static final String PLAY_SUCCESS = "PLAY_SUCCESS";
        public static final String PLAY_ERROR = "PLAY_ERROR";
        public static final String PLAY_PAUSE = "PLAY_PAUSE";
        public static final String PLAY_RESUME = "PLAY_RESUME";
        public static final String PLAY_STOP = "PLAY_STOP";

        // 缓存相关
        public static final String CACHE_READ = "CACHE_READ";
        public static final String CACHE_WRITE = "CACHE_WRITE";
        public static final String CACHE_HIT = "CACHE_HIT";
        public static final String CACHE_MISS = "CACHE_MISS";
        public static final String CACHE_CLEAR = "CACHE_CLEAR";
        public static final String CACHE_EXPIRE = "CACHE_EXPIRE";

        // 电影ID获取和跟踪阶段
        public static final String MOVIE_ID_PARSE = "MOVIE_ID_PARSE";
        public static final String MOVIE_ID_CLICK = "MOVIE_ID_CLICK";
        public static final String MOVIE_ID_TRANSFER = "MOVIE_ID_TRANSFER";
        public static final String MOVIE_ID_USAGE = "MOVIE_ID_USAGE";
    }
    
    public static class LiveStage {
        public static final String CONFIG_INPUT = "CONFIG_INPUT";
        public static final String CONFIG_DOWNLOAD = "CONFIG_DOWNLOAD";
        public static final String CONFIG_PARSE = "CONFIG_PARSE";
        public static final String LIVE_SOURCE_PARSE = "LIVE_SOURCE_PARSE";
        public static final String CHANNEL_PARSE = "CHANNEL_PARSE";
        public static final String EPG_DOWNLOAD = "EPG_DOWNLOAD";
        public static final String EPG_PARSE = "EPG_PARSE";
        public static final String CHANNEL_SELECT = "CHANNEL_SELECT";
        public static final String URL_SELECT = "URL_SELECT";
        public static final String PLAYER_CREATE = "PLAYER_CREATE";
        public static final String PLAYER_START = "PLAYER_START";
        public static final String PLAY_SUCCESS = "PLAY_SUCCESS";
        public static final String PLAY_ERROR = "PLAY_ERROR";
    }

    /**
     * 获取当前时间字符串
     */
    private static String getCurrentTime() {
        return TIME_FORMAT.format(new Date());
    }

    /**
     * 生成新的流程ID
     */
    public static String generateFlowId() {
        return String.valueOf(flowIdGenerator.getAndIncrement());
    }

    /**
     * 记录点播流程日志
     */
    public static void logVod(String flowId, String stage, Level level, String message) {
        logVod(flowId, stage, level, message, null);
    }

    /**
     * 记录点播流程日志（带异常）
     */
    public static void logVod(String flowId, String stage, Level level, String message, Throwable throwable) {
        String logMessage = formatMessage(flowId, stage, message);
        logWithLevel(VOD_TAG, level, logMessage, throwable);
    }

    /**
     * 记录直播流程日志
     */
    public static void logLive(String flowId, String stage, Level level, String message) {
        logLive(flowId, stage, level, message, null);
    }

    /**
     * 记录直播流程日志（带异常）
     */
    public static void logLive(String flowId, String stage, Level level, String message, Throwable throwable) {
        String logMessage = formatMessage(flowId, stage, message);
        logWithLevel(LIVE_TAG, level, logMessage, throwable);
    }

    /**
     * 记录点播配置输入
     */
    public static void logVodConfigInput(String flowId, String configUrl) {
        logVod(flowId, VodStage.CONFIG_INPUT, Level.INFO, 
            String.format("用户输入配置URL: %s", configUrl));
    }

    /**
     * 记录点播配置下载开始
     */
    public static void logVodConfigDownloadStart(String flowId, String configUrl) {
        logVod(flowId, VodStage.CONFIG_DOWNLOAD, Level.INFO, 
            String.format("开始下载配置文件: %s", configUrl));
    }

    /**
     * 记录点播配置下载成功
     */
    public static void logVodConfigDownloadSuccess(String flowId, int contentLength) {
        logVod(flowId, VodStage.CONFIG_DOWNLOAD, Level.INFO, 
            String.format("配置文件下载成功，大小: %d bytes", contentLength));
    }

    /**
     * 记录点播配置下载失败
     */
    public static void logVodConfigDownloadError(String flowId, Throwable error) {
        logVod(flowId, VodStage.CONFIG_DOWNLOAD, Level.ERROR, 
            "配置文件下载失败", error);
    }

    /**
     * 记录点播配置解析开始
     */
    public static void logVodConfigParseStart(String flowId) {
        logVod(flowId, VodStage.CONFIG_PARSE, Level.INFO, "开始解析配置文件");
    }

    /**
     * 记录点播配置解析成功
     */
    public static void logVodConfigParseSuccess(String flowId, int siteCount, int parseCount) {
        logVod(flowId, VodStage.CONFIG_PARSE, Level.INFO, 
            String.format("配置解析成功，站点数: %d，解析器数: %d", siteCount, parseCount));
    }

    /**
     * 记录点播站点初始化
     */
    public static void logVodSiteInit(String flowId, String siteKey, String siteName, String api) {
        logVod(flowId, VodStage.SITE_INIT, Level.INFO, 
            String.format("初始化站点 [%s] %s, API: %s", siteKey, siteName, api));
    }

    /**
     * 记录爬虫加载
     */
    public static void logSpiderLoad(String flowId, String spiderType, String spiderUrl) {
        logVod(flowId, VodStage.SPIDER_LOAD, Level.INFO, 
            String.format("加载爬虫 [%s]: %s", spiderType, spiderUrl));
    }

    /**
     * 记录首页内容获取
     */
    public static void logVodHomeContent(String flowId, String siteKey, boolean success, String result) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("获取首页内容成功 [%s]，返回数据长度: %d", siteKey, result != null ? result.length() : 0) :
            String.format("获取首页内容失败 [%s]: %s", siteKey, result);
        logVod(flowId, VodStage.HOME_CONTENT, level, message);
    }

    /**
     * 记录分类内容获取
     */
    public static void logVodCategoryContent(String flowId, String siteKey, String tid, String pg, boolean success, String result) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("获取分类内容成功 [%s] tid:%s pg:%s，返回数据长度: %d", siteKey, tid, pg, result != null ? result.length() : 0) :
            String.format("获取分类内容失败 [%s] tid:%s pg:%s: %s", siteKey, tid, pg, result);
        logVod(flowId, VodStage.CATEGORY_CONTENT, level, message);
    }

    /**
     * 记录详情内容获取
     */
    public static void logVodDetailContent(String flowId, String siteKey, String vodId, boolean success, String result) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ?
            String.format("获取详情内容成功 [%s] vodId:%s，返回数据长度: %d", siteKey, vodId, result != null ? result.length() : 0) :
            String.format("获取详情内容失败 [%s] vodId:%s: %s", siteKey, vodId, result);
        logVod(flowId, VodStage.DETAIL_CONTENT, level, message);
    }

    // ==================== 详情内容获取详细流程日志方法 ====================

    /**
     * 记录开始获取详情内容
     */
    public static void logVodDetailContentStart(String flowId, String siteKey, String vodId) {
        // 添加直接的Android Log输出
        Log.i("VOD_FLOW", String.format("[%s] [FlowID:%s] [DETAIL_CONTENT_START] 开始获取详情内容 [%s] vodId: %s",
            getCurrentTime(), flowId, siteKey, vodId));

        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("开始获取详情内容 [%s] vodId: %s", siteKey, vodId));
    }

    /**
     * 记录站点信息
     */
    public static void logVodDetailContentSiteInfo(String flowId, String siteKey, String siteName, int siteType, String siteApi) {
        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("站点信息 [%s] %s，类型: %d，API: %s", siteKey, siteName, siteType, siteApi));
    }

    /**
     * 记录Spider类型
     */
    public static void logVodDetailContentSpiderType(String flowId, String siteKey, String spiderType) {
        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("Spider类型 [%s] %s", siteKey, spiderType));
    }

    /**
     * 记录Spider方法调用
     */
    public static void logVodDetailContentSpiderCall(String flowId, String siteKey, String vodId, String method) {
        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("调用Spider方法 [%s] %s(vodId: %s)", siteKey, method, vodId));
    }

    /**
     * 记录Spider响应
     */
    public static void logVodDetailContentSpiderResponse(String flowId, String siteKey, String vodId, int responseLength) {
        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("Spider响应 [%s] vodId: %s，响应长度: %d bytes", siteKey, vodId, responseLength));
    }

    /**
     * 记录HTTP请求
     */
    public static void logVodDetailContentHttpRequest(String flowId, String siteKey, String apiUrl, String params) {
        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("HTTP请求 [%s] URL: %s，参数: %s", siteKey, apiUrl, params));
    }

    /**
     * 记录HTTP响应
     */
    public static void logVodDetailContentHttpResponse(String flowId, String siteKey, int responseLength) {
        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("HTTP响应 [%s] 响应长度: %d bytes", siteKey, responseLength));
    }

    /**
     * 记录详情内容获取完成
     */
    public static void logVodDetailContentComplete(String flowId, String siteKey, String vodId, String vodName, int flagCount, long duration) {
        logVod(flowId, VodStage.DETAIL_CONTENT, Level.INFO,
            String.format("详情内容获取完成 [%s] vodId: %s，影片: %s，线路数: %d，耗时: %dms",
                siteKey, vodId, vodName, flagCount, duration));
    }

    // ==================== 电影ID获取和跟踪日志方法 ====================

    /**
     * 记录电影ID解析
     */
    public static void logMovieIdParse(String parseType, int movieCount, String movieSample) {
        String flowId = "PARSE_" + System.currentTimeMillis() % 100000;
        logVod(flowId, VodStage.MOVIE_ID_PARSE, Level.INFO,
            String.format("%s解析完成，共%d部电影: %s", parseType, movieCount, movieSample));
    }

    /**
     * 记录电影点击和ID获取
     */
    public static void logMovieIdClick(String movieName, String movieId, String siteKey, String sourcePage) {
        String flowId = "CLICK_" + System.currentTimeMillis() % 100000;
        logVod(flowId, VodStage.MOVIE_ID_CLICK, Level.INFO,
            String.format("点击电影: %s，ID: %s，站点: %s，来源: %s", movieName, movieId, siteKey, sourcePage));
    }

    /**
     * 记录电影ID传递
     */
    public static void logMovieIdTransfer(String flowId, String movieName, String movieId, String siteKey, String targetActivity) {
        logVod(flowId, VodStage.MOVIE_ID_TRANSFER, Level.INFO,
            String.format("电影ID传递: %s，ID: %s，站点: %s，目标: %s", movieName, movieId, siteKey, targetActivity));
    }

    /**
     * 记录电影ID使用
     */
    public static void logMovieIdUsage(String flowId, String movieId, String siteKey, String usage) {
        logVod(flowId, VodStage.MOVIE_ID_USAGE, Level.INFO,
            String.format("电影ID使用: ID: %s，站点: %s，用途: %s", movieId, siteKey, usage));
    }

    /**
     * 记录播放地址获取
     */
    public static void logVodPlayerContent(String flowId, String siteKey, String flag, String id, boolean success, String result) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("获取播放地址成功 [%s] flag:%s id:%s，返回数据长度: %d", siteKey, flag, id, result != null ? result.length() : 0) :
            String.format("获取播放地址失败 [%s] flag:%s id:%s: %s", siteKey, flag, id, result);
        logVod(flowId, VodStage.PLAYER_CONTENT, level, message);
    }

    /**
     * 记录URL解析
     */
    public static void logVodParseUrl(String flowId, String parseType, String originalUrl, String parsedUrl, boolean success) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("URL解析成功 [%s] %s -> %s", parseType, originalUrl, parsedUrl) :
            String.format("URL解析失败 [%s] %s", parseType, originalUrl);
        logVod(flowId, VodStage.PARSE_URL, level, message);
    }

    /**
     * 记录播放器创建
     */
    public static void logVodPlayerCreate(String flowId, String playerType, String url) {
        logVod(flowId, VodStage.PLAYER_CREATE, Level.INFO, 
            String.format("创建播放器 [%s] URL: %s", playerType, url));
    }

    /**
     * 记录播放开始
     */
    public static void logVodPlayerStart(String flowId, String url) {
        logVod(flowId, VodStage.PLAYER_START, Level.INFO, 
            String.format("开始播放: %s", url));
    }

    /**
     * 记录播放成功
     */
    public static void logVodPlaySuccess(String flowId, String url, long duration) {
        logVod(flowId, VodStage.PLAY_SUCCESS, Level.INFO, 
            String.format("播放成功: %s，耗时: %dms", url, duration));
    }

    /**
     * 记录播放错误
     */
    public static void logVodPlayError(String flowId, String url, String errorCode, Throwable error) {
        logVod(flowId, VodStage.PLAY_ERROR, Level.ERROR,
            String.format("播放失败: %s，错误码: %s", url, errorCode), error);
    }

    // ==================== JavaScript流程日志方法 ====================

    /**
     * 记录JavaScript引擎初始化
     */
    public static void logJsEngineInit(String flowId, String siteKey, String api) {
        logVod(flowId, VodStage.JS_ENGINE_INIT, Level.INFO,
            String.format("初始化JavaScript引擎 [%s] API: %s", siteKey, api));
    }

    /**
     * 记录JavaScript引擎初始化成功
     */
    public static void logJsEngineInitSuccess(String flowId, String siteKey, long duration) {
        logVod(flowId, VodStage.JS_ENGINE_INIT, Level.INFO,
            String.format("JavaScript引擎初始化成功 [%s]，耗时: %dms", siteKey, duration));
    }

    /**
     * 记录JavaScript引擎初始化失败
     */
    public static void logJsEngineInitError(String flowId, String siteKey, Throwable error) {
        logVod(flowId, VodStage.JS_ENGINE_INIT, Level.ERROR,
            String.format("JavaScript引擎初始化失败 [%s]", siteKey), error);
    }

    /**
     * 记录JavaScript模块下载开始
     */
    public static void logJsModuleDownloadStart(String flowId, String moduleUrl) {
        logVod(flowId, VodStage.JS_MODULE_DOWNLOAD, Level.INFO,
            String.format("开始下载JavaScript模块: %s", moduleUrl));
    }

    /**
     * 记录JavaScript模块下载成功
     */
    public static void logJsModuleDownloadSuccess(String flowId, String moduleUrl, int contentLength, boolean fromCache) {
        logVod(flowId, VodStage.JS_MODULE_DOWNLOAD, Level.INFO,
            String.format("JavaScript模块下载成功: %s，大小: %d bytes，缓存: %s",
                moduleUrl, contentLength, fromCache ? "命中" : "未命中"));
    }

    /**
     * 记录JavaScript模块下载失败
     */
    public static void logJsModuleDownloadError(String flowId, String moduleUrl, Throwable error) {
        logVod(flowId, VodStage.JS_MODULE_DOWNLOAD, Level.ERROR,
            String.format("JavaScript模块下载失败: %s", moduleUrl), error);
    }

    /**
     * 记录JavaScript模块加载开始
     */
    public static void logJsModuleLoadStart(String flowId, String moduleName, String moduleType) {
        logVod(flowId, VodStage.JS_MODULE_LOAD, Level.INFO,
            String.format("开始加载JavaScript模块 [%s] 类型: %s", moduleName, moduleType));
    }

    /**
     * 记录JavaScript模块加载成功
     */
    public static void logJsModuleLoadSuccess(String flowId, String moduleName, long duration) {
        logVod(flowId, VodStage.JS_MODULE_LOAD, Level.INFO,
            String.format("JavaScript模块加载成功 [%s]，耗时: %dms", moduleName, duration));
    }

    /**
     * 记录JavaScript模块加载失败
     */
    public static void logJsModuleLoadError(String flowId, String moduleName, Throwable error) {
        logVod(flowId, VodStage.JS_MODULE_LOAD, Level.ERROR,
            String.format("JavaScript模块加载失败 [%s]", moduleName), error);
    }

    /**
     * 记录JavaScript函数调用开始
     */
    public static void logJsFunctionCallStart(String flowId, String siteKey, String functionName, Object... args) {
        String argsStr = args.length > 0 ? String.valueOf(args.length) + "个参数" : "无参数";
        logVod(flowId, VodStage.JS_FUNCTION_CALL, Level.INFO,
            String.format("调用JavaScript函数 [%s] %s(%s)", siteKey, functionName, argsStr));
    }

    /**
     * 记录JavaScript函数调用成功
     */
    public static void logJsFunctionCallSuccess(String flowId, String siteKey, String functionName, long duration, Object result) {
        String resultStr = result != null ? String.valueOf(result).length() + "字符" : "null";
        logVod(flowId, VodStage.JS_FUNCTION_CALL, Level.INFO,
            String.format("JavaScript函数调用成功 [%s] %s()，耗时: %dms，返回: %s",
                siteKey, functionName, duration, resultStr));
    }

    /**
     * 记录JavaScript函数调用失败
     */
    public static void logJsFunctionCallError(String flowId, String siteKey, String functionName, Throwable error) {
        logVod(flowId, VodStage.JS_FUNCTION_CALL, Level.ERROR,
            String.format("JavaScript函数调用失败 [%s] %s()", siteKey, functionName), error);
    }

    // ==================== 搜索流程日志方法 ====================

    /**
     * 记录打开搜索功能
     */
    public static void logSearchOpen(String flowId, String fromPage) {
        logVod(flowId, VodStage.SEARCH_OPEN, Level.INFO,
            String.format("打开搜索功能，来源页面: %s", fromPage));
    }

    /**
     * 记录搜索输入
     */
    public static void logSearchInput(String flowId, String keyword, int keywordLength) {
        logVod(flowId, VodStage.SEARCH_INPUT, Level.INFO,
            String.format("输入搜索关键词: %s，长度: %d字符", keyword, keywordLength));
    }

    /**
     * 记录开始执行搜索
     */
    public static void logSearchExecute(String flowId, String keyword, String siteKey) {
        logVod(flowId, VodStage.SEARCH_EXECUTE, Level.INFO,
            String.format("开始搜索 [%s] 关键词: %s", siteKey, keyword));
    }

    /**
     * 记录搜索结果
     */
    public static void logSearchResult(String flowId, String keyword, String siteKey, int resultCount, long duration) {
        logVod(flowId, VodStage.SEARCH_RESULT, Level.INFO,
            String.format("搜索完成 [%s] 关键词: %s，结果数: %d，耗时: %dms", siteKey, keyword, resultCount, duration));
    }

    /**
     * 记录搜索失败
     */
    public static void logSearchError(String flowId, String keyword, String siteKey, Throwable error) {
        logVod(flowId, VodStage.SEARCH_RESULT, Level.ERROR,
            String.format("搜索失败 [%s] 关键词: %s", siteKey, keyword), error);
    }

    /**
     * 记录选择搜索结果
     */
    public static void logSearchSelect(String flowId, String movieTitle, String siteKey, int position) {
        logVod(flowId, VodStage.SEARCH_SELECT, Level.INFO,
            String.format("选择搜索结果 [%s] 电影: %s，位置: %d", siteKey, movieTitle, position));
    }

    // ==================== 站点选择流程日志方法 ====================

    /**
     * 记录打开站点选择器
     */
    public static void logSiteSelectorOpen(String flowId, String currentSite) {
        logVod(flowId, VodStage.SITE_SELECTOR_OPEN, Level.INFO,
            String.format("打开站点选择器，当前站点: %s", currentSite));
    }

    /**
     * 记录站点列表加载
     */
    public static void logSiteSelectorLoad(String flowId, int siteCount, long duration) {
        logVod(flowId, VodStage.SITE_SELECTOR_LOAD, Level.INFO,
            String.format("站点列表加载完成，站点数: %d，耗时: %dms", siteCount, duration));
    }

    /**
     * 记录选择站点
     */
    public static void logSiteSelect(String flowId, String siteKey, String siteName, String siteType) {
        logVod(flowId, VodStage.SITE_SELECT, Level.INFO,
            String.format("选择站点 [%s] %s，类型: %s", siteKey, siteName, siteType));
    }

    /**
     * 记录确认站点选择
     */
    public static void logSiteConfirm(String flowId, String siteKey, String siteName, long switchDuration) {
        logVod(flowId, VodStage.SITE_CONFIRM, Level.INFO,
            String.format("确认站点选择 [%s] %s，切换耗时: %dms", siteKey, siteName, switchDuration));
    }

    /**
     * 记录站点选择失败
     */
    public static void logSiteSelectError(String flowId, String siteKey, Throwable error) {
        logVod(flowId, VodStage.SITE_CONFIRM, Level.ERROR,
            String.format("站点选择失败 [%s]", siteKey), error);
    }

    // ==================== 线路选择流程日志方法 ====================

    /**
     * 记录打开线路选择器
     */
    public static void logRouteSelectorOpen(String flowId, String movieTitle, int routeCount) {
        logVod(flowId, VodStage.ROUTE_SELECTOR_OPEN, Level.INFO,
            String.format("打开线路选择器，电影: %s，线路数: %d", movieTitle, routeCount));
    }

    /**
     * 记录线路列表加载
     */
    public static void logRouteSelectorLoad(String flowId, int routeCount, long duration) {
        logVod(flowId, VodStage.ROUTE_SELECTOR_LOAD, Level.INFO,
            String.format("线路列表加载完成，线路数: %d，耗时: %dms", routeCount, duration));
    }

    /**
     * 记录选择线路
     */
    public static void logRouteSelect(String flowId, String routeName, String routeUrl, int position) {
        logVod(flowId, VodStage.ROUTE_SELECT, Level.INFO,
            String.format("选择线路: %s，位置: %d，URL: %s", routeName, position, routeUrl));
    }

    /**
     * 记录确认线路选择
     */
    public static void logRouteConfirm(String flowId, String routeName, String routeUrl, long switchDuration) {
        logVod(flowId, VodStage.ROUTE_CONFIRM, Level.INFO,
            String.format("确认线路选择: %s，切换耗时: %dms，URL: %s", routeName, switchDuration, routeUrl));
    }

    // ==================== 电影详情流程日志方法 ====================

    /**
     * 记录点击电影
     */
    public static void logMovieClick(String flowId, String movieTitle, String siteKey, String fromPage) {
        // 添加直接的Android Log输出，确保日志能被看到
        Log.i("VOD_FLOW", String.format("[%s] [FlowID:%s] [MOVIE_CLICK] 点击电影 [%s] %s，来源: %s",
            getCurrentTime(), flowId, siteKey, movieTitle, fromPage));

        logVod(flowId, VodStage.MOVIE_CLICK, Level.INFO,
            String.format("点击电影 [%s] %s，来源: %s", siteKey, movieTitle, fromPage));
    }

    /**
     * 记录打开电影详情页
     */
    public static void logMovieDetailOpen(String flowId, String movieTitle, String movieId) {
        logVod(flowId, VodStage.MOVIE_DETAIL_OPEN, Level.INFO,
            String.format("打开电影详情页: %s，ID: %s", movieTitle, movieId));
    }

    /**
     * 记录电影详情加载完成
     */
    public static void logMovieDetailLoad(String flowId, String movieTitle, long duration, int episodeCount) {
        logVod(flowId, VodStage.MOVIE_DETAIL_LOAD, Level.INFO,
            String.format("电影详情加载完成: %s，耗时: %dms，集数: %d", movieTitle, duration, episodeCount));
    }

    /**
     * 记录电影详情加载失败
     */
    public static void logMovieDetailError(String flowId, String movieTitle, Throwable error) {
        logVod(flowId, VodStage.MOVIE_DETAIL_LOAD, Level.ERROR,
            String.format("电影详情加载失败: %s", movieTitle), error);
    }

    /**
     * 记录剧集列表加载
     */
    public static void logMovieEpisodesLoad(String flowId, String movieTitle, int episodeCount, long duration) {
        logVod(flowId, VodStage.MOVIE_EPISODES_LOAD, Level.INFO,
            String.format("剧集列表加载完成: %s，集数: %d，耗时: %dms", movieTitle, episodeCount, duration));
    }

    /**
     * 记录选择剧集
     */
    public static void logEpisodeSelect(String flowId, String movieTitle, String episodeName, int episodeIndex) {
        logVod(flowId, VodStage.EPISODE_SELECT, Level.INFO,
            String.format("选择剧集: %s - %s，索引: %d", movieTitle, episodeName, episodeIndex));
    }

    // ==================== 播放控制流程日志方法 ====================

    /**
     * 记录播放暂停
     */
    public static void logPlayPause(String flowId, String movieTitle, long currentPosition) {
        logVod(flowId, VodStage.PLAY_PAUSE, Level.INFO,
            String.format("播放暂停: %s，当前位置: %dms", movieTitle, currentPosition));
    }

    /**
     * 记录播放恢复
     */
    public static void logPlayResume(String flowId, String movieTitle, long currentPosition) {
        logVod(flowId, VodStage.PLAY_RESUME, Level.INFO,
            String.format("播放恢复: %s，当前位置: %dms", movieTitle, currentPosition));
    }

    /**
     * 记录播放停止
     */
    public static void logPlayStop(String flowId, String movieTitle, long totalDuration, long watchedDuration) {
        logVod(flowId, VodStage.PLAY_STOP, Level.INFO,
            String.format("播放停止: %s，总时长: %dms，观看时长: %dms", movieTitle, totalDuration, watchedDuration));
    }

    // ==================== 缓存机制流程日志方法 ====================

    /**
     * 记录缓存读取
     */
    public static void logCacheRead(String flowId, String cacheKey, String cacheType) {
        logVod(flowId, VodStage.CACHE_READ, Level.INFO,
            String.format("读取缓存 [%s] Key: %s", cacheType, cacheKey));
    }

    /**
     * 记录缓存命中
     */
    public static void logCacheHit(String flowId, String cacheKey, String cacheType, long cacheAge) {
        logVod(flowId, VodStage.CACHE_HIT, Level.INFO,
            String.format("缓存命中 [%s] Key: %s，缓存年龄: %dms", cacheType, cacheKey, cacheAge));
    }

    /**
     * 记录缓存未命中
     */
    public static void logCacheMiss(String flowId, String cacheKey, String cacheType) {
        logVod(flowId, VodStage.CACHE_MISS, Level.INFO,
            String.format("缓存未命中 [%s] Key: %s", cacheType, cacheKey));
    }

    /**
     * 记录缓存写入
     */
    public static void logCacheWrite(String flowId, String cacheKey, String cacheType, int dataSize) {
        logVod(flowId, VodStage.CACHE_WRITE, Level.INFO,
            String.format("写入缓存 [%s] Key: %s，数据大小: %d bytes", cacheType, cacheKey, dataSize));
    }

    /**
     * 记录缓存清理
     */
    public static void logCacheClear(String flowId, String cacheType, int clearedCount) {
        logVod(flowId, VodStage.CACHE_CLEAR, Level.INFO,
            String.format("清理缓存 [%s]，清理数量: %d", cacheType, clearedCount));
    }

    /**
     * 记录缓存过期
     */
    public static void logCacheExpire(String flowId, String cacheKey, String cacheType, long expireTime) {
        logVod(flowId, VodStage.CACHE_EXPIRE, Level.INFO,
            String.format("缓存过期 [%s] Key: %s，过期时间: %dms", cacheType, cacheKey, expireTime));
    }

    // ==================== 直播流程日志方法 ====================

    /**
     * 记录直播配置输入
     */
    public static void logLiveConfigInput(String flowId, String configUrl) {
        logLive(flowId, LiveStage.CONFIG_INPUT, Level.INFO, 
            String.format("用户输入直播配置URL: %s", configUrl));
    }

    /**
     * 记录直播配置下载
     */
    public static void logLiveConfigDownload(String flowId, String configUrl, boolean success, String result) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("直播配置下载成功: %s，大小: %d bytes", configUrl, result != null ? result.length() : 0) :
            String.format("直播配置下载失败: %s，错误: %s", configUrl, result);
        logLive(flowId, LiveStage.CONFIG_DOWNLOAD, level, message);
    }

    /**
     * 记录直播配置解析
     */
    public static void logLiveConfigParse(String flowId, String format, boolean success, int liveCount) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("直播配置解析成功 [%s]，直播源数: %d", format, liveCount) :
            String.format("直播配置解析失败 [%s]", format);
        logLive(flowId, LiveStage.CONFIG_PARSE, level, message);
    }

    /**
     * 记录直播源解析
     */
    public static void logLiveSourceParse(String flowId, String liveName, String format, boolean success, int groupCount, int channelCount) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("直播源解析成功 [%s] 格式:%s，分组数: %d，频道数: %d", liveName, format, groupCount, channelCount) :
            String.format("直播源解析失败 [%s] 格式:%s", liveName, format);
        logLive(flowId, LiveStage.LIVE_SOURCE_PARSE, level, message);
    }

    /**
     * 记录频道解析
     */
    public static void logLiveChannelParse(String flowId, String channelName, String groupName, int urlCount) {
        logLive(flowId, LiveStage.CHANNEL_PARSE, Level.INFO, 
            String.format("解析频道 [%s] 分组:%s，播放地址数: %d", channelName, groupName, urlCount));
    }

    /**
     * 记录EPG下载
     */
    public static void logLiveEpgDownload(String flowId, String epgUrl, boolean success, String result) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("EPG下载成功: %s，大小: %d bytes", epgUrl, result != null ? result.length() : 0) :
            String.format("EPG下载失败: %s，错误: %s", epgUrl, result);
        logLive(flowId, LiveStage.EPG_DOWNLOAD, level, message);
    }

    /**
     * 记录EPG解析
     */
    public static void logLiveEpgParse(String flowId, boolean success, int programCount) {
        Level level = success ? Level.INFO : Level.ERROR;
        String message = success ? 
            String.format("EPG解析成功，节目数: %d", programCount) :
            "EPG解析失败";
        logLive(flowId, LiveStage.EPG_PARSE, level, message);
    }

    /**
     * 记录频道选择
     */
    public static void logLiveChannelSelect(String flowId, String channelName, String groupName, int urlCount) {
        logLive(flowId, LiveStage.CHANNEL_SELECT, Level.INFO, 
            String.format("用户选择频道 [%s] 分组:%s，可用地址数: %d", channelName, groupName, urlCount));
    }

    /**
     * 记录播放地址选择
     */
    public static void logLiveUrlSelect(String flowId, String selectedUrl, int urlIndex, int totalUrls) {
        logLive(flowId, LiveStage.URL_SELECT, Level.INFO, 
            String.format("选择播放地址 [%d/%d]: %s", urlIndex + 1, totalUrls, selectedUrl));
    }

    /**
     * 记录直播播放器创建
     */
    public static void logLivePlayerCreate(String flowId, String playerType, String url) {
        logLive(flowId, LiveStage.PLAYER_CREATE, Level.INFO, 
            String.format("创建直播播放器 [%s] URL: %s", playerType, url));
    }

    /**
     * 记录直播播放开始
     */
    public static void logLivePlayerStart(String flowId, String channelName, String url) {
        logLive(flowId, LiveStage.PLAYER_START, Level.INFO, 
            String.format("开始播放直播 [%s]: %s", channelName, url));
    }

    /**
     * 记录直播播放成功
     */
    public static void logLivePlaySuccess(String flowId, String channelName, String url, long duration) {
        logLive(flowId, LiveStage.PLAY_SUCCESS, Level.INFO, 
            String.format("直播播放成功 [%s]: %s，耗时: %dms", channelName, url, duration));
    }

    /**
     * 记录直播播放错误
     */
    public static void logLivePlayError(String flowId, String channelName, String url, String errorCode, Throwable error) {
        logLive(flowId, LiveStage.PLAY_ERROR, Level.ERROR, 
            String.format("直播播放失败 [%s]: %s，错误码: %s", channelName, url, errorCode), error);
    }

    // ==================== 私有工具方法 ====================

    /**
     * 格式化日志消息
     */
    private static String formatMessage(String flowId, String stage, String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        return String.format("[%s] [FlowID:%s] [%s] %s", timestamp, flowId, stage, message);
    }

    /**
     * 根据级别记录日志
     */
    private static void logWithLevel(String tag, Level level, String message, Throwable throwable) {
        switch (level) {
            case DEBUG:
                Logger.t(tag).d(message);
                if (throwable != null) Logger.t(tag).d(throwable.toString());
                break;
            case INFO:
                Logger.t(tag).i(message);
                if (throwable != null) Logger.t(tag).i(throwable.toString());
                break;
            case WARN:
                Logger.t(tag).w(message);
                if (throwable != null) Logger.t(tag).w(throwable.toString());
                break;
            case ERROR:
                Logger.t(tag).e(message);
                if (throwable != null) Logger.t(tag).e(throwable.toString());
                break;
        }
        
        // 同时输出到系统日志
        switch (level) {
            case DEBUG:
                Log.d(tag, message, throwable);
                break;
            case INFO:
                Log.i(tag, message, throwable);
                break;
            case WARN:
                Log.w(tag, message, throwable);
                break;
            case ERROR:
                Log.e(tag, message, throwable);
                break;
        }
    }
}
