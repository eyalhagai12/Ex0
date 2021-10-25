package ex0;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class my_utils {
    public static void log(String text) {
        try {
            File log_file = new File("C:\\Users\\eyal\\IdeaProjects\\Ex0_test\\Ex0_log.log");
            FileWriter log_writer = new FileWriter(log_file, true);

            log_writer.append(text).append("\n");

            log_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reset_log() {
        try {
            File log_file = new File("C:\\Users\\eyal\\IdeaProjects\\Ex0_test\\Ex0_log.log");
            FileWriter log_reset = new FileWriter(log_file);
            log_reset.write("");
            System.out.println("Log successfully reset");

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        log("test log append");
        reset_log();
    }
}
