public class PacmanController extends Thread{
    Pacman pacman;
    int task;
    PacmanController(Pacman new_pacman)
    {
        pacman = new_pacman;
    }
    @Override
    public void run()
    {
        pacman.move();
    }
}
