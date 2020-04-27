package Interface

import Game._
import CoordinateSystem._
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.JPanel
import java.awt.Image
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Font
import javax.swing.JFrame
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map


class GameGUI(val game: Game,val mapImage: BufferedImage) extends JPanel with Runnable{
  
  val panelWidth = Grid.width + TowerMenu.width
  val panelHeight = Grid.height + 250 
  this.setSize(panelWidth, panelHeight)
  
  val towerMenu = new TowerMenu(Game.towerMenu, mapImage.getWidth)
  
  override def run() = {
    
    //Runs until toMenu is true.
    while(!GameGUI.toMenu){
    
      this.repaint()
      Thread.sleep(1)
      game.run()
      
  }
    
    //Set all the variables to "default" for the next (possible) game. 
    Round.clear
    TowerMenu.toDraw = None
    TowerMenu.toDrag = None
    TowerMenu.inMain = true
    TowerMenu.selling = false
    GameGUI.paused = false
    GameGUI.text = None
    GameGUI.toMenu = false
    GameGUI.time = 120
    
    MainMenu.toMenu
         
  }
  
  //Timer to tell how long to display text. It starts to tick downwards to zero. 
  private var textTimer = Settings.textTimer
  
  val gap = Grid.width / 4 //Gap between icons
  
  override def paint(g: Graphics) = {
  
    if(GameGUI.paused){ // Draws popup if game is paused.
      
      val popup = new ExitPopup
      popup.draw(g)
      g.setColor(java.awt.Color.RED)
  
    }else if(!game.isOver || GameGUI.timer > 0){ //When game is running. 
      
        g.setFont(new Font("g", Font.ROMAN_BASELINE, 20))
        g.drawImage(mapImage, 0, 0, null)
        towerMenu.draw(g)
      
        g.drawImage(GameGUI.heartImage, gap * 2 , Grid.height + 10, null)
        g.drawString(game.showLifepoints.toString, gap * 2 + 35, Grid.height + 60)
        
        g.drawString(game.roundNumber.toString + "#", gap * 3 + 90, Grid.height + 60)
        
        g.drawImage(GameGUI.moneyImage, gap - 50  , Grid.height + 20, null)
        g.drawString(game.showMoney.toString, gap - 45, Grid.height + 60)
        
        g.drawImage(GameGUI.playButton, Grid.width - 100, Grid.height + 150, null)

        val g2d = g.asInstanceOf[Graphics2D]
        g2d.setColor(java.awt.Color.RED)
        g2d.setStroke(new java.awt.BasicStroke(6))
        
        game.getEnemies.foreach(a => g.drawImage(a.image, a.coordinates._1, a.coordinates._2, null))
        
        GameGUI.drawBullets(g2d)
        GameGUI.updateBullets
        
        if(GameGUI.text.nonEmpty){
          
          if(textTimer == 0){
            
            GameGUI.text = None
            textTimer = Settings.textTimer
          
          }else{
            
            g2d.drawString(GameGUI.text.get, Grid.width / 2 - 150, Grid.height + 150)
            textTimer -= 1
          
          }
          
        }
        
        game.getTowers.foreach(a => g.drawImage(a.image, a.coordinates._1, a.coordinates._2, null))
            
        if(GameGUI.drag){
          val mouseXY = this.getMousePosition()
             
          if(mouseXY != null) {              
             val x = mouseXY.x - Tile.size / 2
             val y = mouseXY.y - Tile.size / 2
             val image = TowerMenu.toDrag.get.image
                  
             g.drawImage(image, x, y, null)
          }
        }
        
      }else{ // When game is over, this draw gameOverPopup
        
        val popup = new GameOverPopup(game.isWon)
        popup.draw(g)
        
      }
    
  }
  
  this.addMouseListener(new Listener(this, game))
  this.setVisible(true)  

}
object GameGUI{
  
  val tileSize = Tile.size
  var drag = false
  
