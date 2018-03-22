import okhttp3.*;

import java.io.IOException;

public class Main {

    private static String url = "https://simulation.future-processing.pl";
    private static String login ="<YOUR_LOGIN_PLEASE_CHANGE>";
    private static String token = "<YOUR_PASSWORD_PLEASE_CHANGE>";
    public static  String toCommand(String command, String login, String token) {
        return "{'Command':'" + command + "',"
                + "'Login':'" + login + "',"
                + "'Token':'" + token + "',"
                + "}";
    }
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, toCommand("Restart", login , token));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            System.out.println(response.toString());
        }
        System.in.read();
    }

}