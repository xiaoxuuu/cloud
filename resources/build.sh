#!/bin/sh
# 作者：小徐
# 时间：2024-02-26

mvn -DsendCredentialsOverHttp=true -Dmaven.test.skip=true clean install -P local,releases -T 12