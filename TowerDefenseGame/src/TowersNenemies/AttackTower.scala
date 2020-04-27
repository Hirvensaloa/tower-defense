package TowersNenemies

import CoordinateSystem.Tile
import java.awt.Image

//AttackTower is a subclass of Tower and it is meant to attack enemies. 
class AttackTower(name: String, price: Int, damage: Int, radius: Int,attackSpeed: Int, tile: Tile, img: Image) extends Tower(name,price, radius, tile, img)   {
  
  private var damageDone = damage
  
  private var speedOfAttack = attackSpeed
  
  val centerCoords = (tile.coords._1 + Tile.size /2 , tile.coords._2 + Tile.size / 2)
  
  def getSpeed = speedOfAttack
  
  def changeSpeed(amount: Int) = speedOfAttack += amount
  
  def getDamage = damageDone
  
  def changeDamage(amount: Int) = damageDone += amount
  
  override def toString = s"$name\nprice: $price\nradius: $radius\ndamage: $damage\nattackSpeed: $attackSpeed"

  private var timeToAttack = 0 //When this is <0 tower can attack (shoot once) and the this is reset to 1000. 
  
  //Check if its time to attack, if it is, attack else decrease timeToAttack.
  def act() = {
    
    if(timeToAttack < 0 && !tile.neighbors(showRadius).forall(_.enemies.isEmpty)) {
 
        tile.neighbors(showRadius).find(_.enemies.nonEmpty).get.act(this) 
        timeToAttack = 1000
        
    }else{
      
        timeToAttack -= speedOfAttack
      
      }
    }
  
  //Called when tower is removed from the game. 
  def clear = tile.removeTower
}
 
