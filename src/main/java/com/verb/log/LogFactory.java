package com.verb.log;

import com.verb.proto.log.Log;

/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */
public interface LogFactory {

    LogWriter getWriter(String component);

    LogReader getReader(Log.Severity minimumPriority);

    LogReader getReader(Log.Severity minimumPriority, int timeout);
}
