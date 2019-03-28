#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
java -cp "./slack.bot.jar:./slack.bot.properties:./" com.slack.app.main.App