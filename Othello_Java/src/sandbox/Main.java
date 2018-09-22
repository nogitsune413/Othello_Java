package sandbox;

import java.util.Scanner;
public class Main {

    private static final int  L       = 10, // オセロ盤の周囲の「壁」を含めた、オセロ盤の縦と横のマスの数。オセロ版は8×8だが、周囲に壁を設けると10×10になる。
                                 X       =  0, // X軸の値が格納されている配列の位置。二次元配列dirを参照する際に用いる。
                                 Y       =  1, // Y軸の値が格納されている配列の位置。二次元配列dirを参照する際に用いる。
                                 EMPTY   =  0, // オセロ盤のマスに何も置かれていない事を表す。
                                 BLACK   =  1, // オセロ盤のマスに黒石が置かれていること、黒の手番であること、黒番のプレイヤーのこと
                                 WHITE   =  2, // オセロ盤のマスに白石が置かれていること、白の手番であること、白番のプレイヤーのこと
                                 DRAW    =  3,// 引き分けを表す。
                                 WALL    =  9, // オセロ盤の周囲に仮想的に用意した「壁」を表す。
                                 PASS    =  1, // 手番をパスするコマンド
                                 GIVE_UP =  3,// 投了するコマンド
                                 EXIT    =  2;// ゲームをやめるコマンド

    /** オセロのマスに置いた石の周囲8方向を表す座標の組。 */
    private static final int[][] dir     = {{-1,-1},// 左下
                                            { 0,-1},// 下
                                            { 1,-1},// 右下
                                            { 1, 0},//右
                                            { 1, 1},//右上
                                            { 0, 1},//上
                                            {-1, 1},//左上
                                            {-1, 0}};//左

    /** オセロの盤面を座標と見做したときのx軸、y軸の値 */
    private static       int    x,y;

    /** コマンドプロンプト上に描画するメッセージ */
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

    /** オセロ盤の盤面 */
    private static int[][] board   = new int[L][L];

    /* オセロの手番。黒番を先手とする。 */
    private static int     turn    = BLACK,

	/* 勝者を表す。引き分けの場合もある */
                           victory ;

    /** 手番クラス */
    static class Turn{

    	/** 手番を表示する。*/
        private static void show(){
            if    (turn==BLACK){print(BLACK_TURN);}
            else               {print(WHITE_TURN);}
        }

        /** 手番を入れ替える */
        private static void shift(){
            turn = 3 - turn;
        }
    }

    /** オセロ盤クラス */
    static class Board{

    	/** オセロ盤を初期配置にする */
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

        /** オセロ盤をコマンドプロンプト上に表示する */
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

    /** ユーザーの入力を標準入力から読み込む */
    private static Scanner  sc = new Scanner(System.in);

    /** エントリポイント */
    public static void main(String[] args){

        Board.init(); // オセロ盤を初期化する

        print(TITLE); // タイトルと使い方を表示
        print(USAGE);

        out:while(true){
            Turn.show();
            Board.show();
            print(URGES);
            switch(input()){
                case EXIT    : {print(THANKS);       // ゲームをやめる
                                break out;          }
                case GIVE_UP : {victory = 3 - turn ; // 投了する
                                show_result();
                                break out;          }
                case PASS    : {Turn.shift();          // 手番をPASSする
                                continue;           }
            }
            if(update()){            // 盤面を更新
                if(judge()){         // 勝敗を判定
                    show_result();  // 勝敗を表示
                    break;
                }
                Turn.shift();        // 手番を交代する
            } else {
                print(S_ERROR);     // 石が置けなかったとき
            }
        }
    }

    /**
     * 勝敗を判定する
     * @return 勝敗が決まったとき、true
     */
    private static boolean judge(){
        int black = 0,
            white = 0;

        // 石を数える
        for(int i=1;i<L-1;i++){
            for(int j=1;j<L-1;j++){
                if(board[i][j]==BLACK){
                    black++;
                } else if(board[i][j]==WHITE){
                    white++;
                }
            }
        }

        // 盤面が石で埋まっているとき、石の数の多い方を勝者とする。
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

    /**
     * 盤に石を置き、返せる相手の石を引っ繰り返す
     * @return 石を置けるとき、true
     */
    private static boolean update(){
        if(chk_cell()){
            flip();
            return true;
        } else {
        	return false;
        }
    }

    /**
     * 置こうと試みたマスに石が置けるかどうか判定する。
     * @return 石が置けるとき、true
     */
    private static boolean chk_cell(){
        if(board[y][x]!=EMPTY){return false;} // 石を置こうとしたマスが空でないときは、石を置けない。
        boolean result = false;
        out:for(int i=0;i<dir.length;i++){ // 石の周囲を八方向全てチェックする。
            int j=x,k=y;
            j += dir[i][X]; // j,kに石の周囲8マスのうちいずれかひとつの座標を取る。
            k += dir[i][Y];
            if(board[k][j] == 3 - turn){ // 石を置こうとしたマスの周りに相手の石がある。
                while(true){
                    j += dir[i][X]; // 更に先のマスをチェックしてみる。
                    k += dir[i][Y];
                    if(board[k][j]==turn){ // 相手の石のさらに先に自分の石があるので、相手の石を裏返せる。
                        result = true; // 石が置けることが分かったので、trueを返してメソッドを抜ける。
                        break out;
                    } else if(board[k][j]== 3 - turn){ // 相手の石がまだ続いているので、更に先をチェック。
                        continue;
                    }
                    break; // 相手の石の先が空のマスか壁なので、この方向は石を置ける状態にない。
                }
            }
        }
        return result; // 判定結果を返却する。
    }

    /** 相手の石を引っ繰り返す */
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

    /** 勝敗の結果をコマンドプロンプト上に表示する */
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

    /** 文字列出力のための簡易メソッド */
    private static void print(String s,Object... i){System.out.printf(s,i);}

    /** 数値を文字列出力するための簡易メソッド */
    private static void print(int i){System.out.print(i);}

    /** 標準入力からユーザーの入力を読み込む */
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