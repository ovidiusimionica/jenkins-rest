package com.cdancy.jenkins.rest.filters;


import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class JenkinsCrumbResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext req, ClientResponseContext res) throws IOException {
        List<String> cookies = res.getHeaders().get("Set-Cookie");
        if (cookies == null || cookies.isEmpty()) return;

        String jsessionId = cookies.stream()
            .filter(c -> c.startsWith("JSESSIONID="))
            .map(c -> c.split(";", 2)[0].replace("JSESSIONID=", ""))
            .findFirst()
            .orElse(null);

        // Optional: add as header for later access
        if (jsessionId != null) {
            res.getHeaders().putSingle("X-JSESSIONID", jsessionId);
        }
    }
}
