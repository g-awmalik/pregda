import java.awt.*;
import java.util.*;

public class Solution {

  // Add constants for particle types here.
  public static final int EMPTY = 0;
  public static final int METAL = 1;
  public static final int SAND = 2;
  public static final int WATER = 3;
  public static final int WOOD = 4;

  public static final String[] NAMES = {"Empty", "Metal", "Sand", "Water", "Wood"};

  public static final int DOWN = 0;
  public static final int RIGHT = 1;
  public static final int LEFT = 2;

  // Do not add any more fields as part of Lab 5.
  private int[][] grid;
  private SandDisplayInterface display;
  private RandomGenerator random;

  /**
   * Constructor.
   *
   * @param display The display to use for this run
   * @param random The random number generator to use to pick random points
   */
  public Solution(SandDisplayInterface display, RandomGenerator random) {
    this.display = display;
    this.random = random;

    //this.grid = new int[display.getNumColumns()][display.getNumRows()];
    this.grid = new int[display.getNumRows()][display.getNumColumns()];
  }

  /**
   * Called when the user clicks on a location.
   *
   * @param row
   * @param col
   * @param tool
   */
  private void locationClicked(int row, int col, int tool) {
    // TODO: Populate this method in step 3.
    //this.grid[col][row] = tool;
    this.grid[row][col] = tool;
  }

  /** Copies each element of grid into the display. */
  public void updateDisplay() {
    // TODO: Populate this method in step 4 and beyond.

    for (int i = 0; i < this.grid.length; i++) {
      for (int j = 0; j < this.grid[i].length; j++) {
        int particleType = this.grid[i][j];
        if (particleType == EMPTY) {
          this.display.setColor(i, j, Color.BLACK);
        } else if (particleType == METAL) {
          this.display.setColor(i, j, Color.GRAY);
        } else if (particleType == SAND) {
          this.display.setColor(i, j, Color.YELLOW);
        } else if (particleType == WATER) {
          this.display.setColor(i, j, Color.BLUE);
        } else if (particleType == WOOD) {
          this.display.setColor(i, j, new Color(153, 102, 0));
        }
      }
    }
  }

  /** Called repeatedly. Causes one random particle to maybe do something. */
  public void step() {
    // TODO: Populate this method in step 6 and beyond.
    Point randomPtObj = random.getRandomPoint();

    if (this.grid[randomPtObj.row][randomPtObj.column] == SAND) {
      if (canSandMoveDown(randomPtObj.row, randomPtObj.column)) {
        int particleBelow = this.grid[randomPtObj.row+1][randomPtObj.column];
        this.grid[randomPtObj.row][randomPtObj.column] = particleBelow;
        this.grid[randomPtObj.row+1][randomPtObj.column] = SAND;
      }
    } else if (this.grid[randomPtObj.row][randomPtObj.column] == WATER) {
      int direction = random.getRandomDirection();
      if (direction == DOWN &&
        canWaterMoveDown(randomPtObj.row, randomPtObj.column)) {
          int particleBelow = this.grid[randomPtObj.row+1][randomPtObj.column];
          this.grid[randomPtObj.row][randomPtObj.column] = particleBelow;
          this.grid[randomPtObj.row+1][randomPtObj.column] = WATER;
      } else if (direction == RIGHT &&
        canWaterMoveRight(randomPtObj.row, randomPtObj.column)) {
          this.grid[randomPtObj.row][randomPtObj.column] = EMPTY;
          this.grid[randomPtObj.row][randomPtObj.column+1] = WATER;
      } else if (direction == LEFT &&
        canWaterMoveLeft(randomPtObj.row, randomPtObj.column)) {
          this.grid[randomPtObj.row][randomPtObj.column] = EMPTY;
          this.grid[randomPtObj.row][randomPtObj.column-1] = WATER;
      }
    } else if (this.grid[randomPtObj.row][randomPtObj.column] == WOOD) {
      handleWoodParticles(randomPtObj);
    }
  }

  public void handleWoodParticles(Point randomPtObj) {
    if (canWoodMoveDown(randomPtObj.row, randomPtObj.column)) {
      this.grid[randomPtObj.row][randomPtObj.column] = EMPTY;
      this.grid[randomPtObj.row+1][randomPtObj.column] = WOOD;      
    }
  }

  public boolean canWoodMoveDown (int row, int column) {
    if (row+1 < this.display.getNumRows() &&
    this.grid[row+1][column] == EMPTY) {
      return true;
    }

    return false;
  }


  public boolean canSandMoveDown (int row, int column) {
    if (row+1 < this.display.getNumRows() &&
    (this.grid[row+1][column] == EMPTY ||
    this.grid[row+1][column] == WATER)) {
      return true;
    }

    return false;
  }

  public boolean canWaterMoveDown (int row, int column) {
    if (row+1 < this.display.getNumRows() &&
    (this.grid[row+1][column] == EMPTY ||
    this.grid[row+1][column] == WOOD)) {
      return true;
    }

    return false;
  }

