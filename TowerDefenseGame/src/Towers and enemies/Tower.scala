import coordinateSystem.Tile

abstract class Tower(val name: String, price: Int, radius: Int,val toTile: Tile) {
  
  //Tämä kertoo hinnan, joka saadaan jos torni myydään
  private var priceToSell = price * 0.8
  
  private var radiusOfEffect = radius
  
  def getSellPrice = priceToSell
  
  def act(): Unit
  
    
}