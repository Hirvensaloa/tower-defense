

abstract class Tower(val name: String, LP: Int, coordinates: (Int, Int), price: Int) {
  
  private var lifepoints = LP
  
  var coords = coordinates
  
  //Tämä kertoo hinnan, joka saadaan jos torni myydään
  private var priceToSell = price * 0.8
  
  def getSellPrice = priceToSell
  
  def health = lifepoints
  
  def changeCoords(x: Int, y: Int) = coords = (x, y)
  
  def lowerHealth(amount: Int) = lifepoints - amount
  
  def increaseHealth(amount: Int) = lifepoints + amount
  
  def isAlive = lifepoints > 0 
  
  def act(): Unit
  
  
  
}