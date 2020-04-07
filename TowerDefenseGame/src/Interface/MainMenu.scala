package Interface

import Game.GameLoader
import Game.Game
import javax.swing._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.GridLayout
import java.awt.BorderLayout
import java.awt.GridBagLayout
import java.awt.CardLayout
import java.awt.Dimension

object MainMenu extends App {
  
  
  val frame = new JFrame("jeejee")
  val cardLayout = new CardLayout
  
  val menuPanel = new JPanel(cardLayout)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setSize(600, 400)
  frame.setBackground(java.awt.Color.YELLOW)
  frame.setLayout(cardLayout)
  
  val newGame = new JButton("New Game")
  val loadGame = new JButton("Load Game")
  val leaderboards = new JButton("Leaderboards")
  newGame.addActionListener(new Listener)
  
  val panel = new JPanel(new GridLayout(3, 1))
  panel.setBorder(BorderFactory.createEmptyBorder(50, 90, 50, 100))

  panel.add(newGame)
  panel.add(loadGame)
  panel.add(leaderboards)
  
  frame.add(menuPanel)
  menuPanel.add(panel, "menu")
  frame.setVisible(true)
  
  val mapNames = GameLoader.getMapNames
        
  val width = mapNames.size/ 4
        
  val panel1 = new JPanel(new BorderLayout)        
  val maps = new JPanel(new GridLayout(4, width))
  
  for(name <- mapNames){
    val button = new JButton(name)
    
    button.addActionListener(new Listener)
    maps.add(button)
     }
  
  maps.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(50, 40, 40, 60), "Choose a map:"))
        
  panel1.add(maps, BorderLayout.CENTER)
        
  menuPanel.add(panel1, "new")
  
  class Listener extends ActionListener{
    
    def actionPerformed(e: ActionEvent) = {
      
      if(e.getSource == newGame){

      cardLayout.show(menuPanel, "new")
                 
      } else if(e.getSource == loadGame){
        
      }else if(e.getSource == leaderboards){
        
      } else{
        
        val mapName = e.getSource.asInstanceOf[JButton].getText
        
        val game = Game.newGame(mapName, 1)
        
        frame.setVisible(false)
        
        GameGUI(game)
        
      }
      
    }
  }
}