import Communication.RequestFactory;
import Utils.Enums.CommandTypes;
import Utils.Enums.Locations;
import View.MainWindow;
import okhttp3.*;
import okio.Buffer;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JFrame mainFrame = new JFrame("Commander's Communicator");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(new MainWindow().getMainPanel());
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}