import CoordinateSystem.Tile

class AttackTower(name: String, price: Int, damage: Int, radius: Int,attackSpeed: Int, tile: Tile) extends Tower(name,price, radius, tile)   {
  
  private var damageDone = damage
  
  private var speedOfAttack = attackSpeed
  
  def changeSpeed(amount: Int) = speedOfAttack * amount
  
  def changeRadius(amount: Int) = radius * amount
  
  def getDamage = damageDone
  
  def increaseDamage(amount: Double) = damageDone * amount

  def act() = {
    
    ???
    
  }
}