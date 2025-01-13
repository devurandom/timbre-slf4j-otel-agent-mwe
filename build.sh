#!/bin/bash

set -eu
set -o pipefail

exec clj -T:build uber
