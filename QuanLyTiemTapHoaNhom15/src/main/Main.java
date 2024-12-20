package main;

import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.LoginView;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> {
			new LoginView();
		});
	}
}
