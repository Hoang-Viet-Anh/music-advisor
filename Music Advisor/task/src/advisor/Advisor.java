package advisor;

import java.util.Locale;

public class Advisor {

    public boolean access = false;

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
    public void printAuth() {
        System.out.print("https://accounts.spotify.com/authorize" +
                "?client_id=7dce42acd52a4da183fa91e6cb8727b4&redirect_uri=https://localhost:8080&response_type=code\n");
        System.out.println("---SUCCESS---");
        access = true;
    }
}
