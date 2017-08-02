package ru.stepf.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Searcher {
    File dir;
    FilenameFilter musicFilter;
    public Searcher(String dirPath)
    {
        dir = new File(dirPath);
        musicFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".mp3"))
                    return true;
                return false;
            }
        };
    }

    public ArrayList<File> search() {
        ArrayList<File> music = new ArrayList<>();
        File[] list = dir.listFiles(musicFilter);
        int length = list.length;
        for (int i = 0; i < length; i++) {
            music.add(dir.listFiles()[i]);
        }
        return music;
    }
}
