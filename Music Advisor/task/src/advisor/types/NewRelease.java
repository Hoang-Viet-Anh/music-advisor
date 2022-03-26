package advisor.types;

import java.util.Arrays;
import java.util.List;

public class NewRelease {
    private String name;
    private List<String> artists;
    private String url;

    public NewRelease(String name, List<String> artists, String url) {
        this.name = name;
        this.artists = artists;
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getArtists() {
        return artists;
    }
    public void setArtists(List<String> artists) {
        this.artists = artists;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return String.format("%s%n%s%n%s%n", name, artists.toString(), url);
    }
}
