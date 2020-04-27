package CoordinateSystem

import scala.collection.mutable.Buffer
import Interface.GameGUI
import TowersNenemies._

//Tile can have tower added to it if its an grass tile and it can have enemies on it if its road tile. 
class Tile(val forTowers: Boolean, val xCoord: Int, val yCoord: Int,  val grid: Grid) {
  
  def canAddTower = forTowers && hasTower.isEmpty
  
  var hasTower: Option[Tower] = None
  
  var enemies: Buffer[Enemy] = Buffer()
  
  def removeTower() = hasTower = None
  
  def addTower(tower: Tower) = {
    if(canAddTower)
    {
      hasTower = Some(tower)
    }
  }
  
  //When called by tower it passes information to GameGUI about which bullets to draw. This takes basically care of damaging enemies. 
  def act(tower: AttackTower) = {
      
      val enemy = enemies(0)
      val enemyCoords = (enemy.coordinates._1 + Tile.size / 4, enemy.coordinates._2 + Tile.size / 4)
      
      enemy.decreaseLP(tower.getDamage)
      
      val coordinates = (tower.centerCoords, enemyCoords)
      
      GameGUI.bulletsToDraw += coordinates -> Game.Settings.bulletDrawTime         
  
  }

  
  val coords = (xCoord * Tile.size, yCoord * Tile.size)
  
  //These coordinates tell enemies where to go. 
  val destinationCoords = (coords._1 + Tile.size/4, coords._2 + Tile.size/4)
   
  //Returns all neighboring tiles within the radius. 
  def neighbors(radius: Int) = {
    
    val neighboringTiles: Buffer[Tile] = Buffer()
    
    for(y<- yCoord - radius to yCoord + radius)
    {
      for(x<- xCoord - radius to xCoord + radius)
      {
        
        if(!(x == xCoord && y == yCoord) && x >= 0 && y >= 0 && x < grid.listOfTiles(0).size && y < grid.listOfTiles.size)
        {
          neighboringTiles += grid.listOfTiles(y)(x)
        }
        
      }
    }
    
    neighboringTiles.toVector.sortBy(tile => tile.coords).reverse
    
  }
  
}

object Tile {
  
  val size = Game.Settings.tileSize
  
}