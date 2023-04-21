package com.zdpx.coder.utils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

import java.util.List;

/**
 * 用于freeMarker 判断类型.
 */
public class InstanceOfMethod implements TemplateMethodModelEx {

   @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 2) {
            throw new TemplateModelException("Wrong arguments");
        }

        Object bean = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
        String className = arguments.get(1).toString();

        try {
            Class<?> clazz = Class.forName(className);

            return clazz.isInstance(bean);
        }
        catch (ClassNotFoundException ex) {
            throw new TemplateModelException("Could not find the class '" + className + "'", ex);
        }
    }
}
