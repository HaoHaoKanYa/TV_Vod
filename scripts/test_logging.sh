#!/bin/bash

# TV-Vod 日志跟踪系统测试脚本

echo "🚀 TV-Vod 日志跟踪系统测试"
echo "================================"

# 检查ADB连接
echo "📱 检查设备连接..."
if ! adb devices | grep -q "device$"; then
    echo "❌ 未检测到Android设备，请确保："
    echo "   1. 设备已连接并开启USB调试"
    echo "   2. 已安装ADB工具"
    exit 1
fi
echo "✅ 设备连接正常"

# 检查应用是否安装
PACKAGE_NAME="com.fongmi.android.tv"
echo "📦 检查应用安装状态..."
if ! adb shell pm list packages | grep -q "$PACKAGE_NAME"; then
    echo "❌ TV-Vod应用未安装，请先编译并安装应用："
    echo "   ./gradlew assembleDebug"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
    exit 1
fi
echo "✅ 应用已安装"

# 清空日志缓冲区
echo "🧹 清空日志缓冲区..."
adb logcat -c

# 设置日志级别
echo "⚙️  设置日志级别..."
adb shell setprop log.tag.VOD_FLOW VERBOSE
adb shell setprop log.tag.LIVE_FLOW VERBOSE
adb shell setprop log.tag.FlowLogger VERBOSE

echo ""
echo "🎯 选择测试模式："
echo "1) 测试点播系统日志"
echo "2) 测试直播系统日志"
echo "3) 监控所有日志"
echo "4) 分析现有日志"
echo ""
read -p "请选择 (1-4): " choice

case $choice in
    1)
        echo "🎬 启动点播系统日志测试..."
        echo "正在启动LoggedVodActivity..."
        adb shell am start -n "$PACKAGE_NAME/.ui.activity.LoggedVodActivity"
        
        echo ""
        echo "📊 实时监控点播日志 (按Ctrl+C停止):"
        echo "================================"
        adb logcat | grep -E "(VOD_FLOW|FlowID.*VOD)" --line-buffered | while read line; do
            timestamp=$(date '+%H:%M:%S')
            echo "[$timestamp] $line"
        done
        ;;
        
    2)
        echo "📺 启动直播系统日志测试..."
        echo "正在启动LoggedLiveActivity..."
        adb shell am start -n "$PACKAGE_NAME/.ui.activity.LoggedLiveActivity"
        
        echo ""
        echo "📊 实时监控直播日志 (按Ctrl+C停止):"
        echo "================================"
        adb logcat | grep -E "(LIVE_FLOW|FlowID.*LIVE)" --line-buffered | while read line; do
            timestamp=$(date '+%H:%M:%S')
            echo "[$timestamp] $line"
        done
        ;;
        
    3)
        echo "👀 监控所有TV-Vod日志..."
        echo ""
        echo "📊 实时监控所有日志 (按Ctrl+C停止):"
        echo "================================"
        adb logcat | grep -E "(VOD_FLOW|LIVE_FLOW|FlowID)" --line-buffered | while read line; do
            timestamp=$(date '+%H:%M:%S')
            
            # 根据日志类型添加颜色标记
            if echo "$line" | grep -q "ERROR"; then
                echo -e "[$timestamp] \033[31m$line\033[0m"  # 红色
            elif echo "$line" | grep -q "PLAY_SUCCESS"; then
                echo -e "[$timestamp] \033[32m$line\033[0m"  # 绿色
            elif echo "$line" | grep -q "CONFIG_"; then
                echo -e "[$timestamp] \033[34m$line\033[0m"  # 蓝色
            else
                echo "[$timestamp] $line"
            fi
        done
        ;;
        
    4)
        echo "📈 分析现有日志..."
        echo ""
        
        # 统计流程数量
        echo "🔢 流程统计:"
        echo "--------------------------------"
        VOD_FLOWS=$(adb logcat -d | grep "VOD_FLOW" | grep "CONFIG_INPUT" | wc -l)
        LIVE_FLOWS=$(adb logcat -d | grep "LIVE_FLOW" | grep "CONFIG_INPUT" | wc -l)
        echo "点播流程数: $VOD_FLOWS"
        echo "直播流程数: $LIVE_FLOWS"
        echo ""
        
        # 成功率统计
        echo "📊 成功率统计:"
        echo "--------------------------------"
        TOTAL_PLAYS=$(adb logcat -d | grep -c "PLAYER_START")
        SUCCESS_PLAYS=$(adb logcat -d | grep -c "PLAY_SUCCESS")
        if [ $TOTAL_PLAYS -gt 0 ]; then
            SUCCESS_RATE=$(echo "scale=1; $SUCCESS_PLAYS*100/$TOTAL_PLAYS" | bc 2>/dev/null || echo "0")
            echo "总播放次数: $TOTAL_PLAYS"
            echo "成功次数: $SUCCESS_PLAYS"
            echo "成功率: ${SUCCESS_RATE}%"
        else
            echo "暂无播放记录"
        fi
        echo ""
        
        # 错误统计
        echo "❌ 错误统计:"
        echo "--------------------------------"
        ERROR_COUNT=$(adb logcat -d | grep -E "(VOD_FLOW|LIVE_FLOW)" | grep -c "ERROR")
        if [ $ERROR_COUNT -gt 0 ]; then
            echo "总错误数: $ERROR_COUNT"
            echo ""
            echo "错误类型分布:"
            adb logcat -d | grep -E "(VOD_FLOW|LIVE_FLOW)" | grep "ERROR" | \
                sed 's/.*\[/[/' | sed 's/\].*/]/' | sort | uniq -c | sort -nr | head -5
        else
            echo "暂无错误记录"
        fi
        echo ""
        
        # 性能统计
        echo "⚡ 性能统计:"
        echo "--------------------------------"
        CONFIG_TIMES=$(adb logcat -d | grep "CONFIG_DOWNLOAD.*耗时" | sed 's/.*耗时: //' | sed 's/ms//')
        if [ -n "$CONFIG_TIMES" ]; then
            AVG_CONFIG_TIME=$(echo "$CONFIG_TIMES" | awk '{sum+=$1; count++} END {if(count>0) print sum/count; else print 0}')
            echo "配置下载平均耗时: ${AVG_CONFIG_TIME}ms"
        fi
        
        PLAY_TIMES=$(adb logcat -d | grep "PLAY_SUCCESS.*耗时" | sed 's/.*耗时: //' | sed 's/ms//')
        if [ -n "$PLAY_TIMES" ]; then
            AVG_PLAY_TIME=$(echo "$PLAY_TIMES" | awk '{sum+=$1; count++} END {if(count>0) print sum/count; else print 0}')
            echo "播放启动平均耗时: ${AVG_PLAY_TIME}ms"
        fi
        
        echo ""
        echo "💾 完整日志已保存到: tv_vod_analysis.log"
        adb logcat -d | grep -E "(VOD_FLOW|LIVE_FLOW|FlowID)" > tv_vod_analysis.log
        ;;
        
    *)
        echo "❌ 无效选择"
        exit 1
        ;;
esac

echo ""
echo "✅ 测试完成！"
echo ""
echo "💡 提示："
echo "   - 日志文件保存在当前目录"
echo "   - 可以使用 'adb logcat | grep FlowID' 继续监控"
echo "   - 查看完整使用指南: ZMD/如何使用日志跟踪系统.md"
