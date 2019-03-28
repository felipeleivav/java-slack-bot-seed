package com.slack.app.main;

import com.slack.app.annotations.Token;
import com.slack.app.annotations.TokenProperty;
import com.slack.app.util.PropLoader;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {

    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String args[]) throws InterruptedException {
        logger.debug("Initializing");

        PropLoader.initialize();
        Reflections reflections = new Reflections("com.slack.app.bot", new SubTypesScanner(false));
        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
        List<String> tokens = new ArrayList<>();
        List<Class> classes = new ArrayList<>();

        for (Class qlass : allClasses) {
            String token = "";

            if (qlass.isAnnotationPresent(Token.class)) {
                Annotation tokenAnnotation = qlass.getAnnotation(Token.class);
                token = ((Token) tokenAnnotation).value();
            } else if (qlass.isAnnotationPresent(TokenProperty.class)) {
                Annotation tokenAnnotation = qlass.getAnnotation((TokenProperty.class));
                String tokenProperty = ((TokenProperty) tokenAnnotation).value();
                token = PropLoader.get(tokenProperty);
            }

            if (!token.equals("")) {
                tokens.add(token);
                classes.add(qlass);
            }
        }

        if (tokens.size() > 0) {
            BotWorker[] workers = new BotWorker[tokens.size()];

            for (int i = 0; i < tokens.size(); i++) {
                BotWorker worker = new BotWorker(tokens.get(i), classes.get(i));
                workers[i] = worker;
            }

            logger.debug(workers.length + " bots detected");

            ExecutorService taskExecutor = Executors.newFixedThreadPool(workers.length);

            for (int i = 0; i < workers.length; i++) {
                taskExecutor.execute(workers[i]);
            }

            taskExecutor.shutdown();
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
    }

}
