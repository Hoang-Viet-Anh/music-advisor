package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Client {

    private HttpClient httpClient;
    private HttpRequest httpRequest;
    private HttpResponse<String> httpResponse;
    private String accessToken;
    private Map<String, String> categoryList;

    public Client() {
        httpClient = HttpClient.newHttpClient();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    public HttpResponse<String> getHttpResponse() {
        return httpResponse;
    }
    public void setHttpResponse(HttpResponse<String> httpResponse) {
        this.httpResponse = httpResponse;
    }

    public String getAccessToken(String uri, String authCode, String redirectUri) {
        System.out.println("making http request for access_token...");
        try {
            httpRequest = HttpRequest.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic "
                            + Base64.getEncoder()
                            .encodeToString("7dce42acd52a4da183fa91e6cb8727b4:58171254e1934c11b0057bb503cf9a4d".getBytes()))
                    .uri(URI.create(String.format("%s/api/token", uri)))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("%s" +
                                            "&grant_type=authorization_code" +
                                            "&redirect_uri=%s"
                                    , authCode, redirectUri)))
                    .build();
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String responseText = httpResponse.body();
            JsonObject jsonObject = JsonParser.parseString(responseText).getAsJsonObject();
            accessToken = jsonObject.get("access_token").getAsString();
            return httpResponse.body();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void getNewReleases(String apiPath) {
        apiPath = apiPath.concat("/v1/browse/new-releases");
        String albumName = "";
        List<String> list = new ArrayList<>();
        String albumUrl = "";
        String response = "";
        String artistName;

        response = makeRequest(apiPath);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject albumsList = jsonObject.getAsJsonObject("albums");

        JsonArray items = albumsList.getAsJsonArray("items");
        for (JsonElement item :
                items) {
            JsonObject album = item.getAsJsonObject();
            for (JsonElement artist:
                 album.getAsJsonArray("artists")) {
                artistName = artist.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
                list.add(artistName);
            }
            albumName = item.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
            albumUrl = item.getAsJsonObject().getAsJsonObject("external_urls").getAsJsonPrimitive("spotify").getAsString();
            System.out.println(albumName);
            System.out.println(list);
            System.out.println(albumUrl);
            System.out.println();
            list.clear();
        }

    }
    public void getFeatured(String apiPath) {
        apiPath = apiPath.concat("/v1/browse/featured-playlists");
        String playlistName = "";
        String playlistUrl = "";
        String response = "";

        response = makeRequest(apiPath);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject playlists = jsonObject.getAsJsonObject("playlists");

        for (JsonElement item :
                playlists.getAsJsonArray("items")) {
            playlistName = item.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
            playlistUrl = item.getAsJsonObject().getAsJsonObject("external_urls")
                    .getAsJsonPrimitive("spotify").getAsString();
            System.out.println(playlistName);
            System.out.println(playlistUrl);
            System.out.println();
        }
    }
    public void getCategories(String apiPath) {
        apiPath = apiPath.concat("/v1/browse/categories");
        String categoryName = "";
        String categoryId = "";
        String response = "";
        categoryList = new LinkedHashMap<>();

        response = makeRequest(apiPath);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject categories = jsonObject.getAsJsonObject("categories");

        for (JsonElement category :
                categories.getAsJsonArray("items")) {
            categoryName = category.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
            categoryId = category.getAsJsonObject().getAsJsonPrimitive("id").getAsString();
            categoryList.put(categoryName, categoryId);
            System.out.println(categoryName);
        }
    }
    public void getPlaylists(String apiPath, String category) {
        fillCategoryList(apiPath);
        String response = "";
        String message = "";

        if (categoryList.isEmpty() && !categoryList.containsKey(category)) {
            System.out.println("Unknown category name.");
        } else if (categoryList.containsKey(category)){
            apiPath = apiPath.concat(String.format("/v1/browse/categories/%s/playlists", categoryList.get(category)));
            String playlistName = "";
            String playlistUrl = "";

            response = makeRequest(apiPath);

            if (response.contains("\"error\"")) {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                message = jsonObject.getAsJsonObject("error")
                        .getAsJsonPrimitive("message").getAsString();
                System.out.println(message);
            } else {

                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                JsonObject playlists = jsonObject.getAsJsonObject("playlists");

                for (JsonElement item :
                        playlists.getAsJsonArray("items")) {
                    playlistName = item.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
                    playlistUrl = item.getAsJsonObject().getAsJsonObject("external_urls")
                            .getAsJsonPrimitive("spotify").getAsString();
                    System.out.println(playlistName);
                    System.out.println(playlistUrl);
                    System.out.println();
                }
            }
        } else {
            apiPath = apiPath.concat(String.format("/v1/browse/categories/%s/playlists", category));

            response = makeRequest(apiPath);

            if (response.contains("\"error\"")) {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                message = jsonObject.getAsJsonObject("error")
                        .getAsJsonPrimitive("message").getAsString();
                System.out.println(message);
            }
        }
    }
    public void fillCategoryList(String apiPath) {
        apiPath = apiPath.concat("/v1/browse/categories");
        String categoryName = "";
        String categoryId = "";
        String response = "";
        categoryList = new LinkedHashMap<>();

        response = makeRequest(apiPath);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject categories = jsonObject.getAsJsonObject("categories");

        for (JsonElement category :
                categories.getAsJsonArray("items")) {
            categoryName = category.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
            categoryId = category.getAsJsonObject().getAsJsonPrimitive("id").getAsString();
            categoryList.put(categoryName, categoryId);
        }
    }

    public String makeRequest(String apiPath) {
        String response = "";
        try {
            httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(apiPath))
                    .GET()
                    .build();
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            response = httpResponse.body();
        } catch (IOException | InterruptedException exc) {
            exc.printStackTrace();
        }
        return response;
    }
}