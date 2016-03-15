/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.verb.log.impl.SimpleLogFactory;
import com.verb.proto.log.Log;
import com.verb.proto.log.LoggerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Random;

public class SimpleLogPublisher {

    private static String[] text = {
            "The time has come, the walrus said",
            "To speak of many things",
            "Of shoes and ships and sealing wax",
            "Of cabbages and kings",
            "And why the sea is boiling hot",
            "And whether pigs have wings"
    };

    private static String getRandomText() {
        Random r = new Random(System.currentTimeMillis());
        return text[r.nextInt(text.length)];
    }

    public static void main(String[] argv) {

        SimpleLogFactory factory = new SimpleLogFactory("local");
        LogWriter writer = factory.getWriter(SimpleLogPublisher.class.getCanonicalName());

        try {

            ManagedChannel channel;
            LoggerGrpc.LoggerBlockingStub blockingStub;

            channel = ManagedChannelBuilder.forAddress("localhost", 30001)
                    .usePlaintext(true)
                    .build();
            blockingStub = LoggerGrpc.newBlockingStub(channel);

            System.out.println("starting logger service");
            Log.Null nullReq = Log.Null.newBuilder().build();
            Log.LoggerResponse rsp = blockingStub.start(nullReq);
            System.out.println(rsp);

            for (int i = 0; i < 5; ++i) {
                Thread.sleep(1000);
                writer.write(Log.LogPriority.INFO,getRandomText());
            }

            Log.LogFilter req = Log.LogFilter.newBuilder().build();
            Log.GetEventsResponse rsp2 = blockingStub.getEvents(req);
            System.out.println(rsp2.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

}