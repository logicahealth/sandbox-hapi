package org.hspconsortium.platform.api.terminology;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hspconsortium.platform.api.proxy.HttpProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TerminologyProxyConfiguration {

    public static final int NO_PORT = -1;

    @Bean
    public RestTemplate restTemplate() {
        HttpClient client = HttpClientBuilder.create().build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }

    @Bean
    @Autowired
    public HttpProxy httpProxy(
            RestTemplate restTemplate,
            @Value("${hspc.platform.api.fhir.terminology.proxy.protocol:http}") String terminologyProtocol,
            @Value("${hspc.platform.api.fhir.terminology.proxy.host}") String terminologyHost,
            @Value("${hspc.platform.api.fhir.terminology.proxy.port:" + NO_PORT + "}") String terminologyPort) {

        return new HttpProxy(
                restTemplate,
                terminologyProtocol,
                terminologyHost,
                Integer.parseInt(terminologyPort)
        );
    }
}
