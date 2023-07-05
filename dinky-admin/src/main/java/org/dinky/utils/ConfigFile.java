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

package org.dinky.utils;
//
// import java.io.BufferedReader;
// import java.io.File;
// import java.io.IOException;
// import java.io.Reader;
// import java.io.StreamTokenizer;
// import java.text.MessageFormat;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Locale;
// import java.util.Map;
//
// import javax.security.auth.login.AppConfigurationEntry;

// import javax.security.auth.login.Configuration;

//
// import cn.hutool.core.io.FileUtil;
// import cn.hutool.core.util.StrUtil;
// import sun.security.util.Debug;
// import sun.security.util.PropertyExpander;
// import sun.security.util.ResourcesMgr;
//
public class ConfigFile {
    // public class ConfigFile extends Configuration {
    //    private Map<String, List<AppConfigurationEntry>> configuration = new HashMap<>();
    //    private int linenum;
    //    private StreamTokenizer st;
    //    private int lookahead;
    //    private static Debug debugParser = Debug.getInstance("configparser");
    //    private boolean expandProp = true;
    //
    //    public ConfigFile(File file) {
    //        try {
    //            readConfig(FileUtil.getUtf8Reader(file), configuration);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    public ConfigFile(String content) {
    //        try {
    //            readConfig(StrUtil.getReader(content), configuration);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    private void readConfig(Reader reader, Map<String, List<AppConfigurationEntry>> newConfig)
    //            throws IOException {
    //
    //        linenum = 1;
    //
    //        if (!(reader instanceof BufferedReader)) {
    //            reader = new BufferedReader(reader);
    //        }
    //
    //        st = new StreamTokenizer(reader);
    //        st.quoteChar('"');
    //        st.wordChars('$', '$');
    //        st.wordChars('_', '_');
    //        st.wordChars('-', '-');
    //        st.wordChars('*', '*');
    //        st.lowerCaseMode(false);
    //        st.slashSlashComments(true);
    //        st.slashStarComments(true);
    //        st.eolIsSignificant(true);
    //
    //        lookahead = nextToken();
    //        while (lookahead != StreamTokenizer.TT_EOF) {
    //            parseLoginEntry(newConfig);
    //        }
    //    }
    //
    //    private void parseLoginEntry(Map<String, List<AppConfigurationEntry>> newConfig)
    //            throws IOException {
    //
    //        List<AppConfigurationEntry> configEntries = new LinkedList<>();
    //
    //        // application name
    //        String appName = st.sval;
    //        lookahead = nextToken();
    //
    //        if (debugParser != null) {
    //            debugParser.println("\tReading next config entry: " + appName);
    //        }
    //
    //        match("{");
    //
    //        // get the modules
    //        while (peek("}") == false) {
    //            // get the module class name
    //            String moduleClass = match("module class name");
    //
    //            // controlFlag (required, optional, etc)
    //            AppConfigurationEntry.LoginModuleControlFlag controlFlag;
    //            String sflag = match("controlFlag").toUpperCase(Locale.ENGLISH);
    //            switch (sflag) {
    //                case "REQUIRED":
    //                    controlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
    //                    break;
    //                case "REQUISITE":
    //                    controlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
    //                    break;
    //                case "SUFFICIENT":
    //                    controlFlag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
    //                    break;
    //                case "OPTIONAL":
    //                    controlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
    //                    break;
    //                default:
    //                    throw ioException("Configuration.Error.Invalid.control.flag.flag", sflag);
    //            }
    //
    //            // get the args
    //            Map<String, String> options = new HashMap<>();
    //            while (peek(";") == false) {
    //                String key = match("option key");
    //                match("=");
    //                try {
    //                    options.put(key, expand(match("option value")));
    //                } catch (PropertyExpander.ExpandException peee) {
    //                    throw new IOException(peee.getLocalizedMessage());
    //                }
    //            }
    //
    //            lookahead = nextToken();
    //
    //            // create the new element
    //            if (debugParser != null) {
    //                debugParser.println("\t\t" + moduleClass + ", " + sflag);
    //                for (String key : options.keySet()) {
    //                    debugParser.println("\t\t\t" + key + "=" + options.get(key));
    //                }
    //            }
    //            configEntries.add(new AppConfigurationEntry(moduleClass, controlFlag, options));
    //        }
    //
    //        match("}");
    //        match(";");
    //
    //        // add this configuration entry
    //        if (newConfig.containsKey(appName)) {
    //            throw ioException(
    //                    "Configuration.Error.Can.not.specify.multiple.entries.for.appName",
    // appName);
    //        }
    //        newConfig.put(appName, configEntries);
    //    }
    //
    //    private String match(String expect) throws IOException {
    //
    //        String value = null;
    //
    //        switch (lookahead) {
    //            case StreamTokenizer.TT_EOF:
    //                throw ioException("Configuration.Error.expected.expect.read.end.of.file.",
    // expect);
    //
    //            case '"':
    //            case StreamTokenizer.TT_WORD:
    //                if (expect.equalsIgnoreCase("module class name")
    //                        || expect.equalsIgnoreCase("controlFlag")
    //                        || expect.equalsIgnoreCase("option key")
    //                        || expect.equalsIgnoreCase("option value")) {
    //                    value = st.sval;
    //                    lookahead = nextToken();
    //                } else {
    //                    throw ioException(
    //                            "Configuration.Error.Line.line.expected.expect.found.value.",
    //                            new Integer(linenum),
    //                            expect,
    //                            st.sval);
    //                }
    //                break;
    //
    //            case '{':
    //                if (expect.equalsIgnoreCase("{")) {
    //                    lookahead = nextToken();
    //                } else {
    //                    throw ioException(
    //                            "Configuration.Error.Line.line.expected.expect.",
    //                            new Integer(linenum),
    //                            expect,
    //                            st.sval);
    //                }
    //                break;
    //
    //            case ';':
    //                if (expect.equalsIgnoreCase(";")) {
    //                    lookahead = nextToken();
    //                } else {
    //                    throw ioException(
    //                            "Configuration.Error.Line.line.expected.expect.",
    //                            new Integer(linenum),
    //                            expect,
    //                            st.sval);
    //                }
    //                break;
    //
    //            case '}':
    //                if (expect.equalsIgnoreCase("}")) {
    //                    lookahead = nextToken();
    //                } else {
    //                    throw ioException(
    //                            "Configuration.Error.Line.line.expected.expect.",
    //                            new Integer(linenum),
    //                            expect,
    //                            st.sval);
    //                }
    //                break;
    //
    //            case '=':
    //                if (expect.equalsIgnoreCase("=")) {
    //                    lookahead = nextToken();
    //                } else {
    //                    throw ioException(
    //                            "Configuration.Error.Line.line.expected.expect.",
    //                            new Integer(linenum),
    //                            expect,
    //                            st.sval);
    //                }
    //                break;
    //
    //            default:
    //                throw ioException(
    //                        "Configuration.Error.Line.line.expected.expect.found.value.",
    //                        new Integer(linenum),
    //                        expect,
    //                        st.sval);
    //        }
    //        return value;
    //    }
    //
    //    private boolean peek(String expect) {
    //        switch (lookahead) {
    //            case ',':
    //                return expect.equalsIgnoreCase(",");
    //            case ';':
    //                return expect.equalsIgnoreCase(";");
    //            case '{':
    //                return expect.equalsIgnoreCase("{");
    //            case '}':
    //                return expect.equalsIgnoreCase("}");
    //            default:
    //                return false;
    //        }
    //    }
    //
    //    private int nextToken() throws IOException {
    //        int tok;
    //        while ((tok = st.nextToken()) == StreamTokenizer.TT_EOL) {
    //            linenum++;
    //        }
    //        return tok;
    //    }
    //
    //    private String expand(String value) throws PropertyExpander.ExpandException, IOException {
    //
    //        if (value.isEmpty()) {
    //            return value;
    //        }
    //
    //        if (!expandProp) {
    //            return value;
    //        }
    //        String s = PropertyExpander.expand(value);
    //        if (s == null || s.length() == 0) {
    //            throw ioException(
    //
    // "Configuration.Error.Line.line.system.property.value.expanded.to.empty.value",
    //                    new Integer(linenum),
    //                    value);
    //        }
    //        return s;
    //    }
    //
    //    private IOException ioException(String resourceKey, Object... args) {
    //        MessageFormat form =
    //                new MessageFormat(
    //                        ResourcesMgr.getString(resourceKey,
    // "sun.security.util.AuthResources"));
    //        return new IOException(form.format(args));
    //    }
    //
    //    @Override
    //    public AppConfigurationEntry[] getAppConfigurationEntry(String applicationName) {
    //        List<AppConfigurationEntry> list = null;
    //        synchronized (configuration) {
    //            list = configuration.get(applicationName);
    //        }
    //
    //        if (list == null || list.size() == 0) {
    //            return null;
    //        }
    //
    //        AppConfigurationEntry[] entries = new AppConfigurationEntry[list.size()];
    //        Iterator<AppConfigurationEntry> iterator = list.iterator();
    //        for (int i = 0; iterator.hasNext(); i++) {
    //            AppConfigurationEntry e = iterator.next();
    //            entries[i] =
    //                    new AppConfigurationEntry(
    //                            e.getLoginModuleName(), e.getControlFlag(), e.getOptions());
    //        }
    //        return entries;
    //    }
}
