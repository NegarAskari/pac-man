package model;

import controller.BoardController;
import controller.GameController;

public class Game {
    private int score = 0;
    private Direction nextDirection = Direction.NONE;
    private Direction lastDirection = Direction.NONE;
    private int pacmanX = 11;
    private int pacmanY = 9;
    private int[] ghostXs = new int[4];
    private int[] ghostYs = new int[4];
    private int[] ghostStoppedRound = new int[4];
    private CellValue[][] cellValues;
    private int energyModeTimeCounter = -1;
    private int energyModeCounter = 1;
    private boolean isCounterReset = false;
    private CellValue[] underGhosts = new CellValue[4];
    private int lives;
    private int initialToEat;
    private int toEat = 0;
    private boolean isHardMode;
    private transient Board board;

    {
        for (int i = 0; i < 4; i++) {
            underGhosts[i] = CellValue.EMPTY;
            ghostStoppedRound[i] = 7;
        }
        ghostXs[0] = 1;
        ghostXs[1] = 19;
        ghostXs[2] = 1;
        ghostXs[3] = 19;
        ghostYs[0] = 1;
        ghostYs[1] = 1;
        ghostYs[2] = 17;
        ghostYs[3] = 17;

    }

    public Game(CellValue[][] cellValues, Board board, int lives, boolean isHardMode) {
        this.cellValues = cellValues;
        this.board = board;
        this.lives = lives;
        for (CellValue[] cells : cellValues)
            for (CellValue cell : cells)
                if (cell == CellValue.POINT || cell == CellValue.ENERGY_BOMB)
                    toEat++;
        initialToEat = toEat;
        this.isHardMode = isHardMode;
    }

    public int getLives() {
        return lives;
    }

