package com.fongmi.android.tv.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.fongmi.android.tv.api.config.VodConfig;
import com.fongmi.android.tv.bean.Config;
import com.fongmi.android.tv.bean.Site;
import com.fongmi.android.tv.impl.Callback;
import com.fongmi.android.tv.player.PlayerLogger;
import com.fongmi.android.tv.utils.FlowLogger;
import com.github.catvod.crawler.Spider;

/**
 * 带日志跟踪的点播Activity示例
 * 展示如何在实际应用中集成完整的日志跟踪功能
 */
public class LoggedVodActivity extends AppCompatActivity {
    
    private String currentFlowId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 示例：用户输入配置并开始完整的点播流程
        startVodFlowWithLogging();
    }
    
    /**
     * 开始带日志跟踪的完整点播流程
     */
    private void startVodFlowWithLogging() {
        // 模拟用户输入配置URL
        String configUrl = "https://example.com/vod_config.json";
        
        // 创建配置对象
        Config config = new Config();
        config.setUrl(configUrl);
        
        // 开始配置加载流程（自动生成FlowID并记录日志）
        VodConfig.load(config, new Callback() {
            @Override
            public void success() {
                // 配置加载成功，继续内容获取流程
                currentFlowId = VodConfig.getCurrentFlowId();
                startContentFlow();
            }
            
            @Override
            public void error(String msg) {
                FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.CONFIG_PARSE, 
                    FlowLogger.Level.ERROR, "配置加载失败: " + msg);
            }
        });
    }
    
    /**
     * 开始内容获取流程
     */
    private void startContentFlow() {
        Site homeSite = VodConfig.get().getHome();
        if (homeSite == null) {
            FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.SITE_INIT, 
                FlowLogger.Level.ERROR, "未找到首页站点");
            return;
        }
        
        // 获取带日志跟踪的Spider实例
        Spider spider = homeSite.spider(currentFlowId);
        
        // 1. 获取首页内容
        getHomeContent(spider);
    }
    
    /**
     * 获取首页内容
     */
    private void getHomeContent(Spider spider) {
        new Thread(() -> {
            try {
                // SpiderWrapper会自动记录日志
                String homeContent = spider.homeContent(true);
                
                // 解析首页内容后，继续获取分类内容
                runOnUiThread(() -> getCategoryContent(spider));
                
            } catch (Exception e) {
                // 错误已在SpiderWrapper中记录
                FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.HOME_CONTENT, 
                    FlowLogger.Level.ERROR, "首页内容获取异常", e);
            }
        }).start();
    }
    
    /**
     * 获取分类内容
     */
    private void getCategoryContent(Spider spider) {
        new Thread(() -> {
            try {
                // 模拟用户选择分类
                String tid = "1"; // 电影分类
                String pg = "1";  // 第一页
                
                String categoryContent = spider.categoryContent(tid, pg, true, null);
                
                // 解析分类内容后，继续获取详情
                runOnUiThread(() -> getDetailContent(spider));
                
            } catch (Exception e) {
                FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.CATEGORY_CONTENT, 
                    FlowLogger.Level.ERROR, "分类内容获取异常", e);
            }
        }).start();
    }
    
    /**
     * 获取视频详情
     */
    private void getDetailContent(Spider spider) {
        new Thread(() -> {
            try {
                // 模拟用户点击视频
                String vodId = "12345";
                
                String detailContent = spider.detailContent(java.util.Arrays.asList(vodId));
                
                // 解析详情内容后，继续获取播放地址
                runOnUiThread(() -> getPlayerContent(spider));
                
            } catch (Exception e) {
                FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.DETAIL_CONTENT, 
                    FlowLogger.Level.ERROR, "详情内容获取异常", e);
            }
        }).start();
    }
    
    /**
     * 获取播放地址
     */
    private void getPlayerContent(Spider spider) {
        new Thread(() -> {
            try {
                // 模拟用户选择播放
                String flag = "m3u8";
                String id = "episode1";
                
                String playerContent = spider.playerContent(flag, id, VodConfig.get().getFlags());
                
                // 解析播放地址后，开始播放
                runOnUiThread(() -> startPlayer(extractPlayUrl(playerContent)));
                
            } catch (Exception e) {
                FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.PLAYER_CONTENT, 
                    FlowLogger.Level.ERROR, "播放地址获取异常", e);
            }
        }).start();
    }
    
    /**
     * 开始播放
     */
    private void startPlayer(String playUrl) {
        if (playUrl == null || playUrl.isEmpty()) {
            FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.PARSE_URL, 
                FlowLogger.Level.ERROR, "播放地址为空");
            return;
        }
        
        // 检查是否需要二次解析
        if (needParse(playUrl)) {
            parseUrl(playUrl, parsedUrl -> {
                if (parsedUrl != null) {
                    FlowLogger.logVodParseUrl(currentFlowId, "JSON", playUrl, parsedUrl, true);
                    createAndStartPlayer(parsedUrl);
                } else {
                    FlowLogger.logVodParseUrl(currentFlowId, "JSON", playUrl, null, false);
                }
            });
        } else {
            createAndStartPlayer(playUrl);
        }
    }
    
    /**
     * 创建并启动播放器
     */
    private void createAndStartPlayer(String playUrl) {
        try {
            // 记录播放器创建
            String playerType = "ExoPlayer"; // 根据实际情况获取
            PlayerLogger.logVodPlayerCreate(currentFlowId, playerType, playUrl);
            
            // 模拟播放器创建和启动
            createPlayer(playUrl);
            
            // 记录播放开始
            PlayerLogger.logVodPlayerStart(currentFlowId, playUrl);
            
            // 模拟播放成功（实际应该在播放器回调中调用）
            simulatePlaybackSuccess(playUrl);
            
        } catch (Exception e) {
            PlayerLogger.logVodPlayError(currentFlowId, playUrl, "PLAYER_CREATE_ERROR", e);
        }
    }
    
    /**
     * 模拟播放成功
     */
    private void simulatePlaybackSuccess(String playUrl) {
        // 在实际应用中，这应该在播放器的onReady或类似回调中调用
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 模拟播放器准备时间
                runOnUiThread(() -> {
                    PlayerLogger.logVodPlaySuccess(currentFlowId, playUrl);
                    FlowLogger.logVod(currentFlowId, "FLOW_COMPLETE", FlowLogger.Level.INFO, 
                        "点播流程完成，从配置输入到播放成功");
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 从播放内容中提取播放URL（简化实现）
     */
    private String extractPlayUrl(String playerContent) {
        // 实际实现应该解析JSON并提取URL
        return "https://example.com/video.m3u8";
    }
    
    /**
     * 检查是否需要二次解析
     */
    private boolean needParse(String url) {
        // 简化实现，实际应该根据解析器配置判断
        return url.contains("need_parse");
    }
    
    /**
     * URL解析
     */
    private void parseUrl(String url, ParseCallback callback) {
        new Thread(() -> {
            try {
                // 模拟解析过程
                Thread.sleep(500);
                String parsedUrl = "https://example.com/parsed_video.m3u8";
                runOnUiThread(() -> callback.onResult(parsedUrl));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                runOnUiThread(() -> callback.onResult(null));
            }
        }).start();
    }
    
    /**
     * 创建播放器（简化实现）
     */
    private void createPlayer(String url) {
        // 实际实现应该创建ExoPlayer或其他播放器
        FlowLogger.logVod(currentFlowId, FlowLogger.VodStage.PLAYER_CREATE, 
            FlowLogger.Level.INFO, "播放器创建成功");
    }
    
    /**
     * 解析回调接口
     */
    private interface ParseCallback {
        void onResult(String parsedUrl);
    }
}


