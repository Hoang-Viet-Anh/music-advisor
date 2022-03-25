package advisor;

import java.util.*;

public class Advisor {

    private boolean access = false;
    private final String redirectUri = "http://localhost:8080";
    private Client client = new Client();

    public void printNew(String apiPath) {
        if (access) {
            client.getNewReleases(apiPath);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printFeatured(String apiPath) {
        if (access) {
            client.getFeatured(apiPath);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printCategories(String apiPath) {
        if (access) {
            client.getCategories(apiPath);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printPlaylists(String apiPath, String category) {
        if (access) {
            client.getPlaylists(apiPath, category);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
    public void printExit() {
        System.exit(0);
    }
    public void printAuth(String uri) {
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
            String response = client.getAccessToken(uri, authCode, redirectUri);
            if (response != null && response.contains("access_token")) {
                System.out.println("Success!");
                access = true;
            }
        }
    }

    public void startAdvisor(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Advisor advisor = new Advisor();
        String choice;
        String category = "";

        int index = Arrays.asList(args).indexOf("-access");
        String uri = index == -1 ? "https://accounts.spotify.com" : args[index + 1];
        index = Arrays.asList(args).indexOf("-resource");
        String apiPath = index == -1 ? "https://api.spotify.com" : args[index + 1];

        //Main loop
        while (true) {
            choice = scanner.nextLine();
            if (choice.contains("playlists")) {
                category = choice.substring(choice.indexOf(" ") + 1);
                choice = choice.substring(0, choice.indexOf(" "));
            }

            switch (choice) {
                case "new":
                    //Print new releases
                    advisor.printNew(apiPath);
                    break;
                case "featured":
                    //Print featured music
                    advisor.printFeatured(apiPath);
                    break;
                case "categories":
                    //Print categories
                    advisor.printCategories(apiPath);
                    break;
                case "playlists":
                    //Print music by category
                    advisor.printPlaylists(apiPath, category);
                    break;
                case "auth":
                    //Authenticate by spotify
                    advisor.printAuth(uri);
                    break;
                case "exit":
                    //Exit program
                    advisor.printExit();
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }
    }
}
