package com.fongmi.android.tv.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.fongmi.android.tv.api.LiveParser;
import com.fongmi.android.tv.api.config.LiveConfig;
import com.fongmi.android.tv.bean.Channel;
import com.fongmi.android.tv.bean.Config;
import com.fongmi.android.tv.bean.Group;
import com.fongmi.android.tv.bean.Live;
import com.fongmi.android.tv.impl.Callback;
import com.fongmi.android.tv.player.PlayerLogger;
import com.fongmi.android.tv.utils.FlowLogger;

import java.util.List;

/**
 * 带日志跟踪的直播Activity示例
 * 展示如何在直播系统中集成完整的日志跟踪功能
 */
public class LoggedLiveActivity extends AppCompatActivity {
    
    private String currentFlowId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 示例：用户输入直播配置并开始完整的直播流程
        startLiveFlowWithLogging();
    }
    
    /**
     * 开始带日志跟踪的完整直播流程
     */
    private void startLiveFlowWithLogging() {
        // 模拟用户输入直播配置URL
        String configUrl = "https://example.com/live_config.m3u";
        
        // 创建配置对象
        Config config = new Config();
        config.setUrl(configUrl);
        config.setType(1); // 直播配置类型
        
        // 开始配置加载流程（自动生成FlowID并记录日志）
        LiveConfig.load(config, new Callback() {
            @Override
            public void success() {
                // 配置加载成功，继续直播源解析流程
                currentFlowId = LiveConfig.getCurrentFlowId();
                startLiveSourceParsing();
            }
            
            @Override
            public void error(String msg) {
                FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.CONFIG_PARSE, 
                    FlowLogger.Level.ERROR, "直播配置加载失败: " + msg);
            }
        });
    }
    
    /**
     * 开始直播源解析流程
     */
    private void startLiveSourceParsing() {
        List<Live> lives = LiveConfig.get().getLives();
        if (lives.isEmpty()) {
            FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.LIVE_SOURCE_PARSE, 
                FlowLogger.Level.ERROR, "未找到直播源");
            return;
        }
        
        // 解析第一个直播源
        Live firstLive = lives.get(0);
        parseLiveSource(firstLive);
    }
    
    /**
     * 解析直播源
     */
    private void parseLiveSource(Live live) {
        new Thread(() -> {
            try {
                // 使用带日志跟踪的LiveParser
                LiveParser.start(live, currentFlowId);
                
                // 解析完成后，模拟EPG下载
                runOnUiThread(() -> downloadEpg(live));
                
            } catch (Exception e) {
                FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.LIVE_SOURCE_PARSE, 
                    FlowLogger.Level.ERROR, "直播源解析异常", e);
            }
        }).start();
    }
    
    /**
     * 下载EPG节目单
     */
    private void downloadEpg(Live live) {
        new Thread(() -> {
            try {
                // 模拟EPG下载
                String epgUrl = "https://example.com/epg.xml";
                FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.EPG_DOWNLOAD, 
                    FlowLogger.Level.INFO, String.format("开始下载EPG: %s", epgUrl));
                
                // 模拟下载过程
                Thread.sleep(1000);
                String epgContent = downloadEpgContent(epgUrl);
                
                FlowLogger.logLiveEpgDownload(currentFlowId, epgUrl, true, epgContent);
                
                // 解析EPG
                parseEpg(epgContent, live);
                
            } catch (Exception e) {
                FlowLogger.logLiveEpgDownload(currentFlowId, "https://example.com/epg.xml", false, e.getMessage());
            }
        }).start();
    }
    
    /**
     * 解析EPG节目单
     */
    private void parseEpg(String epgContent, Live live) {
        try {
            FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.EPG_PARSE, 
                FlowLogger.Level.INFO, "开始解析EPG节目单");
            
            // 模拟EPG解析
            int programCount = parseEpgContent(epgContent);
            
            FlowLogger.logLiveEpgParse(currentFlowId, true, programCount);
            
            // EPG解析完成后，模拟用户选择频道
            runOnUiThread(() -> simulateChannelSelection(live));
            
        } catch (Exception e) {
            FlowLogger.logLiveEpgParse(currentFlowId, false, 0);
        }
    }
    
    /**
     * 模拟用户选择频道
     */
    private void simulateChannelSelection(Live live) {
        List<Group> groups = live.getGroups();
        if (groups.isEmpty()) {
            FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.CHANNEL_SELECT, 
                FlowLogger.Level.ERROR, "未找到频道分组");
            return;
        }
        
        // 选择第一个分组的第一个频道
        Group firstGroup = groups.get(0);
        List<Channel> channels = firstGroup.getChannel();
        
        if (channels.isEmpty()) {
            FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.CHANNEL_SELECT, 
                FlowLogger.Level.ERROR, "分组中未找到频道");
            return;
        }
        
        Channel selectedChannel = channels.get(0);
        
        // 记录频道选择
        FlowLogger.logLiveChannelSelect(currentFlowId, selectedChannel.getName(), 
            firstGroup.getName(), selectedChannel.getUrls().size());
        
        // 选择播放地址
        selectPlayUrl(selectedChannel);
    }
    
    /**
     * 选择播放地址
     */
    private void selectPlayUrl(Channel channel) {
        List<String> urls = channel.getUrls();
        if (urls.isEmpty()) {
            FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.URL_SELECT, 
                FlowLogger.Level.ERROR, "频道没有可用的播放地址");
            return;
        }
        
        // 选择第一个地址
        String selectedUrl = urls.get(0);
        
        // 记录地址选择
        FlowLogger.logLiveUrlSelect(currentFlowId, selectedUrl, 0, urls.size());
        
        // 开始播放
        startLivePlayer(channel, selectedUrl);
    }
    
    /**
     * 开始直播播放
     */
    private void startLivePlayer(Channel channel, String playUrl) {
        try {
            // 记录播放器创建
            String playerType = "ExoPlayer"; // 根据实际情况获取
            PlayerLogger.logLivePlayerCreate(currentFlowId, playerType, playUrl);
            
            // 模拟播放器创建
            createLivePlayer(playUrl);
            
            // 记录播放开始
            PlayerLogger.logLivePlayerStart(currentFlowId, channel.getName(), playUrl);
            
            // 模拟播放成功（实际应该在播放器回调中调用）
            simulateLivePlaybackSuccess(channel, playUrl);
            
        } catch (Exception e) {
            PlayerLogger.logLivePlayError(currentFlowId, channel.getName(), playUrl, "PLAYER_CREATE_ERROR", e);
        }
    }
    
    /**
     * 模拟直播播放成功
     */
    private void simulateLivePlaybackSuccess(Channel channel, String playUrl) {
        // 在实际应用中，这应该在播放器的onReady或类似回调中调用
        new Thread(() -> {
            try {
                Thread.sleep(1500); // 模拟播放器准备时间
                runOnUiThread(() -> {
                    PlayerLogger.logLivePlaySuccess(currentFlowId, channel.getName(), playUrl);
                    FlowLogger.logLive(currentFlowId, "FLOW_COMPLETE", FlowLogger.Level.INFO, 
                        String.format("直播流程完成，从配置输入到播放成功 [%s]", channel.getName()));
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * 模拟播放地址切换（直播特有功能）
     */
    private void switchPlayUrl(Channel channel, int newUrlIndex) {
        List<String> urls = channel.getUrls();
        if (newUrlIndex >= 0 && newUrlIndex < urls.size()) {
            String newUrl = urls.get(newUrlIndex);
            
            FlowLogger.logLive(currentFlowId, "URL_SWITCH", FlowLogger.Level.INFO, 
                String.format("切换播放地址 [%s] [%d/%d]: %s", 
                    channel.getName(), newUrlIndex + 1, urls.size(), newUrl));
            
            // 重新开始播放
            startLivePlayer(channel, newUrl);
        }
    }
    
    /**
     * 模拟频道切换（直播特有功能）
     */
    private void switchChannel(Channel newChannel) {
        FlowLogger.logLive(currentFlowId, "CHANNEL_SWITCH", FlowLogger.Level.INFO, 
            String.format("切换频道: %s -> %s", "当前频道", newChannel.getName()));
        
        // 重新选择播放地址
        selectPlayUrl(newChannel);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 模拟EPG内容下载
     */
    private String downloadEpgContent(String epgUrl) throws InterruptedException {
        // 模拟网络下载
        Thread.sleep(500);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tv>...</tv>";
    }
    
    /**
     * 模拟EPG内容解析
     */
    private int parseEpgContent(String epgContent) {
        // 模拟解析过程，返回节目数量
        return 150;
    }
    
    /**
     * 创建直播播放器（简化实现）
     */
    private void createLivePlayer(String url) {
        // 实际实现应该创建ExoPlayer或其他播放器
        FlowLogger.logLive(currentFlowId, FlowLogger.LiveStage.PLAYER_CREATE, 
            FlowLogger.Level.INFO, "直播播放器创建成功");
    }
    
    /**
     * 处理播放错误（直播特有的错误处理）
     */
    private void handlePlaybackError(Channel channel, String url, String errorCode, Throwable error) {
        PlayerLogger.logLivePlayError(currentFlowId, channel.getName(), url, errorCode, error);
        
        // 尝试切换到下一个播放地址
        List<String> urls = channel.getUrls();
        int currentIndex = urls.indexOf(url);
        if (currentIndex >= 0 && currentIndex < urls.size() - 1) {
            FlowLogger.logLive(currentFlowId, "AUTO_SWITCH", FlowLogger.Level.INFO, 
                "播放失败，自动切换到下一个地址");
            switchPlayUrl(channel, currentIndex + 1);
        } else {
            FlowLogger.logLive(currentFlowId, "PLAY_FAILED", FlowLogger.Level.ERROR, 
                "所有播放地址都失败");
        }
    }
}
