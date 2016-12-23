package me.zhongl.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.util.Arrays;

class Debugger extends AgentBuilder.Listener.Adapter {
    @Override
    public void onTransformation(TypeDescription td, ClassLoader cl, JavaModule m, DynamicType dt) {
        System.out.println("[TRANSFORM] " + Arrays.asList(td, cl));
    }

    @Override
    public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
        throwable.printStackTrace();
    }
}
