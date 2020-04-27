package TowersNenemies

import CoordinateSystem.Tile
import scala.collection.mutable.Buffer
import java.awt.Image

//Tower that supports other towers by giving them radius, speed and/or damage boost. 
class SupportTower(name: String, price: Int, radius: Int, tile: Tile, bRadius: Int, bDamage: Int, bSpeed: Int, img: Image) extends Tower(name, price, radius, tile, img){
  
  val radiusBoost = bRadius //Tells how much radius boost is given to near-by towers
  val damageBoost = bDamage //Tells how much damage boost is given to near-by towers
  val speedBoost = bSpeed   //Tells how much attack speed boost is given to near-by towers
  
  //Towers which attributes how already been effected by this tower. 
  private val towersEffected: Buffer[Tower] = Buffer()
  
  override def toString = s"$name\nprice: $price\nradius: $radius\nradius boost: $radiusBoost\ndamage boost: $damageBoost\nspeed boost: $speedBoost"
  
  //Gives all towers within this radius boost if not yet given
  def act() = {
    val towers = neighborTiles.filter(_.hasTower.nonEmpty).map(_.hasTower.get).filterNot(towersEffected.contains(_))
    
    for(tower <- towers)
    {
      tower match {
        case t: AttackTower => t.changeDamage(damageBoost); t.changeRadius(radiusBoost); t.changeSpeed(speedBoost)
        case t: Tower => t.changeRadius(radiusBoost)
        case _ => Unit
      }
      
      towersEffected += tower
      
    }
        
  }
  
  //When tower is removed this is used to clear its effects.
  def clear = {
    
    for(tower <- towersEffected){
      tower match {
        case t: AttackTower => t.changeDamage(-damageBoost); t.changeRadius(-radiusBoost); t.changeSpeed(-speedBoost)
        case t: Tower => t.changeRadius(-radiusBoost)
        case _ => Unit
      }
    }
    
    tile.removeTower
    
  }
  
  //When object is created action gets called once so that the effects will take action immediately.
  this.act
  
  
  
}