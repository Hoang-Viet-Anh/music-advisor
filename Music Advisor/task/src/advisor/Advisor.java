package advisor;

import advisor.http.Client;

import java.util.*;

public class Advisor {
    private static final Advisor INSTANCE = new Advisor();
    private boolean access;
    private final String redirectUri = "http://localhost:8080";
    private Client client = Client.getInstance();
    private View view = View.getInstance();

    private Advisor() {}

    public static Advisor getInstance() {
        return INSTANCE;
    }

    public boolean printNew(String apiPath) {
        if (access) {
            client.getNewReleases(apiPath);
        } else {
            System.out.println("Please, provide access for application.");
        }
        return access;
    }
    public boolean printFeatured(String apiPath) {
        if (access) {
            client.getFeatured(apiPath);
        } else {
            System.out.println("Please, provide access for application.");
        }
        return access;
    }
    public boolean printCategories(String apiPath) {
        if (access) {
            client.getCategories(apiPath);
        } else {
            System.out.println("Please, provide access for application.");
        }
        return access;
    }
    public boolean printPlaylists(String apiPath, String category) {
        if (access) {
            client.getPlaylists(apiPath, category);
        } else {
            System.out.println("Please, provide access for application.");
        }
         return access;
    }
    public void printExit() {
        System.exit(0);
    }

    public void printAuth(String uri) {
        access = client.getAuthCode(uri, redirectUri);
    }

    public void startAdvisor(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String choice;
        String category = "";
        access = false;

        int index = Arrays.asList(args).indexOf("-access");
        String uri = index == -1 ? "https://accounts.spotify.com" : args[index + 1];

        index = Arrays.asList(args).indexOf("-resource");
        String apiPath = index == -1 ? "https://api.spotify.com" : args[index + 1];

        index = Arrays.asList(args).indexOf("-page");
        int pages = index == -1 ? 5 : Integer.parseInt(args[index + 1]);
        view.setLimit(pages);

        //Main loop
        while (true) {

            choice = scanner.nextLine();
            try {
                if (choice.contains("playlists ")) {
                    category = choice.substring(choice.indexOf(" ") + 1);
                    choice = choice.substring(0, choice.indexOf(" "));
                }
            } catch (IndexOutOfBoundsException exc) {
                choice = choice.substring(0, choice.indexOf(" "));
            }

            switch (choice) {
                case "new":
                    //Print new releases
                        if (printNew(apiPath)) {
                            View.printPage(client.getNewReleaseList().listIterator(),
                                    client.getNewReleaseList(), scanner);
                        }
                    break;
                case "featured":
                    //Print featured music
                        if (printFeatured(apiPath)) {
                            View.printPage(client.getFeaturedList().listIterator(),
                                    client.getFeaturedList(), scanner);
                        }
                    break;
                case "categories":
                    //Print categories
                        if (printCategories(apiPath)) {
                            View.printPage(client.getCategoryList().listIterator(),
                                    client.getCategoryList(), scanner);
                        }
                    break;
                case "playlists":
                    //Print music by category
                        if (printPlaylists(apiPath, category) && !client.getPlaylists().isEmpty()) {
                            View.printPage(client.getPlaylists().listIterator(),
                                    client.getPlaylists(), scanner);
                        }
                    break;
                case "auth":
                    //Authenticate by spotify
                    printAuth(uri);
                    break;
                case "exit":
                    //Exit program
                    printExit();
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }
    }
}
