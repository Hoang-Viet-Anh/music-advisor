package advisor.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class AuthServer {
    private HttpServer server;
    private String authCode;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public AuthServer(int port) {
        try {
            this.server = HttpServer.create();
            this.server.bind(new InetSocketAddress(port), 0);
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    String query = "post".equalsIgnoreCase(httpExchange.getRequestMethod()) ?
                            new String(httpExchange.getRequestBody().readAllBytes()) : httpExchange.getRequestURI().getQuery();
                    String answer;
                    if (query == null || !query.contains("code=")) {
                        answer = "Authorization code not found. Try again.";
                        httpExchange.sendResponseHeaders(200, answer.length());
                        httpExchange.getResponseBody().write(answer.getBytes());
                        httpExchange.getResponseBody().close();
                    }

                    if (query != null && query.contains("code=")) {
                        authCode = getCode(query);
                        answer = "Got the code. Return back to your program.";
                        httpExchange.sendResponseHeaders(200, answer.length());
                        httpExchange.getResponseBody().write(answer.getBytes());
                        httpExchange.getResponseBody().close();

//                        if (authCode.indexOf("code=") != 0) {
//                            authCode = authCode.substring(authCode.indexOf("code="));
//                        }
//                        if (authCode.indexOf('&') != -1) {
//                            authCode = authCode.substring(0, authCode.indexOf('&'));
//                        }
                        System.out.println("code received");
                    }
                }
            });
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void startServer() {
        this.server.start();
    }
    public void stopServer() {
        this.server.stop(1);
    }

    public String getCode(String query) {
        Map<String, String> parameters = new HashMap<>();
        for (String values :
                query.split("&")) {
            String[] keyVal = values.split("=");
            if (!keyVal[0].isEmpty()) {
                parameters.put(keyVal[0], keyVal[1]);
            }
        }
        return "code=" + parameters.getOrDefault("code", "");
    }
}
