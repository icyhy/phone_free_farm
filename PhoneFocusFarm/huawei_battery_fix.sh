#!/bin/bash

echo "华为Mate 40 Pro电池优化设置指南"
echo "================================"
echo ""
echo "由于华为EMUI系统的特殊性，请按以下步骤手动设置："
echo ""
echo "步骤1：手动进入电池优化设置"
echo "   设置 → 应用 → 应用启动管理 → 找到'Phone Focus Farm'"
echo "   关闭'自动管理'，手动允许："
echo "   ✓ 允许自启动"
echo "   ✓ 允许关联启动" 
echo "   ✓ 允许后台活动"
echo ""
echo "步骤2：电池优化设置"
echo "   设置 → 电池 → 应用启动管理 → 找到'Phone Focus Farm'"
echo "   设置为'手动管理'并允许所有选项"
echo ""
echo "步骤3：后台保护"
echo "   设置 → 电池 → 应用保护 → 找到'Phone Focus Farm'"
echo "   设置为'不保护'或添加到白名单"
echo ""
echo "步骤4：通知管理"
echo "   设置 → 通知 → 应用通知 → 找到'Phone Focus Farm'"
echo "   允许所有通知类型"
echo ""
echo "完成以上设置后，按任意键继续..."
read -n 1 -s -r -p ""

echo "正在重启应用以应用新设置..."
adb shell am force-stop com.phonefocusfarm
sleep 2
adb shell monkey -p com.phonefocusfarm -c android.intent.category.LAUNCHER 1

echo "设置完成！应用应该可以正常运行了。"