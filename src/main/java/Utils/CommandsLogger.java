package Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryukki on 29.03.2018.
 */
public class CommandsLogger {
    private static String commandsLogFilePath = "resources/Logs/CommandsLog.txt";
    private File logFile;

    public CommandsLogger(){
        logFile = new File(commandsLogFilePath);
    }

    public void logCommand(String command) throws IOException {
        FileUtils.writeStringToFile(logFile, command + "\r\n", Charset.defaultCharset(), true);
    }

    public List<String> getCommandsAndStartNewLog(){
        List<String> previousCommands = new ArrayList<>();
        try {
            previousCommands = FileUtils.readLines(logFile, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return previousCommands;
    }

    public void newCommandsLog(){
        if(logFile!=null){
            try {
                FileUtils.writeStringToFile(logFile, "", Charset.defaultCharset(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            logFile = new File(commandsLogFilePath);
        }
    }
}
