/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.verb.proto.log.Log;
import nanomsg.exceptions.IOException;

public interface LogReader {

    // todo change exception to hide the implementation
    Log.LogEvent read() throws IOException, InvalidProtocolBufferException;

    String getTopic();

    void close();
}
