#!/bin/bash

# 简单的编译检查脚本
echo "开始检查Android项目编译..."

# 检查基本的Kotlin语法错误
echo "检查Kotlin语法..."
find app/src -name "*.kt" -exec kotlinc -script -Xcheck-phase {} \; 2>&1 | head -20

echo "检查项目结构..."
# 检查关键文件是否存在
key_files=(
    "app/build.gradle"
    "app/src/main/AndroidManifest.xml"
    "app/src/main/java/com/phonefocusfarm/MainActivity.kt"
)

for file in "${key_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file 存在"
    else
        echo "❌ $file 不存在"
    fi
done

echo "检查依赖配置..."
# 检查gradle配置
if grep -q "implementation" app/build.gradle; then
    echo "✅ 依赖配置正常"
else
    echo "❌ 依赖配置可能有问题"
fi

echo "基础检查完成！"