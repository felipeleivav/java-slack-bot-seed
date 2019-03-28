package com.slack.app.bot;

import com.slack.app.annotations.*;
import com.slack.app.main.App;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

@TokenProperty("slack.token.testbot")
public class TestBot {

    private static Logger logger = Logger.getLogger(App.class);

    @Any
    public String test(@ChannelParam SlackChannel channel, @UserParam SlackUser user, @MessageParam String message,  @IsPMParam boolean isPM, @IsMentionParam boolean isMention) {
        if (isMention) {
            return "How can I help you, <@"+user.getUserName()+">?";
        }
        return null;
    }

    @Message("what time is it?")
    public String theCommandTime(@UserParam SlackUser user) {
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        return "<@"+user.getUserName()+">, here is the time: "+formattedDate;
    }

}
