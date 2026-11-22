package com.group9.gym;

import javax.swing.SwingUtilities;
import com.group9.gym.ui.GymFrame;

public class App {
    public static void main(String[] args) {
        Database.initialize();
        SwingUtilities.invokeLater(() -> new GymFrame().setVisible(true));
        new RenewalAlertThread().start();
    }
}
