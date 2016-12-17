package me.zhongl.agent;

import net.bytebuddy.asm.Advice;

import java.util.LinkedList;
import java.util.List;

public interface Profiler {
    ThreadLocal<Frame> current = new ThreadLocal<>();

    @Advice.OnMethodEnter(inline = false)
    static long enter() {
        final Frame frame = current.get();
        if (frame == null) {
            current.set(new Frame());
        } else {
            current.set(frame.fork());
        }
        return System.nanoTime();
    }

    @Advice.OnMethodExit(inline = false)
    static void exit(@Advice.Origin String method, @Advice.Enter long begin) {
        final Frame frame = current.get();
        final Frame parent = frame.join(method, System.nanoTime() - begin);
        if (parent != null) {
            current.set(parent);
        } else {
            System.out.println(frame.toString());
        }
    }

    class Frame {
        final Frame       parent;
        final List<Frame> children;
        final int         level;

        String method;
        long   elapseNanos;

        Frame() {
            this(null, 1);
        }

        Frame(Frame parent, int level) {
            this.parent = parent;
            this.level = level;
            this.children = new LinkedList<>();
        }

        Frame fork() {
            final Frame frame = new Frame(this, level + 1);
            children.add(frame);
            return frame;
        }

        Frame join(String method, long elapseNanos) {
            this.method = method;
            this.elapseNanos = elapseNanos;
            return parent;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('[').append(elapseNanos).append(']').append(method);
            final int size = children.size();
            for (int i = 0; i < size; i++) {
                sb.append('\n');
                for (int j = 0; j < level; j++) {
                    sb.append('\t');
                }
                if (i == size - 1) sb.append("└── ");
                else sb.append("├── ");
                sb.append(children.get(i));
            }
            return sb.toString();
        }
    }
}
