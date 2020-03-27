package TowersNenemies

import CoordinateSystem.Tile

//Torni, joka tukee toisia torneja antamalla niille lisää kantamaa ja tuhovoimaa
class SupportTower(name: String, price: Int, radius: Int, tile: Tile, bRadius: Double, bDamage: Double, bSpeed: Double) extends Tower(name, price, radius, tile){
  
  //Kertoo kuinka paljon torni antaa lisää kantamaa lähellä oleville torneille
  val radiusBoost = bRadius
  
  //Kertoo paljonko torni antaa lisää tuhovoimaa lähellä oleville torneille
  val damageBoost = bDamage
  
  //Kertoo paljonko tulinopeutta torneille annetaan
  val speedBoost = bSpeed
  
  //Tornit, joille on jo annettu lisää tehoa. Pitää huolen ettei lisätä loputtomasti tehoa samoille torneille.
  private var towersEffected: Vector[Tower] = Vector()
  
  override def toString = s"$name\nprice: $price\nradius: $radius\nradius boost: $radiusBoost\ndamage boost: $damageBoost\nspeed boost: $speedBoost"
  
  
  def act() = {
    val towers = neighborTiles.filter(_.hasTower.nonEmpty).filterNot(towersEffected.contains(_)).map(_.hasTower.get)
    
    for(tower <- towers)
    {
      tower match {
        case t: AttackTower => t.increaseDamage(damageBoost); t.changeRadius(radiusBoost); t.changeSpeed(speedBoost)
        case t: Tower => t.changeRadius(radiusBoost)
        case _ => Unit
      }
    }
    
    towersEffected = towersEffected ++ towers
    
    
  }
  
  
  
}