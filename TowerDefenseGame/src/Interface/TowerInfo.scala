package Interface

import TowersNenemies._
import java.awt.Image
import java.awt.Graphics
import CoordinateSystem.Grid

//This class contains tower info that can be drawn by calling its draw method. Info is draw on top of tower main menu. 
class TowerInfo(val tower: Tower){
  
    //Tells which x-coordinate is set for tower info in the tower menu section.
    val infoCoordX = TowerMenu.menuX + 150   
    
    //Button images
    val exitSign = TowerMenu.images("ExitSign.png").getScaledInstance(60, 40, Image.SCALE_SMOOTH)
    val buyButton = TowerMenu.images("BuyButton.png").getScaledInstance(180, 80, Image.SCALE_SMOOTH)
    val menuBackground = TowerMenu.images("MenuBackground.png").getScaledInstance(TowerMenu.width, TowerMenu.height, Image.SCALE_SMOOTH)
    
    def draw(g: Graphics) = { 
      
    g.drawImage(menuBackground, Grid.width, 0, null)
    g.setColor(java.awt.Color.BLACK)
    g.drawImage(tower.image, infoCoordX, 100, null)
    
    drawMore(g)
    g.drawString("Name: " + tower.name, infoCoordX, 230)
    g.drawImage(exitSign, TowerMenu.menuX + 20, 30, null)
    
    
    tower match{
      case tower: AttackTower => {
        g.drawString("Type: Attack", infoCoordX, 290)
        g.drawString("Damage: " + tower.getDamage, infoCoordX, 320)
        g.drawString("Radius: " + tower.showRadius, infoCoordX, 350)
        g.drawString("Attack speed: " + tower.getSpeed, infoCoordX, 380)
      }
      case tower: SupportTower => {
        g.drawString("Type: Support", infoCoordX, 290)
        g.drawString("Damage boost: +" + tower.damageBoost, infoCoordX, 410)
        g.drawString("Radius boost: +" + tower.radiusBoost, infoCoordX, 350)
        g.drawString("Speed boost: +" + tower.speedBoost, infoCoordX, 380)
        g.drawString("Radius: " + tower.showRadius, infoCoordX, 320)
      }
    }
    
   
  }
   
    /*This method is for subclasses to override. This draws some strings that are meant to be different
    * with subclasses. So now when creating a subclass, there is just this method to override. 
    */
     def drawMore(g: Graphics){
     
     g.drawString("Price: " + tower.price, infoCoordX, 260)
     g.drawImage(buyButton, infoCoordX, 440, null)
     
   }
  
}

//Class for drawing the sell view for certain tower. 
class Info(tower: Tower) extends TowerInfo(tower){
  
  val sellButton = GameGUI.images("SellButton.png").getScaledInstance(180, 80, Image.SCALE_SMOOTH)
  
    override def drawMore(g: Graphics){
    
     g.drawString("Price to sell: " + tower.getSellPrice, infoCoordX, 260)
     g.drawImage(sellButton, infoCoordX, 440, null)
     
  }
  
}