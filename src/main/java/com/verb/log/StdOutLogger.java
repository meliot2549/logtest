/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.verb.log.impl.SimpleLogFactory;
import com.verb.proto.log.Log;

public class StdOutLogger {

    public static void main(String[] args) {

        SimpleLogFactory factory = new SimpleLogFactory("local");
        LogReader reader = factory.getReader(Log.Severity.DEBUG);

        try {
            while (true) {
                Log.Event log = reader.read();
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
