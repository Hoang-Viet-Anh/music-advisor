package advisor.http;

import advisor.*;
import advisor.types.Category;
import advisor.types.Featured;
import advisor.types.NewRelease;
import advisor.types.Playlists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class Client {

    private static final Client INSTANCE = new Client();
    private HttpClient httpClient;
    private HttpRequest httpRequest;
    private HttpResponse<String> httpResponse;
    private String accessToken;
    private LinkedList<Category> categoryList = new LinkedList<>();
    private LinkedList<NewRelease> newRelease = new LinkedList<>();
    private LinkedList<Featured> featured = new LinkedList<>();
    private LinkedList<Playlists> playlists = new LinkedList<>();
    private View view = View.getInstance();

    private Client() {
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    public static Client getInstance() {
        return INSTANCE;
    }

    public LinkedList<Category> getCategoryList() {
        return categoryList;
    }
    public LinkedList<NewRelease> getNewReleaseList() {
        return newRelease;
    }
    public LinkedList<Featured> getFeaturedList() {
        return featured;
    }
    public LinkedList<Playlists> getPlaylists() {
        return playlists;
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
    public boolean getAuthCode(String uri, String redirectUri) {
        int timeout = 30;
        String authCode = null;
        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=7dce42acd52a4da183fa91e6cb8727b4&redirect_uri=%s&response_type=code%n", uri, redirectUri);
        System.out.println("waiting for code...");
        AuthServer server = new AuthServer(8080);
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
            String response = getAccessToken(uri, authCode, redirectUri);
            if (response != null && response.contains("access_token")) {
                System.out.println("Success!");
                return true;
            }
        }
        return false;
    }

    public void getNewReleases(String apiPath) {
        newRelease.clear();
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
            newRelease.add(new NewRelease(albumName, List.copyOf(list), albumUrl));
            list.clear();
        }
    }
    public void getFeatured(String apiPath) {
        featured.clear();
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
            featured.add(new Featured(playlistName, playlistUrl));
        }
    }
    public void getCategories(String apiPath) {
        categoryList.clear();
        apiPath = apiPath.concat("/v1/browse/categories");
        String categoryName = "";
        String categoryId = "";
        String response = "";

        response = makeRequest(apiPath);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject categories = jsonObject.getAsJsonObject("categories");

        for (JsonElement category :
                categories.getAsJsonArray("items")) {
            categoryName = category.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
            categoryId = category.getAsJsonObject().getAsJsonPrimitive("id").getAsString();
            categoryList.add(new Category(categoryName, categoryId));
        }
    }
    public void getPlaylists(String apiPath, String category) {
        fillCategoryList(apiPath);
        playlists.clear();
        String response = "";
        String message = "";
        boolean validCategory = categoryList.stream()
                .anyMatch(category1 -> category1.getName().equalsIgnoreCase(category));

        if (categoryList.isEmpty() || !validCategory) {
            System.out.println("Unknown category name.");
        } else {
            Category categoryObject = categoryList.stream()
                    .filter(category1 -> category1.getName().equalsIgnoreCase(category))
                    .collect(Collectors.toList()).get(0);
            apiPath = apiPath.concat(String.format("/v1/browse/categories/%s/playlists", categoryObject.getId()));
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
                JsonObject jsonPlaylists = jsonObject.getAsJsonObject("playlists");

                for (JsonElement item :
                        jsonPlaylists.getAsJsonArray("items")) {
                    playlistName = item.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
                    playlistUrl = item.getAsJsonObject().getAsJsonObject("external_urls")
                            .getAsJsonPrimitive("spotify").getAsString();
                    playlists.add(new Playlists(playlistName, playlistUrl));
                }
            }
        }
    }
    public void fillCategoryList(String apiPath) {
        if (categoryList.isEmpty()) {
            apiPath = apiPath.concat("/v1/browse/categories");
            String categoryName = "";
            String categoryId = "";
            String response = "";

            response = makeRequest(apiPath);

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonObject categories = jsonObject.getAsJsonObject("categories");

            for (JsonElement category :
                    categories.getAsJsonArray("items")) {
                categoryName = category.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
                categoryId = category.getAsJsonObject().getAsJsonPrimitive("id").getAsString();
                categoryList.add(new Category(categoryName, categoryId));
            }
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