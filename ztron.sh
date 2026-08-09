#!/usr/bin/env bash

[[ -x ./scripts/run.sh ]] || chmod +x ./scripts/run.sh

exec "./scripts/run.sh" "$@"