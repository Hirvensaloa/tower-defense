package Game

import TowersNenemies.Enemy
import Interface.GameGUI
import scala.util.Random
import scala.collection.mutable.Buffer

/*Class Round is responsible for creating enemies and handling them. Round spawns random enemies randomly according to its difficulty.
* Every round is created based on previous rounds. Enemies variable tells all of the enemies to be spawned this round. 
*/
class Round(val number: Int, private val enemies: Vector[Enemy], val difficulty: Int) {
  
  private var over = false
  def isOver = over
  
  private var enemiesOnTheRoad: Buffer[Enemy] = Buffer() //Buffer of all the enemies that are on the road. 
  
  private var rand = new Random()
  private var time = rand.nextInt(350) //Random number which tells when to spawn new enemy. 
  
  def getEnemies = enemiesOnTheRoad.toVector

  private var index = 0

  //This causes round to advance one step. 
  def tick = {
    
    if(time == 0 && index < enemies.size) { //This is executed when new enemy needs to be spawned. 
      
      enemiesOnTheRoad += enemies(index)
      enemies(index).onTile.enemies += enemies(index)

      time = rand.nextInt(150)
      index += 1
      
      enemiesOnTheRoad.foreach(_.act)
      
    } else {
      
      time -= 1
      enemiesOnTheRoad.foreach(_.act)
      
    }

    updateList
    
    if(index >= enemies.size - 1 && enemiesOnTheRoad.isEmpty && !over) {
      
      over = true 
      Round.lastRound = Some(this)
      GameGUI.text = Some(s"Round $number is over!")      
      
    }
    
  }
  
  //Updates the enemy list to get rid of dead enemies and those that have reached goal. 
  private def updateList = enemiesOnTheRoad = enemiesOnTheRoad.filter(a => !a.isDead && !a.isInGoal)
  
}
object Round {
  
  //Creates a new round based on the difficulty. 
  def apply(difficulty: Int, roundNr: Int): Round = 
  {
     if(lastRound.nonEmpty){
       val newDifficulty = lastRound.get.difficulty + originalDifficulty * roundNr
       val newNumber = lastRound.get.number + 1
       val enemies = getRandomEnemies(newDifficulty)
       
       new Round(newNumber, enemies, newDifficulty)
     }
     else 
     {
       val enemies = getRandomEnemies(difficulty)

       new Round(roundNr, enemies, difficulty)
     }
  }
  
  //Keeps record of what is the "original difficulty" as in 1, 2 or 3. That effects how difficulty is acculumated later on.
  private var originalDifficulty = Settings.difficulty
   
  val mapOfEnemyParameters = GameLoader.getEnemyParameters("enemies.txt") //Map of all the enemy parameters.
  
  val mapOfEnemyDifficulty = GameLoader.enemiesMappedToDifficulty(mapOfEnemyParameters) //Enemies mapped to difficulty. 
  
  
  //Produces a random list of enemies for given difficulty from all the available enemies. 
  def getRandomEnemies(difficulty: Int): Vector[Enemy] = {
    
    val rand = new Random()
    
    val combinedDifficultyLevel = difficulty * 3
    
    var combinedDifficulty = 0
    
    val enemies: Buffer[Enemy] = Buffer()
    
    while(combinedDifficulty < combinedDifficultyLevel){
      
        val difficulty = rand.nextInt(3) + 1
        
        if(mapOfEnemyDifficulty.contains(difficulty)){
          
            val size = mapOfEnemyDifficulty(difficulty).size
        
            if(size == 0){
              Unit
            }else {
              val index = rand.nextInt(size)
              
              val name = mapOfEnemyDifficulty(difficulty)(index)
              
              val parameters = mapOfEnemyParameters(name)
              
              enemies += Enemy(name, parameters._1, parameters._2)
              
              combinedDifficulty += difficulty
            }
        }
      
    }
    
    
    enemies.toVector
    
  }

  //Latest round, None if no latest round. 
  private var lastRound: Option[Round] = None 
  
  //Resets lastRound if new game is started. 
  def clear = lastRound = None
  
}