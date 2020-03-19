package pic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame {
	
	private JLabel labelEntreeSonde;
	private JButton boutonEntreeSonde;
	private JTextField texteEntreeSonde;
	
	
	public Gui() {
		setLayout(new FlowLayout());
		
		labelEntreeSonde = new JLabel("Entrée des données dans la sonde");
		add(labelEntreeSonde);
		
		texteEntreeSonde = new JTextField(10);
		add(texteEntreeSonde);
		
		
		boutonEntreeSonde = new JButton("Confirmer");
		add(boutonEntreeSonde);

	}
	
	public static void Fenetre() {
		 Gui fenetre = new Gui();
		 fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 fenetre.setTitle("Projet Electronique Digitale");
		 fenetre.setSize(300,400);
		 fenetre.setLocationRelativeTo(null);
		 fenetre.setVisible(true);
	}
	
	public static void main(String args[]) {
	 Fenetre();
	}
	
	
}
