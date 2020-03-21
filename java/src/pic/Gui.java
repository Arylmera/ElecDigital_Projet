package pic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame {
	
	private JLabel labelEntreeSonde;
	private JButton boutonEntreeSonde;
	private JTextField texteEntreeSonde;
	private double valeurSonde = 0.0;
	
	
	public double getValeurSonde() {
		return this.valeurSonde;
	}
	
	public void setValeurSonde(double valeur) {
		this.valeurSonde = valeur;
	}
	
	public Gui() {
		setLayout(new FlowLayout());
		
		labelEntreeSonde = new JLabel("Entrer un nombre dans la sonde");
		add(labelEntreeSonde);
		
		texteEntreeSonde = new JTextField(10);
		add(texteEntreeSonde);
		// key listener pour empêcher tout caractère sauf les numéros, le backspace et la virgule (numérique)
		texteEntreeSonde.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char chr = e.getKeyChar();
				if((chr < '0' || chr > '9') && chr != '\b' && chr != '.') {
					e.consume();
				}
			}
		});
		
		
		boutonEntreeSonde = new JButton("Confirmer");
		add(boutonEntreeSonde);
		boutonEntreeSonde.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent ae){
			      valeurSonde = Double.parseDouble(texteEntreeSonde.getText());
			   }
			});
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
