package TowersNenemies

import CoordinateSystem.Tile

//Torni, joka tukee toisia torneja antamalla niille lisää kantamaa ja tuhovoimaa
class SupportTower(val name: String, price: Int, radius: Int, tile: Tile, val neighbourTiles: Array[Tile], bRadius: Double, bDamage: Double) extends Tower(name, price, radius, tile){
  
  //Kertoo kuinka paljon torni antaa lisää kantamaa lähellä oleville torneille
  val radiusBoost = bRadius
  
  //Kertoo paljonko torni antaa lisää tuhovoimaa lähellä oleville torneille
  val damageBoost = bDamage
  
  
  def act() = {
    ???
  }
  
  
  
}