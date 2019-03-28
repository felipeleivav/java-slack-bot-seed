#!/bin/bash

rm -R dist
mkdir dist
mvn -X compile
mvn -X package
cp ./target/slack.bot*with-dependencies.jar ./dist
cp ./config/* ./dist
mv ./dist/slack.bot-*.jar ./dist/slack.bot.jar