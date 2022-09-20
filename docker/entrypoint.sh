#!/bin/sh

args="\"$*\""
echo "--args=${args}"

./gradlew build -x test
./downloadSchema.sh
sh -c "./gradlew run --args=${args} -x test --stacktrace"