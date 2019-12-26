package org.slf4j.impl;

import java.util.ArrayList;


public class SimpleLoggerItem {

    String                  logFileName   = "";
    String                  logPath       = "";
    String                  lastWriteDate = "";
    long                    size          = 0;
    long                    nextWriteTime = 0;
    long                    cacheSize     = 0;
    char                    currLogBuff   = 'A';
    ArrayList<StringBuffer> alLogBufA     = new ArrayList<>();
    ArrayList<StringBuffer> alLogBufB     = new ArrayList<>();

}
