package View;

import Communication.RequestSender;
import Utils.CommunicationLogger;
import Utils.Enums.*;
import Utils.StatusDisplayer;
import okhttp3.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by Ryukki on 23.03.2018.
 */
public class MainWindow {
    private JTextArea logTextArea;
    private JButton restartButton;
    private JLabel parameterLabel;
    private JLabel valueLabel;
    private JTextField valueTextField;
    private JButton sendButton;
    private JComboBox parameterComboBox;
    private JComboBox commandComboBox;
    private JRadioButton chaarrOrSimulationRadioButton;
    private JTextField turnTextField;
    private JTextField locationTextField;
    private JPanel mainPanel;
    private JTextArea eventsTextArea;
    private JTextArea lastTurnEventsTextArea;
    private JTextArea equipmentsTextArea;
    private JTextArea logBookTextArea;
    private JTextArea scoresTextArea;
    private JTextArea parametersTextArea;
    private JComboBox valueComboBox;
    private JScrollPane logBookScrollPane;
    private JScrollPane logAreaScrollPane;
    private JScrollPane eventsScrollPane;
    private JButton undoButton;
    private JTextField chaarrHatredTextField;
    private JTextField knowledgeTextField;
    private JTextField crewDeathsTextField;
    private JTextField survivorsDeathsTextField;
    private JTextField savedScienceTextField;
    private JTextField savedSurvivorsTextField;
    private JTextField poludnicaEnergyTextField;
    private JTextField poludnicaMatterTextField;
    private JTextField expeditionEnergyTextField;
    private JTextField expeditionMatterTextField;
    private JPanel parametersPanel;

    private RequestSender requestSender;
    private StatusDisplayer statusDisplayer;
    private CommunicationLogger logger;

    private static String paragraph = "====================";
    private Boolean isTerminated=false;
    private List<String> usedCommands;
    private int currentTurn = 0;

    public MainWindow() {
        statusDisplayer = new StatusDisplayer();
        logger = new CommunicationLogger();
        usedCommands = new ArrayList<>();
        setupListeners();
        setupTextFields();
        manageInputFields();
        getInitialState();
    }

