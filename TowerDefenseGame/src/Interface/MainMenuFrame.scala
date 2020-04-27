package Interface

import javax.swing.JPanel
import java.awt.Font
import java.awt.Graphics
import java.awt.Image

//Frame for the main menu start. 
object MainMenuFrame extends JPanel{
  
  this.setSize(600, 400)
  
  val buttonBackground = GameGUI.images("ButtonBackground.png").getScaledInstance(200, 60, Image.SCALE_SMOOTH)
  val menuBackground = GameGUI.images("MenuBackground.png").getScaledInstance(600, 400, Image.SCALE_SMOOTH)
  
  //Font for the main menu. 
  val mainFont = new Font("main", Font.ITALIC, 25)
  
  override def paint(g: Graphics) = {
    
    
    g.setColor(java.awt.Color.WHITE)
    g.setFont(mainFont)
    
    g.drawImage(menuBackground, 0, 0, null)
    
    g.drawImage(buttonBackground, 200, 100, null)
    g.drawImage(buttonBackground, 200, 200, null)
    g.drawString("NEW GAME", 230, 135)
    g.drawString("LOAD GAME", 230, 235)
  }
  
}