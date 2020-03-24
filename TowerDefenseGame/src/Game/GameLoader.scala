package Game

import TowersNenemies._
import CoordinateSystem._
import scala.collection.mutable.Buffer
import java.io.IOException
import java.io.BufferedReader
import java.io.FileReader

object GameLoader {
  
  //Virheellisestä tiedostosta johtuva poikkeus
  class CorruptedFile(msg: String) extends Exception(msg)
  
  def loadTowerMenu = {
    
    val towers: Buffer[Tower] = Buffer()
    
    val lineReader = new BufferedReader(new FileReader("towers.txt"))
    
    try {
      
      var line = lineReader.readLine()
      
      while(line != null)
      {
        line = lineReader.readLine()
        if(line.nonEmpty && line.trim.toLowerCase == "#start")
        {
          var name: Option[String] = None
          var price: Option[Int] = None
          var radius: Option[Int] = None
          var towerType: Option[String] = None
          var damage: Option[Int] = None
          var radiusBoost: Option[Double] = None
          var damageBoost: Option[Double] = None
          var speedBoost: Option[Double] = None
          var attackSpeed: Option[Int] = None
          
          while(line != null && line.trim.toLowerCase != "#end")
          {
            line = lineReader.readLine().toLowerCase
            
            line.takeWhile(_ != ':').trim match {
              case "name" => name = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "price" => price = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "radius" => radius = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "type" => towerType = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "damage" => damage = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "radiusboost" => radiusBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toInt/100) + 1.0)
              case "damageboost" => damageBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toInt/100) + 1.0)
              case "speedboost" => speedBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toInt/100) + 1.0)
              case "attackspeed" => attackSpeed = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
            }
            
          }
          
          if(!(towerType.nonEmpty && name.nonEmpty && price.nonEmpty && radius.nonEmpty && radiusBoost.nonEmpty && damageBoost.nonEmpty && speedBoost.nonEmpty && damage.nonEmpty && attackSpeed.nonEmpty))
          {
            throw new CorruptedFile("Data corrupted")
          }
            
          val fakeGrid = Grid(Vector(Vector(true)))
          val fakeTile = fakeGrid.listOfTiles(0)(0)
          
          if(towerType.get == "support"){
             towers += new SupportTower(name.get, price.get, radius.get, fakeTile, Vector(), radiusBoost.get, damageBoost.get, speedBoost.get)
          }else if(towerType.get == "attack") {
            towers += new AttackTower(name.get, price.get, damage.get, radius.get, attackSpeed.get, fakeTile)
          }else {
            throw new CorruptedFile("Tower type is not correctly defined.")
          }
        }
        
        
      }
        
    }catch{
      case e: IOException => throw new IOException("Failed to read file.")
      case e: CorruptedFile => throw new CorruptedFile("Data corrupted.")
    
    }finally{
      lineReader.close()
    }
    
    towers.toVector
  }
  
}