#!/bin/bash

echo "=== Android项目编译检查 ==="
echo

# 检查Android清单文件
echo "1. 检查AndroidManifest.xml..."
if [ -f "app/src/main/AndroidManifest.xml" ]; then
    echo "✅ AndroidManifest.xml 存在"
    # 检查基本必需的权限和配置
    if grep -q "android:name=\".MainActivity\"" app/src/main/AndroidManifest.xml; then
        echo "✅ MainActivity已声明"
    else
        echo "⚠️  MainActivity声明可能有问题"
    fi
else
    echo "❌ AndroidManifest.xml 不存在"
fi

echo

# 检查主要Kotlin文件
echo "2. 检查主要Kotlin文件..."
kt_files=(
    "app/src/main/java/com/phonefocusfarm/MainActivity.kt"
    "app/src/main/java/com/phonefocusfarm/ui/components/AnimationManager.kt"
    "app/src/main/java/com/phonefocusfarm/ui/components/SoundManager.kt"
    "app/src/main/java/com/phonefocusfarm/ui/components/farm3d/EnhancedFarm3DScreen.kt"
)

for file in "${kt_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file 存在"
        # 检查基本的语法结构
        if grep -q "package " "$file"; then
            echo "  ✅ 包声明正常"
        else
            echo "  ❌ 缺少包声明"
        fi
        
        if grep -q "import " "$file"; then
            echo "  ✅ 有导入语句"
        else
            echo "  ⚠️  可能没有导入语句"
        fi
    else
        echo "❌ $file 不存在"
    fi
done

echo

# 检查资源文件
echo "3. 检查资源文件..."
res_dirs=(
    "app/src/main/res/values"
    "app/src/main/res/drawable"
    "app/src/main/res/layout"
)

for dir in "${res_dirs[@]}"; do
    if [ -d "$dir" ]; then
        echo "✅ $dir 目录存在"
    else
        echo "⚠️  $dir 目录不存在"
    fi
done

echo

# 检查Gradle配置
echo "4. 检查Gradle配置..."
if grep -q "android {" app/build.gradle; then
    echo "✅ Android插件配置存在"
else
    echo "❌ Android插件配置缺失"
fi

if grep -q "compileSdk" app/build.gradle; then
    echo "✅ compileSdk配置存在"
else
    echo "❌ compileSdk配置缺失"
fi

if grep -q "applicationId" app/build.gradle; then
    echo "✅ applicationId配置存在"
else
    echo "❌ applicationId配置缺失"
fi

echo

# 检查OpenGL ES配置
echo "5. 检查OpenGL ES和3D功能配置..."
if grep -q "android.opengl" app/src/main/java/com/phonefocusfarm/ui/components/farm3d/EnhancedFarm3DScreen.kt; then
    echo "✅ OpenGL ES导入存在"
else
    echo "❌ OpenGL ES导入缺失"
fi

echo
echo "=== 检查完成 ==="
echo
echo "注意：这只是基础检查，实际编译需要完整的Android开发环境。"
echo "建议："
echo "1. 安装Android Studio"
echo "2. 配置Android SDK"
echo "3. 安装必要的构建工具"
echo "4. 使用Android Studio进行完整构建"