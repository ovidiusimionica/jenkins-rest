package com.cdancy.jenkins.rest.filters;


import com.cdancy.jenkins.rest.domain.crumb.Crumb;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class CrumbMessageBodyReader implements MessageBodyReader<Crumb> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return Crumb.class.isAssignableFrom(type);
    }

    @Override
    public Crumb readFrom(Class<Crumb> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream)
        throws IOException, WebApplicationException {

        // 1) Parse stream into Crumb
        Crumb crumb = mapper.readValue(entityStream, Crumb.class);

        // 2) Check header injected by the response filter and set jsessionId if present
        String jsession = null;
        if (httpHeaders != null) {
            jsession = httpHeaders.getFirst("X-JSESSIONID");
            if (jsession == null) {
                // fallback to checking Set-Cookie if needed
                String setCookie = httpHeaders.getFirst("Set-Cookie");
                if (setCookie == null) {
                    setCookie = httpHeaders.getFirst("set-cookie");
                }
                if (setCookie != null) {
                    jsession = setCookie;
                }
            }
        }

        if (jsession != null && !jsession.isEmpty()) {
            crumb.setSessionIdCookie(jsession);
        }

        return crumb;
    }
}