  //Tells the run-method to stop and go back to menu.
  var toMenu = false
  
  //If this is nonEmpty there is some text displayed on the screen. 
  var text: Option[String] = None
  
  //All the images for the game. 
  val images = TowerMenu.images
  
  val grassImage = images("grassTile.jpeg").getScaledInstance(tileSize, tileSize, java.awt.Image.SCALE_SMOOTH)
  val roadImage = images("roadTile.jpeg").getScaledInstance(tileSize, tileSize, java.awt.Image.SCALE_SMOOTH)
  val heartImage = images("Heart.png").getScaledInstance(90, 90, Image.SCALE_SMOOTH)
  val moneyImage = images("Dolla.png").getScaledInstance(110, 60, Image.SCALE_SMOOTH)
  val playButton = images("PlayButton.png").getScaledInstance(80, 60, Image.SCALE_SMOOTH)
  
  //A map that contains line coordinates -> time. Line coordinates represent bullets and time ticks, when zero bullet is removed.
  var bulletsToDraw: Map[((Int, Int), (Int, Int)), Int] = Map()
  private def drawBullets(g: Graphics2D) = bulletsToDraw.keys.foreach(xy => g.drawLine(xy._1._1, xy._1._2, xy._2._1, xy._2._2))
  
  //Updates bulletsToDraw map.
  private def updateBullets = bulletsToDraw = bulletsToDraw.filter(a => a._2 > 0).map(a => (a._1, a._2 - 1))
  
  //If the game is paused this is true. 
  var paused = false
  
  //When the game is over timer starts to tick and when the time is < 0 game is not painted anymore. 
  private var time = 120
  
  private def timer = {
    time -= 1
    time
  }

  //Creates a map from the list (that represents grid) that tells whether the tile is road or grass tile. True for grass and false for road.
  def createMapImage(list: Vector[Vector[Boolean]]) = {
    
    val height = list.size * tileSize
    val width = list(0).size * tileSize
    
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val imageGraphics = image.getGraphics
    
    var xCoord = 0
    var yCoord = 0
    
    for(y <- list){
      
      xCoord = 0
      
      for(x <- y){
        
        if(x) imageGraphics.drawImage(grassImage, xCoord, yCoord, null) else imageGraphics.drawImage(roadImage, xCoord, yCoord, null)
        xCoord += tileSize 
        
      }
      
      yCoord += tileSize
    }
    
    image
  }
  
  //Creates new gui for the given game. 
  def apply(game: Game) = {
            
    val listOfTiles = game.grid.listOfTiles.map(_.map(_.forTowers))
    
    val height = listOfTiles.size * tileSize
    val width = listOfTiles(0).size * tileSize
    
    val image = createMapImage(listOfTiles)
    
    val graphics = image.getGraphics    
    
    new GameGUI(game, image)
    
  }
  
}


//Mouse listener that handles button actions and dragNdrop.
class Listener(parent: java.awt.Container, val game: Game) extends MouseListener{
  
