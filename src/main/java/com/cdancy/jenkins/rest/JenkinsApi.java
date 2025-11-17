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

package com.cdancy.jenkins.rest;


import com.cdancy.jenkins.rest.features.ConfigurationAsCodeApi;
import com.cdancy.jenkins.rest.features.CrumbIssuerApi;
import com.cdancy.jenkins.rest.features.JobsApi;
import com.cdancy.jenkins.rest.features.PluginManagerApi;
import com.cdancy.jenkins.rest.features.QueueApi;
import com.cdancy.jenkins.rest.features.StatisticsApi;
import com.cdancy.jenkins.rest.features.SystemApi;
import com.cdancy.jenkins.rest.features.UserApi;
import com.cdancy.jenkins.rest.filters.CrumbMessageBodyReader;
import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;
import com.cdancy.jenkins.rest.filters.JenkinsCrumbResponseFilter;
import com.cdancy.jenkins.rest.filters.JenkinsNoCrumbAuthenticationFilter;
import com.cdancy.jenkins.rest.filters.JenkinsUserInjectionFilter;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.eclipse.microprofile.rest.client.RestClientBuilder;


/**
 * Builder-style Jenkins API client that uses MicroProfile RestClient.
 * Each API interface is backed by a RestClient proxy.
 */
public final class JenkinsApi
{

    private final CrumbIssuerApi crumbIssuerApi;
    private final JobsApi jobsApi;
    private final PluginManagerApi pluginManagerApi;
    private final QueueApi queueApi;
    private final StatisticsApi statisticsApi;
    private final SystemApi systemApi;
    private final ConfigurationAsCodeApi configurationAsCodeApi;
    private final UserApi userApi;

    private JenkinsApi(Builder builder)
    {
        RestClientBuilder looseSecurityBuilder = fromBuilder(builder)
            .register(new JenkinsNoCrumbAuthenticationFilter(builder.creds))
            .register(JenkinsCrumbResponseFilter.class)
            .register(CrumbMessageBodyReader.class);

        this.crumbIssuerApi = looseSecurityBuilder.build(CrumbIssuerApi.class);

        RestClientBuilder authSecurityBuilder = fromBuilder(builder)
            .register(new JenkinsAuthenticationFilter(builder.creds, crumbIssuerApi))
            .register(new JenkinsUserInjectionFilter(builder.creds));


        this.jobsApi = authSecurityBuilder.build(JobsApi.class);
        this.pluginManagerApi = authSecurityBuilder.build(PluginManagerApi.class);
        this.queueApi = authSecurityBuilder.build(QueueApi.class);
        this.statisticsApi = authSecurityBuilder.build(StatisticsApi.class);
        this.systemApi = authSecurityBuilder.build(SystemApi.class);
        this.configurationAsCodeApi = authSecurityBuilder.build(ConfigurationAsCodeApi.class);
        this.userApi = authSecurityBuilder.build(UserApi.class);
    }

    private RestClientBuilder fromBuilder(Builder builder)
    {
        RestClientBuilder restBuilder = RestClientBuilder.newBuilder()
            .hostnameVerifier(AllTrustedConnectionFactory.getHostnameVerifier())
            .sslContext(AllTrustedConnectionFactory.getSslContext())
            .baseUri(builder.endpoint).followRedirects(true)
            .readTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS);
        Optional.ofNullable(builder.components).ifPresent(components -> {
                for (Object component : components)
                {
                    restBuilder.register(component);
                }
            }
        );
        Optional.ofNullable(builder.properties).ifPresent(properties ->
            properties.forEach( (key,value) -> {
                    restBuilder.property(key, value);
                }
            )
        );

        return restBuilder;
    }

    public CrumbIssuerApi crumbIssuerApi()
    {
        return crumbIssuerApi;
    }

    public JobsApi jobsApi()
    {
        return jobsApi;
    }

    public PluginManagerApi pluginManagerApi()
    {
        return pluginManagerApi;
    }

    public QueueApi queueApi()
    {
        return queueApi;
    }

    public StatisticsApi statisticsApi()
    {
        return statisticsApi;
    }

    public SystemApi systemApi()
    {
        return systemApi;
    }

    public ConfigurationAsCodeApi configurationAsCodeApi()
    {
        return configurationAsCodeApi;
    }

    public UserApi userApi()
    {
        return userApi;
    }

    // ---------------------------------------------------------------------
    // Builder
    // ---------------------------------------------------------------------
    public static final class Builder
    {
        public Map<String, Object> properties;
        private URI endpoint;
        private Object[] components;
        private JenkinsAuthentication creds;

        public Builder endpoint(String endpoint)
        {
            this.endpoint = URI.create(endpoint);
            return this;
        }

        public Builder credentials(JenkinsAuthentication credentials)
        {
            this.creds = credentials;
            return this;
        }

        public Builder components(Object[] components)
        {
            this.components = components;
            return this;
        }

        public Builder properties(Map<String,Object> properties)
        {
            this.properties = properties;
            return this;
        }

        public JenkinsApi build()
        {
            if (endpoint == null)
            {
                throw new IllegalStateException("Endpoint must be set");
            }

            if (creds == null)
            {
                throw new IllegalStateException("Credentials must be set");
            }
            return new JenkinsApi(this);
        }
    }

    private final class AllTrustedConnectionFactory {
        private AllTrustedConnectionFactory() {
            //no-op ssl utility methods
        }


        /**
         * @return a ssl context that trusts all certificates / specially those self-signed
         */
        public static SSLContext getSslContext() {
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, new TrustManager[]{new UnSecureTrustManager()}, new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                throw new IllegalStateException(ex);
            }
            return sslContext;
        }

        /**
         * @return a verifier that always allows the given hostname
         */
        @SuppressWarnings("java:S5527")
        public static HostnameVerifier getHostnameVerifier() {
            return (String hostname, javax.net.ssl.SSLSession sslSession) -> true;
        }


        private static class UnSecureTrustManager implements X509TrustManager
        {

            @SuppressWarnings("java:S4830")
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                //no-op all is trusted
            }

            @SuppressWarnings("java:S4830")
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                //no-op all is trusted
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }
    }
}

