/*
 * Copyright (c) 2010 Georgios Migdos <cyberpython@gmail.com>, Filia Dova
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package reversi.ui;

import ai.MiniMax;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayDeque;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import reversi.AIPlayer;
import reversi.Board;
import reversi.GameController;
import reversi.GameLogger;
import reversi.GameUndoRedoListener;
import reversi.Move;
import reversi.Utils;
import reversi.ai.ReversiEvaluator.EvaluationMethod;

public class TestWindow extends javax.swing.JFrame implements GameUndoRedoListener, GameLogger {

    private static GameController gameController;
    public static DefaultListModel gamelogListModel;
    private static ArrayDeque<String> redoMessages;
    static int WhiteSpinner = 1; 
    static int WhiteAlgo = 1; 
    static int WhiteMethod = 1; 
    static int BlackSpinner = 1; 
    static int BlackAlgo = 1; 
    static int BlackMethod = 1; 

    
    
    /** Creates new form MainWindow */
    public TestWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e2) {
            }
        }
        initComponents();
        this.gamelogListModel = new DefaultListModel();
        this.jList1.setModel(gamelogListModel);
        this.jPanel4.setVisible(true);

        this.setSize(600, 400);
        this.setLocationRelativeTo(null);

        this.gameController = null;
        this.redoMessages = new ArrayDeque<String>();
        this.updateUndoRedoControls();
    }

    public void newGame() {

        this.redoMessages.clear();

        if (this.gameController != null) {
            this.gameController.removeGameUndoRedoListener(this);
            this.gameController.removeGameLogger(this);
        }

        this.gameController = null;

        int player;
        if (this.jRadioButton1.isSelected()) {
            player = Utils.BLACK;
        } else {
            player = Utils.WHITE;
        }

        boolean singlePlayer;
        if (this.jRadioButton3.isSelected()) {
            singlePlayer = false;
        } else {
            singlePlayer = true;
        }

        int d = (Integer) this.jSpinner1.getModel().getValue();
		GameController.bot2spinner = (int) this.jSpinner2.getModel().getValue();;
        
        EvaluationMethod evalMethod;

        if (this.jRadioButton5.isSelected()) {
            evalMethod = EvaluationMethod.VALID_MOVES_AND_TOTAL_SCORE;
        } else if (this.jRadioButton6.isSelected()) {
            evalMethod = EvaluationMethod.VALID_MOVES_AND_SIDES_COUNT;
        } else {
            evalMethod = EvaluationMethod.VALID_MOVES_AND_CORNERS;
        }
        
        if (this.jRadioButton12.isSelected()) {
            GameController.evalMethod1 = EvaluationMethod.VALID_MOVES_AND_TOTAL_SCORE;
        } else if (this.jRadioButton13.isSelected()) {
        	GameController.evalMethod1 = EvaluationMethod.VALID_MOVES_AND_SIDES_COUNT;
        } else {
        	GameController.evalMethod1 = EvaluationMethod.VALID_MOVES_AND_CORNERS;
        }
        
        if (this.JCheckBox.isSelected()) 
            GameController.BotPlayer = true;
        else GameController.BotPlayer = false;


        MiniMax.SearchAlgorithm algorithm;

        if (this.jRadioButton8.isSelected()) {
            algorithm = MiniMax.SearchAlgorithm.MINIMAX;
        } else {
            algorithm = MiniMax.SearchAlgorithm.ALPHA_BETA_PRUNING;
        }
        
        if (this.jRadioButton10.isSelected()) {
        	GameController.algorithm1 = MiniMax.SearchAlgorithm.MINIMAX;
        } else {
        	GameController.algorithm1 = MiniMax.SearchAlgorithm.ALPHA_BETA_PRUNING;
        }

        updateUndoRedoControls();


        this.jPanel7.removeAll();
        BoardPanel boardPanel = new BoardPanel();
        this.jPanel7.add(boardPanel);
        this.gameController = new GameController(singlePlayer, player, new Board(), boardPanel, d, algorithm, evalMethod, Utils.WAIT_FOR_MILLIS);
        this.gameController.addGameUndoRedoListener(this);
        this.gameController.addGameLogger(this);
        this.gameController.startGame();
    }

    private void undo() {
        if (this.gameController != null) {
            this.gameController.undo();
        }
    }

    private void redo() {
        if (this.gameController != null) {
            this.gameController.redo();
        }
    }

    private void updateUndoRedoControls() {
        if (this.gameController != null) {
            this.jMenuItem3.setEnabled(this.gameController.canUndo());
            this.jMenuItem4.setEnabled(this.gameController.canRedo());
        } else {
            this.jMenuItem3.setEnabled(false);
            this.jMenuItem4.setEnabled(false);
        }
    }

    public void undoableEventHappened() {
        this.updateUndoRedoControls();
    }

    private void setComputerAIControlsEnabled(boolean enabled) {
        this.jLabel3.setEnabled(enabled);
        this.jLabel4.setEnabled(enabled);
        this.jLabel7.setEnabled(enabled);
        this.jLabel8.setEnabled(enabled);
        this.jLabel9.setEnabled(enabled);
        this.jRadioButton5.setEnabled(enabled);
        this.jRadioButton6.setEnabled(enabled);
        this.jRadioButton7.setEnabled(enabled);
        this.jRadioButton8.setEnabled(enabled);
        this.jRadioButton9.setEnabled(enabled);
        this.jRadioButton10.setEnabled(enabled);
        this.jRadioButton11.setEnabled(enabled);
        this.jRadioButton12.setEnabled(enabled);
        this.jRadioButton13.setEnabled(enabled);
        this.jRadioButton14.setEnabled(enabled);
        this.JCheckBox.setEnabled(enabled);
        this.jSpinner1.setEnabled(enabled);
        this.jSpinner2.setEnabled(enabled);
    }

    public void newGameStarted() {
        this.gamelogListModel.clear();
        this.gamelogListModel.addElement("New game started");
    }

    public void gameOver() {
        this.gamelogListModel.addElement("Game over!");
        this.gamelogListModel.addElement(BoardPanel.point);
    }
    
    public void Bot1() {
        this.gamelogListModel.addElement("Black Bot:");
    }
    public void Bot2() {
        this.gamelogListModel.addElement("White Bot:");
    }
    
    public void newMove(Move m) {
        this.gamelogListModel.addElement(Utils.getPlayerName(m.getColour()) + " plays: " + (char) ('a' + m.getX()) + "," + (m.getY() + 1));
        this.redoMessages.clear();
        int index = this.gamelogListModel.getSize() - 1;
        this.jList1.setSelectedIndex(index);
        this.jList1.ensureIndexIsVisible(index);
    }

    public void moveUndone() {
        //this.gamelogListModel.addElement("Undo");
        this.redoMessages.push((String) this.gamelogListModel.remove(this.gamelogListModel.size() - 1));
        int index = this.gamelogListModel.getSize() - 1;
        this.jList1.setSelectedIndex(index);
        this.jList1.ensureIndexIsVisible(index);
    }

    public void moveRedone() {
        if (!this.redoMessages.isEmpty()) {
            this.gamelogListModel.addElement(this.redoMessages.pop());
            int index = this.gamelogListModel.getSize() - 1;
            this.jList1.setSelectedIndex(index);
            this.jList1.ensureIndexIsVisible(index);
        }
    }

    private void saveGameLog(){
        final JFileChooser fc = new JFileChooser();
        int retval = fc.showSaveDialog(this);

        if(retval==JFileChooser.APPROVE_OPTION){
            try{
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile())));

                for(int i=0; i<this.gamelogListModel.size(); i++){
                    try{
                        writer.write((String)this.gamelogListModel.get(i));
                        writer.newLine();
                    }catch(IOException ioe){
                        ioe.printStackTrace();
                    }
                }

                try{
                    writer.flush();
                    writer.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
                
            }catch(FileNotFoundException fnfe){
                fnfe.printStackTrace();
            }

        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator(SwingConstants.VERTICAL);
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jSpinner2 = new javax.swing.JSpinner();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jRadioButton13 = new javax.swing.JRadioButton();
        jRadioButton14 = new javax.swing.JRadioButton();
        JCheckBox = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel7 = new javax.swing.JPanel();
        boardPanel1 = new reversi.ui.BoardPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Reversi");
        setMinimumSize(new java.awt.Dimension(1275, 725));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jButton1.setText("Play!");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        //jButton5.setText("Mass Data!");
        //jButton5.addActionListener(new java.awt.event.ActionListener() {
            //public void actionPerformed(java.awt.event.ActionEvent evt) {
             //   jButton5ActionPerformed(evt);
           // }
       // });
        
        jLabel1.setText("<html><b>Bot (White):</b></html>");
        jLabel8.setText("<html><b>Search tree depth :</b></html>");
        jLabel9.setText("<html><b>Evaluation method:</b></html>");

        //buttonGroup1.add(jRadioButton1);
        //jRadioButton1.setText("Black");

        //buttonGroup1.add(jRadioButton2);
        //jRadioButton2.setSelected(true);
        //jRadioButton2.setText("White");

        jLabel2.setText("<html><b>Bot (Black):</b></html>");

        //buttonGroup2.add(jRadioButton3);
        //jRadioButton3.setText("Human");

        //buttonGroup2.add(jRadioButton4);
        //jRadioButton4.setSelected(true);
        //jRadioButton4.setText("Computer");
        jRadioButton4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton4ItemStateChanged(evt);
            }
        });

        jLabel3.setText("<html><b>Evaluation method:</b></html>");

        //jButton2.setText("✕");
        //jButton2.addActionListener(new java.awt.event.ActionListener() {
            //public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jButton2ActionPerformed(evt);
            //}
        //});

        jLabel5.setBackground(javax.swing.UIManager.getDefaults().getColor("textHighlight"));
        jLabel5.setForeground(javax.swing.UIManager.getDefaults().getColor("textHighlightText"));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("<html><b>New Game</b></html>");
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel5.setOpaque(true);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(2, 2, 6, 1));
        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(2, 2, 6, 1));

        buttonGroup3.add(jRadioButton5);
        jRadioButton5.setText("Total Score Method");
        
        buttonGroup3.add(jRadioButton6);
        jRadioButton6.setText("Sides Count Method");

        buttonGroup3.add(jRadioButton7);
        jRadioButton7.setSelected(true);
        jRadioButton7.setText("Corners Method");
        
        buttonGroup5.add(jRadioButton12);
        buttonGroup5.add(jRadioButton13);
        buttonGroup5.add(jRadioButton14);
        
        buttonGroup6.add(jRadioButton10);
        buttonGroup6.add(jRadioButton11);

        jLabel4.setText("<html><b>Search tree depth :</b></html>");

        jLabel7.setText("<html><b>Algorithm:</b></html>");

        buttonGroup4.add(jRadioButton8);
        jRadioButton8.setText("MiniMax");
        
        jRadioButton10.setText("MiniMax");
        jRadioButton10.setSelected(true);
        jRadioButton11.setText("Alpha-Beta Pruning");
        jRadioButton12.setText("Total Score Method");
        jRadioButton12.setSelected(true);
        jRadioButton13.setText("Sides Count Method");
        jRadioButton14.setText("Corners Method");
        JCheckBox.setText("Black Bot is Active");
        JCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	JCheckBoxActionPerformed(evt);
            }

			private void JCheckBoxActionPerformed(ActionEvent evt) {
				
				if (JCheckBox.isSelected()) {
		        	jRadioButton4.setForeground (Color.black);
					jRadioButton5.setForeground (Color.black);
		        	jRadioButton6.setForeground (Color.black);
		        	jRadioButton7.setForeground (Color.black);
		        	jRadioButton8.setForeground (Color.black);
		        	jRadioButton9.setForeground (Color.black);
		        	jLabel2.setForeground (Color.black);
		        	jLabel3.setForeground (Color.black);
		        	jLabel4.setForeground (Color.black);
		        	GameController.WaitTimeManager = 0;
		        	

				} else {
		        	jRadioButton4.setForeground (Color.gray);
					jRadioButton5.setForeground (Color.gray);
		        	jRadioButton6.setForeground (Color.gray);
		        	jRadioButton7.setForeground (Color.gray);
		        	jRadioButton8.setForeground (Color.gray);
		        	jRadioButton9.setForeground (Color.gray);	
		        	jLabel2.setForeground (Color.gray);
		        	jLabel3.setForeground (Color.gray);
		        	jLabel4.setForeground (Color.gray);
		        	GameController.WaitTimeManager = 1000;

		        	}				
			}
        });
        JCheckBox.setSelected(true);
        
        buttonGroup4.add(jRadioButton9);
        jRadioButton9.setSelected(true);
        jRadioButton9.setText("Alpha-Beta Pruning");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                	//.addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                	.addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                	//.addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        //.addComponent(jRadioButton3)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        //.addComponent(jRadioButton4)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        //.addComponent(jRadioButton1)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        //.addComponent(jRadioButton2)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        //.addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton9)
        				.addComponent(JCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton7)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        //.addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, 50)
                    .addGroup(jPanel1Layout.createParallelGroup(LEADING)
                    		.addGroup(jPanel1Layout.createSequentialGroup()
                    				.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    				.addComponent(jRadioButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    				.addComponent(jRadioButton11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    		.addGroup(jPanel1Layout.createSequentialGroup()
                    				.addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    		.addGroup(jPanel1Layout.createSequentialGroup()
                    		//.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    				.addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    				.addComponent(jRadioButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    				.addComponent(jRadioButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    				.addComponent(jRadioButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                //.addContainerGap()
            //.addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                //.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            //.addComponent(jRadioButton1)
                            //.addComponent(jRadioButton2)
                            //.addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton8)
                            .addComponent(jRadioButton9)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton10)
                            .addComponent(jRadioButton11)
                            .addComponent(JCheckBox))
                            //.addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            //.addComponent(jRadioButton3)
                            //.addComponent(jRadioButton4)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton5)
                            .addComponent(jRadioButton6)
                            .addComponent(jRadioButton7)
                            .addComponent(jRadioButton12)
                            .addComponent(jRadioButton13)
                            .addComponent(jRadioButton14)
                    		.addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                //.addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        /*
        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton2)
            	.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        
        */
        jButton3.setText("✕");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        //jLabel6.setBackground(javax.swing.UIManager.getDefaults().getColor("textHighlight"));
        //jLabel6.setForeground(javax.swing.UIManager.getDefaults().getColor("textHighlightText"));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("<html>Game Log</html>");
        //jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        //jLabel6.setOpaque(true);

        jButton4.setText("Save");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton3)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton4))
        );

        jList1.setBackground(javax.swing.UIManager.getDefaults().getColor("TextPane.background"));
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
        );

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout boardPanel1Layout = new javax.swing.GroupLayout(boardPanel1);
        boardPanel1.setLayout(boardPanel1Layout);
        boardPanel1Layout.setHorizontalGroup(
            boardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );
        boardPanel1Layout.setVerticalGroup(
            boardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );

        jPanel7.add(boardPanel1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel6);

        jMenu1.setText("Game");

        //jMenuItem1.setText("New Game");
        //jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            //public void actionPerformed(java.awt.event.ActionEvent evt) {
               // jMenuItem1ActionPerformed(evt);
            //}
        //});
        //jMenu1.add(jMenuItem1);
        //jMenu1.add(jSeparator2);

        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem3.setText("Undo");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Redo");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("View");

        jCheckBoxMenuItem1.setText("Game log");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem1);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.jPanel1.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.jPanel1.setVisible(false);
        this.newGame();
        this.jPanel1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.jPanel1.setVisible(false);
        try {
			play();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.jPanel1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.jPanel1.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.undo();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.redo();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.jPanel4.setVisible(false);
        this.jCheckBoxMenuItem1.setSelected(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        this.jPanel4.setVisible(this.jCheckBoxMenuItem1.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void jRadioButton4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton4ItemStateChanged
        this.setComputerAIControlsEnabled(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_jRadioButton4ItemStateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.saveGameLog();
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
        	
            public void run() {
                new TestWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static reversi.ui.BoardPanel boardPanel1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private static javax.swing.JPanel jPanel7;
	public static boolean gameOver;
    private static javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private static javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private static javax.swing.JRadioButton jRadioButton5;
    private static javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private static javax.swing.JRadioButton jRadioButton8;
    private static javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private static javax.swing.JRadioButton jRadioButton12;
    private static javax.swing.JRadioButton jRadioButton13;
    private javax.swing.JRadioButton jRadioButton14;
    private static javax.swing.JCheckBox JCheckBox;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private static javax.swing.JSpinner jSpinner1;
    private static javax.swing.JSpinner jSpinner2;
    // End of variables declaration//GEN-END:variables

	public void play() throws InterruptedException {
		
		int player;
        boolean singlePlayer;
		player = Utils.BLACK;
		singlePlayer = true;
		EvaluationMethod evalMethod;
		MiniMax.SearchAlgorithm algorithm;
		gameController = null;
        GameController.BotPlayer = true;

//WHITE PLAYER
        
	//WHITE EVAL
        /*
     for (EvaluationMethod method : EvaluationMethod.values())
     {
    	 
     }
     */
    switch (WhiteMethod) { 
        case 1: 
    		GameController.evalMethod1 = EvaluationMethod.VALID_MOVES_AND_TOTAL_SCORE;
            break; 
        case 2:
            GameController.evalMethod1 = EvaluationMethod.VALID_MOVES_AND_SIDES_COUNT;
        	break; 
        case 3:
            GameController.evalMethod1 = EvaluationMethod.VALID_MOVES_AND_CORNERS;
        	break; 
        case 4:
    		GameController.evalMethod1 = EvaluationMethod.VALID_MOVES_AND_TOTAL_SCORE;
        	WhiteMethod = 1;
        	WhiteAlgo ++;
        	break;
    }
    
    //WHITE ALGO
    switch (WhiteAlgo) { 
        case 1: 
    		GameController.algorithm1 = MiniMax.SearchAlgorithm.MINIMAX;
            break; 
        case 2:
            GameController.algorithm1 = MiniMax.SearchAlgorithm.ALPHA_BETA_PRUNING;
        	break; 
        case 3:
        	//end game
        	break; 
    }
            
	//WHITE SPINNER
        // switch statement with int data type 
        switch (WhiteSpinner) { 
	        case 1: 
	        	GameController.bot2spinner = (int) 2;
	        	WhiteSpinner++;
	            break; 
	        case 2:
	        	GameController.bot2spinner = (int) 3;
	        	WhiteSpinner++;
	        	break; 
	        case 3:
	        	GameController.bot2spinner = (int) 4;
	        	WhiteSpinner++;
	        	break; 
	        case 4:
	        	GameController.bot2spinner = (int) 5;
	        	WhiteSpinner++;
	        	break; 
	        case 5:
	        	GameController.bot2spinner = (int) 6;
	        	WhiteSpinner = 1;
	        	WhiteMethod++;
	        	break; 
        }

		
//BLACK PLAYER
	//BLACK SPINNER
        int d = 2;
    //BLACK EVAL
        evalMethod = EvaluationMethod.VALID_MOVES_AND_TOTAL_SCORE;
        //evalMethod = EvaluationMethod.VALID_MOVES_AND_SIDES_COUNT;
        //evalMethod = EvaluationMethod.VALID_MOVES_AND_CORNERS;
    //BLACK ALGO
        algorithm = MiniMax.SearchAlgorithm.MINIMAX;
        //algorithm = MiniMax.SearchAlgorithm.ALPHA_BETA_PRUNING;

		gamelogListModel.addElement("White Player " + algorithm + " " + d + " " + evalMethod);
		gamelogListModel.addElement("Black Player " + GameController.algorithm1 + " " + GameController.bot2spinner + " " + GameController.evalMethod1);
		
        BoardPanel.alreadyPrinted = false;
        if (WhiteAlgo != 3) {
        	jPanel7.removeAll();
            BoardPanel boardPanel1 = new BoardPanel();
            jPanel7.add(boardPanel1);
            Board board = new Board();
            gameController = new GameController(singlePlayer, player, board, boardPanel1, d, algorithm, evalMethod, Utils.WAIT_FOR_MILLIS);
        	//gameController.addGameLogger(this);
            gameController.startGame();
    		gamelogListModel.addElement("Made it to the start");
    		
    		while (!board.getGameOver()) {
    			Thread.sleep(1000);
        		gamelogListModel.addElement("Game Over");
    		}
    		play();
        }
        
	}    

}