    public boolean isHardMode() {
        return isHardMode;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public Direction getNextDirection() {
        return nextDirection;
    }

    public void setNextDirection(Direction nextDirection) {
        this.nextDirection = nextDirection;
    }

    public void setBoard(Board board) {
        this.board = board;
        board.update(cellValues, isOnEnergyMode());
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(Direction lastDirection) {
        this.lastDirection = lastDirection;
    }

    public int getGhostX(int ghostNum) {
        return ghostXs[ghostNum - 1];
    }

    public int getGhostY(int ghostNum) {
        return ghostYs[ghostNum - 1];
    }

    public int getPacmanX() {
        return pacmanX;
    }

    public int getPacmanY() {
        return pacmanY;
    }

    public CellValue getCell(int row, int column) {
        return cellValues[row][column];
    }

    public boolean isOnEnergyMode() {
        return energyModeTimeCounter != -1;
    }

    public void decreaseToEat() {
        toEat--;
        if (toEat == 0) resetBoard();
    }

    private void resetBoard() {
        GameController.playResetMap();
        toEat = initialToEat;
        lives++;
        lastDirection = Direction.NONE;
        nextDirection = Direction.NONE;
        resetPacman();
        resetGhost(CellValue.GHOST_1);
        resetGhost(CellValue.GHOST_2);
        resetGhost(CellValue.GHOST_3);
        resetGhost(CellValue.GHOST_4);
        for (int i = 0; i < 4; i++)
            ghostStoppedRound[i] = 7;
        cellValues = BoardController.readMaze("src/main/resources/view/mazes/chosen.txt");
        board.update(cellValues, isOnEnergyMode());
        GameController.updateLives();
    }

    public void decreaseEnergyModeCounter() {
        if (isCounterReset)
            energyModeTimeCounter = 10;
        else
            energyModeTimeCounter--;
        isCounterReset = false;
        if (energyModeTimeCounter == -1) {
            board.update(cellValues, isOnEnergyMode(), ghostYs[0], ghostXs[0], ghostYs[1], ghostXs[1]);
            board.update(cellValues, isOnEnergyMode(), ghostYs[2], ghostXs[2], ghostYs[3], ghostXs[3]);
            energyModeCounter = 1;
        }
    }

    public void setOnEnergyMode() {
        decreaseToEat();
        energyModeTimeCounter = 10;
        board.update(cellValues, isOnEnergyMode(), ghostYs[0], ghostXs[0], ghostYs[1], ghostXs[1]);
        board.update(cellValues, isOnEnergyMode(), ghostYs[2], ghostXs[2], ghostYs[3], ghostXs[3]);
    }

    public void resetCounter() {
        decreaseToEat();
        isCounterReset = true;
    }

    public int getEnergyModeTimeCounter() {
        return energyModeTimeCounter;
    }


    public void movePacman(Direction direction) {
        cellValues[pacmanY][pacmanX] = CellValue.EMPTY;
        int initialX = pacmanX;
        int initialY = pacmanY;
        switch (direction) {
            case UP:
                cellValues[pacmanY - 1][pacmanX] = CellValue.PACMAN_UP;
                pacmanY--;
                break;
            case DOWN:
                cellValues[pacmanY + 1][pacmanX] = CellValue.PACMAN_DOWN;
                pacmanY++;
                break;
            case RIGHT:
                cellValues[pacmanY][pacmanX + 1] = CellValue.PACMAN_RIGHT;
                pacmanX++;
                break;
            case LEFT:
                cellValues[pacmanY][pacmanX - 1] = CellValue.PACMAN_LEFT;
                pacmanX--;
        }
        board.update(cellValues, isOnEnergyMode(), pacmanY, pacmanX, initialY, initialX);
    }

    public void moveGhost(int ghostNum, Direction direction) {
        if (ghostStoppedRound[ghostNum - 1] != 0) {
            ghostStoppedRound[ghostNum - 1]--;
            return;
        }
        CellValue ghost = getGhost(ghostNum);
        int ghostY = ghostYs[ghostNum - 1];
        int ghostX = ghostXs[ghostNum - 1];
        cellValues[ghostY][ghostX] = underGhosts[ghostNum - 1];
        switch (direction) {
            case UP:
                ghostY--;
                break;
            case DOWN:
                ghostY++;
                break;
            case RIGHT:
                ghostX++;
                break;
            case LEFT:
                ghostX--;
        }
        underGhosts[ghostNum - 1] = cellValues[ghostY][ghostX];
        cellValues[ghostY][ghostX] = ghost;
        board.update(cellValues, isOnEnergyMode(), ghostY, ghostX, ghostYs[ghostNum - 1], ghostXs[ghostNum - 1]);
        ghostYs[ghostNum - 1] = ghostY;
        ghostXs[ghostNum - 1] = ghostX;
    }


    public CellValue getGhost(int ghostNum) {
        switch (ghostNum) {
            case 1:
                return CellValue.GHOST_1;
            case 2:
                return CellValue.GHOST_2;
            case 3:
                return CellValue.GHOST_3;
            case 4:
                return CellValue.GHOST_4;
        }
        return null;
    }


    private void resetGhost(CellValue ghost) {
        int x1, y1;
        int num = getGhostNum(ghost);
        x1 = ghostXs[num];
        y1 = ghostYs[num];
        cellValues[y1][x1] = underGhosts[num];
        underGhosts[num] = CellValue.EMPTY;
        Direction inCaseOfPacman = inCaseOfPacman = handleGhostReset(ghost);
        int ghostNum = getGhostNum(ghost);
        int x2 = ghostXs[ghostNum], y2 = ghostYs[ghostNum];
        if (cellValues[y2][x2] == CellValue.PACMAN_UP || cellValues[y2][x2] == CellValue.PACMAN_DOWN ||
                cellValues[y2][x2] == CellValue.PACMAN_RIGHT || cellValues[y2][x2] == CellValue.PACMAN_LEFT)
            movePacman(inCaseOfPacman);
        cellValues[y2][x2] = ghost;
        board.update(cellValues, isOnEnergyMode(), y1, x1, y2, x2);
    }


    private Direction handleGhostReset(CellValue ghost) {
        Direction inCaseOfPacman = Direction.NONE;
        switch (ghost) {
            case GHOST_1:
                if (cellValues[1][2] == CellValue.WALL) inCaseOfPacman = Direction.DOWN;
                else inCaseOfPacman = Direction.RIGHT;
                ghostYs[0] = ghostXs[0] = 1;
                break;
            case GHOST_2:
                if (cellValues[1][18] == CellValue.WALL) inCaseOfPacman = Direction.DOWN;
                else inCaseOfPacman = Direction.LEFT;
                ghostXs[1] = 19;
                ghostYs[1] = 1;
                break;
            case GHOST_3:
                if (cellValues[17][2] == CellValue.WALL) inCaseOfPacman = Direction.UP;
                else inCaseOfPacman = Direction.RIGHT;
                ghostXs[2] = 1;
                ghostYs[2] = 17;
                break;
            case GHOST_4:
                if (cellValues[17][18] == CellValue.WALL) inCaseOfPacman = Direction.UP;
                else inCaseOfPacman = Direction.LEFT;
                ghostXs[3] = 19;
                ghostYs[3] = 17;
        }
        return inCaseOfPacman;
    }


    private void resetPacman() {
        int x1 = pacmanX;
        int y1 = pacmanY;
        int x2 = pacmanX = 11;
        int y2 = pacmanY = 9;
        cellValues[y1][x1] = CellValue.EMPTY;
        cellValues[y2][x2] = CellValue.PACMAN_RIGHT;
        board.update(cellValues, isOnEnergyMode(), y1, x1, y2, x2);
    }


    public void eatPacman(CellValue ghost) {
        if (ghostStoppedRound[getGhostNum(ghost)] != 0) return;
        GameController.playEatPacman();
        lives--;
        resetPacman();
        resetGhost(CellValue.GHOST_1);
        resetGhost(CellValue.GHOST_2);
        resetGhost(CellValue.GHOST_3);
        resetGhost(CellValue.GHOST_4);
        for (int i = 0; i < 4; i++)
            ghostStoppedRound[i] = 1;
        nextDirection = Direction.NONE;
        lastDirection = Direction.NONE;
        GameController.updateLives();
    }

    private int getGhostNum(CellValue ghost) {
        switch (ghost) {
            case GHOST_1:
                return 0;
            case GHOST_2:
                return 1;
            case GHOST_3:
                return 2;
            case GHOST_4:
                return 3;
        }
        return 4;
    }

    public void eatGhost(CellValue ghost) {
        if (ghostStoppedRound[getGhostNum(ghost)] != 0) return;
        GameController.playEatGhost();
        ghostStoppedRound[getGhostNum(ghost)] = 17;
        resetGhost(ghost);
        score += 200 * energyModeCounter;
        GameController.updateScores();
        energyModeCounter++;
    }
}
