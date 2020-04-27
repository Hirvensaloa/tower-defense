package Interface

import javax.swing.JFrame

//Window which gameGUI is drawn on top. 
class GameWindow(gui: GameGUI) extends JFrame {
  
  this.setSize(gui.getSize)
  this.setResizable(false)
  
  this.add(gui)
  
  //Starts gameGUI on a thread. 
  val thread = new Thread(gui)
  thread.start()
  
  this.setVisible(true)
}