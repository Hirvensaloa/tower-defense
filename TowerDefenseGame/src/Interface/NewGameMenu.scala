package Interface

import javax.swing.JPanel
import Game.GameLoader
import java.awt.Graphics

//Frame for selecting a map to play. 
object NewGameMenu extends JPanel {
  
  this.setSize(600, 400)
  
  val mapNames = GameLoader.getMapNames("maps.txt")
  
  //Takes every maps grid and creates images from them. 
  val grids = mapNames.map(GameLoader.loadGrid(_, "maps.txt"))
  val images = grids.map(a => GameGUI.createMapImage(a.listOfTiles.map(_.map(_.forTowers))).getScaledInstance(250, 170, java.awt.Image.SCALE_SMOOTH))
  
  //Zips the mapName to correct image.
  val nameAndImage = mapNames.zip(images)
  private var index = 0
  
  //Button images and backgrounds
  val rightButton = GameGUI.images("ButtonRight.png").getScaledInstance(30, 40, java.awt.Image.SCALE_SMOOTH)
  val leftButton = GameGUI.images("ButtonLeft.png").getScaledInstance(30, 40, java.awt.Image.SCALE_SMOOTH)
  val buttonBackground = GameGUI.images("ButtonBackground.png").getScaledInstance(200, 40, java.awt.Image.SCALE_SMOOTH)
  val exitSign = GameGUI.images("ExitSign.png").getScaledInstance(60, 40, java.awt.Image.SCALE_SMOOTH)
  val menuBackground = GameGUI.images("MenuBackground.png").getScaledInstance(600, 400, java.awt.Image.SCALE_SMOOTH)
  
  //For scrolling the maps
  def next = if(index < mapNames.size - 1) index += 1 else index = 0
  def previous = if(index == 0) index = mapNames.size - 1 else index -= 1
  
  
  override def paint(g: Graphics) = {
    
    val font = MainMenuFrame.mainFont
    g.setFont(font)
    g.setColor(java.awt.Color.WHITE)
    
    g.drawImage(menuBackground, 0, 0, null)
    g.drawImage(leftButton, 90, 165, null)
    g.drawImage(rightButton, 440, 165, null)
    g.drawImage(exitSign, 30, 30, null)
    
    val mapImage = nameAndImage(index)._2
    val name = nameAndImage(index)._1
    
    g.drawImage(buttonBackground, 190, 300, null)
    g.drawString("Choose map", 215, 330)

    g.setColor(java.awt.Color.BLACK)
    g.drawImage(mapImage, 160, 100, null)
    g.drawString(name, 235, 80)
    
  }
  
  //Gives the map name that is displayed now so that when user clicks map the correct one is chosen. 
  def mapName = nameAndImage(index)._1
  
  
}

