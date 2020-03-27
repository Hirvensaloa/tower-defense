package CoordinateSystem

import scala.collection.mutable.Buffer

//Grid-luokka on jaettu Tile-olioihin. ydim ja xdim kertovat Gridin koon. 
class Grid(private var tileList: Vector[Vector[Tile]]) {
  
  def listOfTiles = tileList
  
  override def toString = {
    var string = ""
    for(tiles <- listOfTiles){
      string = string + tiles.map(a => if(a.forTowers) '1' else '0').mkString + '\n'
    }
    string
  }
}


object Grid{
  //Ottaa parametrina listan, joka kertoo onko kyseessä tie- vai nurmikkotiili
  def apply(list: Vector[Vector[Boolean]]): Grid = {
    
    val grid = new Grid(Vector())
    
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