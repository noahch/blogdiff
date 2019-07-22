package ch.uzh.seal.BLogDiff.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

@Slf4j
public class FileUtils {

    public static void writeToSb(String path, String filename, String content) {
        try {
            checkIfDirectoryExistsOrCreate(path);
            FileWriter fileWriter = new FileWriter(path + filename);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(content);
            printWriter.close();
            log.info(String.format("File %s written.", filename));
        } catch (IOException e) {
            e.printStackTrace();
            log.error(String.format("File %s has not been written.", filename));
        }
    }

    public static void appendFile(String path, String filename, String content) {
        try {
            checkIfDirectoryExistsOrCreate(path);
            File file = new File(path + filename);
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
        }catch (IOException e) {
            log.error(String.format("Error appending file %s ", filename));
        }
    }

    public static String readFile(String file) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Files.lines(Paths.get(file)).forEach(s -> {
                stringBuilder.append(s + "\n");
            });
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(String.format("File %s could not been read.", file));
        }
        return "";

    }

    public static ArrayList<String> readFileAsList(String file) {
        try {
            Scanner s = new Scanner(new File(file));
            ArrayList<String> list = new ArrayList<>();
            while (s.hasNextLine()){
                list.add(s.nextLine());
            }
            s.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(String.format("File %s could not been read.", file));
        }
        return new ArrayList<>();

    }

    private static void checkIfDirectoryExistsOrCreate(String directoryPath){
        File directory = new File(directoryPath);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }

    public static boolean checkIfDirectoryExists(String directoryPath){
        File directory = new File(directoryPath);
        return directory.exists();
    }

}
