package com.dlink.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Slf4j
public class DinkyClassLoader extends URLClassLoader {

    public DinkyClassLoader(URL[] urls, ClassLoader parent) {
        super(new URL[] {}, parent);
    }

    public DinkyClassLoader(URL[] urls) {
        super(new URL[] {});
    }

    public DinkyClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(new URL[] {}, parent, factory);
    }

    public void addURL(URL... urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }

    public void addURL(String... paths) {
        for (String path : paths) {
            File file = new File(path);
            try {
                super.addURL(file.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
