package TowersNenemies

import CoordinateSystem.Tile

abstract class Tower(val name: String,val price: Int, radius: Int,val tile: Tile) {
  
  //Tämä kertoo hinnan, joka saadaan jos torni myydään
  private var priceToSell = price * 0.8
  
  private var radiusOfEffect = radius
  
  def showRadius = radiusOfEffect
  
  def changeRadius(amount: Double) = radiusOfEffect * amount
  
  val neighborTiles = tile.neighbors(radius)
  
  def getSellPrice = priceToSell
  
  def act(): Unit
  
    
}