    private void getInitialState(){
        requestSender = new RequestSender(chaarrOrSimulationRadioButton.isSelected());
        if(requestSender.checkPingPong()){
            showStatus();
        }else{
            JOptionPane.showMessageDialog(new JFrame(), "Sorry, connection with server failed.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStatus(){
        Response statusResponse = requestSender.getStatus();
        if(statusResponse!=null && statusResponse.isSuccessful()){
            displayResponse(statusResponse);
        }else{
            JOptionPane.showMessageDialog(new JFrame(), "Sorry, something went wrong.", "Sending Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResponse(Response response){
        JSONObject currentTurnObject = getJsonFromResponse(response);
        statusDisplayer.setCurrentTurnObject(currentTurnObject);

        try {
            logger.log(statusDisplayer.getResponseForLogging());
        } catch (IOException e) {
            //TODO add information to user?
            e.printStackTrace();
        }

        logTextArea.append(paragraph);
        logTextArea.append(statusDisplayer.getResponseForLogging());
        turnTextField.setText(statusDisplayer.getTurn());
        locationTextField.setText(statusDisplayer.getLocation());
        eventsTextArea.setText(statusDisplayer.getEvents());
        lastTurnEventsTextArea.setText(statusDisplayer.getLastTurnEvents());
        equipmentsTextArea.setText(statusDisplayer.getEquipments());
        logBookTextArea.setText(statusDisplayer.getLogBook());
        scoresTextArea.setText(statusDisplayer.getScores());
        parametersTextArea.setText(statusDisplayer.getParameters());
        moveScrollBars();
        checkAndHandleEndOfSimulation(statusDisplayer);
    }

    private JSONObject getJsonFromResponse(Response response){
        String jsonData = null;
        try {
            Boolean temp = response.isSuccessful();
            jsonData = response.body().string();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
        //TODO rethink null

        return new JSONObject(jsonData);
    }

    private void moveScrollBars(){
        JScrollBar verticalScrollBar = logAreaScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = logBookScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = eventsScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    private void checkAndHandleEndOfSimulation(StatusDisplayer statusDisplayer){
        if(statusDisplayer.checkTerminated()){
            isTerminated=true;
            Object[] choices = {"Restart", "I want to check the logs first."};
            Object defaultChoice = choices[0];
            int selectedOption = JOptionPane.showOptionDialog(null,
                    "Mission failed, we'll get em next time!",
                    "Mission failed!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    choices,
                    defaultChoice);
            if(selectedOption==JOptionPane.YES_OPTION){
                restart();
            }
        }else {
            isTerminated=false;
        }
    }

    private void setupListeners(){
        sendButton.addActionListener(e -> {
            if (!isTerminated) {
                String commandLog = sendRequest();
                if (!commandLog.equals("")){
                    logTextArea.append(commandLog);
                    try {
                        logger.log(paragraph + commandLog + paragraph);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    collectCommand(commandLog);
                    showStatus();
                }
            }else {
                JOptionPane.showMessageDialog(new JFrame(), "Sorry but you failed miserably, please use restart button.", "Mission failed!", JOptionPane.ERROR_MESSAGE);
            }
        });
        undoButton.addActionListener(e -> {
            List<Response> previousTurns = requestSender.undoCommand();
            currentTurn=0;
            if(!previousTurns.isEmpty()){
                statusDisplayer.resetDisplayer();
                logger.newLogFile();
                logTextArea.setText("");
            }
            for(Response response: previousTurns){
                String command = "";
                if(!usedCommands.isEmpty()&& usedCommands.size()>= currentTurn){
                    command = usedCommands.get(currentTurn);
                }
                logTextArea.append(paragraph + command);
                try {
                    logger.log(paragraph + command + paragraph);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                currentTurn++;
                displayResponse(response);
            }
        });

        restartButton.addActionListener(e -> restart());

        commandComboBox.addActionListener(e -> manageInputFields());

        parameterComboBox.addActionListener(e -> manageProduceFields());
    }

    private String sendRequest(){
        logTextArea.append(paragraph);
        String commandLog = commandComboBox.getSelectedItem().toString().toUpperCase() + " " + parameterComboBox.getSelectedItem().toString().toUpperCase() + " ";
        Boolean requestSuccessful  = false;
        if(valueComboBox.isVisible()){
            requestSuccessful  = requestSender.sendRequest((CommandTypes) commandComboBox.getSelectedItem(), (Enum) parameterComboBox.getSelectedItem(), (Locations) valueComboBox.getSelectedItem());
            commandLog += valueComboBox.getSelectedItem().toString().toUpperCase();
        }else if (valueTextField.isEditable()){
            try{
                Integer parameterValue = Integer.parseInt(valueTextField.getText());
                if(parameterValue>0){
                    requestSuccessful  = requestSender.sendRequest((CommandTypes) commandComboBox.getSelectedItem(), (Enum) parameterComboBox.getSelectedItem(), parameterValue);
                    commandLog += valueTextField.getText();
                }else {
                    JOptionPane.showMessageDialog(new JFrame(), "Parameter value should be greater than zero.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    commandLog="";
                }
            }catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(new JFrame(), "Parameter value has to be integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                commandLog="";
            }

        } else if (parametersPanel.isVisible()) {
            Map<String, Integer> restartSimulationParameters = getRestartSimulationParameters();
            if(!restartSimulationParameters.isEmpty()){
                requestSuccessful  = requestSender.sendRequest((CommandTypes) commandComboBox.getSelectedItem(), restartSimulationParameters);
                logTextArea.setText("");
                logBookTextArea.setText("");
                commandLog = commandComboBox.getSelectedItem().toString().toUpperCase();//TODO
            }else{
                commandLog = "";
            }
        } else {
            requestSuccessful  = requestSender.sendRequest((CommandTypes) commandComboBox.getSelectedItem(), (Enum) parameterComboBox.getSelectedItem());
        }
        if(!requestSuccessful){
            JOptionPane.showMessageDialog(new JFrame(), "Sorry, something went wrong while sending your request.", "Sending Error", JOptionPane.ERROR_MESSAGE);
        }
        return commandLog;
    }

    private void collectCommand(String command){
        if(usedCommands.size()<=currentTurn){
            usedCommands.add(command);
        }else{
            usedCommands.set(currentTurn, command);
        }
        currentTurn++;
    }

    private Map<String, Integer> getRestartSimulationParameters() {
        Map<String, Integer> restartSimulationParameters = new HashMap<>();
        try {
            Component[] components = parametersPanel.getComponents();
            for (Component component : components) {
                if (component.getClass().equals(JTextField.class)) {
                    JTextField paramTextField = (JTextField) component;
                    String parameterText = paramTextField.getText();
                    if (!parameterText.equals("") && !parameterText.equals("0")) {
                        Integer parameterValue = Integer.parseInt(parameterText);
                        String parameterName = paramTextField.getName();
                        if (parameterValue > 0) {
                            restartSimulationParameters.put(parameterName, parameterValue);
                            components = ArrayUtils.removeElement(components, component);
                        } else {
                            JOptionPane.showMessageDialog(new JFrame(), "Parameter value should be greater than zero.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return new HashMap<>();
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Parameter value has to be integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return new HashMap<>();
        }
        return restartSimulationParameters;
    }

    private void restart() {
        logger.newLogFile();
        requestSender.sendRequest(CommandTypes.Restart);
        requestSender.setEnvironment(chaarrOrSimulationRadioButton.isSelected());
        setupCommandComboBox();
        logTextArea.setText("");
        statusDisplayer.resetDisplayer();
        showStatus();
        currentTurn=0;
        collectCommand(CommandTypes.Restart.toString().toUpperCase());
    }

    private void manageInputFields(){
        CommandTypes commandType = (CommandTypes) commandComboBox.getSelectedItem();
        valueComboBox.setVisible(false);
        valueTextField.setVisible(false);
        parameterComboBox.setVisible(true);
        parametersPanel.setVisible(false);
        valueLabel.setVisible(true);
        parameterLabel.setVisible(true);
        switch (commandType){
            case Scan:
            case MoveTo:
            case Harvest:
                parameterComboBox.setModel(new DefaultComboBoxModel(Locations.values()));
                valueTextField.setEditable(false);
                break;
            case Produce:
                parameterComboBox.setModel(new DefaultComboBoxModel(ProducedGoods.values()));
                break;
            case Repair:
                parameterComboBox.setModel(new DefaultComboBoxModel(RepairableStuff.values()));
                valueTextField.setEditable(false);
                break;
            case Order:
                parameterComboBox.setModel(new DefaultComboBoxModel(Orders.values()));
                valueComboBox.setVisible(true);
                valueComboBox.setModel(new DefaultComboBoxModel(Locations.values()));
                valueTextField.setEditable(false);
                break;
            case RestartSimulation:
                parametersPanel.setVisible(true);
                parameterComboBox.setVisible(false);
                valueLabel.setVisible(false);
                parameterLabel.setVisible(false);
                break;
            default:
                break;
        }
    }

    private void manageProduceFields(){
        if(parameterComboBox.getSelectedItem()==ProducedGoods.Supplies){
            valueTextField.setEditable(true);
            valueTextField.setVisible(true);
        }else {
            valueTextField.setEditable(false);
            valueTextField.setVisible(false);
        }
        JPanel parentPanel = (JPanel) valueTextField.getParent();
        parentPanel.repaint();
        parentPanel.revalidate();
    }

    private void setupTextFields(){
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        turnTextField.setEditable(false);
        locationTextField.setEditable(false);
        eventsTextArea.setEditable(false);
        lastTurnEventsTextArea.setEditable(false);
        equipmentsTextArea.setEditable(false);
        logBookTextArea.setEditable(false);
        logBookTextArea.setLineWrap(true);
        scoresTextArea.setEditable(false);
        parametersTextArea.setEditable(false);
        parametersPanel.setVisible(false);
        setupCommandComboBox();
        chaarrHatredTextField.setName("chaarrHatred");
        knowledgeTextField.setName("knowledge");
        crewDeathsTextField.setName("crewDeaths");
        survivorsDeathsTextField.setName("survivorsDeaths");
        savedScienceTextField.setName("savedScience");
        savedSurvivorsTextField.setName("savedSurvivors");
        poludnicaMatterTextField.setName("południcaMatter");
        poludnicaEnergyTextField.setName("południcaEnergy");
        expeditionMatterTextField.setName("expeditionMatter");
        expeditionEnergyTextField.setName("expeditionEnergy");
    }

    private void setupCommandComboBox(){
        CommandTypes[] commandTypes = CommandTypes.values();
        ArrayList tempCommandTypesList = new ArrayList<>(Arrays.asList(commandTypes));
        tempCommandTypesList.remove(CommandTypes.Restart);
        if(chaarrOrSimulationRadioButton.isSelected()){
            tempCommandTypesList.remove(CommandTypes.RestartSimulation);
        }
        commandComboBox.setModel(new DefaultComboBoxModel(tempCommandTypesList.toArray()));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}