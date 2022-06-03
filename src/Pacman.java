import javax.swing.*;
import java.awt.*;

public class Pacman {
    int x,y,old_x,old_y,spawn_x,spawn_y;
    int direction,speed,inAction;
    Board board;
    Image image;
    Points points;
    Pacman(Board b, Points p,int x_sp, int y_sp)
    {
        board = b; points = p;
        spawn_x = x_sp; spawn_y = y_sp; speed = 30;inAction = 0;direction = 3;
        respawn();
        image = new ImageIcon("images/pacman_left.png").getImage();
    }
    public void move()
    {
        if(inAction == 0) {
            old_x = x; old_y = y;
            check_tile();
        }
        if(Game.key_hold == Game.STAY && inAction == 0) return;

        direction = Game.key_hold;
        int x_factor = 0, y_factor = 0;
        switch (direction)
        {
            case Game.MOVE_UP -> {
                y_factor = -Game.cell_height;
                image = new ImageIcon("images/pacman_up.png").getImage();
            }
            case Game.MOVE_DOWN -> {
                y_factor = Game.cell_height;
                image = new ImageIcon("images/pacman_down.png").getImage();
            }
            case Game.MOVE_LEFT -> {
                x_factor = -Game.cell_width;
                image = new ImageIcon("images/pacman_left.png").getImage();
            }
            case Game.MOVE_RIGHT -> {
                x_factor = Game.cell_width;
                image = new ImageIcon("images/pacman_right.png").getImage();
            }
        }
        if(inAction == 0)
        {
            if(board.getTile(Board.coord_to_row(y + y_factor),Board.coord_to_column(x + x_factor)) == Board.WALL) return;
            inAction = speed;
        }
        inAction--;

        float shift = (float)(speed - inAction)/speed;
        x = (old_x + (int)(x_factor * shift) + Game.width) % Game.width;
        y = old_y + (int)(y_factor * shift);
    }

    /**
     * Checks tile on pacman positions to find if the points in this file were taken
     */
    void check_tile()
    {
        if(board.getTile(Board.coord_to_row(y),Board.coord_to_column(x)) != Board.EMPTY)
        {
            points.add(board.getTile(Board.coord_to_row(y),Board.coord_to_column(x)));
            board.changeTile(Board.coord_to_row(y),Board.coord_to_column(x),Board.EMPTY);
        }
    }

    /**
     * Teleports pacman to spawn position
     */
    void respawn()
    {
        x = spawn_x; y = spawn_y;
        inAction = 0;direction = 3;
    }
    int getX() {return x;}
    int getY() {return y;}
    int getRow(){return y / Game.cell_height;}
    int getColumn(){return x/Game.cell_width;}
    int getDirection(){return direction - 1;}
    public int getSpeed() {return speed;}

    /**
     * Checks pacman state of animation
     * @return <p> 0 - when pacman ended his animation</p>
     * <p>1 - when pacman did less than 0.3 of his animation</p>
     * <p>2 - when in animation for more than 0.3 of full animation time</p>
     */
     int isMoving(){
        if(inAction == 0) return 0;
        if(inAction > 0.7 * speed) return 1;
        return 2;
    }
    Image getImage(){return image;}
}
