import Communication.RequestFactory;
import Utils.Enums.CommandTypes;
import Utils.Enums.Locations;
import okhttp3.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestFactory requestFactory = new RequestFactory(false);
        Request request = requestFactory.getRequest(CommandTypes.MoveTo, Locations.Asteroids);
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            System.out.println(response.toString());
        }
        System.in.read();
    }

}