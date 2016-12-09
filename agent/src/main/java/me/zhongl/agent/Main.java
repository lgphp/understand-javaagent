package me.zhongl.agent;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("This is a premain callback");
    }

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("This is a agentmain callback");
    }

    public static void main(String[] args) {
        try {
            ByteBuddyAgent.attach(agentJar(), args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Usage : java -jar agent.jar <PID>");
        }
    }

    private static File agentJar() throws URISyntaxException {
        final URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        return new File(url.toURI());
    }

}
