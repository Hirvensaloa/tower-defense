package Interface

import Game.Game
import java.awt.Image
import CoordinateSystem.Grid
import java.awt.Graphics
import java.awt.Font

//Abstract class that describes PopupWindows, each subclass must have draw method that is used to draw the popup.
abstract class PopupWindow {
  
  //Where the popup is drawn  
  val x = Grid.width / 4
  val y = Grid.height / 4
  
  val buttonBackground = GameGUI.images("ButtonBackground.png").getScaledInstance(90, 40, Image.SCALE_SMOOTH)
  val menuBackground = GameGUI.images("BottomBar.png").getScaledInstance(300, 200, Image.SCALE_SMOOTH)
  
  //Size of popups.
  val width = 300
  val height = 200
  
  def draw(g: Graphics): Unit
  
}

//This draws the game popup window. 
class GameOverPopup(val isWon: Boolean) extends PopupWindow{
  
  def draw(g: Graphics) = {
    
    g.setColor(TowerMenu.color)
    
    g.drawImage(menuBackground, x, y, null)
    g.drawImage(buttonBackground, x + 196, y + 145, null)
    
   g.setColor(java.awt.Color.WHITE)
   g.drawString("Back to menu", x + 200, y + 170)
   g.setFont(new Font("g", Font.ROMAN_BASELINE, 30))
   g.setColor(java.awt.Color.RED)
   
    if(isWon){
      
      g.drawString("You won!", x + 90, y + 100)
    }
    else{
      
      g.drawString("You lost!", x + 90, y + 100)
      
    }
    
  }
  
}

//This draws the exit popup where player can choose to exit or cancel.
class ExitPopup extends PopupWindow{
  
  val cancelBackground = buttonBackground.getScaledInstance(60, 30, Image.SCALE_SMOOTH)
  
  def draw(g:Graphics) = {
    
    g.setColor(TowerMenu.color)
    
    g.drawImage(menuBackground, x, y, null)
    
    g.setColor(java.awt.Color.WHITE)
    
    g.drawImage(buttonBackground, x + 30, y + 60, null)
    g.drawImage(buttonBackground, x + 160, y + 60, null)
    g.drawImage(cancelBackground, x + 235, y + 160, null)
    g.drawString("Save and exit", x + 30, y + 85)
    g.drawString("Exit", x + 195, y + 85)
    g.drawString("Cancel", x + 242, y + 180)
    
  }
  
}