package fr.firedream89.trieurseries;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Serie implements Iterable<File>, Iterator<File> {
    private String name;
    private String season;
    private List<File> episodes;

    public Serie(String name, String season) {
        this.name = name;
        this.season = season;
        episodes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null && !name.trim().isEmpty())
            this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        if(season != null && !season.trim().isEmpty())
            this.season = season;
    }

    public void addEpisode(File episode) {
        if(episode != null)
            this.episodes.add(episode);
    }

    public int count() {
        return episodes.size();
    }

    public File get(int episode) {
        return episodes.get(episode);
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer(name + " - Saison " + season);
        for(File e : episodes)
            str.append("\n   " + e.getName());
        return str.toString();
    }

    @Override
    public Iterator<File> iterator() {
        return episodes.iterator();
    }

    @Override
    public boolean hasNext() {
        return episodes.iterator().hasNext();
    }

    @Override
    public File next() {
        return episodes.iterator().next();
    }
}
