package Communication;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class RequestSender {
    private OkHttpClient client;
    RequestFactory requestFactory;

    public RequestSender(boolean environment){
        client = new OkHttpClient();
        requestFactory = new RequestFactory(environment);
    }

    public void setEnvironment(boolean environment){
        requestFactory = new RequestFactory(environment);
    }

    public Response getStatus() throws IOException{
        Request request = requestFactory.getStatusRequest();
        Response response = client.newCall(request).execute();
        return (response);
    }
}
