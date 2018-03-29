package Communication;

import Utils.Enums.CommandTypes;
import Utils.Enums.Locations;
import com.sun.org.apache.regexp.internal.RE;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

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

    public void undoCommand(){
        List<String> commandsList = requestFactory.getCommands();
        for(int i = 0; i<commandsList.size()-1; i++){
            Request request = requestFactory.getRequest(commandsList.get(i));
            send(request);
        }
    }

    public Response getStatus(){
        Request request = requestFactory.getStatusRequest();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (response);
    }

    public boolean checkPingPong(){
        Request request = requestFactory.getPingRequest();
        Boolean sendingSuccesfull = send(request);
        return sendingSuccesfull;
    }

    private boolean send(Request request){
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendRequest(CommandTypes commandType, Enum parameter){
        Request request = requestFactory.getRequest(commandType, parameter);
        Boolean sendingSuccesfull = send(request);
        return sendingSuccesfull;
    }

    public boolean sendRequest(CommandTypes commandType, Enum parameter, int value){
        Request request = requestFactory.getRequest(commandType, parameter, value);
        Boolean sendingSuccesfull = send(request);
        return sendingSuccesfull;
    }

    public boolean sendRequest(CommandTypes commandType, Enum parameter, Locations value){
        Request request = requestFactory.getRequest(commandType, parameter, value);
        Boolean sendingSuccesfull = send(request);
        return sendingSuccesfull;
    }

    public boolean sendRequest(CommandTypes commandType){
        Request request = requestFactory.getRequest(commandType);
        Boolean sendingSuccesfull = send(request);
        return sendingSuccesfull;
    }

}
