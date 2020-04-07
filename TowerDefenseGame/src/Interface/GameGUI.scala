package Interface

import Game._
import CoordinateSystem.Tile
import java.awt.event.MouseListener
import javax.swing.JWindow
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import javax.swing.GroupLayout
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JFrame
import javax.swing.ImageIcon
import java.awt.Graphics
import java.awt.GridLayout
import javax.imageio.ImageIO

//Käyttöliittymä itse pelille. 
class GameGUI(val game: Game,val mapImage: JFrame) {
  
  
  mapImage.setVisible(true)

}
//Luo kartan.
object GameGUI{
  
  val grassImage = ImageIO.read(getClass().getResource("grassTile.jpeg")).getScaledInstance(tileSize, tileSize, java.awt.Image.SCALE_SMOOTH)
  val roadImage = ImageIO.read(getClass().getResource("roadTile.jpeg")).getScaledInstance(tileSize, tileSize, java.awt.Image.SCALE_SMOOTH)
  val roadIcon = new ImageIcon(roadImage)
  val grassIcon = new ImageIcon(grassImage)
  
  
  def apply(game: Game) = {
    
    val listOfTiles = game.grid.listOfTiles.map(_.map(_.forTowers))
    val height = listOfTiles.size * 30
    val width = listOfTiles(0).size * 30
    
    val frame = new JFrame("Tower defense")
    frame.setSize(width, height)
    
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val imageGraphics = image.getGraphics
    
    for(y <- listOfTiles){
      
      for(x <- y){
        
        val xCoord = listOfTiles.indexOf(x) * tileSize + tileSize/2
        val yCoord = listOfTiles.indexOf(y) * tileSize + tileSize/2
        
        println("imageGraphics: " +imageGraphics)
        
        if(x) imageGraphics.drawImage(grassImage, xCoord, yCoord, frame) else imageGraphics.drawImage(roadImage, xCoord, yCoord, frame)
       
      }
    }
    
    frame.setSize(1200, 1000)
    
    new GameGUI(game, frame)
    
  }
  
  val tileSize = Tile.size
  
}