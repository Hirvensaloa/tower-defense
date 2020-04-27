package TowersNenemies

import CoordinateSystem.Tile
import java.awt.Image
import Game.GameLoader
import Game.Game
import scala.math.abs


//Class describes an enemy that tries to reach goal on the other side of the map. Enemies have difficulty which is determined by its speed and lifepoints.
class Enemy(val name: String, LP: Int, val reward: Int, speed1: Int, private var xCoord: Int, private var yCoord: Int, val image: Image) {
    
  private var lifepoints = LP
  
  private var speed = speed1
  def getSpeed = speed
  
  private var index = 0 //Tells which index on the path the enemy is on. 
  
  private var direction: Tile = Enemy.path(index) //Tells the tile which enemy is trying to go. 
  
  private var previousDirection: Tile = Enemy.path(index) //Tells the last tile enemy was trying to go. 
  
  private var destination = direction.destinationCoords
  
  private var reachedGoal = false
  
  override def toString = s"name: $name\nhealth: $lifepoints\nspeed: $speed1\nreward: $reward\n\n"

  var onTile = Enemy.path(index) //Tells which tile the enemy is on. 
  
  def isInGoal = reachedGoal
  
  def isDead = lifepoints <= 0
  
  def decreaseLP(amount: Int) = {
   
    lifepoints -= amount
    
    if(this.isDead) {
      
      this.onTile.enemies -= this
      Game.game.addMoney(this.reward)
      
    }
  }
 
  //Updates onTile. 
  def updateTile = {
    
    if(!isDead && (xCoord - onTile.coords._1).abs > Enemy.tileRadius || (yCoord - onTile.coords._2).abs > Enemy.tileRadius ){
      
      onTile.enemies -= this
      val indexi = Enemy.path.indexOf(onTile) + 1
      
      onTile = if(Enemy.path.size > indexi) Enemy.path(indexi) else onTile
      onTile.enemies += this
      
      
    }
    
  }
  
  def coordinates = (xCoord, yCoord)

  //Updates direction for enemy. 
  def updateDirection = {
    
      index += 1
      
      if(index >= Enemy.path.size){
        
        destination = (destination._1 + Enemy.tileRadius, destination._2)
        
      }else{

      previousDirection = direction 
      direction = Enemy.path(index)
      destination = direction.destinationCoords
      
      }
      
  }
  
  //When this is == 0 enemy can move one step forward, then it is reset to 350 and starts to tick down with enemys speed value. 
  private var whenToMove = 0
  
  //Checks whether the player can move or not. If it can move, move is called. 
  def act = {
    
    if(whenToMove <= 0){
      move
      whenToMove = 350
    } else {
      whenToMove -= speed
    }
    
  }
  
  
  //Moves enemy one step forward towards direction. 
  private def move = {
    
    updateTile    
    
     if(xCoord < destination._1){
       xCoord += 1
     } else if(xCoord > destination._1)
     {
       xCoord -= 1
     }else{
       Unit
     }
     
     if(yCoord < destination._2){
       yCoord += 1
     } else if(yCoord > destination._2)
     {
       yCoord -= 1
     }else{
       Unit
     }
    
     if(xCoord == destination._1 && yCoord == destination._2) updateDirection
     
     if(xCoord == Enemy.goal._1 && yCoord == Enemy.goal._2 && !reachedGoal) {
       
       reachedGoal = true
       Game.game.decreaseLP(1)
       this.onTile.enemies -= this
       }
          
    
  }    
  
}

object Enemy{
  
  def apply(name: String, parameters: Vector[Int], img: Image) = {
    
    new Enemy(name, parameters(0), parameters(1), parameters(2), start._1, start._2, img)
    
  }
      
  //Path that enemies are meant to follow.
  def path = GameLoader.getPath
  
  //Coordinates where enemy starts.
  private def start = (path.head.destinationCoords._1 - tileRadius, path.head.destinationCoords._2)
  
  //Coordinates where enemy is trying to get. 
  private def goal = (path.last.destinationCoords._1 + tileSize/4, path.last.destinationCoords._2)

  val tileSize = Tile.size
  val tileRadius = tileSize / 2
    
  
}