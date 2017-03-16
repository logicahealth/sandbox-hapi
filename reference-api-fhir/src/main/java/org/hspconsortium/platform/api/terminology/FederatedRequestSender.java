package org.hspconsortium.platform.api.terminology;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@RestController
@RequestMapping(value = "/federated")
public class FederatedRequestSender {

    @Value("${hspc.platform.api.fhir.federatedEndpointURL}")
    private String federatedEndpointURL;

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health() {
        return "OK";
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleFederatedRequest(HttpServletRequest request, HttpServletResponse response) {
        Map parameters = request.getParameterMap();
        HttpGet httpGetRequest;
        try {
            httpGetRequest = new HttpGet(buildWithParameters(parameters, federatedEndpointURL).build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(
                    String.format("There was an error creating the Http Request.\n" +
                                    "Error : %s ."
                            , e.getCause()));
        }
        httpGetRequest.addHeader("Accept", "application/json");

        sendRequest(httpGetRequest, response);
    }

    protected URIBuilder buildWithParameters(Map parameters, String endpoint) {
        String[] uri = (String[]) parameters.get("uri");
        String[] pathAndQuery = uri[0].split("\\?");

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        String[] params = pathAndQuery[1].split("&");
        for (String keyValuePair : params) {
            String keyValuePairArray[] = keyValuePair.split("=");
            nameValuePairs.add(new BasicNameValuePair(keyValuePairArray[0], keyValuePairArray[1]));
        }

        return configureBuilder("http", endpoint, pathAndQuery[0], nameValuePairs);
    }

    protected URIBuilder configureBuilder(String schema, String host, String path, List<NameValuePair> parameters) {
        return new URIBuilder()
                .setScheme(schema)
                .setHost(host)
                .setPath(path)
                .setParameters(parameters);
    }

    protected void sendRequest(HttpUriRequest theRequest, HttpServletResponse response) {
        try {
            CloseableHttpClient httpClient = HttpClients.custom().build();
            try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(theRequest)) {
                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                    HttpEntity rEntity = closeableHttpResponse.getEntity();
                    String responseString = EntityUtils.toString(rEntity, StandardCharsets.UTF_8);
                    throw new RuntimeException(String.format("There was a problem contacting the terminology server.\n" +
                                    "Response Status : %s .\nResponse Detail :%s."
                            , closeableHttpResponse.getStatusLine()
                            , responseString));
                }
                response.setHeader("Content-Type", "application/json;charset=utf-8");
                response.getWriter().write(EntityUtils.toString(closeableHttpResponse.getEntity()));
            } finally {
                httpClient.close();
            }
        } catch (IOException io_ex) {
            throw new RuntimeException("Error sending request", io_ex);
        }
    }

}
