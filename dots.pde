/* 
 * Authors: Rodrigo Chamun, Diego Jornada
 */

import java.util.*;

public static int W = 500;
public static int H = 500;
public static int COLUMNS = 6;
public static int ROWS = 6;
public static int BALLDIAMETER = W / ROWS / 2;

Ball balls[][] = new Ball[ROWS][COLUMNS];
List<Ball> selectedBalls = new LinkedList<Ball>();

void setup() {
  size(W, H);
  createBalls();
}

void draw() {
  background(#ffffff);

  if (selectedBalls.size() != 0) {
    Ball b = selectedBalls.get(selectedBalls.size() - 1);
    strokeWeight(2);
    stroke(b.border);
    line(b.x, b.y, mouseX, mouseY);
  }

  for (int i = 0; i < selectedBalls.size() - 1; i++) {
    Ball a = selectedBalls.get(i);
    Ball b = selectedBalls.get(i + 1);
    strokeWeight(2);
    stroke(b.border);
    line(a.x, a.y, b.x, b.y);
  }

  for (int i = 0; i < balls[0].length; i++)
    for (Ball b: balls[i])
      if (b != null)
        b.draw();
}

color[] getRandomColor() {
  color fill[]   = { 
    #ff0000, #00ff00, #0000ff, #00ffff, #ffff00
  };
  color border[]   = { 
    #af0000, #00af00, #0000af, #00afff, #afff00
  };
  color ret[] = {
    0, 0
  };
  int n = (int) random(fill.length);
  ret[0] = fill[n];
  ret[1] = border[n];

  return ret;
}

void mousePressed() {
  // Comeca a uniao
  Ball b = getBallUnderMouse();
  if (b == null)
    return;

  b.select();
  selectedBalls.add(b);
}

boolean xor(boolean a, boolean b) {
  return (a && !b) || (!a && b);
}

void mouseDragged() {
  if (selectedBalls.size() == 0)
    return;
  Ball b = getBallUnderMouse();
  if (b == null)
    return;

  Ball lb = selectedBalls.get(selectedBalls.size() - 1);

  if (!b.isSelected() && b.isNeighbor(lb) && b.fill == lb.fill) {
    b.select();
    selectedBalls.add(b);
  }
}
void mouseReleased() {
  if (selectedBalls.size() == 1) {
    selectedBalls.get(0).unselect();
    selectedBalls.clear();
    return;
  }
  // Termina a uniao
  for (Ball b: selectedBalls) { /* temporario */
    int i = numToCol(b.x);
    int j = numToRow(b.y);
    balls[j][i] = null;
  }

  for (int i = 0; i < COLUMNS; i++) {
    List<Ball> columnBalls = new ArrayList<Ball>(ROWS);
    for (int j = ROWS - 1; j >= 0; j--) {
      if (balls[j][i] != null)
        columnBalls.add(balls[j][i]);
    }
    int columnIndex = ROWS - 1;
    for (int j = 0; j < columnBalls.size(); j++) {
      balls[columnIndex][i] = columnBalls.get(j);
      balls[columnIndex][i].y = columnIndex * (H / ROWS) + (H / ROWS) / 2; 
      columnIndex--;
    }
    while (columnIndex >= 0) {
      newBall(i, columnIndex);
      columnIndex--;
    }
  }
  selectedBalls.clear();
}

int numToCol(float x) { 
  return int(x / (W / COLUMNS));
}
int numToRow(float y) { 
  return int(y / (H / ROWS));
}

public Ball getBallUnderMouse() {
  int col = numToCol(mouseX);
  int row = numToRow(mouseY);
  if (col >= balls[0].length || row >= balls[0].length || row < 0 || col < 0)
    return null;
  Ball b = balls[row][col];
  if (b.inside(mouseX, mouseY))
    return b;
  return null;
}

void newBall(int i, int j) {
  int y = j * (H / ROWS) + (H / ROWS) / 2;
  int x = i * (W / COLUMNS) + (W / COLUMNS) / 2;
  color colors[] = getRandomColor();
  balls[j][i] = new Ball(x, y, colors[0], colors[1]);
}

void createBalls() {
  stroke(#000000);
  for (int j = 0; j < ROWS; j ++) {
    for (int i = 0; i < COLUMNS; i++) {
      newBall(i, j);
    }
  }
}

class Ball {
  float x, y;
  float d = BALLDIAMETER;
  color fill, border;
  boolean selected = false;

  public Ball (float x, float y, color fill, color border) { 
    this.x = x; 
    this.y = y; 
    this.fill = fill; 
    this.border = border;
  }

  public void select() { 
    selected = true;
  }
  public void unselect() { 
    selected = false;
  }

  public boolean inside(float px, float py) {
    PVector center = new PVector(x, y);
    PVector point  = new PVector(px, py);
    return PVector.sub(point, center).mag() <= d / 2;
  }

  public boolean isNeighbor(Ball b) {
    int myCol = numToCol(x);
    int myRow = numToRow(y);
    int bCol = numToCol(b.x);
    int bRow = numToRow(b.y);

    return xor(myCol == bCol, myRow == bRow) &&
      xor(abs(myCol - bCol) == 1, abs(myRow - bRow) == 1);
  }

  public boolean isSelected() { 
    return selected;
  }

  public void draw() { 
    noStroke();
    if (selected) {
      strokeWeight(4);
      stroke(border);
    }
    fill(fill);
    ellipse (x, y, d, d);
  }
}

