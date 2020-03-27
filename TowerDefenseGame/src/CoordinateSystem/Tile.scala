package CoordinateSystem

import scala.collection.mutable.Buffer
import TowersNenemies._

//Tilet ovat alustavasti 30px, 30px kokoisia. Tilella on keskikoordinaatti, jota kohti viholliset pyrkivät ja johon torneja voi laittaa. 
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
  
  val size = 30 
  
  val centerCoords = (xCoord * size + size/2, yCoord * size + size/2)
   
  //Ottaa parametrinaan etäisyyden, joka kertoo miltä alueelta naapurit lasketaan. Esim. 2 niin lasketaan kahden tiilen päästä naapurit, myös sivusuunnassa. 
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
    
    neighboringTiles.toVector
    
  }
  
}