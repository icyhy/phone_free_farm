#!/bin/bash

echo "正在检查连接的Android设备..."
adb devices

echo "正在部署Phone Focus Farm APK到手机..."
adb install -r ./app/build/outputs/apk/debug/app-debug.apk

if [ $? -eq 0 ]; then
    echo "APK部署成功！"
    echo "正在启动应用..."
    adb shell monkey -p com.phonefocusfarm -c android.intent.category.LAUNCHER 1
else
    echo "APK部署失败，请检查设备连接和USB调试设置"
fi