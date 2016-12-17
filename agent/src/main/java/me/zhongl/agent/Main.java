package me.zhongl.agent;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;

import static net.bytebuddy.matcher.ElementMatchers.*;

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
                .with(AgentBuilder.LocationStrategy.ForClassLoader.WEAK)
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override
                    public void onTransformation(TypeDescription td, ClassLoader cl, JavaModule m, DynamicType dt) {
                        System.out.println("[TRANSFORM] " + td);
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .type(not(nameStartsWith("me.zhongl.agent")), not(isBootstrapClassLoader()))
                .transform((b, td, cl) -> b.visit(Advice.to(Profiler.class).on(any())))
                .installOn(inst);
    }

}
