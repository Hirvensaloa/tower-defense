package Tests

import Game._
import CoordinateSystem._
import TowersNenemies._

object UnitTests extends App {
  
  val map = GameLoader.getMapNames(1)
  
  println(map + "\n")
  
  println(GameLoader.loadGrid(map))
  
  println(GameLoader.getPath.map(_.centerCoords))
  
  println("\n\n")
  
  println(GameLoader.getEnemyParameters)
  
  println("\n\n")

  println(Round.mapOfEnemyParameters)
  
  println("\n\n")

  println(Round.mapOfEnemyDifficulty) 
  
  println("\n\n")
  
  println(Game.newRound(3))
  
}