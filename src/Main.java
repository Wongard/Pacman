public class Main {

    static final int FPS = 60;
    private static long frameStart;
    private static int frameTime;
    private static int frameDelay = 1000/FPS;

    /**
     * App start, responsible for refreshing game functions
     * @param args
     */
    public static void main(String[] args) {

        Game game = new Game();
        game.init("Pacman",200, 400, 510, 900);
        while(game.isRunning() == true)
        {
            frameStart = System.currentTimeMillis();
            game.handleEvents();
            game.update();
            game.render();

            frameTime = (int)(System.currentTimeMillis() - frameStart);
            try
            {
                if(frameTime < frameDelay) Thread.sleep(frameDelay - frameTime);
                else System.out.println("LAG?");
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            //Czekaj
        }
        System.out.println("KONIEC");
    }
}

