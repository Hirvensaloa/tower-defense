package CoordinateSystem

import scala.collection.mutable.Buffer
import TowersNenemies._

class Tile(val forTowers: Boolean, val xCoord: Int, val yCoord: Int,  val grid: Grid) {
  
  def canAddTower = forTowers && hasTower.isEmpty
  
  var hasTower: Option[Tower] = None
  
  def removeTower() = hasTower = None
  
  def addTower(tower: Tower) = {
    if(canAddTower)
    {
      hasTower = Some(tower)
    }
  }
   
  //Ottaa parametrinaan etäisyyden, joka kertoo miltä alueelta naapurit lasketaan. Esim. 2 niin lasketaan kahden tiilen päästä naapurit, myös sivusuunnassa. 
  def neighbors(radius: Int) = {
    
    val neighboringTiles: Buffer[Tile] = Buffer()
    
    for(y<- yCoord - radius to yCoord + radius)
    {
      for(x<- xCoord - radius to xCoord + radius)
      {
        
        if(x != xCoord && y != yCoord && x >= 0 && y >= 0)
        {
          neighboringTiles += grid.listOfTiles(y)(x)
        }
        
      }
    }
    
    neighboringTiles.toVector
    
  }
  
}