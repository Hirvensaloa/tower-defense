package Game

import TowersNenemies._
import CoordinateSystem._
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import java.io.IOException
import java.io.BufferedReader
import java.io.FileReader

object GameLoader {
  
  //Virheellisestä tiedostosta johtuva poikkeus
  class CorruptedFile(val msg: String) extends Exception(msg)
  
  def loadTowerMenu = {
    
    val towers: Buffer[Tower] = Buffer()
    
    val lineReader = new BufferedReader(new FileReader("towers.txt"))
    
    try {
      
      var line = lineReader.readLine()
      
      while(line != null)
      {
        line = lineReader.readLine()
        if(line != null && line.nonEmpty && line.trim.toLowerCase == "#start")
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
          
          line = lineReader.readLine().toLowerCase
          
          while(line != null && line.trim.toLowerCase != "#end")
          {
            
            line.takeWhile(_ != ':').trim match {
              case "name" => name = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "price" => price = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "radius" => radius = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "type" => towerType = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "damage" => damage = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "radiusboost" => radiusBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toDouble/100) + 1.0)
              case "damageboost" => damageBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toDouble/100) + 1.0)
              case "speedboost" => speedBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toDouble/100) + 1.0)
              case "attackspeed" => attackSpeed = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case string: String=> if(string.isEmpty) Unit else throw new CorruptedFile("Tower info corrupted.")
            }
            
            line = lineReader.readLine().toLowerCase
            
          }
          
          if(!(towerType.nonEmpty && name.nonEmpty && price.nonEmpty && radius.nonEmpty)) throw new CorruptedFile("Corrupted tower file") 
          
            
          val fakeGrid = Grid(Vector(Vector(true)))
          val fakeTile = fakeGrid.listOfTiles(0)(0)
          
          if(towerType.get == "support"){
             if(radiusBoost.isEmpty || damageBoost.isEmpty || speedBoost.isEmpty) throw new  CorruptedFile(name.get + " info corrupted.")
             towers += new SupportTower(name.get, price.get, radius.get, fakeTile,  radiusBoost.get, damageBoost.get, speedBoost.get)
          }else if(towerType.get == "attack") {
            if(attackSpeed.isEmpty || damage.isEmpty) throw new CorruptedFile(name.get + " info corrupted.") 
            towers += new AttackTower(name.get, price.get, damage.get, radius.get, attackSpeed.get, fakeTile)
          }else {
            throw new CorruptedFile("Tower type is not correctly defined.")
          }
        }
        
        
      }
        
    }catch{
      case e: IOException => throw new IOException("Failed to read file.")
      case e: CorruptedFile => throw e
    
    }finally{
      lineReader.close()
    }
    
    towers.toVector
  }
  
  
  
  
  
  //Lataa asetustiedostoista kaikki kartat ja niiden koordinaatiston. Palauttaa kartan nimen ja siihen liittyvän koordinaatiston ja reitin, jota pitkin tie menee. 
  def loadGrid(name1: String): (Grid, Vector[(Int, Int)]) = {
    val lineReader = new BufferedReader(new FileReader("maps.txt"))
    
    var grid = Buffer[Buffer[Boolean]]()
    var path: Vector[(Int, Int)] = Vector()
    var name: Option[String] = None
    var start: Option[Int] = None
    var end: Option[Int] = None
    var read = false
    
    try {
      
      var line = lineReader.readLine()
      
      while(line != null && !read)
      {
        
        line = lineReader.readLine()
        
        if(line != null && line.trim.nonEmpty && line.trim.head.toLower == 'n' && line.dropWhile(_ != ':').drop(1).trim.toLowerCase == name1)
        {
          line = lineReader.readLine().toLowerCase
          
          while(line != null && line.trim.toLowerCase != "#end")
          {
            
            line.takeWhile(_ != ':').trim.toLowerCase match{
              case "name" => name = Some(line.dropWhile(_ != ':').drop(1).trim)
              case string: String => {
                if(string.nonEmpty)
                {
                if(string.head == 's')
                {
                  start = Some(grid.size)
                  var string1 = string.drop(1)
                  if(string.takeRight(1) == "g") {end = Some(grid.size); string1 = string1.dropRight(1)}

                  grid += string1.map(a => if(a == 'x') true else false).toBuffer
                }
                else if(string.head == '.' || string.head == 'x')
                { 
                  var string1 = string
                  if(string.takeRight(1) == "g") {end = Some(grid.size); string1 = string1.dropRight(1)}
                  
                  grid += string1.map(a => if(a == 'x') true else false).toBuffer
                } else
                {
                  throw new CorruptedFile("Map file is corrupted ") 
                }
                }
              }
              }
                       
            line = lineReader.readLine().toLowerCase
            
          }
          
          if(start.isEmpty || end.isEmpty || grid.isEmpty) throw new CorruptedFile("Map file is corrupted")
          
          path = findPath(start.get, end.get, grid.map(_.toVector).toVector)
          
          read = true
          
        }
        
      }
      
      if(grid.isEmpty) throw new CorruptedFile("Map not found.")
        
      
        
    }catch{
      case e: IOException => throw new IOException("Failed to read file.")
      case e: CorruptedFile => throw e
    
    }finally{
      lineReader.close()
    }
    
    
    (Grid(grid.map(_.toVector).toVector), path)
    
  }
  
  //Etsii koordinaatistosta reitin tielle. Palauttaa sen kokoelmana koordinaatteja. 
  private def findPath(start: Int, end: Int, tiles: Vector[Vector[Boolean]]): Vector[(Int, Int)] = {
    
    var pathIndex = Buffer[(Int, Int)]()
    
    if(tiles(start)(0)) throw new CorruptedFile("No start found for map")
    
    var xy = (start, 0)
    
    pathIndex += xy
    
    var previousxy = Buffer(xy)
    
    while(xy != (tiles(0).size - 1, end))
    {
      val x = xy._1
      val y = xy._2

      xy = {
        if(y - 1 >= 0 && y - 1 < tiles.size && !tiles(y - 1)(x) && !previousxy.contains(x, y - 1)){
          (x, y - 1)
        }else if(y + 1 >= 0 && y + 1 < tiles.size && !tiles(y + 1)(x) && !previousxy.contains(x, y + 1)){
           (x, y + 1)
        }else if(x + 1 < tiles(0).size && !tiles(y)(x + 1) && !previousxy.contains(x + 1, y)){
          (x + 1, y)
        }else if(x - 1 >= 0 && x < tiles(0).size && !tiles(y)(x - 1) && !previousxy.contains(x - 1, y)){
          (x + 1, y)
        }else{
          throw new CorruptedFile("Map has no valid path.")
        }
      }
      
      previousxy += xy
      pathIndex += xy   
    
  }
    
    pathIndex.toVector
    
 }
  
  //Lataa tasojen nimet maps-tiedostosta. 
  def getMapNames: Vector[String] = {
    val lineReader = new BufferedReader(new FileReader("maps.txt"))
    
    val names = Buffer[String]()
    
    try{
      var line = lineReader.readLine()
      
      while(line != null)
      {
        if(line.trim.nonEmpty && line.takeWhile(_ != ':').trim.toLowerCase == "name") names += line.dropWhile(_ != ':').drop(1).trim
        
        line = lineReader.readLine()
      }
      
      if(names.isEmpty) throw new CorruptedFile("Map names not found") 
      
    }catch{
      case e: IOException => throw new IOException("IO exception while reading map file.")
      case e: CorruptedFile => throw e
      
    }finally{
      lineReader.close
    }
    
    names.toVector
  }
  
  
}