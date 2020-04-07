package TowersNenemies

import CoordinateSystem.Tile

class AttackTower(name: String, price: Int, damage: Int, radius: Int,attackSpeed: Int, tile: Tile) extends Tower(name,price, radius, tile)   {
  
  private var damageDone = damage
  
  private var speedOfAttack = attackSpeed
  
  def getSpeed = speedOfAttack
  
  def changeSpeed(amount: Double) = speedOfAttack * amount
  
  def getDamage = damageDone
  
  def increaseDamage(amount: Double) = damageDone * amount
  
  override def toString = s"$name\nprice: $price\nradius: $radius\ndamage: $damage\nattackSpeed: $attackSpeed"

  def act() = tile.act
 
}