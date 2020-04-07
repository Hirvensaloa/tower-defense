package Game

import TowersNenemies.Enemy
import scala.util.Random
import scala.collection.mutable.Buffer

class Round(val number: Int, private val enemies: Vector[Enemy], val difficulty: Int) {
  
  private var over = false
  
  def isOver = over
  
  //Lista kaikista vihollisista, jotka ovat tiellä. 
  private var enemiesOnTheRoad: Buffer[Enemy] = Buffer()
  
  private var rand = new Random()
  private var time = rand.nextInt(30)
  
  var index = 0

  def tick = {
    
    if(time == 0 && index < enemies.size) {
      
      enemiesOnTheRoad += enemies(index)
      
      time = rand.nextInt(30)
      index += 1
      
      enemiesOnTheRoad.foreach(_.act)
      
    } else {
      
      time -= 1
      enemiesOnTheRoad.foreach(_.act)
      
    }
    
    updateList
    
    if(index >= enemies.size - 1 && enemiesOnTheRoad.isEmpty) over = true
    
  }
  
  private def updateList = enemiesOnTheRoad = enemiesOnTheRoad.filterNot(a => a.isDead || a.isInGoal)
  
  override def toString = enemies.toString

  
}
object Round {
  
  def apply(difficulty: Int): Round = 
  {
     if(lastRound.nonEmpty){
       val newDifficulty = lastRound.get.difficulty + originalDifficulty
       val newNumber = lastRound.get.number + 1
       val enemies = getRandomEnemies(newDifficulty)
       
       new Round(newNumber, enemies, newDifficulty)
     }
     else 
     {
       val enemies = getRandomEnemies(difficulty)
       
       setDifficulty(difficulty)
     
       new Round(1, enemies, difficulty)
     }
  }
  
  private var originalDifficulty = 0
  
  def setDifficulty(n: Int) = originalDifficulty = n
  
  
  //Mappi, jossa on kaikkien vihollisten nimet yhdistettyinä niiden parametreihin. 
  val mapOfEnemyParameters = GameLoader.getEnemyParameters
  
  val mapOfEnemyDifficulty = GameLoader.enemiesMappedToDifficulty(mapOfEnemyParameters)
  
  //Tuottaa satunnaisen kokoelman vihollislistasta, jossa on huomioitu vaikeustaso.
  def getRandomEnemies(difficulty: Int): Vector[Enemy] = {
    
    val rand = new Random()
    
    val combinedDifficultyLevel = difficulty * 5
    
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
              
              enemies += Enemy(name, parameters)
              
              combinedDifficulty += difficulty
            }
        }
      
    }
    
    
    enemies.toVector
    
  }
  
  //Lista, joka kertoo kaikki mahdolliset vihollistyypit.
  private var listOfEnemies: Option[Map[String, Vector[Int]]] = None
   
  //Antaa viimeisimmän kierroksen.
  private var lastRound: Option[Round] = None 
  
}