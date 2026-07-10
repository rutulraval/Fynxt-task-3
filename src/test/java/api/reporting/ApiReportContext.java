package api.reporting;

import io.restassured.response.Response;

public final class ApiReportContext {

    private static final ThreadLocal<ApiTransaction> LAST_TRANSACTION = new ThreadLocal<>();

    private ApiReportContext() {
    }

    public static void record(String method, String endpoint, Object requestBody, Response response) {
        String responseBody = response == null ? "<no response>" : response.asPrettyString();
        String responseHeaders = response == null ? "<no response>" : response.getHeaders().toString();
        int statusCode = response == null ? -1 : response.getStatusCode();
        long responseTimeMs = response == null ? -1 : response.getTime();
        String contentType = response == null ? "<unknown>" : String.valueOf(response.getContentType());

        LAST_TRANSACTION.set(new ApiTransaction(
                method,
                endpoint,
                requestBody == null ? "<empty>" : requestBody.toString(),
                statusCode,
                responseTimeMs,
                contentType,
                responseHeaders,
                responseBody));
    }

    public static ApiTransaction getCurrentTransaction() {
        return LAST_TRANSACTION.get();
    }

    public static void clear() {
        LAST_TRANSACTION.remove();
    }

    public static final class ApiTransaction {
        private final String method;
        private final String endpoint;
        private final String requestBody;
        private final int statusCode;
        private final long responseTimeMs;
        private final String contentType;
        private final String responseHeaders;
        private final String responseBody;

        private ApiTransaction(String method,
                               String endpoint,
                               String requestBody,
                               int statusCode,
                               long responseTimeMs,
                               String contentType,
                               String responseHeaders,
                               String responseBody) {
            this.method = method;
            this.endpoint = endpoint;
            this.requestBody = requestBody;
            this.statusCode = statusCode;
            this.responseTimeMs = responseTimeMs;
            this.contentType = contentType;
            this.responseHeaders = responseHeaders;
            this.responseBody = responseBody;
        }

        public String getRequestSummary() {
            return String.join(System.lineSeparator(),
                    "Method: " + method,
                    "Endpoint: " + endpoint,
                    "Request Body: " + requestBody);
        }

        public String getResponseSummary() {
            return String.join(System.lineSeparator(),
                    "Status Code: " + statusCode,
                    "Response Time (ms): " + responseTimeMs,
                    "Content-Type: " + contentType,
                    "Headers: " + responseHeaders,
                    "Body:",
                    responseBody);
        }
    }
}

