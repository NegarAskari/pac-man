package model;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.CellValue;

public class Board extends Group {
    public final static double CELL_WIDTH = 20.0;
    private ImageView[][] images = new ImageView[19][21];
    private Image pacmanRight;
    private Image pacmanUp;
    private Image pacmanDown;
    private Image pacmanLeft;
    private Image ghost1;
    private Image ghost2;
    private Image ghost3;
    private Image ghost4;
    private Image blueGhost;
    private Image wall;
    private Image energyBomb;
    private Image point;

    public Board() {
        loadImages();
        initializeBoard();
    }

    private void loadImages() {
        pacmanRight = new Image(getClass().getResourceAsStream("images/pacmanRight.gif"));
        pacmanUp = new Image(getClass().getResourceAsStream("images/pacmanUp.gif"));
        pacmanDown = new Image(getClass().getResourceAsStream("images/pacmanDown.gif"));
        pacmanLeft = new Image(getClass().getResourceAsStream("images/pacmanLeft.gif"));
        ghost1 = new Image(getClass().getResourceAsStream("images/ghost1.gif"));
        ghost2 = new Image(getClass().getResourceAsStream("images/ghost2.gif"));
        ghost3 = new Image(getClass().getResourceAsStream("images/ghost3.gif"));
        ghost4 = new Image(getClass().getResourceAsStream("images/ghost4.gif"));
        blueGhost = new Image(getClass().getResourceAsStream("images/blueghost.gif"));
        wall = new Image(getClass().getResourceAsStream("images/wall.png"));
        energyBomb = new Image(getClass().getResourceAsStream("images/energyBomb.png"));
        point = new Image(getClass().getResourceAsStream("images/point.png"));
    }

    private void initializeBoard() {
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 21; column++) {
                ImageView imageView = new ImageView();
                imageView.setX((double) column * CELL_WIDTH);
                imageView.setY((double) row * CELL_WIDTH);
                imageView.setFitWidth(CELL_WIDTH);
                imageView.setFitHeight(CELL_WIDTH);
                images[row][column] = imageView;
                this.getChildren().add(imageView);
            }
        }
    }

    public void update(CellValue[][] cellValues, boolean isOnEnergyMode) {
        for (int row = 0; row < 19; row++)
            for (int column = 0; column < 21; column++)
                images[row][column].setImage(getImage(cellValues[row][column], isOnEnergyMode));
    }

    public void update(CellValue[][] cellValues, boolean isOnEnergyMode, int row1, int column1, int row2, int column2) {
        images[row1][column1].setImage(getImage(cellValues[row1][column1], isOnEnergyMode));
        images[row2][column2].setImage(getImage(cellValues[row2][column2], isOnEnergyMode));
    }

    private Image getImage(CellValue cellValue, boolean isOnEnergyMode) {
        switch (cellValue) {
            case GHOST_1:
                if (isOnEnergyMode) return blueGhost;
                return ghost1;
            case GHOST_2:
                if (isOnEnergyMode) return blueGhost;
                return ghost2;
            case GHOST_3:
                if (isOnEnergyMode) return blueGhost;
                return ghost3;
            case GHOST_4:
                if (isOnEnergyMode) return blueGhost;
                return ghost4;
            case PACMAN_UP:
                return pacmanUp;
            case PACMAN_DOWN:
                return pacmanDown;
            case PACMAN_LEFT:
                return pacmanLeft;
            case PACMAN_RIGHT:
                return pacmanRight;
            case WALL:
                return wall;
            case POINT:
                return point;
            case ENERGY_BOMB:
                return energyBomb;
            case EMPTY:
                return null;
        }
        return null;
    }
}
