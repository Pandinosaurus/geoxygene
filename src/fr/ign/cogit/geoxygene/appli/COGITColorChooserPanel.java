package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.table.TableColumn;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * Color Chooser providing the colors of COGIT Laboratory color wheels.
 * 
 * @author Charlotte Hoarau
 *
 */
public class COGITColorChooserPanel extends AbstractColorChooserPanel 
									implements MouseListener{
	private static final long serialVersionUID = 1L;

	JLabel lblCerclesImage;
	JPanel mainPanel;
	JTable tCodesCouleur;
	JPanel tablePanel;
	BufferedImage cerclesImage;
	
	public COGITColorChooserPanel(){
		
		cerclesImage = createCercleImage();
		lblCerclesImage = new JLabel(new ImageIcon(cerclesImage));
		lblCerclesImage.addMouseListener(this);		
		
		tCodesCouleur = new JTable(4,8);
		Font f = tCodesCouleur.getFont();
	    f = f.deriveFont(Font.BOLD);
	    tCodesCouleur.setFont(f);
		tCodesCouleur.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		tCodesCouleur.setGridColor(new  Color(225,225,225));//Couleur des bords
		tCodesCouleur.setBackground(new Color(200,200,200));//Couleur des cases
		tCodesCouleur.setForeground(new Color(100,100,100));//Couleur du texte
		
		tCodesCouleur.setValueAt("COGIT",0,0);
		
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.UsualColor"),1,0); //$NON-NLS-1$
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.Lightness"),2,0); //$NON-NLS-1$
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.ColorKey"),3,0); //$NON-NLS-1$
		
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.Others"),0,2); //$NON-NLS-1$
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.Hexa"),1,2); //$NON-NLS-1$
		tCodesCouleur.setValueAt("sRGB",2,2);
		
		tCodesCouleur.setValueAt("RGB",0,4);
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.Red"),1,4); //$NON-NLS-1$
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.Green"),2,4); //$NON-NLS-1$
		tCodesCouleur.setValueAt(I18N.getString(
				"COGITColorChooserPanel.Blue"),3,4); //$NON-NLS-1$
		
		tCodesCouleur.setValueAt("CIELab",0,6);
		tCodesCouleur.setValueAt("L",1,6);
		tCodesCouleur.setValueAt("a",2,6);
		tCodesCouleur.setValueAt("b",3,6);
		
		for (int i=0;i<6;i++){
			TableColumn colonne = tCodesCouleur.getColumnModel().getColumn(i);
			
			if (i==0){
				colonne.setPreferredWidth(110);
				colonne.setMinWidth(110);
				colonne.setMaxWidth(110);
			}else if (i==4 || i==6){
				colonne.setPreferredWidth(50);
				colonne.setMinWidth(50);
				colonne.setMaxWidth(50);
			}else if (i==1){
				colonne.setPreferredWidth(180);
				colonne.setMinWidth(180);
				colonne.setMaxWidth(180);
			}else{
				colonne.setPreferredWidth(90);
				colonne.setMinWidth(90);
				colonne.setMaxWidth(90); 
			}
		}
		
		//Initialization of the buttons
		tablePanel = new JPanel();
		tablePanel.setBackground(new Color(225,225,225));
		tablePanel.add(tCodesCouleur);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(lblCerclesImage, BorderLayout.NORTH);
		mainPanel.add(tablePanel, BorderLayout.SOUTH);
		
		add(mainPanel);
	}
	
	public BufferedImage createCercleImage(){
		BufferedImage cerclesImage =
			new BufferedImage(1100,450,java.awt.image.BufferedImage.TYPE_INT_RGB);
		Graphics2D g = cerclesImage.createGraphics();
		g.setRenderingHint
			(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(225,225,225));
		g.fillRect(0, 0, 1100, 450);
		
		ColorReferenceSystem crs = ColorReferenceSystem.unmarshall(
				ColorReferenceSystem.class.getResource(
						"/color/ColorReferenceSystem.xml").getPath()); //$NON-NLS-1$
		
		//Création de l'image du cercle principal (Couleurs pures)
		for (int j=0;j<12;j++){
			List<ColorimetricColor> listCouleurs = crs.getSlice(0, j);	
			for (int i=0;i<listCouleurs.size();i++){
				ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
				g.setColor(
						new Color(
								couleur.getRedRGB(),
								couleur.getGreenRGB(),
								couleur.getBlueRGB()));
				g.fillArc(50+i*15, 50+i*15, 300-30*i, 300-30*i, 30*(j+1), 30);
			}
		}
		
		//Création de l'image du cercle des Couleurs grisées
		for (int j=0;j<7;j++){
			List<ColorimetricColor> listCouleurs = crs.getSlice(1,j);	
			for (int i=0;i<listCouleurs.size();i++){
				ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
				g.setColor(
						new Color(
								couleur.getRedRGB(),
								couleur.getGreenRGB(),
								couleur.getBlueRGB()));
				g.fillArc(400+i*15, 50+i*15, 300-30*i, 300-30*i, 52*j, 52);
			}
		}	
		
		//Création de l'image du cercle des Gris colorés
		for (int j=0;j<7;j++){
			List<ColorimetricColor> listCouleurs = crs.getSlice(2,j);	
			for (int i=0;i<listCouleurs.size();i++){
				ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
				g.setColor(
						new Color(
								couleur.getRedRGB(),
								couleur.getGreenRGB(),
								couleur.getBlueRGB()));
				g.fillArc(750+i*15, 50+i*15, 300-30*i, 300-30*i, 52*j, 52);
			}
		}
		
		//Création de l'image de la gamme de Gris, Noir et Blanc
		List<ColorimetricColor> listCouleurs = crs.getSlice(3,0);	
		for (int i=0;i<listCouleurs.size();i++){
			ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
			g.setColor(
					new Color(
							couleur.getRedRGB(),
							couleur.getGreenRGB(),
							couleur.getBlueRGB()));
			g.fillRect(550+i*40, 400, 40, 25);
		}
		
		//Création de l'image de la gamme de Marrons
		List<ColorimetricColor> listCouleursM = crs.getSlice(3,1);	
		for (int i=0;i<listCouleursM.size();i++){
			ColorimetricColor couleur = listCouleursM.get(listCouleursM.size()-1-i);
			g.setColor(
					new Color(
							couleur.getRedRGB(),
							couleur.getGreenRGB(),
							couleur.getBlueRGB()));
			g.fillRect(220+i*40, 400, 40, 25);
		}

		// Création des bords pour marquer les deux gammes horizontaux et
			// le centre des cercles
		g.setColor(new Color(225,225,225));
		g.fillArc(155, 155, 90, 90, 0, 360);
		g.fillArc(460, 110, 180, 180, 0, 360);
		g.fillArc(810, 110, 180, 180, 0, 360);
		
		g.setColor(new Color(200,200,200));
		g.drawRect(220, 400, 40*7, 25);
		g.drawRect(550, 400, 40*9, 25);
		g.drawArc(155, 155, 90, 90, 0, 360);
		g.drawArc(460, 110, 180, 180, 0, 360);
		g.drawArc(810, 110, 180, 180, 0, 360);
		
		return cerclesImage;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == lblCerclesImage){
		    int xpos = e.getX();
		    int ypos = e.getY();
		    int rgb = cerclesImage.getRGB(xpos, ypos);
		    Color color = new Color(rgb);
		    ColorimetricColor newColor = ColorReferenceSystem.searchColor(color);
		    updateTable(newColor);
		}
	}
	
	public void updateTable(ColorimetricColor c){

		float[] labCodes = new float[3];
		labCodes = c.getLab();

		//Setting the new values on the JTable
		if (c.getRedRGB() == 225 ||
				c.getGreenRGB() == 225 ||
				c.getBlueRGB() == 225){
		}else{
			tCodesCouleur.setValueAt(c.getUsualName(),1,1);
			tCodesCouleur.setValueAt(c.getLightness(),2,1);
			tCodesCouleur.setValueAt(c.getCleCoul(),3,1);
			
			tCodesCouleur.setValueAt(Integer.toHexString(c.toColor().getRGB()),1,3);
			tCodesCouleur.setValueAt(c.toColor().getRGB(),2,3);
			
			tCodesCouleur.setValueAt(c.getRedRGB(),   1,5);
			tCodesCouleur.setValueAt(c.getGreenRGB(), 2,5);
			tCodesCouleur.setValueAt(c.getBlueRGB(),  3,5);
			
			tCodesCouleur.setValueAt(Math.round(labCodes[0]), 1, 7);
			tCodesCouleur.setValueAt(Math.round(labCodes[1]), 2, 7);
			tCodesCouleur.setValueAt(Math.round(labCodes[2]), 3, 7);
		}
		
		getColorSelectionModel().setSelectedColor(c.toColor());
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        Color c = COGITColorChooserPanel.showDialog(new JButton(),
                    I18N.getString("StyleEditionFrame.PickAColor"), Color.BLUE); //$NON-NLS-1$
		System.out.println(c);
	}

	/**
	 * Creates and returns a new dialog containing the specified ColorChooser
	 * pane along with "OK", "Cancel", and "Reset" buttons. 
	 * If the "OK" or "Cancel" buttons are pressed,
	 * the dialog is automatically hidden (but not disposed). 
	 * If the "Reset" button is pressed,
	 * the color-chooser's color will be reset to the color which was set the
	 * last time show was invoked on the dialog and the dialog will remain showing. 
	 * @param component
	 * @param initialColor
	 * @return
	 */
	public static Color showDialog(Component component, String title, Color initialColor){
		JColorChooser colorChooser = new JColorChooser(initialColor != null?
                initialColor : Color.white);
		
		colorChooser.addChooserPanel(new COGITColorChooserPanel());

		JDialog dialog = JColorChooser.createDialog(
				component, title, true, colorChooser, null, null);
		dialog.setVisible(true);
		Color c = colorChooser.getColor();
		
		return c;
	}
	@Override
	// We did this work in the constructor so we can skip it here.
	protected void buildChooser() {}

	@Override
	public String getDisplayName() {
		return I18N.getString(
				"COGITColorChooserPanel.COGITColorReferenceSystem"); //$NON-NLS-1$
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public void updateChooser() {
		Color c = getColorSelectionModel().getSelectedColor();
		ColorimetricColor cRef = new ColorimetricColor(c, true);
		updateTable(cRef);
	}
}
