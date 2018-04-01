package Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ryukki on 26.03.2018.
 */
public class StatusDisplayer {
    private JsonFormatter currentTurnObject;
    private JsonFormatter previousTurnObject;

    public void setCurrentTurnObject(JSONObject currentTurnObject) {
        JsonFormatter jsonFormatter = new JsonFormatter(currentTurnObject);
        if (this.currentTurnObject != null) {
            this.previousTurnObject = this.currentTurnObject;
            this.currentTurnObject = jsonFormatter;
        } else {
            this.previousTurnObject = this.currentTurnObject = jsonFormatter;
        }
    }

    public void resetDisplayer(){
        currentTurnObject = null;
        previousTurnObject = null;
    }

    public String getTurn(){
        return "Turn: " + currentTurnObject.getTurn();
    }

    public String getLocation(){
        String currentLocation = currentTurnObject.getLocation();
        String previousLocation = previousTurnObject.getLocation();
        String locationInfo;
        if (currentLocation.equals(previousLocation)){
            locationInfo = currentLocation;
        }else{
            locationInfo = previousLocation + " -> " + currentLocation;
        }
        return "Location: " + locationInfo;
    }

    public String getEvents(){
        List<String> eventsList = currentTurnObject.getEvents();
        return "Events (from whole expedition):" + showList(eventsList);
    }

    public String getLastTurnEvents(){
        List<String> lastTurnEventsList = currentTurnObject.getLastTurnEvents();
        return "Last Turn Events:" + showList(lastTurnEventsList);
    }

    public String getEquipments(){
        List<String> equipmentsList = currentTurnObject.getEquipments();
        return "Equipments:" + showList(equipmentsList);
    }

    public String getLogBook(){
        List<String> currentLogList = currentTurnObject.getLogBook();
        String currentLogBook = showList(currentLogList);
        List<String> previousLogList = previousTurnObject.getLogBook();
        String previousLogBook = showList(previousLogList);
        String finalLogMessage;
        if(currentTurnObject.getTurn().equals(previousTurnObject.getTurn())){
            finalLogMessage = "\nTurn " + currentTurnObject.getTurn() + ":\n" + currentLogBook;
        }
        else if(!previousLogBook.equals(currentLogBook)){
            String newestLog = currentLogBook.replace(previousLogBook, "");
            finalLogMessage = previousLogBook + "\nTurn " + currentTurnObject.getTurn() + ":" + newestLog;
        }else {
            finalLogMessage = currentLogBook +"\nTurn " + currentTurnObject.getTurn() + ":\nNothing new happened";
        }
        return "Log Book:" + finalLogMessage;
    }

    public String getScores(){
        Map<String, Integer> currentScores = currentTurnObject.getScores();
        Map<String,Integer> previousScores = previousTurnObject.getScores();
        Map<String, String> mapWithDifferences = iterateMap(currentScores, previousScores);
        return "Scores:" + showMap(mapWithDifferences);
    }

    public String getParameters(){
        Map<String, Integer> currentParameters = currentTurnObject.getParameters();
        Map<String,Integer> previousParameters = previousTurnObject.getParameters();
        Map<String, String> mapWithDifferences = iterateMap(currentParameters, previousParameters);
        return "Parameters:" + showMap(mapWithDifferences);
    }

    private Map<String, String> iterateMap(Map<String, Integer> current, Map<String,Integer> previous){
        Map<String, String> mapShowingDifferences = new HashMap<String, String>();
        for (Map.Entry<String, Integer> currentEntry:current.entrySet()) {
            String key = currentEntry.getKey();
            Integer previousValue = previous.get(key);
            mapShowingDifferences.put(key, showDifference(currentEntry.getValue(), previousValue));
        }
        return mapShowingDifferences;
    }

    private String showDifference(int currentValue, int previousValue){
        Integer difference = currentValue - previousValue;
        if (difference==0){
            return String.valueOf(currentValue);
        }else if (difference>0){
            return currentValue + " (\u2191" + difference + ")";
        }else{
            difference = -difference;
            return currentValue + " (\u2193" + difference + ")";
        }
    }

    public String getEntireResponse(){
        return currentTurnObject.getEntireResponse();
    }

    public Boolean checkTerminated(){
        return currentTurnObject.checkTerminated();
    }

    public String getResponseForLogging(){
        return currentTurnObject.getResponseForLogging();
    }

    private String showList(List<String> list){
        String result ="";
        for (String element: list) {
            result += "\n" + element;
        }
        return result;
    }

    private String showMap(Map<String, String> map){
        String result ="";
        for (Map.Entry<String, String> element: map.entrySet()) {
            result += "\n" + element.getKey() + ": " + element.getValue();
        }
        return result;
    }
}
