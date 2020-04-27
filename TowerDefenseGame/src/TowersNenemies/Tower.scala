package TowersNenemies

import CoordinateSystem.Tile
import Interface.Info
import java.awt.Image

//Abstract class for class AttackTower and SupportTower. All towers must have name, price, radius (in Tiles) and image. 
abstract class Tower(val name: String,val price: Int, radius: Int,val tile: Tile, val image: Image) {
  
  private var radiusOfEffect = radius
  
  def showRadius = radiusOfEffect
  
  def changeRadius(amount: Int) = {
    
    radiusOfEffect += amount
    neighborTileList = tile.neighbors(radiusOfEffect)
    
  }
  
  private var neighborTileList = tile.neighbors(radius)
  
  val coordinates = tile.coords
  
  lazy val info = new Info(this)  //Contains info about the tower that can be then drawn. 
  
  def neighborTiles = neighborTileList
  
  def getSellPrice = (price * 0.8).toInt
  
  //Clears all the space and other stuff that is caused/reserved by tower.
  def clear: Unit
  
  def act(): Unit
  
    
}

