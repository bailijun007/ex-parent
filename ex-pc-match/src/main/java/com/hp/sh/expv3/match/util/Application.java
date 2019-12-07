package com.hp.sh.expv3.match.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import sun.misc.Signal;
//import sun.misc.SignalHandler;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Application {
    protected void onStart() {
    }

    protected void onStop() {
    }

    protected void onCommand(String[] command) {
    }

    AtomicBoolean bQuit = new AtomicBoolean(false);
    Scanner sc = new Scanner(System.in, "UTF-8");

    protected void run() {
        Thread.currentThread().setName("MainProcess");
        bQuit.set(false);
        hookQuit();
        try {
            onStart();
        } catch (Exception ex) {
            ex.printStackTrace();
            bQuit.set(true);
        }

        try {
            while (!bQuit.get()) {
                String str = sc.nextLine();
                if (bQuit.get()) break;
                String[] commandLineFields = str.split(" ");
                if (commandLineFields != null && commandLineFields.length > 0) {
                    if (!syscmd(commandLineFields)) {
                        try {
                            onCommand(commandLineFields);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            onStop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public void quit() {
        bQuit.set(true);
        sc.close();
    }

    protected boolean syscmd(String[] cmd) {
        if ("quit".equalsIgnoreCase(cmd[0])) {
            quit();
            return true;
        }
        return false;
    }

    final Logger logger = LoggerFactory.getLogger(getClass());

    private void hookQuit() {
//        SignalHandler handler = signal -> logger.warn("input quit instead");
//        Signal.handle(new Signal("INT"), handler);
//        Signal.handle(new Signal("TERM"), handler);
    }
}
