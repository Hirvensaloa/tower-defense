package Interface

import TowersNenemies.Tower
import java.awt.Graphics
import java.awt.Image
import CoordinateSystem.Tile

//This is main menu for the shop. This displays all the towers icons. 
class TowerMainMenu(val towerList: Vector[Tower], xCoord: Int){
  
  val exitButton = TowerMenu.images("Exit.png").getScaledInstance(60, 60, Image.SCALE_SMOOTH)
  val menuBackground = TowerMenu.images("MenuBackground.png").getScaledInstance(TowerMenu.width, TowerMenu.height, Image.SCALE_SMOOTH)
  
  def draw(g: Graphics) = {
    
    var x = xCoord + 40 
    var y = 100
    
    //Background for the tower menu.
    g.drawImage(menuBackground, TowerMenu.menuX, 0, null)    
 
    for(tower <- towerList) {
      
      g.drawImage(tower.image, x, y, null)
      TowerMenu.mapOfTowerImage += ((x, x + Tile.size), (y, y + Tile.size)) -> tower
      TowerMenu.towers += tower -> new TowerInfo(tower)
      
      x += 120
      
      if(x == xCoord + 400) {x = xCoord + 40;y += 120}
    }
    
    g.setColor(java.awt.Color.BLACK)
    g.drawImage(exitButton, TowerMenu.menuX + 420, 20, null)
    
  }
  
}