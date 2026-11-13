/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.context;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * The FreeMarkerHolder class manages FreeMarker templates and provides methods to interact with them.
 */
public class FreeMarkerHolder {

    /**
     * The FreeMarker configuration.
     */
    private final Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

    /**
     * The StringTemplateLoader to load FreeMarker templates from strings.
     */
    private final StringTemplateLoader stringLoader = new StringTemplateLoader();

    /**
     * Initializes the FreeMarkerHolder, setting up the configuration.
     */
    public FreeMarkerHolder() {
        configuration.setTemplateLoader(stringLoader);
        configuration.setNumberFormat("computer");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configureTemplateRendering(configuration);
    }

    /**
     * Configures template rendering so a template is processed only against the data provided to it,
     * and can only construct types that the configuration explicitly allows (none by default).
     *
     * @param configuration The FreeMarker configuration to apply these settings to.
     */
    static void configureTemplateRendering(Configuration configuration) {
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
        configuration.setAPIBuiltinEnabled(false);
        DefaultObjectWrapperBuilder objectWrapperBuilder =
                new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_32);
        objectWrapperBuilder.setExposureLevel(DefaultObjectWrapper.EXPOSE_SAFE);
        configuration.setObjectWrapper(objectWrapperBuilder.build());
    }

    /**
     * Adds a FreeMarker template to the holder.
     *
     * @param name            The name of the template.
     * @param templateContent The content of the template as a string.
     */
    public void putTemplate(String name, String templateContent) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Template name is null or empty.");
        }
        stringLoader.putTemplate(name, templateContent);
    }

    /**
     * Builds a FreeMarker template with the provided data model.
     *
     * @param name      The name of the template to build.
     * @param dataModel The data model containing variables to populate the template.
     * @return A string representation of the template after processing.
     * @throws IOException       If an I/O error occurs while processing the template.
     * @throws TemplateException If an error occurs during template processing.
     */
    public String buildWithData(String name, Map<String, Object> dataModel) throws IOException, TemplateException {
        Template template = configuration.getTemplate(name);
        StringWriter stringWriter = new StringWriter();
        template.process(dataModel, stringWriter);
        return stringWriter.toString();
    }

    /**
     * Renders the given template content against the provided data model.
     *
     * @param templateContent The template content as a string.
     * @param dataModel       The data model containing variables to populate the template.
     * @return A string representation of the template after processing.
     * @throws IOException       If an I/O error occurs while processing the template.
     * @throws TemplateException If an error occurs during template processing.
     */
    public static String render(String templateContent, Map<String, Object> dataModel)
            throws IOException, TemplateException {
        FreeMarkerHolder holder = new FreeMarkerHolder();
        String name = "inline";
        holder.putTemplate(name, templateContent);
        return holder.buildWithData(name, dataModel);
    }
}
