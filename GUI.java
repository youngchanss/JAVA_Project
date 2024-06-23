import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    public static void main(String[] args) {
        YachtGUI yachtGUI = new YachtGUI();
    }
}

class YachtGUI extends JFrame implements CommandConstants {
    /* GUI 정보 */
    LoginPanel loginPanel;
    LoadPanel loadPanel;
    GamePanel gamePanel;

    /* Client 정보 */
    private YachtClient client;

    /* Player1 */
    public String playerName1 = "";
    int[] playerScores1 = new int[Constant.CATENUMS];
    /* Player2 */
    String playerName2 = "";
    int[] playerScores2 = new int[Constant.CATENUMS];

    public YachtGUI(){
        setTitle(Constant.NAME);
        setSize(Constant.WIDTH, Constant.HEIGHT);
        setBackground(Color.white);
        setResizable(false);            // 프로그램 창 사이즈 고정
        setLocationRelativeTo(null);            // 프로그램 실행 시 정중앙 위치
        setDefaultCloseOperation(EXIT_ON_CLOSE);   // 프로그램 정상 종료
        setLayout(null);

        loginPanel = new LoginPanel();
        add(loginPanel);

        loadPanel = new LoadPanel();
        loadPanel.setVisible(false);
        add(loadPanel);

        gamePanel = new GamePanel();
        gamePanel.setVisible(false);
        add(gamePanel);

        setVisible(true);
    }

    /*Panel 관리*/
    class LoginPanel extends JPanel implements ActionListener {
        private JPanel namePanel;
        private JLabel nameLabel;
        private JButton enterBtn;
        private JTextField nameField;

        public String getName() {
            String name = nameField.getText();
            if(name == null || name.isEmpty())
                return "null";
            else
                return name;
        }

        public LoginPanel(){
            setSize(Constant.WIDTH, 600);
            setBackground(Color.WHITE);
            setLayout(null);
            setLocation(0, 0);

            namePanel = new JPanel();
            namePanel.setSize(50,40);
            namePanel.setLayout(null);
            namePanel.setLocation(220, 280);
            namePanel.setVisible(true);
            namePanel.setBackground(Constant.GRAY1);

            nameLabel = new JLabel();
            nameLabel.setSize(50,40);
            nameLabel.setLocation(0, 0);
            nameLabel.setText("이름:");
            nameLabel.setFont(new Font("BOLD", Font.BOLD, 12));
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            nameLabel.setVisible(true);
            namePanel.add(nameLabel);
            add(namePanel);

            nameField = new JTextField();
            nameField.setSize(170, 40);
            nameField.setLocation(270, 280);
            nameField.setVisible(true);
            add(nameField);

            enterBtn = new JButton();
            enterBtn.setSize(80,60);
            enterBtn.setFont(new Font("BOLD", Font.BOLD, 12));
            enterBtn.setHorizontalAlignment(JButton.CENTER);
            enterBtn.setText("ENTER");
            enterBtn.setLocation(500, 270);
            enterBtn.setVisible(true);
            enterBtn.addActionListener(this);
            add(enterBtn);

            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = getName();
            client = new YachtClient(name);
            client.setPanel(loginPanel, loadPanel, gamePanel);

            String command = CtoS_LOGIN + " " + name;
            client.sendToServer(command);
        }
    }

    class LoadPanel extends JPanel{
        JLabel loading = new JLabel();

        public LoadPanel(){
            setSize(Constant.WIDTH, 600);
            setBackground(Color.WHITE);
            setLayout(null);
            setLocation(0, 0);

            loading.setSize(400,300);
            loading.setLocation(200, 150);
            loading.setText("Loading...");
            loading.setFont(new Font("BOLD", Font.BOLD, 60));
            loading.setHorizontalAlignment(JLabel.CENTER);
            loading.setVisible(true);
            add(loading);
        }
    }

    class GamePanel extends JPanel{
        private int temp;
        private int rollcnt = Constant.ROLLCOUNT;

        ScoreBoard scoreBoard; //점수판
        DiceButton diceArray[] = new DiceButton[Constant.DICENUM]; //주사위 5개
        JLabel RollCount = new JLabel(); //주사위 리롤 횟수

        JButton rollButton = new JButton("ROLL"); //주사위 리롤 버튼
        JButton scoreButton = new JButton("GET SCORE"); //점수 반영 버튼

