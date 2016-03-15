/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.verb.proto.log.Log.*;

public class ProtobufWriter {

    public static void main(String[] argv) {

        LogEvent event = LogEvent.newBuilder()
                .setTimestamp(System.currentTimeMillis()*1000)
                .setPriority(LogPriority.CRITICAL)
                .setSource("Foobar")
                .setDescription("I am totally foobar")
                .build();

        System.out.println(event.toString());
    }

}
