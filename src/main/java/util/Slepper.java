package util;

import static java.lang.Thread.sleep;

public class Slepper {

    public static void sleepSeconds(int seconds) {
        try {
            sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
