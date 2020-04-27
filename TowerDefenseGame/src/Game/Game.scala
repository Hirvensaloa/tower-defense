package Game

import TowersNenemies._
import CoordinateSystem._
import scala.collection.mutable.Buffer


/*Describes game, it has grid which consists of tiles that can have towers on them. Game has round which controls enemies. 
*	Game ends when lifepoints are zero or round limit is achieved. Game handles tower creation and placing them. 
*/
class Game(val grid: Grid, towers1: Buffer[Tower], money1: Int, hp: Int, firstRound: Round, val difficulty: Int, val roundLimit: Int){
  
  
  private var lifepoints = hp
  private var money = money1
  private var currentRound = firstRound
  
  //Contains all the towers that are on the grid. 
  private val towers = towers1
  
  def isOver = lifepoints <= 0 || currentRound.number > roundLimit
  
  def isWon = isOver && lifepoints > 0
  
  //Tells if game is can continue.
  private var play = false
  
  def hasStarted = play
  
  def changePlay(value: Boolean) = play = value
  
  def showMoney = money
  def addMoney(amount: Int) = money += amount
  
  def showLifepoints = lifepoints
  def decreaseLP(amount: Int) = lifepoints -= amount
  def increaseLP(amount: Int) = lifepoints += amount
  
  def getTowers = towers.toVector
  def getEnemies = currentRound.getEnemies
 
  def roundNumber = currentRound.number
  def round = currentRound
  
  //Adds tower to the game.
  def addTower(x: Int, y: Int, towerModel: Tower) = {
      
    val tile = grid.listOfTiles(y)(x)
    
    val tower = createTower(tile, towerModel)
    
    if(tile.canAddTower)
    {
      tile.addTower(tower)
      towers += tower
      money -= tower.price
    }
    else
    {
      Unit
    }
    
  }
  
  //Creates tower. Basically copies a new tower object from the given tower model. 
  private def createTower(tile: Tile, towerModel: Tower): Tower = {

    towerModel match {
      case tower: AttackTower => new AttackTower(tower.name, tower.price, tower.getDamage, tower.showRadius, tower.getSpeed, tile, tower.image)
      case tower: SupportTower => new SupportTower(tower.name, tower.price, tower.showRadius, tile, tower.radiusBoost, tower.damageBoost, tower.speedBoost, tower.image)    
    }
    
  }
  
  //Removes tower from the game. 
  def removeTower(tower: Tower) = {
    towers -= tower
    tower.clear
    money += tower.getSellPrice.toInt
   }
  
  //Method which is meant to be called constantly so that the game can "run". 
  def run() = 
  {
    
     if(!currentRound.isOver && play && !this.isOver){
       
     towers.foreach(_.act)
     currentRound.tick
     
     } else if(play){
       
       play = false
       currentRound = Game.newRound(this.difficulty, 1)
       
     }
       
     
  }
  
  
}
//Companion object handles game creation. 
object Game{
  
  //List of towers which are available. 
  val towerMenu = GameLoader.loadTowerMenu("towers.txt")
  
  //Creates a new Game. 
  def newGame(mapName: String) = {
    
    val grid = GameLoader.loadGrid(mapName, "maps.txt")
    val difficulty = Settings.difficulty
    val money = Settings.money
    val health = Settings.lifepoints
    val roundLimit = Settings.roundLimit
    
    val game = new Game(grid, Buffer(), money, health, newRound(difficulty, 1), difficulty, roundLimit)
    
    recentGame = Some(game)
    
    game
  }
  
  //Loads new game if there is one to be loaded. Returns none if no game save. 
  def loadGame = {
    
    val game = GameLoader.loadGameSave
    
    if(game.nonEmpty){
      recentGame = game
    }
    
    game
  }
  
  def saveGame = GameLoader.saveGame(game)
  
  private var recentGame: Option[Game] = None
  def game = recentGame.get
  
  //Calls Round objects apply method which creates new round with given difficulty,
  def newRound(difficulty: Int, nro: Int): Round = Round(difficulty, nro)
  
  
}