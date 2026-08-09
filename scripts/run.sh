#!/bin/bash
set -e

if ! command -v java >/dev/null 2>&1; then
    echo "Java is not installed. Run ztron install"
    exit 1
fi

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null)
JAR="./target/ztron-${VERSION}.jar"

if [[ ! -f "$JAR" ]]; then
    echo "zTron has not been built. Run mvn package"
    exit 1
fi

exec java -jar "$JAR" "$@"
