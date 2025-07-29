#!/bin/bash

# DRMP开发环境启动脚本

echo "🚀 启动DRMP开发环境..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行，请先启动Docker"
    exit 1
fi

# 启动基础设施服务
echo "📦 启动基础设施服务 (MySQL, Redis, Nacos等)..."
docker-compose up -d mysql-master redis nacos rocketmq-nameserver rocketmq-broker elasticsearch minio

# 等待服务启动
echo "⏳ 等待基础服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose ps

echo "✅ 基础设施服务启动完成！"
echo ""
echo "🌐 服务访问地址:"
echo "   - Nacos控制台: http://localhost:8848/nacos (用户名/密码: nacos/nacos)"
echo "   - MinIO控制台: http://localhost:9001 (用户名/密码: drmp/drmp123456)"
echo "   - Elasticsearch: http://localhost:9200"
echo ""
echo "📝 下一步:"
echo "   1. 启动后端服务: cd backend && mvn spring-boot:run"
echo "   2. 启动前端服务: cd frontend && npm run dev"
echo ""