package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    private TextView youWonTextView;
    private TextView androidWonTextView;
    private TextView tieTextView;
    private Button extBtn;

    // Restart Button
    private Button startButton;
    // Game Over
    Boolean mGameOver;
    Boolean human_first;
    int human_won_time = 0;
    int android_won_time = 0;
    int tie_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGame = new TicTacToeGame();
        mBoardButtons = new Button[mGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.button0);
        mBoardButtons[1] = (Button) findViewById(R.id.button1);
        mBoardButtons[2] = (Button) findViewById(R.id.button2);
        mBoardButtons[3] = (Button) findViewById(R.id.button3);
        mBoardButtons[4] = (Button) findViewById(R.id.button4);
        mBoardButtons[5] = (Button) findViewById(R.id.button5);
        mBoardButtons[6] = (Button) findViewById(R.id.button6);
        mBoardButtons[7] = (Button) findViewById(R.id.button7);
        mBoardButtons[8] = (Button) findViewById(R.id.button8);
        mInfoTextView = (TextView) findViewById(R.id.information);
        youWonTextView=(TextView)findViewById(R.id.human_won);
        androidWonTextView=(TextView)findViewById(R.id.android_won);
        tieTextView=(TextView)findViewById(R.id.tie_time);

        startButton=(Button)findViewById(R.id.button_restart);
        extBtn=(Button)findViewById(R.id.button_exit);
        mGame = new TicTacToeGame();
        startButton.setText(R.string.restart_zh);
        startButton.setOnClickListener(new RestartButtonClickListener());
        extBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            }
        });
        human_first=true;
        loadPreferences();
        startNewGame();
    }

    //--- Set up the game board.
    private void startNewGame() {

        mGameOver = false;
        mGame.clearBoard();
        //---Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        //---Human goes first
        // mInfoTextView.setText("You go first.");
        if (human_first==true){
            mInfoTextView.setText(R.string.info_zh);
            human_first=!human_first;
        }else{
            mInfoTextView.setText(R.string.info_android_zh);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            human_first=!human_first;
        }
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    public void savePreferences(String h, String w,String t) {
        SharedPreferences pref = getSharedPreferences("TICTACTOE", MODE_PRIVATE);
        pref.edit().putString("human", h).apply();
        pref.edit().putString("android", w).apply();
        pref.edit().putString("tic", t).apply();
    }
    public void loadPreferences() {
        SharedPreferences pref = getSharedPreferences("TICTACTOE", MODE_PRIVATE);
        youWonTextView.setText(youWonTextView.getText()+":"+pref.getString("human", "0"));
        androidWonTextView.setText(androidWonTextView.getText()+":"+pref.getString("android", "0"));
        tieTextView.setText(tieTextView.getText()+":"+pref.getString("tic", "0"));

        human_won_time=Integer.parseInt(pref.getString("human", "0"));
        android_won_time=Integer.parseInt(pref.getString("android", "0"));
        tie_time=Integer.parseInt(pref.getString("tic", "0"));
    }

    //---Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        @Override
        public void onClick(View v) {
            if (mGameOver == false) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);
                    //--- If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                       // mInfoTextView.setText("It's Android's turn.");
                        mInfoTextView.setText(R.string.android_turn);
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }
                    if (winner == 0) {
                        mInfoTextView.setTextColor(Color.rgb(0, 0, 0));
                        // mInfoTextView.setText("It's your turn (X).");
                        mInfoTextView.setText(R.string.your_turn);
                    } else if (winner == 1) {
                        mInfoTextView.setTextColor(Color.rgb(0, 0, 200));
                       //  mInfoTextView.setText("It's a tie!");
                        mInfoTextView.setText(R.string.tie);
                        mGameOver = true;
                        tie_time+=1;
                        String str = getResources().getString(R.string.tie)+":"+String.valueOf(tie_time);
                        tieTextView.setText(str);
                        savePreferences(String.valueOf(human_won_time),String.valueOf(android_won_time),String.valueOf(tie_time));
                    } else if (winner == 2) {
                        mInfoTextView.setTextColor(Color.rgb(0, 200, 0));
                        // mInfoTextView.setText("You won!");
                        mInfoTextView.setText(R.string.your_won);
                        mGameOver = true;
                        human_won_time+=1;
                        String str = getResources().getString(R.string.android_turn)+":"+String.valueOf(human_won_time);
                        youWonTextView.setText(str);
                        savePreferences(String.valueOf(human_won_time),String.valueOf(android_won_time),String.valueOf(tie_time));
                    } else {
                        mInfoTextView.setTextColor(Color.rgb(200, 0, 0));
                       // mInfoTextView.setText("Android won!");
                        mInfoTextView.setText(R.string.android_won);
                        mGameOver = true;
                        android_won_time+=1;
                        String str = getResources().getString(R.string.android_turn)+":"+String.valueOf(android_won_time);
                        androidWonTextView.setText(str);
                        savePreferences(String.valueOf(human_won_time),String.valueOf(android_won_time),String.valueOf(tie_time));
                    }
                }
            }
        }
    }

    private class RestartButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            startNewGame();
        }
    }
}
