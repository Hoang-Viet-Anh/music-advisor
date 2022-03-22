package advisor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Advisor advisor = new Advisor();
        String choice;
        String category = "";

        while (true) {
            choice = scanner.nextLine();
            if (choice.contains("playlists")) {
                category = choice.substring(choice.indexOf(" ") + 1);
                choice = choice.substring(0, choice.indexOf(" "));
            }

            switch (choice) {
                case "new":
                    advisor.printNew();
                    break;
                case "featured":
                    advisor.printFeatured();
                    break;
                case "categories":
                    advisor.printCategories();
                    break;
                case "playlists":
                    advisor.printPlaylists(category);
                    break;
                case "exit":
                    advisor.printExit();
                    System.exit(0);
                default:
                    break;
            }
        }
    }
}
