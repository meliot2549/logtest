/**
 * Copyright (c) 2016 Verb Surgical, Inc. All rights reserved.
 */

package com.verb.log;

import com.verb.log.impl.SimpleLogFactory;
import com.verb.proto.log.Log;
import com.verb.proto.log.LoggerGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nanomsg.exceptions.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class LoggerService implements LoggerGrpc.Logger {

    private Thread thread = null;
    private List<Log.LogEvent> store = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    public LoggerService() {
    }

    private class LogThread extends Thread {

        public LogThread() {
            super("Logger Thread");
        }

        @Override
        public void run() {
            System.out.println("logging thread running");

            SimpleLogFactory factory = new SimpleLogFactory("local");
            LogReader reader = factory.getReader(Log.LogPriority.DEBUG, 1000);

            try {
                while( ! interrupted() ) {
                    try {
                        Log.LogEvent log = reader.read();
                        if( log != null ) {
                            lock.lock();

                            // Naive implemenmtation using an array
                            store.add(log);

                            lock.unlock();
                            System.out.println(log.toString());
                        }
                    } catch (IOException e) {
                        // expected
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reader.close();
                System.out.println("logging thread terminating");
            }
        }
    }

    @Override
    public synchronized void start(Log.Null request, StreamObserver<Log.LoggerResponse> responseObserver) {
        System.out.println("start");
        if (thread == null) {
            thread = new Thread(new LogThread());
            thread.start();
            responseObserver.onNext(Log.LoggerResponse
                    .newBuilder()
                    .setCode(Log.LoggerResponse.LoggerResponseCode.SUCCESSFUL)
                    .build());
            responseObserver.onCompleted();
        }
        else {
            responseObserver.onNext(Log.LoggerResponse
                    .newBuilder()
                    .setCode(Log.LoggerResponse.LoggerResponseCode.ERROR_ALREADY_STARTED)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public synchronized void stop(Log.Null request, StreamObserver<Log.LoggerResponse> responseObserver) {
        System.out.println("stop");
        if (thread != null) {
            try {
                thread.interrupt();
                thread.join(5000);
                if( thread.isAlive() ) {
                    System.out.println("logging thread join failed");
                    responseObserver.onNext(Log.LoggerResponse
                            .newBuilder()
                            .setCode(Log.LoggerResponse.LoggerResponseCode.ERROR_INTERNAL)
                            .setDescription("logging thread failed to join")
                            .build());
                    responseObserver.onCompleted();
                }
                else {
                    System.out.println("logging thread stopped");
                    responseObserver.onNext(Log.LoggerResponse
                            .newBuilder()
                            .setCode(Log.LoggerResponse.LoggerResponseCode.SUCCESSFUL)
                            .build());
                    responseObserver.onCompleted();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                thread = null;
            }
        }
        else {
            responseObserver.onNext(Log.LoggerResponse
                    .newBuilder()
                    .setCode(Log.LoggerResponse.LoggerResponseCode.ERROR_ALREADY_STARTED)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public synchronized void isRunning(Log.Null request, StreamObserver<Log.GetIsRunningResponse> responseObserver) {
        Log.GetIsRunningResponse rsp = Log.GetIsRunningResponse.newBuilder().setRunning(thread != null).build();
        responseObserver.onNext(rsp);
        responseObserver.onCompleted();
    }

    @Override
    public void getTimeRange(Log.Null request, StreamObserver<Log.GetTimeRangeResponse> responseObserver) {

    }

    @Override
    public void getEvents(Log.LogFilter request, StreamObserver<Log.GetEventsResponse> responseObserver) {
        Log.GetEventsResponse.Builder rspBuilder = Log.GetEventsResponse.newBuilder();
        rspBuilder.getRspBuilder().setCode(Log.LoggerResponse.LoggerResponseCode.SUCCESSFUL);
        Iterator<Log.LogEvent> i = store.iterator();
        lock.lock();
        while( i.hasNext() ) {
            rspBuilder.addEvents(i.next());
        }
        lock.unlock();
        responseObserver.onNext(rspBuilder.build());
        responseObserver.onCompleted();
    }

    public static void main(String[] args) {
        try {
            System.out.println("starting server");

            LoggerService service = new LoggerService();
//            service.start(null,null);

            Server server = ServerBuilder.forPort(30001)
                    .addService(LoggerGrpc.bindService(service))
                    .build();
            server.start();

            while(true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
