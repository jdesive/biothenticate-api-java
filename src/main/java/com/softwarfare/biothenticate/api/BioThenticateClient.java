package com.softwarfare.biothenticate.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwarfare.biothenticate.api.exceptions.BadCredentialsException;
import com.softwarfare.biothenticate.api.exceptions.ServerErrorException;
import com.softwarfare.biothenticate.api.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class BioThenticateClient {

    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String url;
    private String token;

    private TokenUser tokenPayload;

    public BioThenticateClient(String url, String email, String password) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(32 * 1000)
                .setConnectionRequestTimeout(32 * 1000)
                .setSocketTimeout(32 * 1000).build();
        this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        this.url = url;
        try {
            LoginResponse response = this.login(email, password);
            this.token = response.getToken();
            this.tokenPayload = this.parseToken(this.token);
        } catch (IOException e) {
            log.error("Error logging in with supplied credentials", e);
        }
    }

    public BioThenticateClient(String email, String password) {
        this("http://biothenticate.net:9502", email, password);
    }

    public UUID getTenantId() {
        return this.tokenPayload.getTenantId();
    }

    public SubscriptionType getSubscriptionType() {
        return this.tokenPayload.getSubscriptionType();
    }

    public TokenUser parseToken(String token) throws JsonProcessingException {
        String[] split_string = token.split("\\.");
        String base64EncodedBody = split_string[1];
        return objectMapper.readValue(new String(Base64.decodeBase64(base64EncodedBody)), TokenUser.class);
    }

    /**
     * Set http connection timeout
     *
     * @param timeout Timeout is seconds. Default: 32sec
     */
    public BioThenticateClient setConnectionTimeout(int timeout) {
        this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build()).build();
        return this;
    }

    /*
        Users
     */

    /**
     * Get paginated list of users in tenant
     *
     * @param page Page number
     * @param size Page size
     * @return #{$PaginatedList<User>}
     * @throws IOException If error with sending request
     */
    public PaginatedList<User> getUsers(int page, int size) throws IOException {
        HttpGet httpGet = new HttpGet(url + Endpoints.USER.getPath() + "?tenantId=" + this.getTenantId() + "&page=" + page + "&size=" + size);
        this.setHeaders(httpGet, true);

        JsonNode node = makeRequest(httpGet, JsonNode.class);
        return getPaginatedList(node, User[].class);
    }

    /**
     * Get all users in tenant
     *
     * @param filter Filter for users
     * @param showInactive True if the list should include inactive users
     * @return #{$User[]}
     * @throws IOException If error with sending request
     */
    public User[] getAllUsers(UserFilter filter, boolean showInactive) throws IOException {
        HttpGet httpGet = new HttpGet(url + Endpoints.USER_ALL.getPath() + "?tenantId=" + this.getTenantId() + "&filter=" + filter.getValue() + "&inactive=" + showInactive);
        this.setHeaders(httpGet, true);

        return makeRequest(httpGet, User[].class);
    }

    /*
        Biometric Authentication
     */

    /**
     * Request a biometric MFA
     *
     * @param authType Biometrics type
     * @param email Biometric user
     * @param message Message in request
     * @param request Request message in request??
     * @param title Title in request
     * @return #{$AuthenticationResponse}
     * @throws IOException If error with sending request
     */
    public AuthenticateResponse authenticate(AuthenticationType authType, String email, String message, String request, String title) throws IOException {
        HttpPost httpPost = new HttpPost(url + Endpoints.AUTHENTICATE.getPath());
        AuthenticateRequest request1 = new AuthenticateRequest();
        request1.setAuthType(authType.getValue());
        request1.setEmail(email);
        request1.setMessage(message);
        request1.setRequest(request);
        request1.setTitle(title);

        String body = objectMapper.writeValueAsString(request1);
        StringEntity entity = new StringEntity(body);
        httpPost.setEntity(entity);
        this.setHeaders(httpPost, true);
        return makeRequest(httpPost, AuthenticateResponse.class);
    }

    /*
        Authentication
     */

    /**
     * Logs in to the BioThenticate application
     *
     * @param email The email of the user
     * @param password The pass word of the user
     * @return #{$LoginResponse}
     * @throws IOException If error with sending request
     */
    public LoginResponse login(String email, String password) throws IOException {
        HttpPost httpPost = new HttpPost(url + Endpoints.LOGIN.getPath());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        String body = objectMapper.writeValueAsString(loginRequest);
        StringEntity entity = new StringEntity(body);

        httpPost.setEntity(entity);
        this.setHeaders(httpPost, false);
        return makeRequest(httpPost, LoginResponse.class);
    }

    /**
     * Makes a request to BioThenticate and returns the response as a model.
     *
     * @param method HttpMethod
     * @param clazz The response model class
     * @param <T> The Response
     * @return Response object model
     * @throws IOException - If error with sending request
     */
    public <T> T makeRequest(HttpRequestBase method,  Class<T> clazz) throws IOException {
        try (CloseableHttpResponse response1 = httpClient.execute(method)) {

            if (response1.getStatusLine().getStatusCode() == 401) {
                throw new BadCredentialsException("Unauthorized. Bad Credentials. Status code: " + 401);
            }

            if (response1.getStatusLine().getStatusCode() == 500) {
                throw new ServerErrorException("Server 500 error");
            }

            HttpEntity entity = response1.getEntity();
            String body = new String(entity.getContent().readAllBytes());
            T response = objectMapper.readValue(body, clazz);
            EntityUtils.consume(entity);
            return response;
        }
    }

    /*
        Private Methods
     */

    private void setHeaders(HttpRequestBase base, boolean auth) {
        base.setHeader("Content-Type", "application/json");
        if (auth) {
            base.setHeader("Authorization", "Bearer " + this.token);
        }
    }

    private <T> PaginatedList<T> getPaginatedList(JsonNode node, Class<T[]> clazz) throws JsonProcessingException {
        PaginatedList<T> list = new PaginatedList<>();
        list.setContent(objectMapper.readValue(objectMapper.writeValueAsString(node.get("content")), clazz));
        list.setEmpty(node.get("empty").asBoolean());
        list.setPageElements(node.get("pageElements").asInt());
        list.setPageNumber(node.get("pageNumber").asInt());
        list.setPageSize(node.get("pageSize").asInt());
        list.setTotalElements(node.get("totalElements").asInt());
        list.setTotalPages(node.get("totalPages").asInt());
        return list;
    }

}
