package com.__oo__.runner;

import com.__oo__.validator.Validator;
import com.oocourse.TimableOutput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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

    public static void writeFile(String fileName, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException {
        InputSequence seq = InputSequence.generate();
        String timestamp =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss"));
        writeFile("testdata-" + timestamp + ".txt", seq.toString());

        addDir(args[0]);
        Method mainMethod = Class.forName(args[1]).getMethod("main", String[].class);
        Runnable main = new Runnable() {
            @Override
            public void run() {
                try {
                    mainMethod.invoke(null, new Object[] {new String[0]});
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread mainThread = new Thread(main);
        Thread feedThread = new Thread(seq.feed());

        mainThread.start();
        feedThread.start();

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

        String[] request = TimableOutput.output.toString().split("\n");
        boolean result = Validator.validate(seq.getRequests(), request);
        if (result) {
            System.out.println("Pass!");
        } else {
            System.out.println("Fail!");
        }

        writeFile("result-" + timestamp + ".txt", String.join("\n", request));
    }
}
