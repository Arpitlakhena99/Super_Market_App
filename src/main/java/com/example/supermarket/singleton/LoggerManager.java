package com.example.supermarket.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerManager {
    private static LoggerManager instance;
    private final Logger logger;

    private LoggerManager() {
        this.logger = LoggerFactory.getLogger("SupermarketLogger");
    }

    public static synchronized LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }
}

