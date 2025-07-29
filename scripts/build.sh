#!/bin/bash

# DRMP项目构建脚本

echo "🔨 开始构建DRMP项目..."

# 检查Java版本
java_version=$(java -version 2>&1 | grep "version" | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt "17" ]; then
    echo "❌ 需要Java 17或更高版本，当前版本: $java_version"
    exit 1
fi

# 检查Node版本
node_version=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$node_version" -lt "18" ]; then
    echo "❌ 需要Node.js 18或更高版本，当前版本: $(node -v)"
    exit 1
fi

# 构建后端
echo "🏗️  构建后端服务..."
cd backend
mvn clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ 后端构建失败"
    exit 1
fi
cd ..

# 构建前端
echo "🎨 构建前端应用..."
cd frontend
npm ci
npm run build
if [ $? -ne 0 ]; then
    echo "❌ 前端构建失败"
    exit 1
fi
cd ..

echo "✅ 项目构建完成！"
echo ""
echo "📦 构建产物:"
echo "   - 后端: backend/target/"
echo "   - 前端: frontend/dist/"
echo ""