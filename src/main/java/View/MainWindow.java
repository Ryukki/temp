package View;

import Communication.JsonFormatter;
import Communication.RequestFactory;
import Communication.RequestSender;
import Utils.Enums.CommandTypes;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

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

    private RequestSender requestSender;

    //nie zapomnieć logować jaka komenda z jakimi parametrami
    public MainWindow() {
        setupListeners();
        setupTextFields();

        requestSender = new RequestSender(chaarrOrSimulationRadioButton.isSelected());
        try {
            displayResponse(requestSender.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayResponse(Response response){
        try {
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            logTextArea.append(jsonObject.toString(4));
            JsonFormatter jsonFormatter = new JsonFormatter(jsonObject);
            turnTextField.setText("Turn: " + jsonFormatter.getTurn());
            locationTextField.setText("Location: " + jsonFormatter.getLocation());
            eventsTextArea.setText("Events:");
            for (String event: jsonFormatter.getEvents()) {
                eventsTextArea.append("\n" + event);
            }
            lastTurnEventsTextArea.setText("Last Turn Events:");
            for (String lastTurnEvent: jsonFormatter.getLastTurnEvents()) {
                lastTurnEventsTextArea.append("\n" + lastTurnEvent);
            }
            equipmentsTextArea.setText("Equipments:");
            for (String equipment: jsonFormatter.getEquipments()) {
                equipmentsTextArea.append("\n" + equipment);
            }
            logBookTextArea.setText("Log Book:");
            for (String log: jsonFormatter.getLogBook()) {
                logBookTextArea.append("\n" + log);
            }
            scoresTextArea.setText("Scores:");
            for (Map.Entry<String, Integer> score:jsonFormatter.getScores().entrySet()) {
                scoresTextArea.append("\n" + score.getKey() + ": " + score.getValue());
            }
            parametersTextArea.setText("Parameters:");
            for (Map.Entry<String, Integer> parameter:jsonFormatter.getParameters().entrySet()) {
                parametersTextArea.append("\n" + parameter.getKey() + ": " + parameter.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners(){
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        commandComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void setupTextFields(){
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        //logTextArea.setVisible(false);
        turnTextField.setEditable(false);
        locationTextField.setEditable(false);
        eventsTextArea.setEditable(false);
        lastTurnEventsTextArea.setEditable(false);
        equipmentsTextArea.setEditable(false);
        logBookTextArea.setEditable(false);
        logBookTextArea.setLineWrap(true);
        scoresTextArea.setEditable(false);
        parametersTextArea.setEditable(false);
        commandComboBox.setModel(new DefaultComboBoxModel(CommandTypes.values()));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
