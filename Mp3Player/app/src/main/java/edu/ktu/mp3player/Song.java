package edu.ktu.mp3player;

public class Song {
    int path;
    String name;

    public Song(int path, String name) {
        this.path = path;
        this.name = name;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
