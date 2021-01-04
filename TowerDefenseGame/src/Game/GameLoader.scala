package Game

import TowersNenemies._

import java.awt.Image
import CoordinateSystem._
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import java.io.IOException
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter
import javax.imageio.ImageIO
import java.io.File

//This handles all the reading of files. 
object GameLoader {
  
  
  //Exception that is caused by incorrectly formatted file. 
  class CorruptedFile(val msg: String) extends Exception(msg)
  
  val images = (new File("Images")).listFiles //All the tower and enemy images. 
  val menuImages = (new File("MenuImages")).listFiles //All the buttons, signs etc. 
  
  
  //Loads all the available towers from file and returns a Vector of them. 
  def loadTowerMenu(fileName: String): Vector[Tower] = {
    
    val towers: Buffer[Tower] = Buffer()
    
    val lineReader = new BufferedReader(new FileReader(fileName))
    
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
          var radiusBoost: Option[Int] = None
          var damageBoost: Option[Int] = None
          var speedBoost: Option[Int] = None
          var attackSpeed: Option[Int] = None
          var image: Option[String] = None
          
          line = lineReader.readLine()
          
          while(line != null && line.trim.toLowerCase != "#end")
          {
            
            line.takeWhile(_ != ':').trim.toLowerCase match {
              case "name" => name = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "price" => price = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "radius" => radius = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "type" => towerType = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "damage" => damage = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "radiusboost" => radiusBoost = Some(line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toInt)
              case "damageboost" => damageBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toInt))
              case "speedboost" => speedBoost = Some((line.dropWhile(_ != ':').drop(1).trim.dropRight(1).toInt))
              case "attackspeed" => attackSpeed = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "image" => image = Some(line.dropWhile(_ != ':').drop(1).trim)
              case string: String=> if(string.isEmpty) Unit else throw new CorruptedFile("Tower info corrupted.")
            }
            
