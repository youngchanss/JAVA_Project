public interface CommandConstants {
    int ON = 1;
    int OFF = 0;

    // Client -> Server
    int CtoS_LOGIN = 0;
    int CtoS_ROLL = 5;
    int CtoS_SCORE = 6;
    


    // Server -> Client
    int StoC_WAIT = 0;
    int StoC_GAME = 1;
    int StoC_TURN = 2;
    int StoC_DICES = 3;
    int StoC_ROLL = 4;
    int StoC_SCORE = 7;
}