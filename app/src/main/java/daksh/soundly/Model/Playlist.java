package daksh.soundly.Model;

public class Playlist {
    public String name;
    public int numberOfsong;

    public Playlist(String name, int numberOfsong) {
        this.name = name;
        this.numberOfsong = numberOfsong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfsong() {
        return numberOfsong;
    }

    public void setNumberOfsong(int numberOfsong) {
        this.numberOfsong = numberOfsong;
    }
}
