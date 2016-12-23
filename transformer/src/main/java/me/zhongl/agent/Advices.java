package me.zhongl.agent;

import net.bytebuddy.asm.Advice;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public interface Advices {

    class Servlet {

        @Advice.OnMethodEnter
        public static boolean enter(@Advice.Argument(0) ServletRequest request) {
            if (!(request instanceof HttpServletRequest)) return false;

            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            final String method = httpServletRequest.getMethod();
            final String requestURI = httpServletRequest.getRequestURI();
            return StackFrame.setIfAbsent(method + " " + requestURI);
        }

        @Advice.OnMethodExit()
        public static void exit(@Advice.Enter boolean set) {
            if (set) StackFrame.print();
        }
    }

    class Traceable {

        @Advice.OnMethodEnter
        public static void enter(@Advice.Origin("#t::#m") String method) {
            StackFrame.fork(method);
        }

        @Advice.OnMethodExit()
        public static void exit() {
            StackFrame.join();
        }
    }
}
