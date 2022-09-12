#!/bin/sh

args="\"$*\""
echo "--args=${args}"

./gradlew build
./downloadSchema.sh
sh -c "./gradlew run --args=${args}"