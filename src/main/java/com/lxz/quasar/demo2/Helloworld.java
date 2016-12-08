package com.lxz.quasar.demo2;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;

import java.util.concurrent.ExecutionException;

public class Helloworld {
    static void m1() throws SuspendExecution, InterruptedException {
        String m = "m1";
        System.out.println("m1 begin");
        m = m2();
        m = m3();
        System.out.println("m1 end");
        System.out.println(m);
    }

    static String m2() throws SuspendExecution, InterruptedException {
        return "m2";
    }

    static String m3() throws SuspendExecution, InterruptedException {
        return "m3";
    }

    static public void main(String[] args) throws ExecutionException, InterruptedException {
        new Fiber<Void>("Caller", () -> m1()).start();
    }
}
