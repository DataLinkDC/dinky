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

package org.dinky.alert;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import java.net.URI;

/**
 * Follows redirects only when the next address is a well-defined HTTP or HTTPS webhook address.
 */
public final class AlertSenderRedirectStrategy extends DefaultRedirectStrategy {

    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {
        URI location = super.getLocationURI(request, response, context);
        if (location == null || !AlertSenderAddress.isWellDefined(location.toString())) {
            throw new ProtocolException(AlertSenderAddress.NOT_WELL_DEFINED_MESSAGE);
        }
        return location;
    }
}
