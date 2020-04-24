package pic;

import gnu.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Gui extends JFrame {

	private static OutputStream outStream;
	private static InputStream inStream;
	private static SerialPort serialPort;
	private final JTextField guiValeurMax = new JTextField(10);
	private static final JTextArea guiText = new JTextArea(20, 20);
	private static int minValue = 0;

	static int baudRate = 9600;
	static int timeOut = 5000;

	/**
	 * MAIN
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			connect("COM2");
			Fenetre();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
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
		JButton guiBtnClose = new JButton("Close");
		add(guiBtnClose);
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
			try {
				minValue = Integer.parseInt(guiValeurMax.getText());
				System.out.println("Envois de la valeur minimale : " + minValue);
				// send value to pic
				sendData(minValue);
			}
			catch (NumberFormatException | IOException e){}
		});

		guiBtnClose.addActionListener(ae -> {
			try {
				close();
			}
			catch (Exception e){
				System.out.println("Error in btn close : " + e.getMessage());
			}
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
	 * print ligne de donnée
	 * @param value
	 */
	private static void printText(String value){
		guiText.append(value + " cm || distance minimum = " + minValue +" cm \n");
	}

	/**
	 * initalisation de la connection
	 * @param portName
	 * @throws IOException
	 * @throws NoSuchPortException
	 * @throws PortInUseException
	 */
	private static void connect(String portName) throws IOException, NoSuchPortException, PortInUseException {
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
		serialPort = (SerialPort) portId.open("ElecJavaInterface", timeOut);
		try {
			serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException ex) { System.err.println(ex.getMessage());}

		try {
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException ex) { System.err.println(ex.getMessage());}

		outStream = serialPort.getOutputStream();
		inStream = serialPort.getInputStream();

		try {
			serialPort.addEventListener(new SerialListener());
			serialPort.notifyOnDataAvailable(true);
		}
		catch (Exception e) {
			System.out.println(" error in open = " + e.getMessage());
		}
	}

	/**
	 * fermeture du port serial
	 */
	public static void close(){
		try {
			serialPort.close();
			System.exit(0);
		}
		catch (Exception e){
			System.out.println(" error in close = " + e.getMessage());
		}
	}

	/**
	 * envois des données a la pic
	 * @param valueToSend
	 * @throws IOException
	 */
	public void sendData(int valueToSend) throws IOException {
		guiText.append(valueToSend + " cm définie comme valeur maximum \n");
		outStream.write(valueToSend);
	}

	/**
	 * handle input from serial
	 */
	static class SerialListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			System.out.println("Valeur détectée sur le port");
			if ( event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				System.out.println("if");
				try {
					System.out.println("try");
					int available = inStream.available();
					System.out.println("available : " + available);
					byte[] data = new byte[available];
					System.out.println("data : " + Arrays.toString(data));
					if (available > 0) {
						inStream.read(data, 0, available);
						System.out.println("chunk after read : " + Arrays.toString(data));
						String value = new String(data);
						System.out.println("value : " + value);
						printText(value);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

