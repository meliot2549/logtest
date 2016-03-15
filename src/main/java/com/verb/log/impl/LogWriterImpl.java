package com.verb.log.impl;

import com.verb.log.LogWriter;
import com.verb.proto.log.Log;
import nanomsg.pubsub.PubSocket;

/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

public class LogWriterImpl implements LogWriter {

    private String component;
    private String topic;
    private PubSocket sock = new PubSocket();

    public LogWriterImpl(String component, String topic) {
        this.component = component;
        this.topic = topic;
        sock.bind(SimpleLogFactory.bindURL);
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public void write(Log.LogPriority priority, String message) {
        Log.LogEvent log = SimpleLogFactory.createLogEvent(component, priority, message);
        byte[] packet = SimpleLogFactory.createLogPacket(topic,log);
        sock.send(packet);
    }

    @Override
    public void close() {
        sock.close();
    }
}
