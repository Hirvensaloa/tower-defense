package Game

import TowersNenemies.Enemy
import scala.collection.mutable.Buffer

class Round(val number: Int, private val enemies: Buffer[Enemy], val difficulty: Double) {
  ???
}
object Round {
  def apply(difficulty: Double): Round = 
  {
     if(lastRound.nonEmpty){
       ???
     }
     else 
     {
       ???
     }
  }
  
  def giveListOfEnemies(list: Vector[Enemy]) = listOfEnemies = Some(list)
  
  //Tuottaa satunnaisen kokoelman vihollislistasta, jossa on huomioitu vaikeustaso
  def getRandomEnemies(difficulty: Double): Buffer[Enemy] = ???
  
  //Lista, joka kertoo kaikki mahdolliset vihollistyypit
  private var listOfEnemies: Option[Vector[Enemy]] = None
   
  //Antaa viimeisimmän kierroksen
  private var lastRound: Option[Round] = None 
  
}