  public boolean canWaterMoveLeft (int row, int column) {
    if (column-1 >= 0 &&
    this.grid[row][column-1] == EMPTY) {
      return true;
    }

    return false;
  }

  public boolean canWaterMoveRight (int row, int column) {
    if (column+1 < this.display.getNumColumns() &&
    this.grid[row][column+1] == EMPTY) {
      return true;
    }

    return false;
  }

  /********************************************************************/
  /********************************************************************/
  /**
   * DO NOT MODIFY
   *
   * <p>The rest of this file is UI and testing infrastructure. Do not modify as part of pre-GDA Lab
   * 5.
   */
  /********************************************************************/
  /********************************************************************/

  private static class Point {
    private int row;
    private int column;

    public Point(int row, int column) {
      this.row = row;
      this.column = column;
    }

    public int getRow() {
      return row;
    }

    public int getColumn() {
      return column;
    }
  }

  /**
   * Special random number generating class to help get consistent results for testing.
   *
   * <p>Please use getRandomPoint to get an arbitrary point on the grid to evaluate.
   *
   * <p>When dealing with water, please use getRandomDirection.
   */
  public static class RandomGenerator {
    private static Random randomNumberGeneratorForPoints;
    private static Random randomNumberGeneratorForDirections;
    private int numRows;
    private int numCols;

    public RandomGenerator(int seed, int numRows, int numCols) {
      randomNumberGeneratorForPoints = new Random(seed);
      randomNumberGeneratorForDirections = new Random(seed);
      this.numRows = numRows;
      this.numCols = numCols;
    }

    public RandomGenerator(int numRows, int numCols) {
      randomNumberGeneratorForPoints = new Random();
      randomNumberGeneratorForDirections = new Random();
      this.numRows = numRows;
      this.numCols = numCols;
    }

    public Point getRandomPoint() {
      return new Point(
          randomNumberGeneratorForPoints.nextInt(numRows),
          randomNumberGeneratorForPoints.nextInt(numCols));
    }

    /**
     * Method that returns a random direction.
     *
     * @return an int indicating the direction of movement: 0: Indicating the water should attempt
     *     to move down 1: Indicating the water should attempt to move right 2: Indicating the water
     *     should attempt to move left
     */
    public int getRandomDirection() {
      return randomNumberGeneratorForDirections.nextInt(3);
    }
  }

  public static void main(String[] args) {
    // Test mode, read the input, run the simulation and print the result
    Scanner in = new Scanner(System.in);
    int numRows = in.nextInt();
    int numCols = in.nextInt();
    int iterations = in.nextInt();
    Solution lab =
        new Solution(new NullDisplay(numRows, numCols), new RandomGenerator(0, numRows, numCols));
    lab.readGridValues(in);
    lab.runNTimes(iterations);
    lab.printGrid();
  }

  /**
   * Read a grid set up from the input scanner.
   *
   * @param in
   */
  private void readGridValues(Scanner in) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        grid[i][j] = in.nextInt();
      }
    }
  }

  /** Output the current status of the grid for testing purposes. */
  private void printGrid() {
    for (int i = 0; i < grid.length; i++) {
      System.out.println(Arrays.toString(grid[i]));
    }
  }

  /** Runner that advances the display a determinate number of times. */
  private void runNTimes(int times) {
    for (int i = 0; i < times; i++) {
      runOneTime();
    }
  }

  /** Runner that controls the window until it is closed. */
  public void run() {
    while (true) {
      runOneTime();
    }
  }

  /**
   * Runs one iteration of the display. Note that one iteration may call step repeatedly depending
   * on the speed of the UI.
   */
  private void runOneTime() {
    for (int i = 0; i < display.getSpeed(); i++) {
      step();
    }
    updateDisplay();
    display.repaint();
    display.pause(1); // Wait for redrawing and for mouse
    int[] mouseLoc = display.getMouseLocation();
    if (mouseLoc != null) { // Test if mouse clicked
      locationClicked(mouseLoc[0], mouseLoc[1], display.getTool());
    }
  }

  /**
   * An implementation of the SandDisplayInterface that doesn't display anything. Used for testing.
   */
  static class NullDisplay implements SandDisplayInterface {
    private int numRows;
    private int numCols;

    public NullDisplay(int numRows, int numCols) {
      this.numRows = numRows;
      this.numCols = numCols;
    }

    public void pause(int milliseconds) {}

    public int getNumRows() {
      return numRows;
    }

    public int getNumColumns() {
      return numCols;
    }

    public int[] getMouseLocation() {
      return null;
    }

    public int getTool() {
      return 0;
    }

    public void setColor(int row, int col, Color color) {}

    public int getSpeed() {
      return 1;
    }

    public void repaint() {}
  }

  /** Interface for the UI of the SandLab. */
  public interface SandDisplayInterface {
    public void repaint();

    public void pause(int milliseconds);

    public int[] getMouseLocation();

    public int getNumRows();

    public int getNumColumns();

    public void setColor(int row, int col, Color color);

    public int getSpeed();

    public int getTool();
  }
}
