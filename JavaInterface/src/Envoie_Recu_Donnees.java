package pic;

import jssc.*;
import java.io.*;

public class Envoie_Recu_Donnees {
	private static SerialPort portSerie;
	static double valeur = pic.Gui.getValeurSonde();
	
	public static void main(String[] args) {
		String[] nomPorts = SerialPortList.getPortNames();
		
		if(nomPorts.length == 0) {
			System.out.println("Il n'y a pas de ports disponibles.");
			
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
		
		portSerie = new SerialPort("COM2");
		try {
			portSerie.openPort();
			portSerie.setParams(SerialPort.BAUDRATE_9600,
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
			
			portSerie.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
			
			portSerie.addEventListener(new Reader(), SerialPort.MASK_RXCHAR);
			portSerie.writeString(String.valueOf(valeur));
			portSerie.closePort();
		
		}
		catch(SerialPortException err){
			System.out.println("Erreur d'envois || " + err);
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
					System.out.println("Erreur de réception || " + ex);
				}
			
			
		}
		}
		
		
	}
}