  def mouseClicked(e: MouseEvent) = {
    
    val x = e.getX
    val y = e.getY
    
    if(x < Grid.width && y < Grid.height){
      
      val popupX = Grid.width / 4 
      val popupY = Grid.height / 4
      
      if(game.isOver && x > popupX + 200 && x < popupX + 250 && y < popupY + 180 && y > popupY + 160){ // If game is over and user clicks to menu button.
        
        GameGUI.toMenu = true
        
      }else if(GameGUI.paused){ // This is executed if player has paused the game and has possibly pressed popups buttons. 
        
          if(y < popupY + 100 && y > popupY + 60){
            
            if(x < popupX + 130 && x > popupX + 30){
              
              Game.saveGame
              GameGUI.toMenu = true
              
            } else if(x < popupX + 260 && x > popupX + 160){
              
              GameGUI.toMenu = true
              
            }else Unit
            
          }else if(y < popupY + 190 && y > popupY + 160 && x > popupX + 235 && x < popupX + 295){
            
            GameGUI.paused = false
            
          }else Unit
        
        
       }else{ // Checks if user clicked a tower that is on the map.
      
        val gridX = x / Tile.size
        val gridY = y / Tile.size
        val tower = game.grid.listOfTiles(gridY)(gridX).hasTower
        
        if(tower.nonEmpty){
  
        TowerMenu.inMain = false
        TowerMenu.selling = true
        TowerMenu.toDrag = None
        TowerMenu.toDraw = Some(tower.get.info)
        
        }
      }
      
    }else{ // If user clicked somewhere else than the grid area. 
      
      //Finds from tower menu the tower that corresponds to the tower icon that user clicked, if clicked.
      val tower = TowerMenu.mapOfTowerImage.find(a => a._1._1._2 >= x && a._1._1._1 <= x && a._1._2._2 >= y && a._1._2._1 <= y)
      
      if(TowerMenu.inMain && tower.nonEmpty){ // If user is in tower main menu and clicks some tower icon.
  
        val t = tower.get._2
  
        TowerMenu.toDraw = Some(TowerMenu.towers(t))
        TowerMenu.toDrag = Some(t)
        TowerMenu.selling = false
        TowerMenu.inMain = false
        
      }else if(x > TowerMenu.menuX + 20 && x < TowerMenu.menuX + 80 && y < 70 && y > 30){ // If user is in looking at some tower info and clicks back-button.
        
        TowerMenu.toDraw = None
        TowerMenu.toDrag = None
        TowerMenu.selling = false
        TowerMenu.inMain = true
        
      }else if(!game.hasStarted && x < Grid.width - 20 && x > Grid.width - 100 && y > Grid.height + 150 && y < Grid.height + 210){ // If user clicked the play button.
        
         game.changePlay(true)
         
      }else if(!TowerMenu.inMain && TowerMenu.selling && x > TowerMenu.menuX + 140 && x < TowerMenu.menuX + 330 && y > 440 && y < 520){ // If user clicked to sell button for a specific tower. 
        
        val tower1 = TowerMenu.toDraw.get.tower
        game.removeTower(tower1)
        GameGUI.text = Some("You sold " + tower1.name + " for " + tower1.getSellPrice + "!")
        
        TowerMenu.selling = false
        TowerMenu.toDraw = None
        TowerMenu.toDrag = None
        TowerMenu.inMain = true
        
      }else if(TowerMenu.inMain && x > TowerMenu.menuX + 420 && x < TowerMenu.menuX + 480 && y > 20 && y < 80){ // If user clicked X-button in the top right corner. 
        
        GameGUI.paused = true
        
      }
      
    }
    
    
}
  
  def mousePressed(e: MouseEvent) = {
    
    val x = e.getX
    val y = e.getY
    
    if(TowerMenu.toDrag.nonEmpty && !TowerMenu.inMain && x > TowerMenu.menuX + 150 && x < TowerMenu.menuX + 330 && y > 440 && y < 520) { // If player clicks buy button. 
      
      if(game.showMoney >= TowerMenu.toDrag.get.price) GameGUI.drag = true else GameGUI.text = Some("You don't have enough money!")
    }
  
  }
    
  // When player is buying a tower and dragging it to a location and releases this catches it and gives coordinates to game for tower creation. 
  def mouseReleased(e: MouseEvent) = {
    
    if(GameGUI.drag){
      val x = e.getX()
      val y = e.getY()
      
      if(x < Grid.width && y < Grid.height){
        
        val gridX = x / Tile.size
        val gridY = y / Tile.size
        val tower = TowerMenu.toDrag.get
        
        game.addTower(gridX, gridY, tower)
        
      }
    }
    
    GameGUI.drag = false
  }
  
  def mouseExited(e: MouseEvent) = Unit
  def mouseEntered(e: MouseEvent) = Unit

}



  
  