            line = lineReader.readLine()
            
          }
          
          if(!(towerType.nonEmpty && name.nonEmpty && price.nonEmpty && radius.nonEmpty && image.nonEmpty)) throw new CorruptedFile("Corrupted tower file") 
          
          val img = {
            val file = images.find(_.getName == image.get)
            
            if(file.isEmpty) throw new CorruptedFile("No image found. " + image.get)
            
            ImageIO.read(file.get).getScaledInstance(Tile.size, Tile.size, java.awt.Image.SCALE_SMOOTH)
          }
          
          val fakeGrid = Grid(Vector(Vector(true)), "")
          val fakeTile = fakeGrid.listOfTiles(0)(0)
          
          if(towerType.get.toLowerCase == "support"){
             if(radiusBoost.isEmpty || damageBoost.isEmpty || speedBoost.isEmpty) throw new  CorruptedFile(name.get + " info corrupted.")
             towers += new SupportTower(name.get, price.get, radius.get, fakeTile,  radiusBoost.get, damageBoost.get, speedBoost.get, img)
          }else if(towerType.get.toLowerCase == "attack") {
            if(attackSpeed.isEmpty || damage.isEmpty) throw new CorruptedFile(name.get + " info corrupted.") 
            towers += new AttackTower(name.get, price.get, damage.get, radius.get, attackSpeed.get, fakeTile, img)
          }else {
            throw new CorruptedFile("Tower type is not correctly defined.")
          }
        }
        
        
      }
        
    }catch{
      case e: CorruptedFile => throw e
      case e: IOException => throw new IOException("Failed to read file.")
    
    }finally{
      lineReader.close()
    }
    
    towers.toVector
  }
  
  
  
  
  
  
  
  //Loads a grid for the name given and returns it. 
  def loadGrid(name1: String, fileName: String): Grid = {
    
    val lineReader = new BufferedReader(new FileReader(fileName))
    
    var grid = Buffer[Buffer[Boolean]]()
    var path: Vector[Tile] = Vector()
    var name: Option[String] = None
    var start: Option[Int] = None
    var end: Option[Int] = None
    var read = false
    
    //Tells how many times these have been found, both should be 1 in the end. 
    var startFound = 0
    var endFound = 0 
    
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
                  startFound += 1
                  start = Some(grid.size)
                  var string1 = string.drop(1)
                  if(string.takeRight(1) == "g") {end = Some(grid.size); string1 = string1.dropRight(1); endFound += 1}

                  grid += string1.map(a => if(a == 'x') true else false).toBuffer
                }
                else if(string.head == '.' || string.head == 'x')
                { 
                  var string1 = string
                  if(string.takeRight(1) == "g") {end = Some(grid.size); string1 = string1.dropRight(1); endFound += 1}
                  
                  grid += string1.map(a => if(a == 'x') true else false).toBuffer
                } else
                {
                  throw new CorruptedFile("Map file is corrupted ") 
                }
                }
              }
              }
                       
            line = lineReader.readLine()
            
          }
          
          if(start.isEmpty || end.isEmpty || grid.isEmpty || startFound != 1 || endFound != 1) throw new CorruptedFile("Map file is corrupted")
          
          
          read = true
          
        }
        
      }
      
      if(grid.isEmpty) throw new CorruptedFile("Map not found.")
        
      if(grid(0).size < 5 || grid.size < 3) throw new CorruptedFile("Map too small")
      
        
      for(g <- grid){
        
        if(g.size != grid(0).size) throw new CorruptedFile("Map is not a rectangle")
        
      }
      
    }catch{
      case e: CorruptedFile => throw e
      case e: IOException => throw new IOException("Failed to read file.")
    
    }finally{
      lineReader.close()
    }
    
    
    val grid1 = Grid(grid.map(_.toVector).toVector, name1)
    
    findPath(start.get, end.get,grid.map(_.toVector).toVector, grid1)
    
    gridWidth = Some(grid1.listOfTiles(0).size)
    gridHeight = Some(grid1.listOfTiles.size)

    grid1

    
  }
  
  private var gridWidth: Option[Int] = None
  private var gridHeight: Option[Int] = None
  
  def mapWidth = gridWidth.get * Tile.size
  def mapHeight = gridHeight.get * Tile.size
  
  
  //Finds a path for grid. In other words finds the road coordinates. 
  private def findPath(start: Int, end: Int, tiles: Vector[Vector[Boolean]], grid: Grid) = {
    
    var pathIndex = Buffer[(Int, Int)]()
    
    if(tiles(start)(0)) throw new CorruptedFile("No start found for map")
    
    var xy = (0, start)
    
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
          (x - 1, y)
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
  
  
  
  
  
  
  //Loads map names from file.
  def getMapNames(fileName: String): Vector[String] = {
    val lineReader = new BufferedReader(new FileReader(fileName))
    
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
      case e: CorruptedFile => throw e
      case e: IOException => throw new IOException("IO exception while reading map file.")
      
    }finally{
      lineReader.close
    }
    
    names.toVector
  }
  
  
  
  
  
  
  //Reads "enemies.txt" file and return a map with enemy name -> enemy parameters. 
  def getEnemyParameters(fileName: String): Map[String, (Vector[Int], Image)] = {
    
    val enemies: Map[String, (Vector[Int], Image)] = Map()
    
    val lineReader = new BufferedReader(new FileReader(fileName))
    
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
          var image: Option[String] = None
          var parameters: Buffer[Int] = Buffer()
          
          line = lineReader.readLine()
          
          while(line != null && line.trim.toLowerCase != "#end")
          {
            
            line.takeWhile(_ != ':').trim.toLowerCase match {
              case "name" => name = Some(line.dropWhile(_ != ':').drop(1).trim)
              case "speed" => speed = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "lifepoints" => lifepoints = Some(line.dropWhile(_ != ':').drop(1).trim.toInt)
              case "reward" => reward = Some(line.dropWhile(_ != ':').drop(1).trim.takeWhile(_.isDigit).trim.toInt)
              case "image" => image = Some(line.dropWhile(_ != ':').drop(1).trim)
              case string: String=> if(string.isEmpty) Unit else throw new CorruptedFile("Tower info corrupted.")
            }
            
            line = lineReader.readLine().toLowerCase
            
          }
          
          if(!(name.nonEmpty && speed.nonEmpty && lifepoints.nonEmpty && reward.nonEmpty && image.nonEmpty)) throw new CorruptedFile("Corrupted enemy file.") 
          
           parameters ++= Buffer(lifepoints.get, reward.get, speed.get)
           
           val img = {
            val file = images.find(_.getName == image.get)
            
            if(file.isEmpty) throw new CorruptedFile("No image found. " + image.get)
            
            ImageIO.read(file.get).getScaledInstance(Tile.size/2, Tile.size/2, java.awt.Image.SCALE_SMOOTH)
          }
           
           enemies += name.get -> (parameters.toVector, img)
        }
        
       
      }
      
      if(enemies.isEmpty) throw new CorruptedFile("No enemies found.")
        
    }catch{
      case e: CorruptedFile => throw e
      case e: IOException => throw new IOException("Failed to read file.")
    
    }finally{
      lineReader.close()
    }
    
    enemies
  }
  //Map enemies by their difficulty.
  def enemiesMappedToDifficulty(enemies: Map[String, (Vector[Int], Image)]) = {
    val values = enemies.values.map(_._1)
    
    val maxSpeed = values.map(_(0)).max
        
    val maxLifepoints = values.map(_(2)).max

    val enemiesByDifficulty = enemies.mapValues(a => calculateDifficulty(a._1(0), maxLifepoints, a._1(2), maxSpeed)).groupBy(_._2).mapValues(_.keys)
    
    enemiesByDifficulty.mapValues(_.toVector)
  }
  
  
  //Calculates a difficulty for enemy based on the average enemy. 
  def calculateDifficulty(value: Int, maxValue: Int, value1: Int, maxValue1: Int): Int = {
    
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
  
  
  
  //Loads all the menu images. Buttons, signs etc. 
  def loadMenuImages: Map[String, Image] = {
    
    val fileList = menuImages
    
    val map: Map[String, Image] = Map()
    
    try{
      
      for(file <- fileList){
        
        val image = ImageIO.read(file)
        val name = file.getName
        
        map += name -> image
        
        
      }
      
    }catch{
      case e: IOException => throw new IOException("Failed to read images")
    }
    
    map
    
  }
  
  
  
  
  //Reads game settings from settings.txt file. Returns a vector containing lifepoints, difficulty and money, respectively. 
  def readSettings: Vector[Int] = {
    
    val lineReader = new BufferedReader(new FileReader("settings.txt"))
    
    var lifepoints: Option[Int] = None
    var difficulty: Option[Int] = None
    var money: Option[Int] = None
    var roundLimit: Option[Int] = None

    try{
      
      var line = lineReader.readLine()
      
      while(line != null)
      {
        if(line.nonEmpty && line.contains(':')){
          
           line = line.trim
          
           val name = line.takeWhile(_ != ':').trim.toLowerCase
           val value = line.dropWhile(_ != ':').drop(1).trim
           
           name match{
             case "lifepoints" => lifepoints = Some(value.toInt)
             case "coins" => money = Some(value.toInt)
             case "difficulty" => difficulty = if(value.toLowerCase == "hard") Some(3) else if(value.toLowerCase == "medium") Some(2) else if(value.toLowerCase == "easy") Some(1) else throw new CorruptedFile("No such difficulty.")
             case "round limit" => roundLimit = Some(value.toInt)
             case _ => Unit
           }
          
        }
        
        line = lineReader.readLine()
      }
      
      if(lifepoints.isEmpty || difficulty.isEmpty || money.isEmpty || roundLimit.isEmpty) throw new CorruptedFile("Settings file corrupted")
            
      
    }catch{
      case e: IOException => throw new IOException("IO exception while reading settings file.")
      case e: NumberFormatException => throw new CorruptedFile("Settings file corrupted")
      case e: CorruptedFile => throw e
      
    }finally{
      lineReader.close
    }
    
    Vector(lifepoints.get, difficulty.get, money.get, roundLimit.get)
    
  }
  
  
  //Saves given game to "GameSave.txt" file. 
  def saveGame(game: Game) = {
    
    val writer = new PrintWriter("GameSave.txt", "UTF-8")
    
    try{
      
      writer.println("You are not supposed to edit this file in anyway. Editing might lead to the game not running properly.")
      
      for(tower <- game.getTowers){
        
        writer.println("tower: " + tower.name + " " + tower.coordinates)
        
      }
      
      writer.println("roundnro:" + game.roundNumber)
      writer.println("difficulty:" + game.round.difficulty)
      writer.println("hp:" + game.showLifepoints)
      writer.println("money:" + game.showMoney)
      writer.println("mapname:" + game.grid.name)
      
    }catch{
      case e: IOException => throw new IOException("Failed to write to file")
      
    }finally{
      writer.close()
    }
    
  }
  
  def loadGameSave: Option[Game] = {
    
    try{
      
      val reader = new BufferedReader(new FileReader("GameSave.txt"))
      
      val towers: Buffer[(Tower, (Int, Int))] = Buffer()
      var mapName: Option[String] = None
      var HP: Option[Int] = None
      var difficulty: Option[Int] = None
      var roundNumber: Option[Int] = None
      var money: Option[Int] = None

      var line = reader.readLine
      
      while(line != null){
        
        line.takeWhile(_ != ':') match{
          case "roundnro" => roundNumber = Some(line.dropWhile(_ != ':').drop(1).toInt)
          case "hp" => HP = Some(line.dropWhile(_ != ':').drop(1).toInt)
          case "money" => money = Some(line.dropWhile(_ != ':').drop(1).toInt)
          case "difficulty" => difficulty = Some(line.dropWhile(_ != ':').drop(1).toInt)
          case "mapname" => mapName = Some(line.dropWhile(_ != ':').drop(1))
          case "tower" => {
            
            val parameters = line.trim.dropWhile(_ != ' ').trim.split(" ")
            
            if(parameters.size == 2){
              
              val model = Game.towerMenu.find(_.name == parameters(0))
              
              if(model.nonEmpty){
                
                val x = parameters(1).drop(1).takeWhile(_ != ',').toInt / 100
                val y = parameters(1).dropWhile(_ != ',').drop(1).takeWhile(_ != ')').toInt / 100
                
                towers += model.get -> (x, y)
                
              }
              
            }
            
          }
          case _ => Unit
        }
        
        line = reader.readLine
        
      }
      
      if(HP.isEmpty || mapName.isEmpty || difficulty.isEmpty || roundNumber.isEmpty || money.isEmpty){
        
        None
      
      }else{
        
        val towerList: Buffer[Tower] = Buffer()
        val grid = loadGrid(mapName.get, "maps.txt")
        val round = Round(difficulty.get, roundNumber.get)
        val gameDifficulty = Settings.difficulty
        val roundLimit = Settings.roundLimit
        
        val game = new Game(grid, towerList, money.get, HP.get, round, gameDifficulty, roundLimit)
        
        towers.foreach(a => game.addTower(a._2._1, a._2._2, a._1))
        
        Some(game)
        
      }
      
    }catch{
       case e: CorruptedFile => throw e
       case e: java.io.FileNotFoundException => throw new java.io.FileNotFoundException("Save game file could not be found.")
        case e: IOException => throw new IOException("Failed while reading save file.")
     }
  
      
    
        
  
  }  
  
  
  

}