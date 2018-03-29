package Utils;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * Created by Ryukki on 29.03.2018.
 */
public class Logger {
    private static String temporaryLogFilePath = "resources/Logs/LogFile-";
    private File temporaryLogFile;

    public Logger(){
        newLogFile();
    }

    public void log(String logMessage) throws IOException {
        FileUtils.writeStringToFile(temporaryLogFile, logMessage, Charset.defaultCharset(), true);
    }

    public void newLogFile(){
        String path = makeNewPath();
        temporaryLogFile = new File(path);
    }

    private String makeNewPath(){
        String newPath = temporaryLogFilePath +  LocalDateTime.now().toString();
        newPath =newPath.replace('.', '-').replace(':', '-');
        newPath= newPath  + ".txt";
        return newPath;
    }
}
