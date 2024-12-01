package com.example.a2048;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private Game2048 game;
    private GridLayout gameBoard;
    private TextView scoreTextView;
    private GestureDetectorCompat gestureDetector;
    private Button restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        game = new Game2048();
        gameBoard = findViewById(R.id.gameBoard);
        scoreTextView = findViewById(R.id.scoreTextView);
        gestureDetector = new GestureDetectorCompat(this, this);

        // Initialize restart button
        restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        updateUI();
    }

    private void restartGame() {
        game = new Game2048();  // Create a new game instance
        updateUI();  // Update the UI to reflect the new game state
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over!")
                .setMessage("Final Score: " + game.getScore())
                .setCancelable(false)
                .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restartGame();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateUI() {
        gameBoard.removeAllViews();
        int[][] board = game.getBoard();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                TextView tile = new TextView(this);
                tile.setWidth(250);
                tile.setHeight(250);
                tile.setTextSize(36);
                tile.setGravity(android.view.Gravity.CENTER);

                if (board[i][j] != 0) {
                    tile.setText(String.valueOf(board[i][j]));
                    tile.setBackgroundColor(getTileColor(board[i][j]));
                } else {
                    tile.setBackgroundColor(0xFFCCC0B3);
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.setMargins(5, 5, 5, 5);
                gameBoard.addView(tile, params);
            }
        }

        scoreTextView.setText("Score: " + game.getScore());

        if (game.isGameOver()) {
            showGameOverDialog();
        }
    }

    private int getTileColor(int value) {
        switch (value) {
            case 2: return 0xFFEEE4DA;
            case 4: return 0xFFEDE0C8;
            case 8: return 0xFFF2B179;
            case 16: return 0xFFF59563;
            case 32: return 0xFFF67C5F;
            case 64: return 0xFFF65E3B;
            case 128: return 0xFFEDCF72;
            case 256: return 0xFFEDCC61;
            case 512: return 0xFFEDC850;
            case 1024: return 0xFFEDC53F;
            case 2048: return 0xFFEDC22E;
            default: return 0xFFCDC1B4;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                if (diffX > 0) {
                    game.move(Game2048.Direction.RIGHT);
                } else {
                    game.move(Game2048.Direction.LEFT);
                }
            }
        } else {
            if (Math.abs(diffY) > 100 && Math.abs(velocityY) > 100) {
                if (diffY > 0) {
                    game.move(Game2048.Direction.DOWN);
                } else {
                    game.move(Game2048.Direction.UP);
                }
            }
        }

        updateUI();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
