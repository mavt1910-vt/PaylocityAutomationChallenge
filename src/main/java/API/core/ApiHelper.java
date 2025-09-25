package API.core;

import Common.LogHelper;
import UI.core.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class ApiHelper {

    private static final String base = resolveBase();

    private ApiHelper() {}

    private static String resolveBase() {
        String apiBase = ConfigManager.getProperty("API_BASE_URL");
        if (apiBase != null && !apiBase.isBlank()) {
            LogHelper.info("API_BASE_URL detected: " + apiBase);
            return apiBase.replaceAll("/+$", "");
        }
        String url = ConfigManager.baseUrl();
        int idx = url.indexOf("/Prod");
        String derived = (idx > 0) ? url.substring(0, idx + 5) : url;
        LogHelper.info("Derived API base: " + derived);
        return derived.replaceAll("/+$", "");
    }

    private static RequestSpecification baseSpec() {
        return RestAssured.given()
                .baseUri(base)
                .relaxedHTTPSValidation()
                .header("Authorization", ConfigManager.getProperty("AUTH_CODE"))
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
    }

    private static RequestSpecification baseSpec(boolean withAuth) {
        RequestSpecification spec = RestAssured.given()
                .baseUri(base)
                .relaxedHTTPSValidation()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
        if (withAuth) {
            spec.header("Authorization", ConfigManager.getProperty("AUTH_CODE"));
        }
        return spec;
    }

    private static String norm(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) return "";
        return endpoint.startsWith("/") ? endpoint : "/" + endpoint;
    }

    public static Response get(String endpoint) {
        String ep = norm(endpoint);
        LogHelper.info("GET " + base + ep);
        Response resp = baseSpec().get(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }

    public static Response getNoAuth(String endpoint) {
        String ep = norm(endpoint);
        LogHelper.info("GET (no auth) " + base + ep);
        Response resp = baseSpec(false).get(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }

    public static Response delete(String endpoint) {
        String ep = norm(endpoint);
        LogHelper.info("DELETE " + base + ep);
        Response resp = baseSpec().delete(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }

    public static Response post(String endpoint, Object body) {
        String ep = norm(endpoint);
        LogHelper.info("POST " + base + ep);
        LogHelper.info("Request body:");
        LogHelper.pretty(body);
        Response resp = baseSpec().body(body).post(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }

    public static Response put(String endpoint, Object body) {
        String ep = norm(endpoint);
        LogHelper.info("PUT " + base + ep);
        LogHelper.info("Request body:");
        LogHelper.pretty(body);
        Response resp = baseSpec().body(body).put(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }

    public static Response patch(String endpoint, Object body) {
        String ep = norm(endpoint);
        LogHelper.info("PATCH " + base + ep);
        LogHelper.info("Request body:");
        LogHelper.pretty(body);
        Response resp = baseSpec().body(body).patch(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }

    private static RequestSpecification baseSpecNoContentType(boolean withAuth) {
        RequestSpecification spec = RestAssured.given()
                .baseUri(base)
                .relaxedHTTPSValidation()
                .accept(ContentType.JSON);
        if (withAuth) {
            spec.header("Authorization", ConfigManager.getProperty("AUTH_CODE"));
        }
        return spec;
    }

    public static Response postNoContentType(String endpoint, Object body) {
        String ep = norm(endpoint);
        LogHelper.info("POST (no Content-Type) " + base + ep);
        LogHelper.info("Request body:");
        LogHelper.pretty(body);
        Response resp = baseSpecNoContentType(true).body(body).post(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }


    public static Response postRawJson(String endpoint, String rawJson) {
        String ep = norm(endpoint);
        LogHelper.info("POST raw JSON " + base + ep);
        LogHelper.info("Request body:\n" + rawJson);
        Response resp = baseSpec().body(rawJson).post(ep);
        LogHelper.info("Status: " + resp.statusCode());
        LogHelper.prettyJson(resp.asString());
        return resp;
    }

}
