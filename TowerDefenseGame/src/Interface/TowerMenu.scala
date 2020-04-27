package Interface

import TowersNenemies._
import Game.GameLoader
import java.awt.Graphics
import scala.collection.mutable.Map
import CoordinateSystem._
import java.awt.Image

//This describes shop area in the game where you can buy towers. 
class TowerMenu(towers: Vector[Tower], xCoord: Int) {

  //List of all possible towers to buy, max amount is 18.
  val towerList = if(towers.size <= 18) towers else towers.take(18)
 
  val mainMenu = new TowerMainMenu(towerList, xCoord)
  
  val bottomImage = TowerMenu.images("BottomBar.png").getScaledInstance(Grid.width, 250, Image.SCALE_SMOOTH)
 
  def draw(g: Graphics) = {
    
    g.setColor(TowerMenu.color)
    
    if(TowerMenu.toDraw.isEmpty){
      mainMenu.draw(g)
    }else{
      TowerMenu.toDraw.get.draw(g)
    }
    
    //Draws the background for the bottom section.
    g.setColor(TowerMenu.color)    
    g.drawImage(bottomImage, 0, Grid.height, null)
    g.setColor(java.awt.Color.BLACK)
  }
  
}
object TowerMenu{
  
  val mapOfTowerImage: Map[((Int, Int), (Int, Int)), Tower] = Map()
  val color = java.awt.Color.LIGHT_GRAY
  
  val width = 500
  def height = Grid.height + 250
  def menuX = Grid.width 
  
  var inMain = true //Is user in the menus main page.
  var selling = false //Is user in the to sell section.
  val images = GameLoader.loadMenuImages
  var toDrag: Option[Tower] = None //If user is in the buy section, this gets value which is the tower that is being possibly bought.
  val towers: Map[Tower, TowerInfo] = Map()
  var toDraw: Option[TowerInfo] = None //This is nonEmpty if user is not on main page. This is for the gui to tell what to draw instead of main. 
  
  
}





