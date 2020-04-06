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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class Main {
    public static boolean verbose = true;

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

    public static Method getMainMethod(String path, String mainClass) throws ClassNotFoundException, IOException, NoSuchMethodException {
        System.out.println("Loading " + mainClass + " from " + path);
        addDir(path.trim());
        Method mainMethod = Class.forName(mainClass.trim()).getMethod("main", String[].class);
        return mainMethod;
    }

    public static void doFileTest(Method main, String fileName) throws NoSuchMethodException, IOException, ClassNotFoundException {
        InputSequence seq = InputSequence.parseFromText(Files.readAllLines(new File(fileName).toPath()));
        boolean result = runTest(main, seq);
        if (result) {
            System.out.println("Pass!");
        } else {
            System.out.println("Fail!");
        }
    }

    public static void doRandomTest(String path, Method mainMethod) throws NoSuchMethodException, IOException, ClassNotFoundException {
        int failCount = 0;
        while (true) {
            InputSequence seq = InputSequence.generate();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss"));

            boolean result = runTest(mainMethod, seq);

            if (result) {
                System.out.println("Pass!");
            } else {
                System.out.println("Fail!");
                failCount++;
                writeFile(path, "input-" + timestamp + ".txt", seq.toString());
            }
            System.out.println("Total failures: " + failCount);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException {
        if (args[0].equals("random")) {
            verbose = true;
            Method mainMethod = getMainMethod(args[1], args[2]);
            doRandomTest(args[1], mainMethod);
        } else if (args[0].equals("file")) {
            verbose = true;
            Method mainMethod = getMainMethod(args[1], args[2]);
            doFileTest(mainMethod, args[3]);
        } else if (args[0].equals("benchmark")) {
            verbose = false;
            Method mainMethod = getMainMethod(args[1], args[2]);
            for(Path p : Files.list(new File(args[3]).toPath()).collect(Collectors.toList()))
            {
                long start = System.currentTimeMillis();

                doFileTest(mainMethod, p.toString());

                long end = System.currentTimeMillis();
                System.out.println(String.format("%s: %d ms", p, end - start));
            }
        } else if (args[0].equals("generate")) {
            InputSequence seq = InputSequence.generate();
            System.out.println(seq.toString());
        }
    }
}
