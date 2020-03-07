

class Enemy(val name: String, LP: Int, val reward: Int, speed1: Double) {
    
  private var lifepoints = LP
  
  private var speed = speed1
  
  def getSpeed = speed
  
  def changeSpeed(amount: Double) = speed = speed * amount
  
  def decreaseLP(amount: Int) = lifepoints -= amount
  
  def isAlive = lifepoints > 0
  
  
}