package fr.firedream89.trieurseries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SeriesManager implements Iterable<Serie>, Iterator<Serie> {
    private List<Serie> series;

    public SeriesManager() {
        series = new ArrayList<>();
    }

    public List<Serie> getList() {
        return series;
    }

    public int count() {
        return series.size();
    }

    public Serie findSerie(String name, String season) {
        for(Serie serie : series) {
            if(serie.getName().equals(name) && serie.getSeason().equals(season))
                return serie;
        }
        return null;
    }

    public Iterator<Serie> iterator() {
        return series.iterator();
    }

    public void add(Serie serie) {
        series.add(serie);
    }


    @Override
    public String toString() {
        StringBuffer str = new StringBuffer("SerieManager : \n");
        str.append("Series count : ").append(this.count());
        str.append("\n");
        for(Serie serie : series)
            str.append(serie + "\n");
        return str.toString();
    }

    @Override
    public boolean hasNext() {
        return series.iterator().hasNext();
    }

    @Override
    public Serie next() {
        return series.iterator().next();
    }
}
