package TowersNenemies

import CoordinateSystem.Tile

abstract class Tower(val name: String,val price: Int, radius: Int,val tile: Tile) {
  
  private var radiusOfEffect = radius
  
  def showRadius = radiusOfEffect
  
  def changeRadius(amount: Double) = {
    radiusOfEffect * amount
    neighborTileList = tile.neighbors(radius)
  }
  
  private var neighborTileList = tile.neighbors(radius)
  
  val coordinates = tile.centerCoords
  
  def neighborTiles = neighborTileList
  
  def getSellPrice = price * 0.8
  
  def act(): Unit
  
    
}