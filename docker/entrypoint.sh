#!/bin/sh

args="\"$*\""
echo "--args=${args}"

./gradlew build --no-daemon
./downloadSchema.sh
sh -c "./gradlew run --args=${args}"