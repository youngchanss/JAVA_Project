
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerHandler implements Runnable, CommandConstants {
    private String name;
    private Socket socket;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private YachtServer yachtServer;

    public ServerHandler(Socket s, YachtServer yachtServer) {
        this.yachtServer = yachtServer;
        try {
            socket = s;
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToClient(String command) {
        out.println(command);
    }

    @Override
    public void run() {
        StringTokenizer st = null;
        while (true) {
            try {
                System.out.println(name + " readLine 대기 중");
                String line = in.readLine();
                System.out.println(name + " ServerHandler : " + line);
                System.out.println(name + " readLine 실행 후");
                if(line == null) throw new IOException();

                st = new StringTokenizer(line);
                
                int cmd = Integer.parseInt(st.nextToken());

                String command;
                switch (cmd) {
                    case CtoS_LOGIN:
                    	// CtoS_LOGIN : 클라이언트가 로그인을 요청한 경우
                        name = st.nextToken();

                        command = StoC_WAIT + "";
                        sendToClient(command);
                        while (!yachtServer.isStarted()) {;}

                        // 두 클라이언트에게 보내줘야 함.
                        String player1 = YachtServer.players.get(0).getName();
                        String player2 = YachtServer.players.get(1).getName();
                        
                        // StoC_GAME : gamePanel로 이동시키는 명령. player1과 player2의 이름 전송
                        command = StoC_GAME + " " + player1 + " " + player2;
                        yachtServer.broadCasting(command);
                        
                        // StoC_TURN : player1의 turn을 의미 
                        command = StoC_TURN + " " + player1;
                        yachtServer.broadCasting(command);
                        
                        // StoC_DICES : dice 값들을 보내줌. - 4 5 2 1 6
                        // 여기서는 주사위 초기값 보내줌.
                        command = "";
                        command += StoC_DICES;
                        int[] diceValues1 = yachtServer.getDiceValues();
                        for(int value : diceValues1)
                            command += " " + value;
                        yachtServer.broadCasting(command);
                        break;
                    case CtoS_ROLL:
                    	// CtoS_ROLL : 클라이언트가 주사위 roll 버튼을 누른 경우
                        command = "";
                        command += StoC_DICES;
                        int[] diceValues2 = yachtServer.getDiceValues();
                        for(int value : diceValues2)
                            command += " " + value;
                        yachtServer.broadCasting(command);
                        break;
                    case CtoS_SCORE:
                        //CtoS_SCORE : 클라이언트가 GET_SCORE버튼을 누른 경우
                        name = st.nextToken();
                        int category = Integer.parseInt(st.nextToken());
                        
                        //주사위 값 대신 점수로 변경할것.
                        int[] diceValues3 = yachtServer.getDiceValues();
                        int score_sum = 0;
                        for(int value : diceValues3) score_sum+=value;

                        command = "";
                        command += StoC_SCORE+" "+name+" ";
                        command += category +" "+score_sum;
                        System.out.println(command + "보냄!!");
                        yachtServer.broadCasting(command);
                        break;
                    default:
                        System.out.println("Command Type Error!");
                }
            } catch (IOException e) {
                System.out.println("ServerHandler Error!");
                System.exit(-1);
            }
        }
    }

    public String getName() {
        return name;
    }
}