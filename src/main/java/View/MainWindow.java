package View;

import Communication.RequestFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField;
    private JTextField textField7;
    private JTextField textField8;
    private JPanel mainPanel;

    public MainWindow() {
        OkHttpClient client = new OkHttpClient();

        RequestFactory requestFactory = new RequestFactory(false);
        Request request = requestFactory.getStatusRequest();

        try {
            Response response = client.newCall(request).execute();
            displayResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayResponse(Response response){
        try {
            String jsonData = response.body().string();
            JSONObject Jobject = new JSONObject(jsonData);
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

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
