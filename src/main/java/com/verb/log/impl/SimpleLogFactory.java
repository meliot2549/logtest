/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.verb.log.LogReader;
import com.verb.log.LogWriter;
import com.verb.proto.log.Log;
import com.verb.proto.log.Log.LogEvent;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SimpleLogFactory implements com.verb.log.LogFactory {

    public static String bindURL = "ipc:///tmp/log";

    private static String logTopicPrefix = "log.";
    private static long timeOffset;    // 2000-01-01 00:00:00.000000

    static {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(2000,1,1,0,0,0);
        timeOffset = cal.getTimeInMillis() * 1000;
    }
    private String topic;

    public SimpleLogFactory(String topic) {
        this.topic = logTopicPrefix + topic;
    }

    @Override
    public LogWriter getWriter(String component) {
        return new LogWriterImpl(component,this.topic);
    }

    @Override
    public LogReader getReader(Log.LogPriority minimumPriority) {
        return new LogReaderImpl(this.topic);
    }

    @Override
    public LogReader getReader(Log.LogPriority minimumPriority, int timeout) {
        return new LogReaderImpl(this.topic, timeout);
    }

    static LogEvent createLogEvent(String component, Log.LogPriority prio, String message) {
        long time = (System.currentTimeMillis() * 1000) - timeOffset;
        return LogEvent.newBuilder()
                .setTimestamp(time)
                .setPriority(prio)
                .setSource(component)
                .setDescription(message)
                .build();
    }

    static byte[] createLogPacket(String topic, LogEvent log) {
        ByteBuffer buffer = ByteBuffer.allocate(topic.length()+log.getSerializedSize());
        buffer.put(topic.getBytes()).put(log.toByteArray());
        buffer.flip();
        byte[] packet = new byte[buffer.limit()];
        buffer.get(packet);
        return packet;
    }

    static LogEvent parseLogPacket(String topic, byte[] packet)
            throws InvalidProtocolBufferException {
        byte[] msg = Arrays.copyOfRange(packet,topic.length(),packet.length);
        return LogEvent.parseFrom(msg);
    }
}
