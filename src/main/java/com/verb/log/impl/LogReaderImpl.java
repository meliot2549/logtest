package com.verb.log.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.verb.log.LogReader;
import com.verb.proto.log.Log;
import nanomsg.exceptions.IOException;
import nanomsg.pubsub.SubSocket;

/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */
public class LogReaderImpl implements LogReader {

    private String topic;
    private SubSocket sock = new SubSocket();


    public LogReaderImpl(String topic) {
        this.topic = topic;
        sock.connect(SimpleLogFactory.bindURL);
        sock.setRecvTimeout(-1);
        sock.subscribe(this.topic);
    }

    public LogReaderImpl(String topic, int timeout) {
        this.topic = topic;
        sock.connect(SimpleLogFactory.bindURL);
        sock.setRecvTimeout(timeout);
        sock.subscribe(this.topic);
    }

    @Override
    public String getTopic() {
        return this.topic;
    }

    @Override
    public Log.Event read()
            throws IOException, InvalidProtocolBufferException {
        byte[] packet = sock.recvBytes();
        if(packet != null && packet.length > 0)
            return SimpleLogFactory.parseLogPacket(topic,packet);
        else
            return null;
    }

    @Override
    public void close() {
        sock.close();
    }

}
