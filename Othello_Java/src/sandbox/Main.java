package sandbox;

import java.util.Scanner;
public class Main {

    private static final int  L       = 10,
                                 X       =  0,
                                 Y       =  1,
                                 EMPTY   =  0,
                                 BLACK   =  1,
                                 WHITE   =  2,
                                 DRAW    =  3,
                                 WALL    =  9,
                                 PASS    =  1,
                                 GIVE_UP =  3,
                                 EXIT    =  2;

    private static final int[][] dir     = {{-1,-1},
                                            { 0,-1},
                                            { 1,-1},
                                            { 1, 0},
                                            { 1, 1},
                                            { 0, 1},
                                            {-1, 1},
                                            {-1, 0}};

    private static       int    x,y;
    private static final String LINE       = "--------------------------\n",
                                TITLE      = "\n" + LINE + "---       オセロ       ---\n" + LINE + "\n",
                                USAGE      = "  ---    遊び方    ---    \n  縦 5,横 3 のマスに置く => 「5 3」と入力。\n パス: pass \n 投了：give up\n ゲームの終了：exit\n",
                                URGES      = "\n駒を置いて下さい。=>  ",
                                RANGE      = "[1-8]",
                                S_BLACK    = "黒",
                                S_WHITE    = "白",
                                S_TURN     = "番",
                                S_DRAW     = "\n   ---   引き分け   ---   \n",
                                PRE_LINE   = "\n   ---   ",
                                POST_LINE  = "   ---       \n\n",
                                BLACK_TURN = PRE_LINE + S_BLACK + S_TURN + POST_LINE,
                                WHITE_TURN = PRE_LINE + S_WHITE + S_TURN + POST_LINE,
                                S_VICTORY  = "の勝ち  ---   \n",
                                S_ERROR    = "駒が置けません。別のマスを選択して下さい。\n",
                                THANKS     = "Thank you for playing. Good by the next time.";

    private static int[][] board   = new int[L][L];
    private static int     turn    = BLACK,
                           victory ;

    static class Turn{
        private static void show(){
            if    (turn==BLACK){print(BLACK_TURN);}
            else               {print(WHITE_TURN);}
        }
        private static void shift(){
            turn = 3 - turn;
        }
    }

    static class Board{

        public static void init(){
            for(int i=0;i<L;i++){
                board[0][i] =
                board[9][i] = WALL;
            }
            for(int i=1;i<L-1;i++){
                board[i][0] =
                board[i][9] = WALL;
            }
            board[4][4] =
            board[5][5] = WHITE;
            board[4][5] =
            board[5][4] = BLACK;
        }

        public static void show(){

            print(LINE);
            print("     ");
            for(int i=1;i<L-1;i++){
                print(i);
                if(i<L-2){print(" ");}
            }

            for(int i=0;i<L;i++){
                if(0<i && i<L-1){
                    print("\n %d ",i);
                }else {
                    print("\n   ");
                }
                for(int j=0;j<L;j++){
                    switch(board[i][j]){
                        case WALL  : {print("+"); break;}
                        case EMPTY : {print("_"); break;}
                        case WHITE : {print("o"); break;}
                        case BLACK : {print("*");       }
                    }
                    print(" ");
                }
                if(i<L-1){print(" ");}
                else     {print("\n") ;}
            }
            print(LINE);
        }
    }

    private static Scanner  sc = new Scanner(System.in);

    public static void main(String[] args){

        Board.init();

        print(TITLE);
        print(USAGE);

        out:while(true){
            Turn.show();
            Board.show();
            print(URGES);
            switch(input()){
                case EXIT    : {print(THANKS);
                                break out;          }
                case GIVE_UP : {victory = 3 - turn ;
                                show_result();
                                break out;          }
                case PASS    : {Turn.shift();
                                continue;           }
            }
            if(update()){
                if(judge()){
                    show_result();
                    break;
                }
                Turn.shift();
            } else {
                print(S_ERROR);
            }
        }
    }

    private static boolean judge(){
        int black = 0,
            white = 0;

        for(int i=1;i<L-1;i++){
            for(int j=1;j<L-1;j++){
                if(board[i][j]==BLACK){
                    black++;
                } else if(board[i][j]==WHITE){
                    white++;
                }
            }
        }

        if(black+white==8*8){
            if(black<white){
                victory = WHITE;
            } else if(black==white){
                victory = DRAW;
            } else {
                victory = BLACK;
            }
            return true;
        }
        return false;
    }

    private static boolean update(){
        boolean ret;
        if(chk_cell()){
            flip();
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    private static boolean chk_cell(){
        if(board[y][x]!=EMPTY){return false;}
        boolean result = false;
        out:for(int i=0;i<dir.length;i++){
            int j=x,k=y;
            j += dir[i][X];
            k += dir[i][Y];
            if(board[k][j] == 3 - turn){
                while(true){
                    j += dir[i][X];
                    k += dir[i][Y];
                    if(board[k][j]==turn){
                        result = true;
                        break out;
                    } else if(board[k][j]== 3 - turn){
                        continue;
                    }
                    break;
                }
            }
        }
        return result;
    }

    private static void flip(){
        board[y][x] = turn;
        for(int i=0;i<dir.length;i++){
            int j=x,k=y;
            j += dir[i][X];k += dir[i][Y];
            if(board[k][j] == 3 - turn){
                out:while(true){
                    j += dir[i][X];k += dir[i][Y];
                    if(board[k][j]==turn){
                        while(true){
                            j -= dir[i][X]; k -= dir[i][Y];
                            if(board[k][j]==turn){break out;}
                            board[k][j] = turn;
                        }
                    } else if(board[k][j]== 3 - turn){
                        continue;
                    }
                    break;
                }
            }
        }
    }

    private static void show_result(){
        String ret;
        if   (victory == DRAW) {ret = S_DRAW ;}
        else{
            String v;
            if    (victory == BLACK){v = S_BLACK ;}
            else                    {v = S_WHITE ;}
            ret = PRE_LINE + v + S_VICTORY ;
        }
        print(ret);
        Board.show();
    }

    private static void print(String s,Object... i){System.out.printf(s,i);}
    private static void print(int i){System.out.print(i);}

    private static int input(){
        int ret = 0;
        while(true){
            String[] s = sc.nextLine().split("\\s");
            if(s.length==1){
                if     (s[0].equals("pass")){ret = PASS ; break;}
                else if(s[0].equals("exit")){ret = EXIT ; break;}
            } else if(s.length==2){
                if     (s[0].equals("give") && s[1].equals("up")){ret = GIVE_UP ; break;}
                else if(s[0].matches(RANGE) && s[1].matches(RANGE)){
                    y = Integer.parseInt(s[0]);
                    x = Integer.parseInt(s[1]);
                    break;
                }
            }
            print(S_ERROR);
            print(USAGE);
            print(URGES);
        }
        return ret;
    }
}