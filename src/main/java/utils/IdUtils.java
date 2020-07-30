package utils;

/**
 * @author lvcc
 */
public class IdUtils {

    private static int increaseId = 0;

    public synchronized static int getId() {
        increaseId++;
        return increaseId;
    }
}
