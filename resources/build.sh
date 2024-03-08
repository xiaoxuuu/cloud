#!/bin/sh
# 作者：小徐
# 时间：2024-02-26

echo "mvn deploy start"
mvn -DsendCredentialsOverHttp=true -Dmaven.test.skip=true clean install -P local,releases -T 12 -q
echo "mvn deploy finish"