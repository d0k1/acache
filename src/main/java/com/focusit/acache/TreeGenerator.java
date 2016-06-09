package com.focusit.acache;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Walks through directory to dump it hierarchical structure, i.e. get list of all it's files recursively.
 * Created by doki on 09.06.16.
 */
public class TreeGenerator {

    public static final String DIRS_BIN = "data" + File.separator + "dirs.bin";
    public static final String FILES_BIN = "data" + File.separator + "files.bin";

    public void load(List directories, List files) throws IOException, ClassNotFoundException {
        try (FileInputStream fos = new FileInputStream(new File(DIRS_BIN))) {
            try (ObjectInputStream oos = new ObjectInputStream(fos)) {
                directories.addAll((List) oos.readObject());
            }
        }

        System.out.println("Dirs: loaded " + directories.size());

        try (FileInputStream fos = new FileInputStream(new File(FILES_BIN))) {
            try (ObjectInputStream oos = new ObjectInputStream(fos)) {
                files.addAll((List) oos.readObject());
            }
        }

        System.out.println("Files: loaded " + files.size());
    }

    private void dump(Path dir, List directories, List files) throws IOException {
        Files.walk(dir).forEach(f -> {
            if (Files.isDirectory(f)) {
                directories.add(f.toString());
            } else {
                files.add(f.toString());
            }
        });

        System.out.println("Dirs:" + directories.size());
        System.out.println("Files:" + files.size());

        try (FileOutputStream fos = new FileOutputStream(new File(DIRS_BIN))) {
            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(directories);
                oos.flush();
            }
        }

        System.out.println("Dirs: dumped");

        try (FileOutputStream fos = new FileOutputStream(new File(FILES_BIN))) {
            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(files);
                oos.flush();
            }
        }

        System.out.println("Files: dumped");
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        List<String> dirs = new ArrayList<>();
        List<String> files = new ArrayList<>();

        Path dir = FileSystems.getDefault().getPath(args[0]);
        TreeGenerator generator = new TreeGenerator();
        generator.dump(dir, dirs, files);

        List<String> dirs1 = new ArrayList<>();
        List<String> files1 = new ArrayList<>();
        generator.load(dirs1, files1);
    }

}
