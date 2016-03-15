
/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.verb.proto.log.Log;

public interface LogWriter {

    void write(Log.LogPriority priority, String message);

    String getTopic();

    void close();
}
