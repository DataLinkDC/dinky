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

package org.dinky.configure;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests the default H2 console configuration and the properties that turn it back on.
 */
public class H2ConsoleConfigurationTest {

    @Test
    public void defaultH2ProfileDoesNotEnableConsole() throws IOException {
        ConfigurableEnvironment environment = environmentWith();
        Assert.assertEquals(Boolean.FALSE, environment.getProperty("spring.h2.console.enabled", Boolean.class));
    }

    @Test
    public void consoleCanBeEnabledWithSpringProperty() throws IOException {
        ConfigurableEnvironment environment =
                environmentWith(Collections.singletonMap("spring.h2.console.enabled", "true"));
        Assert.assertEquals(Boolean.TRUE, environment.getProperty("spring.h2.console.enabled", Boolean.class));
    }

    @Test
    public void consoleCanBeEnabledWithDedicatedProperty() throws IOException {
        ConfigurableEnvironment environment = environmentWith(Collections.singletonMap("H2_CONSOLE_ENABLED", "true"));
        Assert.assertEquals(Boolean.TRUE, environment.getProperty("spring.h2.console.enabled", Boolean.class));
    }

    @Test
    public void defaultConsoleSettingsUseLocalPathAndHost() throws IOException {
        ConfigurableEnvironment environment = environmentWith();
        Assert.assertEquals("/api/h2", environment.getProperty("spring.h2.console.path"));
        Assert.assertEquals(
                Boolean.FALSE, environment.getProperty("spring.h2.console.settings.web-allow-others", Boolean.class));
    }

    private static ConfigurableEnvironment environmentWith() throws IOException {
        return environmentWith(Collections.emptyMap());
    }

    private static ConfigurableEnvironment environmentWith(Map<String, Object> overrides) throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        environment.setActiveProfiles("h2");
        if (!overrides.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("testOverrides", new HashMap<>(overrides)));
        }
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        Resource resource = new ClassPathResource("application-h2.yml");
        Assert.assertTrue("application-h2.yml should be on the classpath", resource.exists());
        List<PropertySource<?>> sources = loader.load("application-h2", resource);
        for (PropertySource<?> source : sources) {
            environment.getPropertySources().addLast(source);
        }
        return environment;
    }
}
