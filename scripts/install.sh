#!/usr/bin/env bash
set -e

if command -v java >/dev/null 2>&1; then
    echo "Java: installed"
else
    echo "Java: missing"
fi

if command -v mvn >/dev/null 2>&1; then
    echo "Maven: installed"
else
    echo "Maven: missing"
fi
