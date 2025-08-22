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

package org.dinky.sandbox;

import org.dinky.assertion.Asserts;
import org.dinky.data.exception.DinkyException;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;

public class SandboxFactory {

    private static Optional<Sandbox> get(String type) {
        Asserts.checkNotNull(type, "The type of the sandbox cannot be left empty.");
        ServiceLoader<Sandbox> sandboxes = ServiceLoader.load(Sandbox.class);
        Iterator<Sandbox> sandboxesIterator = sandboxes.iterator();

        // There may be an issue where the class can't be found, so the exception needs to be caught
        while (sandboxesIterator.hasNext()) {
            try {
                Sandbox sandbox = sandboxesIterator.next();
                if (sandbox.canHandle(type)) {
                    return Optional.of(sandbox);
                }
            } catch (Throwable t) {
                // Do nothing
            }
        }
        return Optional.empty();
    }

    public static Sandbox getSandbox(String type) {
        synchronized (Sandbox.class) {
            Optional<Sandbox> optionalSandbox = get(type);
            if (!optionalSandbox.isPresent()) {
                throw new DinkyException(String.format("Missing {} dependency package", type));
            }
            return optionalSandbox.get();
        }
    }

    public static Sandbox getDefaultSandbox() {
        synchronized (Sandbox.class) {
            Optional<Sandbox> optionalSandbox = get("memory");
            if (!optionalSandbox.isPresent()) {
                throw new DinkyException(String.format("Missing dinky-sandbox-memory dependency package"));
            }
            return optionalSandbox.get();
        }
    }
}
