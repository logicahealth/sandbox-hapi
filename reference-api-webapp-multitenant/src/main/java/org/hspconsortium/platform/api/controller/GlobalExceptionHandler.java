package org.hspconsortium.platform.api.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.hspconsortium.platform.messaging.model.mail.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@ControllerAdvice
@Profile("globalexception")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${sender.name:HSPC Sandbox}")
    private String senderName;

    @Value("${sender.email:sandbox@hspconsortium.org}")
    private String senderEmail;

    @Value("${spring.application.name:Unknown Application}")
    private String applicationName;

    @Value("${hspc.platform.support.name:HSPC Support}")
    private String supportName;

    @Value("${hspc.platform.support.email:support@hspconsortium.org}")
    private String supportEmail;

    @Value("${hspc.platform.messaging.url}")
    private String messagingUrl;

    @Value("${hspc.platform.messaging.errorReportPath:/mailsender}")
    private String errorReportPath;

    private RestTemplate restTemplate = new RestTemplate();

    @ExceptionHandler(value = Exception.class)
    public void handleException(HttpServletRequest req, Exception e) throws Exception {
        logger.info("Begin handling exception: " + e.getMessage());
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it.
        // AnnotationUtils is a Spring Framework utility class.
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            if (responseStatus.value().is5xxServerError()) {
                // log 500's because they are server errors
                logger.error("Internal error handled in GlobalExceptionHandler", e);
                doSendErrorReport(e);
            }
            // don't create a report for other errors with ResponseStatus (ex: 400, 404)
        } else {
            // if the error isn't annotated with ResponseStatus
            // log it
            logger.error("Unknown error handled in GlobalExceptionHandler", e);
            // create an error report for unknown errors
            doSendErrorReport(e);
        }
        // throw it so the framework can deal with it
        throw e;
    }

    private void doSendErrorReport(Exception e) {
        if (messagingUrl != null && errorReportPath != null) {
            logger.info("Creating error report for exception: " + e.getMessage());
            String stackTrace = stackTraceToString(e);

            Message message = new Message(true, Message.ENCODING);
            message.setSubject("Error in " + applicationName);
            message.setAcceptHtmlMessage(true);

            message.setSenderName(senderName);
            message.setSenderEmail(senderEmail);
            message.addRecipient(supportName, supportEmail);

            message.setTemplateName("email-errorreport");
            message.setTemplateFormat(Message.TemplateFormat.HTML);

            message.addVariable("errorMessage", e.getMessage());
            message.addVariable("errorDetails", stackTrace);

            String url = messagingUrl + errorReportPath;

            try {
                sendEmailToMessaging(url, message);
            } catch (IOException ioe) {
                logger.error("Error sending email", ioe);
                throw new RuntimeException(ioe);
            }
        }
    }

    private void sendEmailToMessaging(String url, Message message) throws IOException {
        HttpPost postRequest = new HttpPost(url);
        postRequest.addHeader("Content-Type", "application/json");
        postRequest.addHeader("Accept", "application/json");

        postRequest.setEntity(new StringEntity(toJson(message)));
//        postRequest.setHeader("Authorization", "BEARER " + oAuthUserService.getBearerToken(request));

        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).useSSL().build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            logger.error("Error loading ssl context", e);
            throw new RuntimeException(e);
        }
        HttpClientBuilder builder = HttpClientBuilder.create();
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        builder.setSSLSocketFactory(sslConnectionFactory);
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
        builder.setConnectionManager(ccm);

        CloseableHttpClient httpClient = builder.build();

        try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(postRequest)) {
            if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                org.apache.http.HttpEntity rEntity = closeableHttpResponse.getEntity();
                String responseString = EntityUtils.toString(rEntity, StandardCharsets.UTF_8);
                String errorMsg = String.format("There was a problem sending the email.\n" +
                                "Response Status : %s .\nResponse Detail :%s. \nUrl: :%s",
                        closeableHttpResponse.getStatusLine(),
                        responseString,
                        url);
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

        } catch (IOException e) {
            logger.error("Error posting to " + url, e);
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("Error closing HttpClient");
            }
        }
    }

    private String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String toJson(Message message) {
        Gson gson = new Gson();
        Type type = new TypeToken<Message>() {
        }.getType();
        return gson.toJson(message, type);
    }

}
