#!/bin/bash

set -e

DIR=app/src/main/assets/sing-box
rm -rf $DIR
mkdir -p $DIR
cd $DIR

# [已修改] 注释掉 API 获取函数，避免触发 GitHub Rate Limit 导致 404
# get_latest_release() {
#   curl --silent "https://api.github.com/repos/$1/releases/latest" | # Get latest release from GitHub api
#     grep '"tag_name":' |                                            # Get tag line
#     sed -E 's/.*"([^"]+)".*/\1/'                                    # Pluck JSON value
# }

####
# VERSION_GEOIP=`get_latest_release "SagerNet/sing-geoip"`
VERSION_GEOIP="20251212" # [已修改] 硬编码版本号 (SagerNet 使用日期作为版本)
echo VERSION_GEOIP=$VERSION_GEOIP
echo -n $VERSION_GEOIP > geoip.version.txt
curl -fLSsO https://github.com/SagerNet/sing-geoip/releases/download/$VERSION_GEOIP/geoip.db
xz -9 geoip.db

####
# VERSION_GEOSITE=`get_latest_release "SagerNet/sing-geosite"`
VERSION_GEOSITE="20251212" # [已修改] 硬编码版本号
echo VERSION_GEOSITE=$VERSION_GEOSITE
echo -n $VERSION_GEOSITE > geosite.version.txt
curl -fLSsO https://github.com/SagerNet/sing-geosite/releases/download/$VERSION_GEOSITE/geosite.db
xz -9 geosite.db
