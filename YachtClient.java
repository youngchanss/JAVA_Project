import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.Flow.Subscription;

public class YachtClient implements CommandConstants {
    String name = null;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private ClientReceiver receiver;
    private YachtGUI.LoginPanel loginPanel;
    private YachtGUI.LoadPanel loadPanel;
    private YachtGUI.GamePanel gamePanel;

    String player1;
    String player2;

    public void sendToServer(String command) {
        out.println(command);
        System.out.println("서버에 " + command +"전송 완료");
        out.flush(); // Ensure data is sent immediately
    }

    public YachtClient(String name) {
        try {
            this.name = name;
            socket = new Socket("localhost", 9000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            receiver = new ClientReceiver(in);
            receiver.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPanel(YachtGUI.LoginPanel loginPanel, YachtGUI.LoadPanel loadPanel, YachtGUI.GamePanel gamePanel) {
        this.loginPanel = loginPanel;
        this.loadPanel = loadPanel;
        this.gamePanel = gamePanel;
    }

    class ClientReceiver extends Thread {
        private BufferedReader in;

        public ClientReceiver(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            StringTokenizer st = null;
            while (true) {
                try {
                    String line = in.readLine();
                    if(line == null) throw new IOException();
                    System.out.println("command: " + line);

                    st = new StringTokenizer(line);
                    int cmd = Integer.parseInt(st.nextToken());

                    switch (cmd) {
                        case StoC_WAIT:
                            loginPanel.setVisible(false);
                            loadPanel.setVisible(true);
                            System.out.println(name + " Player 대기 중...");
                            break;
                        case StoC_GAME:
                           // StoC_GAME Player1이름 Player2이름
                            player1 = st.nextToken();
                            player2 = st.nextToken();

                            // gamePanel 값 설정하는 과정
                            gamePanel.scoreBoard.setPlayersName(player1, player2);
                            // 점수판도 확인해보기
                            gamePanel.setVisible(true);
                            loadPanel.setVisible(false);
                            break;
                        case StoC_TURN:
                           // StoC_TURN name1 -> name1의 턴.
                           // button on / off 하는 과정 필요
                            String turnName = st.nextToken();    // turn 해야할 client 이름. 
                            
                            if(name.equals(turnName)) {
                                gamePanel.onButton();
                            } else {
                                gamePanel.offButton();
                            }
                            break;
                        case StoC_DICES:
                            //서버에서 받은 다이스 값을 GUI에 나타내는 과정
                            // StoC_DICES 1 2 3 4 5 -> 주사위 값 5개를 보내주는 명령
                            int[] diceValues = new int[Constant.DICENUM];
                            for(int i=0; i<diceValues.length; i++)
                                diceValues[i] = Integer.parseInt(st.nextToken());
                            System.out.println(diceValues);
                            YachtGUI.DiceButton.setDicesValue(gamePanel.diceArray, diceValues);
                            break;
                        case StoC_SCORE:
                            //서버에서 받은 점수값을 GUI에 나타내는 과정
                            //StoC_SCORE name 3 12 -> name의 3번째 카테고리에 12를 더하는 명령
                            System.out.println("7인식");
                            String plusname = st.nextToken();
                            int index = Integer.parseInt(st.nextToken());
                            int score = Integer.parseInt(st.nextToken());
                            if(player1.equals(plusname)){
                                System.out.println("P1 응답.");
                                gamePanel.scoreBoard.p1Score.SETSCORES(index, score);
                            }
                            else {
                                System.out.println("P2 응답.");
                                gamePanel.scoreBoard.p2Score.SETSCORES(index, score);
                            }
                            break;
                    }   
                    /**
                     *  지금까지 login -> loading -> gamePanel까지 됐고, 초기 턴 지정까지 완료.
                     *    남은 할일
                     *    1. 현재 턴인 client가 roll 버튼을 눌렀을 때
                     *    2. 현재 턴인 client가 getScore버튼을 눌렀을 때
                     *    3. 남은 라운드가 모두 끝났을 때 종료 처리 
                     */

                } catch (IOException e) {
                    System.out.println("ClientReceiver Error!");
                    continue;
                }
            }
        }
    }
}
