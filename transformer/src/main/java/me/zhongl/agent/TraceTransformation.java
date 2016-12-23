package me.zhongl.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target.BOOTSTRAP;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class TraceTransformation implements Consumer<Instrumentation> {
    @Override
    public void accept(Instrumentation inst) {
        final Class<StackFrame> type = StackFrame.class;
        Map<? extends TypeDescription, byte[]> map = Collections.singletonMap(desc(type), bytesOf(type));
        UsingInstrumentation.of(new File("/tmp"), BOOTSTRAP, inst).inject(map);

        new AgentBuilder.Default()
//                .with(new Debugger())
.ignore(any(), loaders()).or(types())
.type(isSubTypeOf(Servlet.class)).transform(serviceMethod())
.type(not(isSubTypeOf(Servlet.class))).transform(traceableMethods())
.installOn(inst);
    }

    private ElementMatcher.Junction<TypeDescription> types() {
        return isInterface()
                .or(isAnnotation())
                .or(nameStartsWith("sun."))
                .or(nameStartsWith("com.sun."))
                ;
    }

    private ElementMatcher.Junction<ClassLoader> loaders() {
        return isBootstrapClassLoader()
                .or(is(getClass().getClassLoader()))
                ;
    }

    private TypeDescription.ForLoadedType desc(Class<StackFrame> type) {
        return new TypeDescription.ForLoadedType(type);
    }

    private byte[] bytesOf(Class<StackFrame> type) {
        try {
            final ClassFileLocator locator = ClassFileLocator.ForClassLoader.of(type.getClassLoader());
            return locator.locate(type.getName()).resolve();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private AgentBuilder.Transformer serviceMethod() {
        return (b, td, cl) -> b.visit(Advice.to(Advices.Servlet.class).on(named("service")));
    }

    private AgentBuilder.Transformer traceableMethods() {
        return (b, td, cl) -> b.visit(Advice.to(Advices.Traceable.class).on(
                not(isFinalizer())
                        .and(not(isGetter()))
                        .and(not(isSetter()))
                        .and(not(isToString()))
                        .and(not(isEquals()))
                        .and(not(isHashCode()))
                        .and(not(isClone()))
                        .and(not(isConstructor()))
                        .and(not(isTypeInitializer()))
                        .and(not(isNative()))))
                ;
    }


}
