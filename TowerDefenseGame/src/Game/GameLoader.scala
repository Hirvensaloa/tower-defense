package Game

import TowersNenemies._
import CoordinateSystem._
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import java.io.IOException
import java.io.BufferedReader
import java.io.FileReader

//Olio hoitaa asetustiedostojen lukemisen eli kartan, tornien ja vihollisten. 
object GameLoader {
  
  
  //Virheellisestä tiedostosta johtuva poikkeus
  class CorruptedFile(val msg: String) extends Exception(msg)
  
  
  //Lataa towers.txt tiedostosta kaikki pelissä olevat tornityypit ja palauttaa niistä kokoelman. 
  def loadTowerMenu: Vector[Tower] = {
    
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
  def loadGrid(name1: String): Grid = {
    val lineReader = new BufferedReader(new FileReader("maps.txt"))
    
    var grid = Buffer[Buffer[Boolean]]()
    var path: Vector[Tile] = Vector()
    var name: Option[String] = None
    var start: Option[Int] = None
    var end: Option[Int] = None
    var read = false
    
    try {
      
      var line = lineReader.readLine()
      
      while(line != null && !read)
      {
        
        line = lineReader.readLine()
        
        if(line != null && line.trim.nonEmpty && line.trim.head.toLower == 'n' && line.dropWhile(_ != ':').drop(1).trim.toLowerCase == name1.toLowerCase)
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
    
    
    val grid1 = Grid(grid.map(_.toVector).toVector)
    
    findPath(start.get, end.get,grid.map(_.toVector).toVector, grid1)
    
    grid1

    
  }
  
  
  
  
  
  //Etsii koordinaatistosta reitin tielle. Palauttaa sen kokoelmana koordinaatteja. 
  private def findPath(start: Int, end: Int, tiles: Vector[Vector[Boolean]], grid: Grid) = {
    
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
    
   path =  Some(pathIndex.map(a => grid.listOfTiles(a._2)(a._1)).toVector)
    
 }
  
  private var path: Option[Vector[Tile]] = None
  
  def getPath = {
    try{
      if(path.isEmpty) throw new CorruptedFile("No path found.")
      
      if(path.get.filter(_.canAddTower).nonEmpty) throw new CorruptedFile("Not a valid path.")
      
    }catch{
      case e: CorruptedFile => e
    }
    
    path.get
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
  
  
  
  
  
  
  //Palauttaa Mapin, jossa avaimina on vihollisten nimet ja arvoina on niiden parametrit. 
  def getEnemyParameters: Map[String, Vector[Int]] = {
    
    val enemies: Map[String, Vector[Int]] = Map()
    
    val lineReader = new BufferedReader(new FileReader("enemies.txt"))
    
    try {
      
      var line = lineReader.readLine()
      
      while(line != null)
      {
        line = lineReader.readLine()
        if(line != null && line.nonEmpty && line.trim.toLowerCase == "#start")
        {
          
          var name: Option[String] = None
          var speed: Option[Int] = None
          var lifepoints: Option[Int] = None
          var reward: Option[Int] = None
          var parameters: Buffer[Int] = Buffer()
          
          line = lineReader.readLine().toLowerCase
          
          while(line != null && line.trim.toLowerCase != "#end")
          {
            
            line.takeWhile(_ != ':').trim match {
              case "name" => name = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "speed" => speed = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "lifepoints" => lifepoints = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "reward" => reward = Some(line.dropWhile(_ != ':').drop(1).trim.takeWhile(_.isDigit).trim.toInt)
              case string: String=> if(string.isEmpty) Unit else throw new CorruptedFile("Tower info corrupted.")
            }
            
            line = lineReader.readLine().toLowerCase
            
          }
          
          if(!(name.nonEmpty && speed.nonEmpty && lifepoints.nonEmpty && reward.nonEmpty)) throw new CorruptedFile("Corrupted enemy file.") 
          
           parameters ++= Buffer(lifepoints.get, reward.get, speed.get)
           
           enemies += name.get -> parameters.toVector
        }
        
       
      }
      
      if(enemies.isEmpty) throw new CorruptedFile("No enemies found.")
        
    }catch{
      case e: IOException => throw new IOException("Failed to read file.")
      case e: CorruptedFile => throw e
    
    }finally{
      lineReader.close()
    }
    
    enemies
  }
  
  def enemiesMappedToDifficulty(enemies: Map[String, Vector[Int]]) = {
    val values = enemies.values
    
    val maxSpeed = values.map(_(0)).max
        
    val maxLifepoints = values.map(_(2)).max

    val enemiesByDifficulty = enemies.mapValues(a => calculateDifficulty(a(0), maxLifepoints, a(2), maxSpeed)).groupBy(_._2).mapValues(_.keys)
    
    enemiesByDifficulty.mapValues(_.toVector)
  }
  
  //Laskee vaikeustason viholliselle perustuen sen parametreihin. 
  def calculateDifficulty(value: Int, maxValue: Int, value1: Int, maxValue1: Int) = {
    
    val interval = maxValue / 3
    
    val difficulty = {
      if(value < interval){
        1
    }
    else if(value > interval * 2){
      3
    }else{
      2
    }
  }
    
    val interval1 = maxValue1 / 3
    
    
    
    val difficulty1 = {
      if(value1 < interval1){
        1
    }
    else if(value1 > interval1){
      3
    }else{
      2
    }
  }
    
    (difficulty + difficulty1) / 2
    
  }
  
  
}