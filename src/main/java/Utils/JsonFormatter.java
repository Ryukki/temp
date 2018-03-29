package Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ryukki on 23.03.2018.
 */
public class JsonFormatter {
    private JSONObject jsonObject;

    public JsonFormatter(JSONObject objectToFormat){
        this.jsonObject=objectToFormat;
    }

    private String getSingleValue(String key){
        return jsonObject.get(key).toString();
    }

    private List<String> getList(String key){
        List<String> valuesList = new ArrayList<String>();
        JSONArray array = jsonObject.getJSONArray(key);
        for (int i=0; i<array.length();i++){
            valuesList.add(array.get(i).toString());
        }
        return valuesList;
    }

    private Map<String, Integer> getMap(String key){
        JSONObject innerJSON = jsonObject.getJSONObject(key);
        Map<String, Integer> innerMap = new HashMap<String, Integer>();
        for (String jsonKey :innerJSON.keySet()) {
            Integer value = innerJSON.getInt(jsonKey);
            innerMap.put(jsonKey, value);
        }
        return innerMap;
    }

    public String getTurn(){
        return getSingleValue("turn");
    }

    public String getLocation(){
        return getSingleValue("location");
    }

    public List<String> getEvents(){
        return getList("events");
    }

    public List<String> getLastTurnEvents(){
        return getList("lastTurnEvents");
    }

    public List<String> getEquipments(){
        return getList("equipments");
    }

    public List<String> getLogBook(){
        return getList("logBook");
    }

    public Map<String, Integer> getScores(){
        return getMap("scores");
    }

    public Map<String, Integer> getParameters(){
        return getMap("parameters");
    }

    public String getEntireResponse(){
        return jsonObject.toString(4);
    }

    public String getResponseForLogging(){
        JSONObject tempJsonObject = new JSONObject(jsonObject.toString());
        tempJsonObject.remove("events");
        tempJsonObject.remove("logBook");
        String responseString = tempJsonObject.toString(4);
        responseString = responseString.replaceAll("\n", "\r\n");
        return responseString;
    }

    public Boolean checkTerminated(){
        String isTerminated = getSingleValue("isTerminated");
        if (isTerminated.equals("false")){
            return false;
        }
        return true;
    }
}
