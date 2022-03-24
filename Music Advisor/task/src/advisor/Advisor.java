package advisor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Locale;

public class Advisor {

    private boolean access = false;
    private String redirect_uri = "http://localhost:8080";
    private AuthServer server;

    public void printNew() {
        if (access) {
            System.out.println("---NEW RELEASES---\n" +
                    "Mountains [Sia, Diplo, Labrinth]\n" +
                    "Runaway [Lil Peep]\n" +
                    "The Greatest Show [Panic! At The Disco]\n" +
                    "All Out Life [Slipknot]");
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printFeatured() {
        if (access) {
            System.out.println("---FEATURED---\n" +
                    "Mellow Morning\n" +
                    "Wake Up and Smell the Coffee\n" +
                    "Monday Motivation\n" +
                    "Songs to Sing in the Shower");
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printCategories() {
        if (access) {
            System.out.println("---CATEGORIES---\n" +
                    "Top Lists\n" +
                    "Pop\n" +
                    "Mood\n" +
                    "Latin");
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printPlaylists(String category) {
        if (access) {
            System.out.printf("---%s PLAYLISTS---\n" +
                    "Walk Like A Badass  \n" +
                    "Rage Beats  \n" +
                    "Arab Mood Booster  \n" +
                    "Sunday Stroll\n", category.toUpperCase(Locale.ROOT));
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printExit() {
        System.out.println("---GOODBYE!---");
    }
    public void printAuth(String uri) {
        int timeout = 30;
        String authCode = null;
        System.out.println("use this link to request the access code:");
        System.out.println(String.format("%s/authorize?client_id=7dce42acd52a4da183fa91e6cb8727b4&redirect_uri=%s&response_type=code", uri, redirect_uri));
        System.out.println("waiting for code...");
        server = new AuthServer(8080);
        server.startServer();
        while (authCode == null && timeout > 0) {
            authCode = server.getAuthCode();
            timeout--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        server.stopServer();
        if (authCode != null) {
            String response = getAccessToken(uri, authCode);
            if (response != null && response.contains("access_token")) {
                System.out.printf("response:%n%s%n", response);
                System.out.println("---SUCCESS---");
                access = true;
            }
        }
    }

    public String getAccessToken(String uri, String authCode) {
        System.out.println("making http request for access_token...");
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic "
                            + Base64.getEncoder()
                            .encodeToString("7dce42acd52a4da183fa91e6cb8727b4:58171254e1934c11b0057bb503cf9a4d".getBytes()))
                    .uri(URI.create(String.format("%s/api/token", uri)))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("%s" +
                                            "&grant_type=authorization_code" +
                                            "&redirect_uri=%s"
                                    , authCode, redirect_uri)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
