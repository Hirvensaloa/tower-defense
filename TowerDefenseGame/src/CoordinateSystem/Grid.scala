package CoordinateSystem

import scala.collection.mutable.Buffer
import Game.GameLoader

//Grid consists of Tiles.  
class Grid(private var tileList: Vector[Vector[Tile]], val name: String) {
  
  def listOfTiles = tileList
  
}
//Companion object helps create Grid.
object Grid{
  
  def width = GameLoader.mapWidth
  def height = GameLoader.mapHeight
  
  /*Takes as parameter a list is a grid but with boolean values that tell if the tile is 
  * grass or road tile. Then it creates the Tile. 
  */
  def apply(list: Vector[Vector[Boolean]], name: String): Grid = {
  
    
    val grid = new Grid(Vector(), name)
    
    val tiles: Buffer[Buffer[Tile]] = Buffer()
    
    for(y <- list.indices){
      tiles += Buffer()
      
      for(x <- list(y).indices){
        
        tiles.last += new Tile(list(y)(x), x, y, grid)
        
      }
    }
    
    grid.tileList = tiles.map(_.toVector).toVector
    
    grid
  }
}