package me.zhongl.demo;

public class Main {
    public static void main(String[] args) throws Exception {
        a();
        b();
    }

    private static void b() throws InterruptedException {
        c();
        Thread.sleep(1);
    }

    private static void c() throws InterruptedException {
        Thread.sleep(1);
    }

    private static void a() throws InterruptedException {
        Thread.sleep(1);
    }
}
