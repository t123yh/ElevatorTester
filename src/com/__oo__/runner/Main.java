package com.__oo__.runner;

import com.__oo__.validator.Validator;
import com.oocourse.TimableOutput;
import com.oocourse.elevator2.ElevatorInput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void addDir(String s) throws IOException {
        File f = new File(s);
        URL u = f.toURL();
        Class[] parameters = new Class[]{URL.class};
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, u);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    public static void writeFile(String path, String fileName, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(path, fileName).toString()));
        writer.write(content);
        writer.close();
    }

    public static boolean runTest(Method mainMethod, InputSequence seq) {
        System.out.println(String.format("%d elevators", seq.getElevatorNum()));
        TimableOutput.output.reset();
        ElevatorInput.InputQueue.clear();

        Runnable main = new Runnable() {
            @Override
            public void run() {
                try {
                    mainMethod.invoke(null, new Object[]{new String[0]});
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread mainThread = new Thread(main);
        Thread feedThread = new Thread(seq.feed());

        feedThread.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainThread.start();

        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            feedThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] request = TimableOutput.output.toString().replace("\r", "").split("\n");
        return Validator.validate(seq.getRequests(), seq.getElevatorNum(), request);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException {
        System.out.println("Loading " + args[1] + " from " + args[0]);
        addDir(args[0]);
        Method mainMethod = Class.forName(args[1]).getMethod("main", String[].class);
        int failCount = 0;

        if (args.length >= 3) {
            InputSequence seq = InputSequence.parseFromText(Files.readAllLines(new File(args[2]).toPath()));
            boolean result = runTest(mainMethod, seq);
            if (result) {
                System.out.println("Pass!");
            } else {
                System.out.println("Fail!");
            }
        } else {
            while (true) {
                InputSequence seq = InputSequence.generate();
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss"));

                boolean result = runTest(mainMethod, seq);

                if (result) {
                    System.out.println("Pass!");
                } else {
                    System.out.println("Fail!");
                    failCount++;
                    writeFile(args[0], "input-" + timestamp + ".txt", seq.toString());
                }
                System.out.println("Total failures: " + failCount);
            }
        }

    }
}
