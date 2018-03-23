package Communication;

import Utils.Enums.CommandTypes;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

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

    public RequestFactory(boolean environment){
        if(environment==false){
            url=simulationUrl;
        }else{
            url=chaarrUrl;
        }
    }

    public Request getRequest(CommandTypes action) {
        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "}";

        return buildRequest(command);
    }

    public Request getRequest(CommandTypes action, Enum parameter) {
        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "'Parameter':'" + parameter.toString() + "',"
                + "}";

        return buildRequest(command);
    }

    public Request getRequest(CommandTypes action, Enum parameter, int value) {
        String command = "{'Command':'" + action + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "'Parameter':'" + parameter.toString() + "',"
                + "'Value':'" + value + "',"
                + "}";

        return buildRequest(command);
    }

    private Request buildRequest(String command){
        MediaType mediaType = MediaType.parse(requestMediaType);
        RequestBody body = RequestBody.create(mediaType, command);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        return request;
    }
}
