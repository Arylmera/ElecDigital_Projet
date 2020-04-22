package pic;

//import gnu.io.*;
import jssc.*;
import jssc.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Gui extends JFrame {

	private final JTextField guiValeurMax = new JTextField(10);
	private static final JTextArea guiText = new JTextArea(400, 200);
	private static double valeurSonde = 0.0;
	private static SerialPort portSerie;

	public static void main(String[] args) {
		Fenetre();
		listPorts();
	}

	/**
	 * GUI builder
	 */
	public Gui() {
		// build layout
		setLayout(new FlowLayout());
		JLabel guiLabelMax = new JLabel("Distance avant alerte");
		add(guiLabelMax);
		add(guiValeurMax);
		JButton guiBtnMax = new JButton("Confirmer");
		add(guiBtnMax);
		add(guiText);

		// key listener pour empécher tout caractère sauf les numéros, le backspace et la virgule (numérique)
		guiValeurMax.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char chr = e.getKeyChar();
				if((chr < '0' || chr > '9') && chr != '\b' && chr != '.') {
					e.consume();
				}
			}
		});

		// event listener btn press
		guiBtnMax.addActionListener(ae -> {
			valeurSonde = Double.parseDouble(guiValeurMax.getText());
			System.out.println(valeurSonde);
			sendData((int) valeurSonde);
		});
	}

	/**
	 * setup fenetre GUI
	 */
	public static void Fenetre() {
		Gui fenetre = new Gui();
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.setTitle("Projet Electronique Digitale");
		fenetre.setSize(600,400);
		fenetre.setLocationRelativeTo(null);
		fenetre.setVisible(true);
	}

	/**
	 * liste des port disponibles
	 */
	public static void listPorts(){
		String[] nomPorts = SerialPortList.getPortNames();

		if(nomPorts.length == 0) {
			System.out.println("Il n'y a pas de ports disponibles.");
		}

		try {
			System.in.read();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		guiText.append("Liste des ports diponibles : \n");
		// liste des ports utilisables
		for (String nomPort : nomPorts) {
			System.out.println(nomPort);
			guiText.append("- " + nomPort + "\n");
		}
	}

	/**
	 * envois des donées a la pic
	 * @param valeurSonde
	 */
	public static void sendData(int valeurSonde){

		portSerie = new SerialPort("COM2");
		try {
			guiText.append("Envois de : "+ valeurSonde + " sur la pic comme valeur maximale\n");
			portSerie.openPort();
			portSerie.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			portSerie.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

			portSerie.addEventListener(new Reader(), SerialPort.MASK_RXCHAR);
			portSerie.writeString(String.valueOf(valeurSonde));
			portSerie.closePort();

		}
		catch(SerialPortException err){
			System.out.println("Erreur d'envois || " + err);
			guiText.append("Erreur d'envois du message || " + err + "\n");
		}
	}

	/**
	 * réception des données dynamique depuis la pic
	 */
	public static class Reader implements SerialPortEventListener {
		@Override
		public void serialEvent(SerialPortEvent e) {
			if(e.isRXCHAR()) {
				if (e.getEventValue() > 0 ) {
					try {
						String valeurRecue = portSerie.readString(e.getEventValue());
						System.out.println("Les données reçues du port sont " + valeurRecue);
						guiText.append(" Distance = " + valeurRecue + "\n");
					} catch (SerialPortException ex) {
						System.out.println("Erreur de réception || " + ex);
					}
				}
			}
		}
	}
}

