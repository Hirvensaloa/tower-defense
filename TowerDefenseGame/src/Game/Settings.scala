package Game

//Contains variables for settings which are set in settings.txt file. Also contains other settings such as bullet draw time. 
object Settings {
  
  private val list = GameLoader.readSettings
  
  val lifepoints = list(0)
  val difficulty = list(1)
  val money = list(2)
  val roundLimit = list(3)
   
  val bulletDrawTime = 100 //How long bullets are displayed.
  val textTimer = 2000 //How long texts are displayed.
  val tileSize = 100
  
  
}