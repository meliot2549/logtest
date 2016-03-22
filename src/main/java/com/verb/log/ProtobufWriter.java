/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.verb.proto.log.Log;

import static com.verb.proto.log.Log.Severity.CRITICAL;

public class ProtobufWriter {

    public static void main(String[] argv) {

        Log.Event event = Log.Event.newBuilder()
                .setTimestamp(System.currentTimeMillis()*1000)
                .setPriority(CRITICAL)
                .setSource("Foobar")
                .setDescription("I am totally foobar")
                .build();

        System.out.println(event.toString());
    }

}