        public void onButton() {
            rollButton.setVisible(true);
            scoreButton.setVisible(true);
        }
        
        public void offButton() {
            rollButton.setVisible(false);
            scoreButton.setVisible(false);
        } 
        
        public GamePanel(){
            setSize(Constant.WIDTH, 600);
            setBackground(Color.WHITE);
            setLayout(null);
            setLocation(0, 0);

            //SCOREBOARD PANEL SET. 임시로 넣어놓음.
            scoreBoard = new ScoreBoard(null, null);

            scoreBoard.setLocation(0, 0); //위치 설정
            add(scoreBoard); //frame에 추가.

            //DICE SET. 임시로 넣어놓음.
            for(int i=0;i<Constant.DICENUM;i++) {
                diceArray[i] = new DiceButton((int)(Math.random()*6+1));
                diceArray[i].setLocation(Constant.DICEPOSX[i],Constant.DICEPOSY[i]);;
                add(diceArray[i]);
            }

            // roll button 설정
            rollButton.setFont(new Font(Constant.DEFAULT_FONT, Font.BOLD, Constant.FONT_SIZE1));
            rollButton.setLocation(532, 330);
            rollButton.setSize(135, 51);

            // roll button 눌렸을 때의 동작
            rollButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    /**
                    * 주사위 굴릴 수 있는 횟수 체크
                    * 굴릴 수 있다면 서버에 roll 명령 요청
                    */
                    System.out.println(client.name+"  Roll 버튼 눌렀어요!!!!");
                    String command = CtoS_ROLL + "";
                    client.sendToServer(command);
                }
            });
            add(rollButton);

            //SCORE BUTTON SET.
            scoreButton.setFont(new Font(Constant.DEFAULT_FONT, Font.BOLD, Constant.FONT_SIZE1));
            scoreButton.setLocation(532, 403);
            scoreButton.setSize(135, 51);
            scoreButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    String command = CtoS_SCORE+" "+client.name+" ";
                    
                    //어떤 칸의 점수를 넣을지 확인.
                    int sel_index = -1;
                    for(int i=0;i<Constant.CATENUMS;i++){
                        if (scoreBoard.SELBUTTON[i].isSelected()) {
                            sel_index = i;
                            break;
                        }
                    }
                    System.out.println("score 버튼 눌렀어요!!");
                    command += sel_index;
                    client.sendToServer(command);
                }
            });
            add(scoreButton);

            //ROLL COUNT SET.
            RollCount.setSize(260,50);
            RollCount.setHorizontalAlignment(JLabel.CENTER);
            RollCount.setLocation(470, 20);
            RollCount.setFont(new Font(Constant.DEFAULT_FONT, Font.BOLD, Constant.FONT_SIZE2));
            RollCount.setText("ROLL LEFT : "+rollcnt+" / "+Constant.ROLLCOUNT);
            add(RollCount);
        }
    }

    class DiceButton extends JButton implements ActionListener {
        private int diceValue;
        private boolean isPressed = false;
        private ImageIcon diceImage;
        
        public static void setDicesValue(DiceButton[] diceButtons, int[] diceValues) {
            int num = diceButtons.length;
            
            for(int i=0; i<num; i++)
                diceButtons[i].Updatedice(diceValues[i]);
        }

        public DiceButton(int diceValue) {
            this.diceValue = diceValue;
            this.isPressed = false;

            // 주사위 눈의 수에 따라 이미지 로드
            diceImage = new ImageIcon("Dicepng/" + diceValue + ".png");

            setIcon(diceImage);
            //setContentAreaFilled(false);

            addActionListener(this);

            setSize(75,75);
            setBackground(Constant.GRAY1);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            isPressed = !isPressed;
            if (isPressed) {
                setBorder(BorderFactory.createLineBorder(Color.RED, 1));  // 테두리 진하게
            } else {
                setBorder(BorderFactory.createEmptyBorder());  // 원래 상태로
            }
        }

        public boolean getisPressed(){
            return isPressed;
        }

        public int getdice(){
            return diceValue;
        }

        //주사위 사진 재설정.
        public void Updatedice(int diceValue) {
            this.diceValue = diceValue;
            diceImage = new ImageIcon("Dicepng/" + diceValue + ".png");
            setIcon(diceImage);
        }
    }

    class ScoreBoard extends JPanel {
        JPanel TURNLEFT = new JPanel();
        JLabel TURNLEFT_1 = new JLabel();
        JLabel TURNINT = new JLabel();

        JPanel CATEGORIES = new JPanel();
        JLabel CATEGORIES_1 = new JLabel();

        CATEPanel CATEPanel[] = new CATEPanel[Constant.CATENUMS];

        SELBUTTON SELBUTTON[] = new SELBUTTON[Constant.CATENUMS];
        ButtonGroup SELBUTTONGroup = new ButtonGroup();
        JPanel SELBUTTONPANEL;

        PlayerScoredIsPlay p1Score;
        PlayerScoredIsPlay p2Score;
        
        public void setPlayersName(String player1, String player2) {
            p1Score.setPlayerName(player1);
            p2Score.setPlayerName(player2);
        }

        public ScoreBoard(String p1, String p2) {
            setSize(400, Constant.HEIGHT);
            setLayout(null);
            setBackground(Color.GRAY);

            //set TURNLEFT
            //TURN LEFT 패널
            TURNLEFT.setSize(140, 80);
            TURNLEFT.setLocation(0, 0);
            TURNLEFT.setLayout(null);
            TURNLEFT.setBackground(Constant.GRAY1);
            TURNLEFT.setBorder(new LineBorder(Color.BLACK));

            //TURN LEFT 라벨 설정
            TURNLEFT_1.setSize(88, 15);
            TURNLEFT_1.setLocation(31, 25);
            TURNLEFT_1.setFont(new Font("BOLD", Font.BOLD, 14));
            TURNLEFT_1.setText("TURN LEFT");
            TURNLEFT.add(TURNLEFT_1);

            // turn / 12 설정
            TURNINT.setSize(33,14);
            TURNINT.setLocation(53, 45);
            TURNINT.setLayout(null);
            TURNINT.setFont(new Font("BOLD", Font.BOLD, 12));
            TURNINT.setText(" 1/12");
            TURNLEFT.add(TURNINT);
            add(TURNLEFT);

            //set CATEGORIES
            //카테고리 패널
            CATEGORIES.setSize(140,40);
            CATEGORIES.setLocation(0, 80);
            CATEGORIES.setLayout(null);
            CATEGORIES.setBackground(Constant.GRAY1);
            CATEGORIES.setBorder(new LineBorder(Color.BLACK));

            //카테고리 라벨
            CATEGORIES_1.setSize(75,15);
            CATEGORIES_1.setLocation(32, 12);
            CATEGORIES_1.setFont(new Font("BOLD", Font.BOLD, 12));
            CATEGORIES_1.setText("CATEGORIES");
            CATEGORIES.add(CATEGORIES_1);
            add(CATEGORIES);

            //카테고리 이름들 설정.
            for(int i=0;i<Constant.CATENUMS;i++){
                CATEPanel[i] = new CATEPanel(Constant.CATE[i]);
                CATEPanel[i].setLocation(40, 120+32*i);
                add(CATEPanel[i]);
            }

            //set CATESEL Radiobuttons
            for(int i=0;i<Constant.CATENUMS;i++){
                //카테고리 buttongroup display.
                //6,7,14번은 점수를 추가할수 있는 항목이 아니므로 제외하기 위함 패널 추가.
                if(i == 6 || i == 7 || i == 14){
                    SELBUTTON[i] = new SELBUTTON(); //null 방지.

                    SELBUTTONPANEL = new JPanel();
                    SELBUTTONPANEL.setSize(40,32);
                    SELBUTTONPANEL.setLayout(null);
                    SELBUTTONPANEL.setBackground(Constant.GRAY1);
                    SELBUTTONPANEL.setLocation(0, 120+32*i);
                    SELBUTTONPANEL.setVisible(true);
                    add(SELBUTTONPANEL);
                }
                else{
                    SELBUTTON[i] = new SELBUTTON();
                    SELBUTTON[i].setLocation(0, 120+32*i);
                    SELBUTTONGroup.add(SELBUTTON[i]);
                    add(SELBUTTON[i]);
                }
            }

            //set PLAYER INDICATES.
            p1Score = new PlayerScoredIsPlay(p1);
            p2Score = new PlayerScoredIsPlay(p2);
            p1Score.setLocation(140,0);
            p2Score.setLocation(270,0);
            add(p1Score);
            add(p2Score);
            setVisible(true);
        }

        public void setturns(int turn){
            if(turn < 10) TURNINT.setText(" "+turn+"/12");
            else TURNINT.setText(turn+"/12");
        }

        class CATEPanel extends JPanel{
            JLabel CATELables_1 = new JLabel();

            public CATEPanel(String line){
                setSize(100,32);
                setLayout(null);
                setBackground(Constant.GRAY1);
                setBorder(new LineBorder(Color.BLACK));

                CATELables_1.setSize(100,32);
                CATELables_1.setText(line);
                CATELables_1.setHorizontalAlignment(JLabel.CENTER);
                CATELables_1.setFont(new Font("BOLD", Font.BOLD, 12));
                add(CATELables_1);
                setVisible(true);
            }
        }

        class SELBUTTON extends JRadioButton{
            public SELBUTTON(){
                setSize(40, 32);
                setLayout(null);
                setBackground(Constant.GRAY1);
                setBorder(new LineBorder(Color.BLACK));
                setHorizontalAlignment(JRadioButton.CENTER); // 라디오버튼을 가운데 정렬
                setVerticalAlignment(JRadioButton.CENTER); // 라디오버튼을 수직 중앙 정렬
                setVisible(true);
            }
        }

        class PlayerScoredIsPlay extends JPanel{
            JLabel playerNameLabel;
            JPanel PLAYERGRID;

            JPanel SCOREGRID[];
            JLabel SCORES[];
            
            int[] INT_SCORES = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            boolean[] SCORE_LOCK = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};

            public void setPlayerName(String name) {
                playerNameLabel.setText(name);
            }

            public PlayerScoredIsPlay(String name){
                playerNameLabel = new JLabel();
                PLAYERGRID = new JPanel();
                SCOREGRID = new JPanel[Constant.CATENUMS];
                SCORES = new JLabel[Constant.CATENUMS];

                PLAYERGRID.setSize(130,120);
                PLAYERGRID.setLayout(null);
                PLAYERGRID.setVisible(true);
                PLAYERGRID.setBorder(new LineBorder(Color.BLACK));
                PLAYERGRID.setLocation(0, 0);

                playerNameLabel.setSize(130,120);
                playerNameLabel.setHorizontalAlignment(JLabel.CENTER);
                playerNameLabel.setFont(new Font("BOLD", Font.BOLD, 14));
                playerNameLabel.setText(name);
                playerNameLabel.setVisible(true);
                PLAYERGRID.add(playerNameLabel);
                add(PLAYERGRID);

                //SCOREGRID SET.
                for(int i=0;i<Constant.CATENUMS;i++){
                    SCOREGRID[i] = new JPanel();
                    SCORES[i] = new JLabel();

                    SCOREGRID[i].setSize(130,32);
                    SCOREGRID[i].setLayout(null);
                    SCOREGRID[i].setVisible(true);
                    SCOREGRID[i].setBackground(Color.WHITE);
                    SCOREGRID[i].setBorder(new LineBorder(Color.BLACK));
                    SCOREGRID[i].setLocation(0, 120+32*i);

                    SCORES[i].setSize(130,32);
                    SCORES[i].setLayout(null);
                    SCORES[i].setVisible(true);
                    SCORES[i].setHorizontalAlignment(JLabel.CENTER);
                    SCORES[i].setFont(new Font("BOLD", Font.BOLD, 14));
                    SCORES[i].setText(INT_SCORES[i]+"");
                    SCOREGRID[i].add(SCORES[i]);
                    add(SCOREGRID[i]);
                }
                setSize(130,600);
                setLayout(null);
                setVisible(true);
            }

            public void SETSCORES(int index, int score){
                if(!SCORE_LOCK[index]){
                        INT_SCORES[index] += score;
                        SCORES[index].setText(INT_SCORES[index]+"");
                        SCORE_LOCK[index] = true;
                        if(index >= 0 && index < 6){
                            INT_SCORES[Constant.SUBTOTAL] += score;
                            SCORES[Constant.SUBTOTAL].setText(INT_SCORES[index]+"");
                        }
                        INT_SCORES[Constant.TOTAL] += score;
                        SCORES[Constant.TOTAL].setText(INT_SCORES[index]+"");
                }
            }

            public void SETNAMES(String name){
                playerNameLabel.setText(name);
            }
        }
    }
}