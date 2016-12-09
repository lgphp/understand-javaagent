package me.zhongl.agent;

import java.lang.instrument.Instrumentation;

public class Main {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("This is a premain callback");
    }

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("This is a agentmain callback");
    }
}
