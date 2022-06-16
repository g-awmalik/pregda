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
  //private int[][] grid;
  private Particle[][] grid;
  private SandDisplayInterface display;
  private RandomGenerator random;
  private Color[] sandTints;

  /**
   * Constructor.
   *
   * @param display The display to use for this run
   * @param random The random number generator to use to pick random points
   */
  public Solution(SandDisplayInterface display, RandomGenerator random) {
    this.display = display;
    this.random = random;
    this.grid = new Particle[display.getNumRows()][display.getNumColumns()];

    for (int i = 0; i < this.grid.length; i++) {
      for (int j = 0; j < this.grid[i].length; j++) {
        this.grid[i][j] = new Particle(EMPTY);
      }
    }

    generateSandTints();
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

    Particle particleType = new Particle(tool);
    this.grid[row][col] = particleType;
  }

  /** Copies each element of grid into the display. */
  public void updateDisplay() {
    // TODO: Populate this method in step 4 and beyond.

    for (int i = 0; i < this.grid.length; i++) {
      for (int j = 0; j < this.grid[i].length; j++) {
        Particle particleObj = this.grid[i][j];

        if (particleObj.getType() == EMPTY) {
          this.display.setColor(i, j, Color.BLACK);
        } else if (particleObj.getType() == METAL) {
          this.display.setColor(i, j, Color.GRAY);
        } else if (particleObj.getType() == SAND) {
          Color sandTint = particleObj.getTint();
          if (sandTint == null) {
            sandTint = getRandomSandTint();
            particleObj.setTint(sandTint);
          }
          //this.display.setColor(i, j, new Color(153, 102, 0));
          this.display.setColor(i, j, sandTint);
        } else if (particleObj.getType() == WATER) {
          this.display.setColor(i, j, Color.BLUE);
        } else if (particleObj.getType() == WOOD) {          
          this.display.setColor(i, j, new Color(153, 102, 0));
        }
      }
    }
  }

  private Color getRandomSandTint() {
    Random rand = new Random();
    int upperbound = 4;
    int int_random = rand.nextInt(upperbound);

    return sandTints[int_random];
  }

  private void generateSandTints() {
    this.sandTints = new Color[5];
    this.sandTints[0] = new Color(168, 102, 50);
    this.sandTints[1] = new Color(168, 127, 50);
    this.sandTints[2] = new Color(168, 156, 50);
    this.sandTints[3] = new Color(115, 104, 13);
    this.sandTints[4] = new Color(227, 191, 113);
  }

  /** Called repeatedly. Causes one random particle to maybe do something. */
  public void step() {
    // TODO: Populate this method in step 6 and beyond.
    Point randomPtObj = random.getRandomPoint();
    int direction = random.getRandomDirection();

    if (this.grid[randomPtObj.row][randomPtObj.column].getType() == SAND) {
      Color currentSandTint = this.grid[randomPtObj.row][randomPtObj.column].getTint();
      if (canSandMoveDown(randomPtObj.row, randomPtObj.column)) {
        int particleBelow = this.grid[randomPtObj.row+1][randomPtObj.column].getType();
        this.grid[randomPtObj.row][randomPtObj.column].setType(particleBelow);
        this.grid[randomPtObj.row+1][randomPtObj.column].setType(SAND);
        this.grid[randomPtObj.row+1][randomPtObj.column].setTint(currentSandTint);
      } else {
        if (direction == LEFT &&
          canSandSagLeft(randomPtObj.row, randomPtObj.column)) {
            int particleLeft = this.grid[randomPtObj.row][randomPtObj.column-1].getType();
            this.grid[randomPtObj.row][randomPtObj.column].setType(particleLeft);
            this.grid[randomPtObj.row][randomPtObj.column-1].setType(SAND);
            this.grid[randomPtObj.row][randomPtObj.column-1].setTint(currentSandTint);
        } else if (direction == RIGHT &&
          canSandSagRight(randomPtObj.row, randomPtObj.column)) {
            int particleRight = this.grid[randomPtObj.row][randomPtObj.column+1].getType();
            this.grid[randomPtObj.row][randomPtObj.column].setType(particleRight);
            this.grid[randomPtObj.row][randomPtObj.column+1].setType(SAND);
            this.grid[randomPtObj.row][randomPtObj.column+1].setTint(currentSandTint);
        }
      }
    } else if (this.grid[randomPtObj.row][randomPtObj.column].getType() == WATER) {
      if (direction == DOWN &&
        canWaterMoveDown(randomPtObj.row, randomPtObj.column)) {
          int particleBelow = this.grid[randomPtObj.row+1][randomPtObj.column].getType();
          this.grid[randomPtObj.row][randomPtObj.column].setType(particleBelow);
          this.grid[randomPtObj.row+1][randomPtObj.column].setType(WATER);
      } else if (direction == RIGHT &&
        canWaterMoveRight(randomPtObj.row, randomPtObj.column)) {
          this.grid[randomPtObj.row][randomPtObj.column].setType(EMPTY);
          this.grid[randomPtObj.row][randomPtObj.column+1].setType(WATER);

      } else if (direction == LEFT &&
        canWaterMoveLeft(randomPtObj.row, randomPtObj.column)) {
          this.grid[randomPtObj.row][randomPtObj.column].setType(EMPTY);
          this.grid[randomPtObj.row][randomPtObj.column-1].setType(WATER);
      } //else if (this.grid[randomPtObj.row - 1][randomPtObj.column].getType() == METAL){
        /////this.grid[randomPtObj.row][randomPtObj.column].setType(EMPTY);
      //} //Add this as a

    } else if (this.grid[randomPtObj.row][randomPtObj.column].getType() == WOOD) {
      handleWoodParticles(randomPtObj);
    } else if (this.grid[randomPtObj.row][randomPtObj.column].getType() == METAL){
      // Check Right and adjust the water level
      if(this.grid[randomPtObj.row][randomPtObj.column+1].getType() == WATER && this.grid[randomPtObj.row][randomPtObj.column-1].getType() == EMPTY){
        //System.out.print("FOUND WATER ON RIGHT");
        this.grid[randomPtObj.row][randomPtObj.column+1].setType(EMPTY);
        this.grid[randomPtObj.row][randomPtObj.column-1].setType(WATER);
      } 
      // Check left and adjust the water level
      else if(this.grid[randomPtObj.row][randomPtObj.column-1].getType() == WATER && this.grid[randomPtObj.row][randomPtObj.column+1].getType() == EMPTY){
        //System.out.print("FOUND WATER ON LEFT");
        this.grid[randomPtObj.row][randomPtObj.column-1].setType(EMPTY);
        this.grid[randomPtObj.row][randomPtObj.column+1].setType(WATER);
      }
      else if(this.grid[randomPtObj.row+1][randomPtObj.column+1].getType() == WATER){
        this.grid[randomPtObj.row-1][randomPtObj.column].setType(EMPTY);
      } 
        //this.grid[randomPtObj.row][randomPtObj.column].setType(EMPTY);
    }
    
  } 

  public void handleWoodParticles(Point randomPtObj) {
    if (canWoodMoveDown(randomPtObj.row, randomPtObj.column)) {
      this.grid[randomPtObj.row][randomPtObj.column].setType(EMPTY);
      this.grid[randomPtObj.row+1][randomPtObj.column].setType(WOOD);
    }
  }

  public boolean canWoodMoveDown (int row, int column) {
    if (row+1 < this.display.getNumRows() &&
    this.grid[row+1][column].getType() == EMPTY) {
      return true;
    } else {

    return false;
    }
  }

  // sand will sag only if there are atleast two pixel empty or watery
  // to the left or right of it
  // some comments to demostrate branching commits
  public boolean canSandSagLeft (int row, int column) {
    if (column == 0 ||
    row >= this.display.getNumRows() - 2) {
      return false;
    }

    if ((this.grid[row+1][column-1].getType() == EMPTY ||
    this.grid[row+1][column-1].getType() == WATER) &&
    (this.grid[row+2][column-1].getType() == EMPTY ||
    this.grid[row+2][column-1].getType() == WATER)) {
      return true;
    }

    return false;
  }

  public boolean canSandSagRight (int row, int column) {
    if (column == this.display.getNumColumns() - 1 ||
    row >= this.display.getNumRows() - 2) {
      return false;
    }

    if ((this.grid[row+1][column+1].getType() == EMPTY ||
    this.grid[row+1][column+1].getType() == WATER) && 
    (this.grid[row+2][column+1].getType() == EMPTY ||
    this.grid[row+2][column+1].getType() == WATER)) {
      return true;
    }

    return false;
  }


  public boolean canSandMoveDown (int row, int column) {
    if (row+1 < this.display.getNumRows() &&
    (this.grid[row+1][column].getType() == EMPTY ||
    this.grid[row+1][column].getType() == WATER)) {
      return true;
    }

    return false;
  }

  public boolean canWaterMoveDown (int row, int column) {
    if (row+1 < this.display.getNumRows() &&
    (this.grid[row+1][column].getType() == EMPTY ||
    this.grid[row+1][column].getType() == WOOD)) {
      return true;
    }

    return false;
  }

  public boolean canWaterMoveLeft (int row, int column) {
    if (column-1 >= 0 &&
    this.grid[row][column-1].getType() == EMPTY) {
      return true;
    }

    return false;
  }

  public boolean canWaterMoveRight (int row, int column) {
    if (column+1 < this.display.getNumColumns() &&
    this.grid[row][column+1].getType() == EMPTY) {
      return true;
    }

    return false;
  }

  // 
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

  private static class Particle {
    private int type;
    private Color tint;

    public Particle(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }
    public Color getTint() {
      return tint;
    }
    public void setType(int type) {
      this.type = type;
    }
    public void setTint(Color tint) {
      this.tint = tint;
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
        grid[i][j].setType(in.nextInt());
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