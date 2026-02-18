package fifteen_divideandconq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel
        implements MouseListener, ActionListener {

    // Core
    BoardState bs = new BoardState();
    Bot bot = new Bot(bs);
    MoveAnimation anim = new MoveAnimation();

    // Game State
    boolean playerTurn = true;
    boolean paused = false;
    boolean started = false;
    boolean win = false;

    // Stats
    int playerMoves = 0;
    int botMoves = 0;

    // Time
    long startTime = 0;
    long pauseStart = 0;
    long totalPaused = 0;

    // Store board before player move
    int[] lastPlayerState = null;

    // Layout
    final int TILE = 110;
    final int GAP = 12;
    final int TOP = 150;

    final int BTN_W = 140;
    final int BTN_H = 40;

    // Timer
    Timer timer = new Timer(16, this);

    // Animation
    int[] animNext;

    public GamePanel() {

        addMouseListener(this);
        setBackground(new Color(10,10,20));

        timer.start();
    }

    /* ================= Layout ================= */

    int boardWidth() {
        return 4 * TILE + 3 * GAP;
    }

    int boardX() {
        return (getWidth() - boardWidth()) / 2;
    }

    Rectangle pauseButton() {

        int cx = getWidth() / 2;

        return new Rectangle(
                cx - BTN_W - 20,
                90,
                BTN_W,
                BTN_H
        );
    }

    Rectangle newGameButton() {

        int cx = getWidth() / 2;

        return new Rectangle(
                cx + 20,
                90,
                BTN_W,
                BTN_H
        );
    }

    /* ================= Game Loop ================= */

    public void actionPerformed(ActionEvent e) {

        if (paused || win) {
            repaint();
            return;
        }

        // Animation
        if (anim.active) {

            if (!anim.update()) {

                bs.setState(animNext);
                anim.active = false;

                // Check win
                if (bs.isSolved()) {
                    win = true;
                    paused = true;
                }
            }
        }

        // Bot Turn
        else if (!playerTurn) {

            int[] cur = bs.copy();

            int[] nxt =
                bot.chooseMove(cur, lastPlayerState);

            startAnimation(cur, nxt);

            botMoves++;

            playerTurn = true;
        }

        repaint();
    }

    /* ================= Animation ================= */

    void startAnimation(int[] old, int[] nxt) {

        animNext = nxt;

        int z1 = 0, z2 = 0;

        for (int i = 0; i < 16; i++) {

            if (old[i] == 0) z1 = i;
            if (nxt[i] == 0) z2 = i;
        }

        int sx = boardX() +
                 (z2 % 4) * (TILE + GAP);

        int sy = TOP +
                 (z2 / 4) * (TILE + GAP);

        int ex = boardX() +
                 (z1 % 4) * (TILE + GAP);

        int ey = TOP +
                 (z1 / 4) * (TILE + GAP);

        anim.start(nxt[z1], sx, sy, ex, ey);
    }

    /* ================= Drawing ================= */

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Background
        g2.setColor(new Color(10,10,20));
        g2.fillRect(0,0,getWidth(),getHeight());

        // Time
        long time = 0;

        if (started) {

            time = (System.currentTimeMillis()
                  - startTime
                  - totalPaused) / 1000;
        }

        int cx = getWidth() / 2;

        // Stats
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.CYAN);

        g2.drawString("Time: " + time + "s",
                      cx - 220, 35);

        g2.drawString("Player: " + playerMoves,
                      cx - 60, 35);

        g2.drawString("Bot: " + botMoves,
                      cx + 100, 35);

        // Title
        String title;

        if (win)
            title = "Completed";
        else
            title = playerTurn ?
                    "Player Move" :
                    "Bot Move";

        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.setColor(new Color(0,220,220));

        int sw = g2.getFontMetrics()
                   .stringWidth(title);

        g2.drawString(title,
                      cx - sw/2,
                      75);

        // Buttons
        drawButton(g2,
                pauseButton(),
                paused && !win ?
                  "Resume" : "Pause",
                new Color(0,90,200));

        drawButton(g2,
                newGameButton(),
                "New Game",
                new Color(150,0,0));

        // Board
        int[] st = bs.board;

        int z = bs.zeroPos();

        int zr = z / 4;
        int zc = z % 4;

        for (int i = 0; i < 16; i++) {

            int v = st[i];

            if (v == 0) continue;

            int r = i / 4;
            int c = i % 4;

            int x = boardX() +
                    c * (TILE + GAP);

            int y = TOP +
                    r * (TILE + GAP);

            Color col;

            // Rainbow on win
            if (win) {

                float hue = (float) i / 16f;

                col = Color.getHSBColor(
                        hue, 0.8f, 0.9f);

            }

            // Highlight movable
            else if (Math.abs(zr - r) +
                     Math.abs(zc - c) == 1) {

                col = new Color(0,200,200);

            }

            // Normal
            else {

                col = new Color(40,40,70);
            }

            drawTile(g2, x, y, v, col);
        }

        // Animated tile
        if (anim.active) {

            drawTile(g2,
                anim.getX(),
                anim.getY(),
                anim.val,
                new Color(0,220,220));
        }
    }

    void drawTile(Graphics g,
                  int x,
                  int y,
                  int val,
                  Color col) {

        g.setColor(col);

        g.fillRoundRect(
            x, y,
            TILE, TILE,
            20,20
        );

        g.setColor(Color.WHITE);

        g.setFont(
          new Font("Arial",
                   Font.BOLD,
                   32)
        );

        String s = "" + val;

        int sw =
          g.getFontMetrics()
           .stringWidth(s);

        g.drawString(s,
            x + TILE/2 - sw/2,
            y + TILE/2 + 12);
    }

    void drawButton(Graphics g,
                    Rectangle r,
                    String txt,
                    Color bg) {

        g.setColor(bg);

        g.fillRoundRect(
            r.x, r.y,
            r.width, r.height,
            15,15
        );

        g.setColor(Color.WHITE);

        g.setFont(
          new Font("Arial",
                   Font.BOLD,
                   16)
        );

        int sw =
          g.getFontMetrics()
           .stringWidth(txt);

        g.drawString(txt,
            r.x + r.width/2 - sw/2,
            r.y + 26);
    }

    /* ================= Mouse ================= */

    public void mouseClicked(MouseEvent e) {

        int mx = e.getX();
        int my = e.getY();

        // New Game
        if (newGameButton().contains(mx,my)) {
            restart();
            return;
        }

        // Pause
        if (pauseButton().contains(mx,my)) {

            if (!win) {

                paused = !paused;

                if (paused)
                    pauseStart =
                        System.currentTimeMillis();
                else
                    totalPaused +=
                        System.currentTimeMillis()
                        - pauseStart;
            }

            repaint();
            return;
        }

        if (win || paused || anim.active || !playerTurn)
            return;

        // Start time
        if (!started) {
            started = true;
            startTime = System.currentTimeMillis();
        }

        int bx = mx - boardX();
        int by = my - TOP;

        int c = bx / (TILE + GAP);
        int r = by / (TILE + GAP);

        if (r < 0 || r >= 4 ||
            c < 0 || c >= 4)
            return;

        int idx = r * 4 + c;

        int z = bs.zeroPos();

        int zr = z / 4;
        int zc = z % 4;

        if (Math.abs(zr - r) +
            Math.abs(zc - c) == 1) {

            int[] cur = bs.copy();

            // Save state
            lastPlayerState = cur.clone();

            int[] nxt = bs.copy();

            nxt[z] = nxt[idx];
            nxt[idx] = 0;

            startAnimation(cur, nxt);

            playerMoves++;

            playerTurn = false;
        }
    }

    /* ================= Restart ================= */

    void restart() {

        bs = new BoardState();
        bot = new Bot(bs);

        playerMoves = 0;
        botMoves = 0;

        started = false;
        paused = false;
        win = false;

        startTime = 0;
        totalPaused = 0;

        lastPlayerState = null;

        anim.active = false;

        repaint();
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
