package TowersNenemies

import CoordinateSystem.Tile
import Game.GameLoader
import Game.Game
import scala.math.abs

/*Luokka kuvaa vihollista, joka pyrkii pääsemään tietä pitkin maaliin. Joka kierroksella syntyy tietty määrä vastustajia, riippuen niiden vaikeustasosta. 
* Jokaisen vihollisen vaikeustaso määrittyy muiden vihollisten nopeuden ja elämien avulla. 
*/
class Enemy(val name: String, LP: Int, val reward: Int, speed1: Int, private var xCoord: Int, private var yCoord: Int) {
    
  private var lifepoints = LP
  
  private var speed = speed1
  
  //Kertoo, millä indeksillä tiili on polulla, jota kohti vihollinen etenee.
  private var index = 0
  
  //Kertoo, minkä tiilen keskikohtaa kohti vihollisen pitää kulkea. 
  private var direction: Tile = Enemy.path(index)
  
  //Kertoo aikaisemman sijainnin, josta voidaan päätellä millä tiilellä vihollinen on. Tiili on aluksi sama kuin ensimmäinen tiili. 
  private var previousDirection: Tile = Enemy.path(index)
  
  private var destination = direction.centerCoords
  
  private var reachedGoal = false
  
  override def toString = s"name: $name\nhealth: $lifepoints\nspeed: $speed1\nreward: $reward\n\n"

  def isInGoal = reachedGoal
  
  //Kertoo, millä tiilellä vastustaja on.
  private var onTile = Enemy.path(index)
  
  //Päivittää tiedon millä tiilellä vastustaja on. 
  def updateTile = {
    
    if((xCoord - onTile.centerCoords._1).abs > Enemy.tileRadius || (yCoord - onTile.centerCoords._2).abs > Enemy.tileRadius ){
      
      onTile.enemies -= this
      val index = Enemy.path.indexOf(this) + 1
      
      onTile = if(Enemy.path.size >= index) Enemy.path(index) else onTile
      onTile.enemies += this
      
    }
    
  }
  
  def coordinates = (xCoord, yCoord)

  def updateDirection = {
    
      index += 1
      
      if(index >= Enemy.path.size){
        
        destination = (destination._1 + Enemy.tileRadius, destination._2)
        
      }else{
        
      previousDirection = direction 
      direction = Enemy.path(index)
      destination = direction.centerCoords
      
      }
      
  }
  
  //Kun muuttuja on <= 0 niin vihollinen voi liikkua yhden, jonka jälkeen muuttujasta tulee 1000 ja tätä aletaan vähentää muuttujan speed-arvolla. 
  private var whenToMove = 0
  
  //Katsoo voiko vihollinen liikkua ja liikkuu sen mukaan. 
  def act = {
    
    if(whenToMove <= 0){
      move
      whenToMove = speed
    } else {
      whenToMove -= speed
    }
    
  }
  
  
  //Liikuttaa vihollista yhdellä kohti directionia. 
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
     
     if(yCoord < destination._1){
       yCoord += 1
     } else if(yCoord > destination._1)
     {
       yCoord -= 1
     }else{
       Unit
     }
    
     if(xCoord == destination._1 && yCoord == destination._2) updateDirection
     
     if(xCoord == Enemy.goal._1 && yCoord == Enemy.goal._2 && !reachedGoal) {reachedGoal = true; Enemy.damage}
          
    
  }
  
  def getSpeed = speed

  def decreaseLP(amount: Int) = lifepoints -= amount
  
  def isDead = lifepoints <= 0
  
  
  
}

object Enemy{
  
  def apply(name: String, parameters: Vector[Int]) = {
    new Enemy(name, parameters(0), parameters(1), parameters(2), goal._1, goal._2)
  }
      
  //Reitti, jota pitkin viholliset kulkevat.
  lazy val path = GameLoader.getPath
  
  //Maali, joka vihollisten tulee saavuttaa eli pari xy-koordinaatteja. 
  lazy val goal = (path.last.centerCoords._1 + tileRadius, path.last.centerCoords._2)
  
  val tileSize = Tile.size
  
  val tileRadius = tileSize / 2
  
  private var game: Option[Game] = None
  
  def updateGame(game1: Game) = game = Some(game1)
  
  def damage = if(game.nonEmpty) game.get.decreaseLP(1)
    
  
}