package me.zhongl.agent;

import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class Main {
    public static void premain(String args, Instrumentation inst) throws Exception {
        List<URL> urls = nestArchiveUrls(createArchive(Main.class));

        final URLClassLoader loader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));

        for (Consumer<Instrumentation> consumer : ServiceLoader.load(Consumer.class, loader)) {
            consumer.accept(inst);
        }
    }

    private static List<URL> nestArchiveUrls(Archive archive) throws IOException {
        final List<Archive> archives = archive.getNestedArchives(new Archive.EntryFilter() {
            @Override
            public boolean matches(Archive.Entry entry) {
                return !entry.isDirectory() && entry.getName().startsWith("lib/");
            }
        });

        List<URL> urls = new ArrayList<>();

        for (Archive entries : archives) {
            urls.add(entries.getUrl());
        }
        return urls;
    }

    private static Archive createArchive(Class<?> klass) throws Exception {
        ProtectionDomain protectionDomain = klass.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
        String path = (location == null ? null : location.getSchemeSpecificPart());
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (!root.exists() || root.isDirectory()) {
            throw new IllegalStateException(
                    "Unable to determine code source archive from " + root);
        }
        return new JarFileArchive(root);
    }


}
