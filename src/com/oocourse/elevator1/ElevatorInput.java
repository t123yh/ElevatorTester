package com.oocourse.elevator1;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ElevatorInput implements Closeable {
    public static BlockingQueue<PersonRequest> InputQueue = new LinkedBlockingQueue<>();

    public ElevatorInput() { }
    public ElevatorInput(InputStream stream) { }

    @Override
    public void close() throws IOException { }

    public PersonRequest nextPersonRequest() {
        PersonRequest req = null;
        while (req == null) {
            try {
                req = InputQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (req instanceof EndOfRequest)
            return null;
        else
            return req;
    }
}
