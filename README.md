## Slack Bot Seed Project

This is a small seed project I've been using for some bot experiments. It's basically a little bot framework for Slack. It let's you create as many bots as you want running from a single application and in a multithread fashion.

It's based on the [simple slack api](https://github.com/Ullink/simple-slack-api).

### Usage

First, clone this repo (maven required):

    git clone https://github.com/felipeleivav/java-slack-bot-seed

Then, in your Slack account, create a bot and generate a token (don't know how? refer to [this](https://api.slack.com/bot-users))

Add your token to your properties file:

    config/slack.bot.properties

And... build n' launch! :rocket:

    ./build.sh
    cd dist
    ./launch.sh

### Creating a bot

You just have to create a new class in the `com.slack.app.bot` package.

Any class you create there will be considered a Slack bot.

In order to connect that class to Slack, you have to add the `@TokenProperty` annotation, pointing to your token key in the properties file.

    @TokenProperty("slack.bot.token")
    public class NewBot {
    }

Now, you can start creating functions for your bot:

    @IsMention
    public String someoneCallingMe(@UserParam SlackUser user) {
      String userCalling = user.getUsername();
      return userCalling+", what do you want dude?";
    }

Each method will be basically a message handler.

There are `method annotations` you can use to filter those messages:

  * `@IsMention` handle only messages where your bot is mentioned
  * `@IsPM` handle only direct messages
  * `@Channel("general")` handle only messages from *#general*
  * `@User("felipe")` handle only messages from user *felipe*
  * `@Message("hello")` filter all messages but *hello*s
  * `@MessageRegex(".*hello.*")` same that message but w/ java regexes

There is also a special one that let you handle `@Any` message.

Finally, there are the `parameter annotations` you can use to pass certain data to your bot functions:

  * `@IsMentionParam` is a `boolean` that tells you if the message has a mention for your bot
  * `@IsPMParam` is a `boolean` that tells you if message is direct
  * `@MessageParam` gives you access to the raw message
  * `@UserParam` is a `SlackUser` object which gives you access to the user data who sent the message
  * `@ChannelParam` is a `SlackChannel` object which contains the channel info

The return must be a string which will be a chat response. No response? Just return `null`.  

### Multi-bot & concurrency

You can create as many bots you want, all of them will run within this single application. Each bot will run in a different thread (using java concurrency, see `App` class).

### Final notes

The [simple slack api](https://github.com/Ullink/simple-slack-api) has lot more functions, those should be implemented from the `com.slack.app.main.BotWorker` class using annotations and Reflection API.
