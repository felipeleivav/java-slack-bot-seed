package com.slack.app.main;

import com.slack.app.annotations.*;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class BotWorker implements Runnable {

    private static Logger logger = Logger.getLogger(BotWorker.class);

    private String token;
    private Class qlass;

    public BotWorker(String token, Class qlass) {
        this.token = token;
        this.qlass = qlass;
    }

    public void run() {
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(this.token);

        // callback for message processing
        session.addMessagePostedListener((event, session1) -> {
            if (session1.sessionPersona().getId().equals(event.getSender().getId())) {
                return;
            }

            // loop all methods of the bot class thru reflection
            for (Method method : qlass.getDeclaredMethods()) {

                // if method has the @Any annotation, then will catch all the messages
                if (!method.isAnnotationPresent(Any.class)) {
                    if (!method.isAnnotationPresent(Channel.class) && !method.isAnnotationPresent(User.class)
                            && !method.isAnnotationPresent(Message.class) && !method.isAnnotationPresent(MessageRegex.class)
                            && !method.isAnnotationPresent(IsPM.class) && !method.isAnnotationPresent(IsMention.class)) {
                        continue;
                    }

                    if (method.isAnnotationPresent(IsPM.class) && !event.getChannel().isDirect()) {
                        continue;
                    }

                    if (method.isAnnotationPresent(IsMention.class) && !event.getMessageContent().contains("<@" + session1.sessionPersona().getId() + ">")) {
                        continue;
                    }

                    if (method.isAnnotationPresent(Channel.class)) {
                        SlackChannel channel = event.getChannel();
                        String annotationChannel = method.getAnnotation(Channel.class).value();
                        if (!channel.getName().equalsIgnoreCase(annotationChannel)) {
                            continue;
                        }
                    }

                    if (method.isAnnotationPresent(User.class)) {
                        SlackUser user = event.getUser();
                        String annotationUser = method.getAnnotation(User.class).value();
                        if (!user.getUserName().equalsIgnoreCase(annotationUser)) {
                            continue;
                        }
                    }

                    if (method.isAnnotationPresent(Message.class)) {
                        String message = event.getMessageContent();
                        String annotationMessage = method.getAnnotation(Message.class).value();
                        if (!message.equals(annotationMessage)) {
                            continue;
                        }
                    }

                    if (method.isAnnotationPresent(MessageRegex.class)) {
                        String message = event.getMessageContent();
                        String annotationMessage = method.getAnnotation(MessageRegex.class).value();
                        if (!message.matches(annotationMessage)) {
                            continue;
                        }
                    }
                }

                try {
                    Parameter[] parameters = method.getParameters();
                    Object[] inputs = new Object[parameters.length];

                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].isAnnotationPresent(ChannelParam.class)) {
                            inputs[i] = event.getChannel();
                        } else if (parameters[i].isAnnotationPresent(UserParam.class)) {
                            inputs[i] = event.getSender();
                        } else if (parameters[i].isAnnotationPresent(MessageParam.class)) {
                            inputs[i] = event.getMessageContent();
                        } else if (parameters[i].isAnnotationPresent(IsMentionParam.class)) {
                            inputs[i] = event.getMessageContent().contains("<@" + session1.sessionPersona().getId() + ">");
                        } else if (parameters[i].isAnnotationPresent(IsPMParam.class)) {
                            inputs[i] = event.getChannel().isDirect();
                        } else {
                            inputs[i] = null;
                        }
                    }

                    Object methodReturn = method.invoke(qlass.newInstance(), inputs);
                    if (methodReturn != null && methodReturn instanceof String) {
                        session1.sendMessage(event.getChannel(), (String) methodReturn);
                    }
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    logger.error("Error invoking method ", e);
                }
            }
        });

        try {
            session.connect();
        } catch (IOException e) {
            logger.error("Slack connection error ", e);
        }
    }

}
