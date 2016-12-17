package me.zhongl.agent;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class Main {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("This is a premain callback");
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("This is a agentmain callback");
        main(args, inst);
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

    private static void main(String args, Instrumentation inst) {
        new AgentBuilder.Default()
                .type(named("me.zhongl.demo.Main"))
                .transform((b, td, cl) -> b.method(named("main")).intercept(to(TimingInterceptor.class)))
                .installOn(inst);
    }

}
