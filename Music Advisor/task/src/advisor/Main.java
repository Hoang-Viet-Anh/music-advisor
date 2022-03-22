package advisor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Advisor advisor = new Advisor();
        String choice;
        String category = "";

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
                    advisor.printNew();
                    break;
                case "featured":
                    //Print featured music
                    advisor.printFeatured();
                    break;
                case "categories":
                    //Print categories
                    advisor.printCategories();
                    break;
                case "playlists":
                    //Print music by category
                    advisor.printPlaylists(category);
                    break;
                case "auth":
                    //Authenticate by spotify
                    advisor.printAuth();
                    break;
                case "exit":
                    //Exit program
                    advisor.printExit();
                    System.exit(0);
                default:
                    break;
            }
        }
    }
}
