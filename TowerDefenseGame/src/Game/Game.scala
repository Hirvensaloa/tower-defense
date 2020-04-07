package Game

import TowersNenemies._
import CoordinateSystem._
import scala.collection.mutable.Buffer
import java.io.IOException
import java.io.BufferedReader
import java.io.FileReader


//Parametri towers1 sisältää kaikki pelikentällä olevat tornit, jos niitä on. Towermenu sisältää kaikki pelissä ostettavat tornit. 
class Game(val grid: Grid, towers1: Buffer[Tower], money1: Int, hp: Int, private val towerMenu: Vector[Tower], firstRound: Round, val difficulty: Int) {
  
  
  private var lifepoints = hp
  
  private var money = money1
  
  def showMoney = money
  
  def showLifepoints = lifepoints
  
  def decreaseLP(amount: Int) = lifepoints -= amount
  
  def increaseLP(amount: Int) = lifepoints += amount
  
  private var currentRound = firstRound
  
  def roundNumber = currentRound.number
  
  //Bufferi, joka sisältää kaikki kentällä olevat tornit
  private val towers = towers1
  
  def addTower(x: Int, y: Int, index: Int) = {
      
    val tile = grid.listOfTiles(y)(x)
    
    val tower = createTower(tile, index)
    
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
  
  //Luo tornin tornivalikon indeksiltä. 
  private def createTower(tile: Tile, index: Int): Tower = {
    
    val towerModel = towerMenu(index)
    
    towerModel match {
      case tower: AttackTower => new AttackTower(tower.name, tower.price, tower.getDamage, tower.showRadius, tower.getSpeed, tile)
      case tower: SupportTower => new SupportTower(tower.name, tower.price, tower.showRadius, tile, tower.radiusBoost, tower.damageBoost, tower.speedBoost)    
    }
    
  }
  
  def removeTower(tower: Tower) = {
    towers -= tower
    tower.tile.removeTower()
    money += tower.getSellPrice.toInt
   }
  
  def advance() = 
  {
     currentRound.tick
     
     towers.foreach(_.act)
  }
  
  
  
}
//Kumppaniolio hoitaa pelin luomisen. 
object Game{
  
  val towerMenu = GameLoader.loadTowerMenu
  
  def newGame(mapName: String, difficulty: Int) = {
    val grid = GameLoader.loadGrid(mapName)
    
    val game = new Game(grid, Buffer(), 500, 100, towerMenu, newRound(difficulty), difficulty)
    
    thisGame = Some(game)
    
    game
  }
  
  private var thisGame: Option[Game] = None
  def getGame = thisGame.get
  
  
  def newRound(difficulty: Int): Round = Round(difficulty)
  
  
}