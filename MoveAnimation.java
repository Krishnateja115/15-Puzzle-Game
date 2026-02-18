package fifteen_divideandconq;

public class MoveAnimation {

    int steps = 10;
    int cur = 0;

    int val;

    int sx, sy, ex, ey;

    boolean active = false;

    void start(int v, int x1, int y1, int x2, int y2) {

        val = v;

        sx = x1;
        sy = y1;

        ex = x2;
        ey = y2;

        cur = 0;
        active = true;
    }

    boolean update() {

        cur++;

        if (cur >= steps)
            active = false;

        return active;
    }

    int getX() {
        return sx + (ex - sx) * cur / steps;
    }

    int getY() {
        return sy + (ey - sy) * cur / steps;
    }
}
