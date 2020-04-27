package Tests

import Game._
import CoordinateSystem._
import TowersNenemies._
import java.io.PrintWriter
import org.junit.Test
import org.junit.Assert._


class UnitTests{

  //Test that GameLoader can properly load maps and also not load invalid or missing maps. 
  @Test def testLoadMap(){
    
    val grid1 = GameLoader.loadGrid("map1", "mapTestFile")
    
    val grid1Equals = Vector(Vector(false, true, true, false, false, false, true),
                             Vector(false, false, false, false, true, false, true),
                             Vector(true, true, true, true, true, false, true),
                             Vector(true, true, true, true, true, false, false))
    
    assertEquals(grid1.listOfTiles.map(_.map(_.canAddTower)), grid1Equals)
    
    //Should throw exception because map doesn't exist. 
    try{
      GameLoader.loadGrid("map200", "mapTestFile")
      fail()
    }catch{
      case e: GameLoader.CorruptedFile =>
    }
    
    //Should throw an exception because path is invalid. 
    try{
      GameLoader.loadGrid("map3", "mapTestFile")
      fail()
    }catch{
      case e: GameLoader.CorruptedFile =>
    }
    
  }
  
  //Test that towers can be loaded properly. 
  @Test def testLoadTowers = {
    
    val towers = GameLoader.loadTowerMenu("towerTestFile")

    assertTrue(towers.size == 2)
    assertTrue(towers(0).name == "Yellow" && towers(0).showRadius == 2 && towers(0).price == 800 && towers(0).isInstanceOf[SupportTower])
    assertTrue(towers(1).name == "Green" && towers(1).showRadius == 3 && towers(1).price == 4000 && towers(1).isInstanceOf[AttackTower])
    
    val supportTower = towers(0).asInstanceOf[SupportTower]
    val attackTower = towers(1).asInstanceOf[AttackTower]
    
    assertTrue(supportTower.damageBoost == 50 && supportTower.radiusBoost == 1 && supportTower.speedBoost == 1)
    assertTrue(attackTower.getDamage == 70 && attackTower.getSpeed == 3)
    
  }
  
  //Tests if enemies are loaded properly. 
  @Test def testLoadEnemies = {
    
    val enemies = GameLoader.getEnemyParameters("enemyTestFile")
    
    assertTrue(enemies.contains("big boy") && enemies.contains("fast boy") && enemies.contains("little boy"))
    assertTrue(enemies("big boy")._1  == Vector(100, 100, 10))
    assertTrue(enemies("fast boy")._1 == Vector(40, 70, 50)) 
    assertTrue(enemies("little boy")._1 == Vector(60, 40, 30))
        
  }
  
  //Tests if the difficulty accumulates properly. Enemies combined difficulty must be greater or equal to previous round. 
  @Test def testDifficultyGeneration(){
    
    val game = Game.newGame("Grassfield")
    
    val difficulty = 3
    
    val round = Round(difficulty, 1)
    
    while(!round.isOver) round.tick
    
    val nextRound = Round(difficulty, 2)
    
    assertTrue(round.difficulty < nextRound.difficulty)    
  }
  
}