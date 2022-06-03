import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    final static int STAY = 0, MOVE_UP = 1, MOVE_DOWN = 2, MOVE_LEFT = 3, MOVE_RIGHT = 4;
    final static int MENU = 0, PRE_GRAME = 1 ,GAME_SCENE = 2, EXIT_SCENE = 3;
    static int width,height,cell_width,cell_height;
    static int key_pressed, key_hold;
    static int signal;
    int pacman_speed;
    boolean running;
    int scene, rankingPlace;
    PacmanContent content;
    Game()
    {
        System.out.println("gra Konstr");
    }

    /**
     * Inits needed variables, classes and window for the game
     * @param title - title of the window
     * @param xpos - x position of the window
     * @param ypos - y position of the window
     * @param w - width of the window
     * @param h - height of the window
     */
    void init(String title, int xpos, int ypos, int w, int h)
    {
        width = w; height = h;
        cell_height = h / Board.rows; cell_width = w / Board.cols;
        pacman_speed = 30;
        running = true;


//Init Game Objects
        controller = new GhostController[4];
        content = new PacmanContent();
        createNewGame();

        JFrame window = new JFrame(title);

        keyListner = new CustomKeyListner();
        content.setPreferredSize(new Dimension(w + 150,h));
        content.setBackground(new Color(45, 41, 41));
        window.setContentPane(content);
        content.setFocusable(true);
        content.requestFocus();
        content.addKeyListener(keyListner);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(width , height + 20);
        window.setLocation(xpos,ypos);
        window.setResizable(false);
        window.setVisible(true);
    }

    /**
     * Catches events created by timer or singals
     */
    void handleEvents() {
        if (signal == 11) {
            try {
                SaveScoreToFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("File not found");
            }
            createNewGame();
        }
        if (scene == 3) return;
        if (signal == 10) scene = 3;

        switch (scene) {
            case PRE_GRAME -> {
                timer.addTick();
                if (timer.changeToGameScene()) {
                    scene = Game.GAME_SCENE;
                    timer.reset();
                }
            }
            case GAME_SCENE -> {
                timer.addTick();
                content.requestFocus();
                if (points.allPointsTaken()) {
                    scene = 1;
                    nextLevel();
                }
                if (pacman.isMoving() == 0) key_hold = key_pressed;
                if (pacman.isMoving() == 1) key_pressed = 0;
                if (timer.inkyTrigger()) changeInkyBehaviour();
                if (timer.closeWallTrigger()) board.changeTile(13, 8, Board.WALL);
            }
            case EXIT_SCENE -> {
                setExitScene();
            }
        }

    }

    /**
     * Updates moves and collisions in the game
     */
    void update()
    {
        time_message.setText(timer.getTime());
        switch (scene)
        {
            case GAME_SCENE -> {
                message.setText("Points: "+points.getPoints());
                updatePositions();
                checkColisions();
            }
        }
    }
    /**
     * Renders game objects
     */
    void render()
    {
        content.repaint();
    }
    /**
     * Updates positions of every ghost and pacman on the board
     * Positions of the 3 ghosts are calculated by threads
     */
    void updatePositions() {
        try {
            pac_controller = new PacmanController(pacman);
            pac_controller.start();
            for (int i = 0; i < 3; i++) {
                controller[i] = new GhostController(ghosts.get(i), 0);
                controller[i].start();
            }
            inky.move();
            pac_controller.join();
            for (int i = 0; i < 3; i++)
                controller[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks collisions between pacman and ghosts (by threads)
     */
    private void checkColisions() {
        try{
            for(int i = 0; i < 3; i++)
            {
                controller[i] = new GhostController(ghosts.get(i), 1);
                controller[i].start();
            }
            inky.colision_check();
            for(int i = 0; i < 3; i++)
                controller[i].join();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    void changeInkyBehaviour() {
        int change = rand.nextInt(3);
        System.out.println("Zmiana w " + change);
        switch (change) {
            case 0 -> inky = new Blinky(inky);
            case 1 -> inky = new Pinky(inky);
            case 2 -> inky = new Clyde(inky);
        }
    }
    void createNewGame( )
    {

        ghosts = new ArrayList<>();
        score_panel = new JLabel[8];
        scene = 1;
        signal = 0;

        board = new Board(30,17);
        points = new Points(board);
        pacman = new Pacman(board,points,8 * cell_width,3 * cell_height);
        pinky = new Pinky(pacman,board,7 * cell_width,15 * cell_height);
        ghosts.add(pinky);
        blinky = new Blinky(pacman,board,8 * cell_width,15 * cell_height);
        ghosts.add(blinky);
        clyde = new Clyde(pacman, board, 8 * cell_width,14 * cell_height);
        ghosts.add(clyde);
        inky = new Inky(pacman,board,9 * cell_width, 15 * cell_height);
        rand = new Random();
        timer = new CustomTimer();
        changeInkyBehaviour();


        tryAgainButton.setBounds(510,120,120,30);
        textScore.setVisible(false);
        for(int i = 0; i< 8; i++)
            if(score_panel[i]!= null)score_panel[i].setBounds(1000,1000,120,30);
    }

    /**
     * Reloads arena after collecting every single point on the board by pacman
     */
    void nextLevel()
    {
        board.repaintBoard();
        board.changeTile(13,8,Board.EMPTY);
        for(Ghost ghost : ghosts)
            ghost.respawn();
        inky = new Inky(pacman,board,9 * cell_width, 15 * cell_height);
        pacman.respawn();
        timer.reset();
    }

    /**
     * Saves scores of all players to the file
     * @throws FileNotFoundException
     */
    private void SaveScoreToFile() throws FileNotFoundException {
        scores.get(rankingPlace).changeName(textScore.getText());
        PrintWriter pw = new PrintWriter(new FileOutputStream("score.txt"));
        for (Score score : scores)
            pw.println(score.getName() + " " + score.getScore());
        pw.close();
    }

    /**
     * Creates a scene that is shown until pacman death
     */
    void setExitScene()
    {
        scene = 3;
        scores = new ArrayList<>();
        try{
            File myObj = new File("score.txt");
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
                toArrays(data,scores);
            }
            myReader.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Couldn't find file");
            e.printStackTrace();
        }
        rankingPlace = Score.addAndSort(scores,new Score("Your Score",points.getPoints()));
        System.out.println("====SCORES====");
        Score.show(scores);
        content.exitSceneButtons();
    }

    /**
     * Adds data from the string to array with players score
     * @param data - single line from the file
     * @param scores array of players score
     */
    void toArrays(String data, ArrayList<Score> scores)
    {
        int i = 0;
        for(char c: data.toCharArray())
        {
            if(c == ' ')
            {
                Score new_score = new Score(data.substring(0,i),Integer.parseInt(data.replaceAll("\\D+","")));
                Score.addAndSort(scores,new_score);
                return;
            }
            i++;
        }
    }
    int getScene(){return scene;}
    boolean isRunning(){return running;}
    private JButton newGameButton, tryAgainButton;
    private JLabel message,time_message;
    private JLabel[] score_panel;
    JTextField textScore;
    Pacman pacman;
    Board board;
    Ghost pinky,blinky,inky,clyde;
    PacmanController pac_controller;
    GhostController[] controller;
    CustomTimer timer;
    Random rand;
    Points points;
    ArrayList<Ghost>ghosts;
    CustomKeyListner keyListner;
    ArrayList<Score> scores;

    /**
     * Responsible for content in presented in the game window
     */
    class PacmanContent extends JPanel implements ActionListener, MouseListener
    {
        public PacmanContent()
        {
            setLayout(null);
            setPreferredSize(new Dimension(550,250));

            setBackground(Color.BLACK);
            addMouseListener(this);
            tryAgainButton = new JButton("Try Again");
            tryAgainButton.addActionListener(this);
            message = new JLabel("POINTS",JLabel.CENTER);
            message.setFont(new Font("Serif",Font.BOLD,14));
            message.setForeground(Color.CYAN);
            time_message = new JLabel("Time message",JLabel.LEFT);
            time_message.setFont(new Font("Serif",Font.BOLD,14));
            time_message.setForeground(Color.CYAN);
            textScore = new JTextField(20);

            add(tryAgainButton);
            add(message);
            add(time_message);
            add(textScore);

            message.setBounds(210,2,150,30);
            time_message.setBounds(100,2,150,30);
            textScore.setBounds(310,200,120,30);

            //textScore.setBounds(310,200,120,30);
        }
        public void actionPerformed(ActionEvent evt)
        {
            Object src = evt.getSource();
            if (src == tryAgainButton)
                signal = 11;
        }
        /**
         * Loads buttons that are shown until GAME OVER screen
         */
        public void exitSceneButtons()
        {
            tryAgainButton.setBounds(310,250,120,30);
            textScore.setVisible(true);

            for(int i = 0; i < Math.min(8,scores.size()); i++)
            {
                score_panel[i] = new JLabel(scores.get(i).name + "  "+scores.get(i).getScore(),JLabel.CENTER);
                score_panel[i].setForeground(Color.RED);
                if(i%2 == 0) score_panel[i].setForeground(Color.cyan);
                add(score_panel[i]);
                score_panel[i].setVisible(true);
                score_panel[i].setBounds(95,35+i*20,150,15);
            }
        }
        public void mousePressed(MouseEvent evt)
        {

        }
        public void mouseReleased(MouseEvent evt) { }
        public void mouseClicked(MouseEvent evt) { }
        public void mouseEntered(MouseEvent evt) { }
        public void mouseExited(MouseEvent evt) { }
        public void paintComponent(Graphics g) {
            if (pacman == null) return;
            for (int i = 0; i < Board.rows; i++) {
                for (int j = 0; j < Board.cols; j++) {
                    if (board.board[i][j] == 2) g.setColor(Color.BLUE);
                    else if (board.board[i][j] == 1) g.setColor(Color.GREEN);
                    else g.setColor(Color.black);
                    g.fillRect(j * Game.cell_width, i * Game.cell_height, Game.cell_width, Game.cell_height);
                }
            }
            g.setColor(Color.BLACK);
            g.fillRect(Board.cols + Game.cell_width, Board.rows * Game.cell_height, 100,400);
            g.drawImage(pinky.getImage(), pinky.getX(), pinky.getY(), this);
            g.drawImage(blinky.getImage(), blinky.getX(), blinky.getY(), this);
            g.drawImage(clyde.getImage(), clyde.x, clyde.y, this);
            g.drawImage(inky.getImage(), inky.x, inky.y, this);
            g.drawImage(pacman.getImage(), pacman.getX(), pacman.getY(), this);
            if(scene != 3) return;
            g.setColor(Color.black);
            g.fillRect(10,30,300,300);
        }
    }
    class CustomKeyListner implements KeyListener
    {
        public void keyTyped(KeyEvent evt)
        {
            //System.out.println("KEY TYPED");
        }
        public void keyPressed(KeyEvent evt)
        {
            switch (evt.getKeyCode()) {
                default -> key_pressed = STAY;
                case KeyEvent.VK_W, KeyEvent.VK_UP -> key_pressed = MOVE_UP;
                case KeyEvent.VK_S, KeyEvent.VK_DOWN -> key_pressed = MOVE_DOWN;
                case KeyEvent.VK_A, KeyEvent.VK_LEFT -> key_pressed = MOVE_LEFT;
                case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> key_pressed = MOVE_RIGHT;
            }
        }
        public void keyReleased(KeyEvent evt)
        {
            //System.out.println("KEY RELEASED");
        }
    }
}

