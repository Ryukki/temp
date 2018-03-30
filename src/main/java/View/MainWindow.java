package View;

import Communication.RequestSender;
import Utils.Enums.*;
import Utils.Logger;
import Utils.StatusDisplayer;
import okhttp3.Response;
import org.json.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ryukki on 23.03.2018.
 */
public class MainWindow {
    private JTextArea logTextArea;
    private JButton restartButton;
    private JLabel ParameterLabel;
    private JLabel ValueLabel;
    private JTextField valueTextField;
    private JButton sendButton;
    private JComboBox parameterComboBox;
    private JComboBox commandComboBox;
    private JLabel CommandLabel;
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
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;

    private RequestSender requestSender;
    private StatusDisplayer statusDisplayer;
    private Logger logger;

    private static String paragraph = "====================";
    private JSONObject currentTurnObject = null;
    private JSONObject previousTurnObject = null;
    private Boolean isTerminated=false;

    //nie zapomnieć logować jaka komenda z jakimi parametrami
    public MainWindow() {
        statusDisplayer = new StatusDisplayer();
        logger = new Logger();
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
        if(currentTurnObject != null){
            previousTurnObject = currentTurnObject;
            currentTurnObject = getJsonFromResponse(response);
        }else {
            previousTurnObject = currentTurnObject = getJsonFromResponse(response);
        }
        if(currentTurnObject!=null && previousTurnObject!= null){
            statusDisplayer.setCurrentTurnObject(currentTurnObject);
            statusDisplayer.setPreviousTurnObject(previousTurnObject);

            try {
                logger.log(statusDisplayer.getResponseForLogging());
            } catch (IOException e) {
                //TODO add information to user?
                e.printStackTrace();
            }

            logTextArea.append(paragraph);
            String temp = statusDisplayer.getEntireResponse();
            temp = statusDisplayer.getResponseForLogging();
            logTextArea.append(statusDisplayer.getEntireResponse());
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
    }

    private JSONObject getJsonFromResponse(Response response){
        String jsonData = null;
        try {
            jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                restartSimulation();
            }
        }else {
            isTerminated=false;
        }
    }

    private void setupListeners(){
        sendButton.addActionListener(e -> {
            if(isTerminated==false){
                String commandLog = sendRequest();
                if (!commandLog.equals("")){
                    logTextArea.append(commandLog);
                    showStatus();
                    try {
                        logger.log(paragraph + commandLog + paragraph);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }else {
                JOptionPane.showMessageDialog(new JFrame(), "Sorry but you failed miserably, please use restart button.", "Mission failed!", JOptionPane.ERROR_MESSAGE);
            }
        });

        undoButton.addActionListener(e -> {
            requestSender.undoCommand();
            showStatus();
        });

        restartButton.addActionListener(e -> restartSimulation());

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

        }else {
            requestSuccessful  = requestSender.sendRequest((CommandTypes) commandComboBox.getSelectedItem(), (Enum) parameterComboBox.getSelectedItem());
        }
        if(!requestSuccessful){
            JOptionPane.showMessageDialog(new JFrame(), "Sorry, something went wrong while sending your request.", "Sending Error", JOptionPane.ERROR_MESSAGE);
        }
        return commandLog;
    }

    private void restartSimulation(){
        logger.newLogFile();
        requestSender.sendRequest(CommandTypes.Restart);
        requestSender.setEnvironment(chaarrOrSimulationRadioButton.isSelected());
        setupCommandComboBox();
        logTextArea.setText("");
        currentTurnObject = null;
        previousTurnObject = null;
        showStatus();
    }

    private void manageInputFields(){
        CommandTypes commandType = (CommandTypes) commandComboBox.getSelectedItem();
        valueComboBox.setVisible(false);
        valueTextField.setVisible(false);
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
                //TODO Można ustawiać kilka parametrow jednoczesnie...
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
        setupCommandComboBox();
    }

    private void setupCommandComboBox(){
        CommandTypes[] commandTypes = CommandTypes.values();
        ArrayList tempCommandTypesList = new ArrayList<CommandTypes>(Arrays.asList(commandTypes));
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