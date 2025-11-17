/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.jenkins.rest.filters;

import static com.cdancy.jenkins.rest.auth.AuthenticationType.Anonymous;
import static com.cdancy.jenkins.rest.auth.AuthenticationType.UsernameApiToken;
import static com.cdancy.jenkins.rest.auth.AuthenticationType.UsernamePassword;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.JenkinsAuthentication;
import com.cdancy.jenkins.rest.auth.AuthenticationType;
import com.cdancy.jenkins.rest.domain.crumb.Crumb;
import com.cdancy.jenkins.rest.features.CrumbIssuerApi;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.Optional;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * ClientRequestFilter that:
 *  - adds Basic Authorization header
 *  - fetches Jenkins crumb via CrumbIssuerApi and adds the crumb header (field:value)
 *  - caches the crumbPair; synchronized refresh
 * Notes:
 *  - Crumb fetching is lazy.
 *  - Consider refreshing on 403 or time-based TTL in production.
 */
@Provider
public class JenkinsAuthenticationFilter implements ClientRequestFilter
{

    private static final String CRUMB_HEADER = "Jenkins-Crumb";


    private final JenkinsAuthentication creds;
    private final CrumbIssuerApi crumbIssuerApi;

    // Simple cached crumb; volatile for safe read across threads
    private volatile Crumb cachedCrumb;

    public JenkinsAuthenticationFilter(JenkinsAuthentication creds, CrumbIssuerApi crumbIssuerApi) {
        this.creds = Objects.requireNonNull(creds, "creds");
        this.crumbIssuerApi = Objects.requireNonNull(crumbIssuerApi, "crumbIssuerApi");
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        // Add Auth header
        // Password and API Token are both Basic authentication (there is no Bearer authentication in Jenkins)
        if (creds.authType() == UsernameApiToken || creds.authType() == UsernamePassword)
        {
            final String authHeader = creds.authType().getAuthScheme() + " " + creds.authValue();
            requestContext.getHeaders().putSingle(AUTHORIZATION, authHeader);
        }


        // Anon and Password need the crumb and the cookie when POSTing
        if (requestContext.getMethod().equals("POST") &&
            (creds.authType() == UsernamePassword || creds.authType() == Anonymous)
        )
        {
            final Crumb localCrumb = getCrumb();
            if (localCrumb.getCrumb() != null)
            {
                requestContext.getHeaders().putSingle(CRUMB_HEADER, localCrumb.getCrumb());
                Optional.ofNullable(localCrumb.getSessionIdCookie())
                    .ifPresent(sessionId -> requestContext.getHeaders().putSingle(HttpHeaders.COOKIE, sessionId));
            } else
            {
                throw new RuntimeException("Crumb coudn't be obtained!");
            }
        }

    }

    private Crumb getCrumb()
    {
        Crumb crumbValueInit = this.cachedCrumb;
        if (crumbValueInit == null)
        {
            synchronized (this)
            {
                crumbValueInit = this.cachedCrumb;
                if (crumbValueInit == null)
                {
                    final Crumb crumb = crumbIssuerApi.crumb();
                    this.cachedCrumb = crumbValueInit = crumb;
                }
            }
        }
        return crumbValueInit;
    }

}
