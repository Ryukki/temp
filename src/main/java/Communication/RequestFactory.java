package Communication;

import Utils.CommandsLogger;
import Utils.Enums.CommandTypes;
import Utils.Enums.Locations;
import Utils.Enums.Parameters;
import Utils.Logger;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Ryukki on 22.03.2018.
 */
public class RequestFactory{
    private static String simulationUrl = "https://simulation.future-processing.pl/";
    private static String chaarrUrl = "https://chaarr.future-processing.pl/";
    private static String url;
    private static String login ="kuba.widlak@gmail.com.google";
    private static String token = "1CF318553CA65A97E611D9B42F04A0B5";
    private static String requestMediaType = "application/json";

    private CommandsLogger logger;

    public RequestFactory(boolean environment){
        if(environment==false){
            url=simulationUrl;
        }else{
            url=chaarrUrl;
        }
        logger = new CommandsLogger();
    }

    public Request getRequest(CommandTypes action) {
        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "'"
                + "}";

        return buildRequest(command);
    }

    public Request getRequest(CommandTypes action, Enum parameter) {
        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "'Parameter':'" + parameter.toString() + "'"
                + "}";

        return buildRequest(command);
    }

    public Request getRequest(CommandTypes action, Enum parameter, int value) {
        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "'Parameter':'" + parameter.toString() + "',"
                + "'Value':'" + value + "'"
                + "}";

        return buildRequest(command);
    }

    public Request getRequest(CommandTypes action, Enum parameter, Locations value) {
        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "'Parameter':'" + parameter.toString() + "',"
                + "'Value':'" + value.toString() + "'"
                + "}";

        return buildRequest(command);
    }

    public Request getRequest(String command){
        return buildRequest(command);
    }

    public Request getRequest(CommandTypes action, Map<Parameters, Integer> restartSimulationParameters){
        String parameters = "";
        for(Map.Entry<Parameters, Integer> resetParameter: restartSimulationParameters.entrySet()){
            parameters += "'" + resetParameter.getKey().toString() + "': " + resetParameter.getValue() + ",";
        }

        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "'Parameter':{"
                + parameters
                + "}}";

        return buildRequest(command);
    }

    private Request buildRequest(String command){
        MediaType mediaType = MediaType.parse(requestMediaType);
        RequestBody body = RequestBody.create(mediaType, command);

        if(command.contains("Restart")||command.contains("RestartSimulation")){
            logger.newCommandsLog();
        }

        try {
            logger.logCommand(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(url+"execute")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        return request;
    }

    public Request getStatusRequest(){
        Request request = new Request.Builder()
                .url(url+"describe?login=" + login + "&token=" + token)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        return request;
    }

    public Request getPingRequest(){
        Request request = new Request.Builder()
                .url(url)
                .build();

        return request;
    }

    public List<String> getCommands(){
        return logger.getCommandsAndStartNewLog();
    }
}
