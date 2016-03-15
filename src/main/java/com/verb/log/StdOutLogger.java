/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.verb.log.impl.SimpleLogFactory;
import com.verb.proto.log.Log;
import com.verb.proto.log.Log.LogEvent;

public class StdOutLogger {

    public static void main(String[] args) {

        SimpleLogFactory factory = new SimpleLogFactory("local");
        LogReader reader = factory.getReader(Log.LogPriority.DEBUG);

        try {
            while (true) {
                LogEvent log = reader.read();
                System.out.println("Received:");
                System.out.println(log.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
    }

}
