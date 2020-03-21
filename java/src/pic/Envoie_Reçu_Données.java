package pic;

import jssc.*;
import java.io.*;
import java.util.*;


public class Envoie_Reçu_Données {
	private static SerialPort portSerie;
	static double valeur = Gui.getValeurSonde();
	
	public static void main(String[] args) {
		String[] nomPorts = SerialPortList.getPortNames();
		
		if(nomPorts.length == 0) {
			System.out.println("Il n'y a pas de ports, utilise VSPE");
			
			try {
				System.in.read();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		// liste des ports utilisables
		for(int i = 0; i<nomPorts.length; i++) {
			System.out.println(nomPorts[i]);
		}
		
		portSerie = new SerialPort("COM1");
		try {
			portSerie.openPort();
			portSerie.setParams(SerialPort.BAUDRATE_9600,
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
			
			portSerie.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
			
			portSerie.addEventListener(new Reader(), SerialPort.MASK_RXCHAR);
			portSerie.writeString(String.valueOf(valeur));
		
		}
		catch(SerialPortException err){
			System.out.println("Erreur d'écriture " + err);
		}
	}
	
	public static class Reader implements SerialPortEventListener {
		
		@Override
		
		public void serialEvent(SerialPortEvent e) {
			if(e.isRXCHAR() && e.getEventValue() > 0 ) {
				try {
					String valeurRecue = portSerie.readString(e.getEventValue());
					System.out.println("Les données reçues du port sont " + valeurRecue);
				}
				catch(SerialPortException ex) {
					System.out.println("Erreur " + ex);
				}
			
			
		}
		}
		
		
	}
}
