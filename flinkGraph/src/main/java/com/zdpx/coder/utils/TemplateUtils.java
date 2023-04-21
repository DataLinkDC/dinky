package com.zdpx.coder.utils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * freemarker 工具类.
 *
 * @author Licho Sun
 */
@Slf4j
public class TemplateUtils {
    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);

    static {
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateLoader(new ClassTemplateLoader(TemplateUtils.class, "/templates"));
        configuration.setSharedVariable("instanceOf", new InstanceOfMethod());
    }

    private TemplateUtils() {
    }

    public static String format(String name, Object dataModel, String context) {
        final StringWriter stringWriter = new StringWriter();
        try {
            Template template = new Template(name, new StringReader(context), configuration);
            template.process(dataModel, stringWriter);
        } catch (TemplateException | IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
        return stringWriter.toString();
    }
}
