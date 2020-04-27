package Interface

import Game.Game
import javax.swing.JFrame
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.CardLayout

//Main menu that has all the menu pages and controls them according to user inputs. 
object MainMenu {
  
  val frame = new JFrame("Tower Defense")
  
  val cardLayout = new CardLayout
  
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setSize(600, 400)
  frame.setLayout(cardLayout)
  
  val contentPanel = frame.getContentPane()
  
  contentPanel.add(MainMenuFrame, "menu")
  contentPanel.add(NewGameMenu, "new")
  
  frame.addMouseListener(new ClickListener)
  
  private var gui: Option[GameWindow] = None
  
  private var inMain = true //Tells if user is in main menu. 
  
  //"Starts" menu so makes it visible.
  def start = frame.setVisible(true)
  
  //Takes user to menu.
  def toMenu = {
    
    frame.setVisible(true)
    
    if(gui.nonEmpty)
    {
      gui.get.dispose
      gui = None
    }
    
    cardLayout.show(contentPanel, "menu")
    inMain = true

  }
  
  //Listens to users click so that it can react properly with buttons. 
  class ClickListener extends MouseAdapter {
    
    override def mouseClicked(e: MouseEvent) = {
      
      val x = e.getX
      val y = e.getY
      
      
      if(inMain){ // Checks if user clicked new game button or load game button. 
         
        if(x > 200 && x < 400){
          
          if(y > 130 && y < 190){
            
            cardLayout.show(contentPanel, "new")
            inMain = false
            
          } else if(y > 230 && y < 290){
            
            val game = Game.loadGame
            
            if(game.nonEmpty){
            val window = new GameWindow(GameGUI(game.get))
            gui = Some(window)
            
            frame.setVisible(false)
            }else{
              
              println("GameSave not found or empty")
              
            }
            
          } else Unit
          
        }
        
      }else { // Checks if user chooses to play map or wants to see previous or next one. 
       
        if(y > 195 && y < 235){
          
          if(x > 90 && x < 120){
            
            NewGameMenu.previous
            NewGameMenu.repaint()
            
          }else if(x > 440 && x < 470){
            
            NewGameMenu.next
            NewGameMenu.repaint()
            
          }else Unit
          
        }else if(x > 30 && x < 90 && y > 60 && y < 100){
          
          cardLayout.show(contentPanel, "menu")
          inMain = true
          
        }else if(x > 190 && x < 390 && y > 330 && y < 370){
          
          val mapName = NewGameMenu.mapName
          val game = Game.newGame(mapName)
          
          val window = new GameWindow(GameGUI(game))
          gui = Some(window)
          
          frame.setVisible(false)
          
        }else Unit
        
      }
      
    }
    
  }
  
  
}