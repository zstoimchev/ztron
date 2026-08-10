#!/usr/bin/env bash
set -euo pipefail

echo "Checking dependencies..."
if ! command -v java >/dev/null 2>&1; then
    echo "Java is not installed. Run ./ztron install (or install Java manually)"
    exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
    echo "Maven is not installed. Run ./ztron install (or install mvn manually)"
    exit
fi

echo "Building zTron..."
mvn clean package >/dev/null 2>&1
echo "zTron built successfully."
