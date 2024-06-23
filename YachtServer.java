import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class YachtServer implements CommandConstants {
    private static final int PORT = 9000;
    static final int MAX_PLAYER = 2;
    static final ArrayList<ServerHandler> players = new ArrayList<>();
    volatile static boolean started = false; 
    static boolean finished = false;
    private Random random = new Random();
    /*Server가 가지고 있는 정보*/
    public static int roll_count= 3; //기본적으로 3번 던질 수 있음
    
    public void broadCasting(String line) {
        players.get(0).sendToClient(line);
        players.get(1).sendToClient(line);
    }

    public boolean isStarted() { //게임 플레이어 2명이 되면 LoadingPanel에서 GamePanel로 넘어가는 신호
        return started;
    }

    public void toggleStarted() {
        started = !started;
    }

    public boolean isFinished() {
        return finished;
    }

    public static synchronized void addPlayer(ServerHandler player) {
        players.add(player);
    }

    public static void main(String[] args) {
        YachtServer server = new YachtServer();
        server.start();
    }

    public void start() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);

            while(!isStarted()) {
                try {
                    clientSocket = serverSocket.accept();

                    ServerHandler serverHandler = new ServerHandler(clientSocket, this);
                    Thread th = new Thread(serverHandler);
                    addPlayer(serverHandler);

                    //몇 명 들어 왔는 지 확인 코드
                    System.out.println("현재 서버에 "+ players.size() + " 명");
                    th.start();

                    if (players.size() == MAX_PLAYER) {
                        toggleStarted();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            while (!isFinished()) {
                // 게임 시작
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Yacht 계산 관련 추가할 예정
     */
    public int[] getDiceValues() {
        int[] diceValues = new int[Constant.DICENUM];
        for(int i=0; i<diceValues.length; i++)
            diceValues[i] = random.nextInt(6) + 1;
    
        return diceValues;
    }
}
