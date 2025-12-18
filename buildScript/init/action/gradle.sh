#!/bin/bash

# 获取当前环境安装的 NDK 版本（取第一个找到的）
NDK_VER=$(ls -1 ${ANDROID_HOME}/ndk | head -n 1)

echo "sdk.dir=${ANDROID_HOME}" > local.properties
# 动态写入检测到的 NDK 版本，而不是硬编码 25.0.8775105
if [ ! -z "$NDK_VER" ]; then
    echo "ndk.dir=${ANDROID_HOME}/ndk/${NDK_VER}" >> local.properties
else
    # 如果没找到，尝试让 Gradle 自己决定（或者你可以指定一个默认值）
    echo "ndk.dir=${ANDROID_HOME}/ndk-bundle" >> local.properties
fi

# 保持原有逻辑
export LOCAL_PROPERTIES=""
