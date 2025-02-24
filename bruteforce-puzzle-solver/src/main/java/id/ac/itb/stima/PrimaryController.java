package id.ac.itb.stima;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class PrimaryController {
    
    @FXML private Label fileLabel;
    @FXML private GridPane gridPane;
    @FXML private Label timeLabel;
    @FXML private Label countLabel;
    @FXML private Label timeShow;
    @FXML private Label countShow;
    @FXML private Button fileChooserButton;
    @FXML private Button saveTxtButton;
    @FXML private Button saveImageButton;
    @FXML private Label solutionFoundLabel;

    @FXML private TextField textFieldTxt;
    @FXML private TextField textFieldImage;

    private File selectedFile;
    char[][] gridFinal;
    long startTime;
    private static final int CELL_SIZE = 30;


    @FXML
    private void showTime(double miliseconds){
        String text = String.valueOf(miliseconds).concat(" ms");
        timeShow.setText(text);
    }

    @FXML
    private void showCount(String count){
        String text = count.concat(" attempts");
        countShow.setText(String.valueOf(text));
    }

    @FXML
    private void chooseFile() {
        solutionFoundLabel.setVisible(false);
        solutionFoundLabel.setManaged(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Input File");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) fileChooserButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileLabel.setText("Selected file: " + selectedFile.getName());
            readFileContent();
        } else {
            fileLabel.setText("No file selected");
        }
    }
    
    @FXML
    private void saveTxt() {
        String filePathBase = "output/txt/"; 
        String filename = textFieldTxt.getText().trim(); 
        if (filename.isEmpty()) {
            filename = "default_output.txt"; 
        }
    
        File directory = new File(filePathBase);
        if (!directory.exists()) {
            directory.mkdirs(); 
        }
    
        String filePath = filePathBase + filename; 
    
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            displayBlockToFile(gridFinal, writer);
            System.out.println("File saved successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving file: " + filePath);
        }
    }
    
    
    @FXML
    private void saveImage(){
        String filePathBase = "output/images/";
        String filename = textFieldImage.getText().trim(); 
        if (filename.isEmpty()) {
            filename = "default_output.png"; 
        } else if (!filename.endsWith(".png")) {
            filename += ".png"; 
        }

        File directory = new File(filePathBase);
        if (!directory.exists()) {
            directory.mkdirs(); 
        }
    
        File outputFile = new File(filePathBase + filename);
        int width = CELL_SIZE * gridFinal[0].length;
        int height = CELL_SIZE * gridFinal.length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        for (int i = 0; i < gridFinal.length; i++) {
            for (int j = 0; j < gridFinal[0].length; j++) {
                char cellChar = gridFinal[i][j];
                java.awt.Color color = AwtColorMapping.getAwtColor(cellChar);
                g.setColor(color);
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g.setColor(java.awt.Color.BLACK);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
        String filePath = filePathBase + filename; 

        g.dispose();

        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("Image saved successfully at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving image!");
        }
    }

    private void updateGridDisplay(char[][] grid, String count) {
        gridPane.getChildren().clear(); 
        int rows = grid.length;
        int cols = grid[0].length;
        countShow.setText(count.concat(" attempts"));
        long currentTime = System.nanoTime(); 
        double miliseconds = (currentTime - startTime) / 1_000_000;
        timeShow.setText(String.valueOf(miliseconds).concat(" ms"));
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                javafx.scene.paint.Color color = JavaFXColorMapping.getFXColor(cell);
                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
                rect.setFill(color);
                rect.setStroke(javafx.scene.paint.Color.BLACK);
                gridPane.add(rect, j, i);
            }
        }
    }
    
    private void displayBlockToFile(char[][] grid, PrintWriter writer) {
        for (char[] row : grid) {
            for (char cell : row) {
                writer.print(cell == '\0' ? '.' : cell); 
            }
            writer.println();
        }
    }

    private void readFileContent() {
        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            
            String line = br.readLine().replaceAll("\\s+$", "");
            int[] firstline = parseFirstLine(line);
            line = br.readLine().replaceAll("\\s+$", "");
            String secondline = line;
            line = br.readLine().replaceAll("\\s+$", "");

            List<List<char[][]>> listOfTransformedBlocks = new ArrayList<>();

            if (!"DEFAULT".equals(secondline)) return;

            for (int index = 0; index < firstline[2]; index++) {
                List<String> tempList = new ArrayList<>();
                int maxRow = 0;
                int maxCol = 0;
                char currentChar = getCurrentChar(line);

                while (line != null && currentChar == getCurrentChar(line)) {
                    tempList.add(line);
                    maxRow++;
                    if (line.length() > maxCol) {
                        maxCol = line.length();
                    }

                    line = br.readLine();
                    if (line != null) {
                        line = line.replaceAll("\\s+$", "");
                    }
                }

                char[][] block = new char[maxRow][maxCol];
                for (int i = 0; i < maxRow; i++) {
                    char[] rowChars = tempList.get(i).toCharArray();
                    for (int j = 0; j < maxCol; j++) {
                        if (j < rowChars.length) {
                            block[i][j] = (rowChars[j] == ' ') ? '\0' : rowChars[j]; 
                        } else {
                            block[i][j] = '\0';
                        }
                    }
                }

                List<char[][]> transformedBlocks = generateBlockVariants(block);
                listOfTransformedBlocks.add(transformedBlocks);
            }


           
            int N = firstline[0];
            int M = firstline[1];
            int P = firstline[2];
            char[][] grid = new char[N][M];
            displayListOfBlocks(listOfTransformedBlocks);
            long zero = 0L;
            long[] count = {zero};

            List<char[][]> gridStack = new ArrayList<>();
            gridStack.add(copyGrid(grid));
            
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    startTime = System.nanoTime();
                    boolean success = backtrack(gridStack, listOfTransformedBlocks, N, M, P, count);
                    long endTime = System.nanoTime(); 
                    double miliseconds = (endTime - startTime) / 1_000_000;
                    System.out.printf("Execution Time: %.0f ms%n", miliseconds);
                    
                    
                    Platform.runLater(() -> {
                        if (success){
                            System.out.println("PUZZLE SUCCESSFULLY SOLVED");
                            System.out.println("Total Backtrack Count: " + String.valueOf(count[0]));
                            
                            showCount(String.valueOf(count[0]));
                            showTime(miliseconds);
                            gridFinal = gridStack.get(gridStack.size()-1);
                            displayBlock(gridFinal);
                        } else {
                            showCount(String.valueOf(count[0]));
                            showTime(miliseconds);
                            solutionFoundLabel.setVisible(true);
                            solutionFoundLabel.setManaged(true);
                            System.out.println("NO SOLUTION FOUND");
                            System.out.println("Total Backtrack Count: " + String.valueOf(count[0]));
                        }
                    });
                    return null;
                }
            };

            new Thread(task).start();  
                 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<char[][]> generateBlockVariants(char[][] currentBlock) {
        List<char[][]> variants = new ArrayList<>();
        
        char[][] rotated0 = currentBlock;
        char[][] rotated90 = rotateBlock90(rotated0);
        char[][] rotated180 = rotateBlock90(rotated90);
        char[][] rotated270 = rotateBlock90(rotated180);

        variants.add(rotated0);
        variants.add(rotated90);
        variants.add(rotated180);
        variants.add(rotated270);

        char[][] mirrored0 = mirrorBlock(rotated0);
        char[][] mirrored90 = mirrorBlock(rotated90);
        char[][] mirrored180 = mirrorBlock(rotated180);
        char[][] mirrored270 = mirrorBlock(rotated270);

        variants.add(mirrored0);
        variants.add(mirrored90);
        variants.add(mirrored180);
        variants.add(mirrored270);

        return variants;
    }

    private boolean backtrack(List<char[][]> gridStack, List<List<char[][]>> listOfTransformedBlocks, int N, int M, int P, long[] count) {
        char[][] grid = gridStack.get(gridStack.size() - 1);
    
        if (listOfTransformedBlocks.isEmpty()) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (grid[i][j] == '\0') {
                        return false;
                    }
                }
            }
            Platform.runLater(() -> updateGridDisplay(grid,String.valueOf(count[0])));
            return true;
        } else {
            List<char[][]> transformedBlocks = listOfTransformedBlocks.get(0);
            for (int gridX = 0; gridX < N; gridX++) {
                for (int gridY = 0; gridY < M; gridY++) {
                    for (char[][] transformedBlock : transformedBlocks) {
                        char[][] newGrid = copyGrid(grid);
                        gridStack.add(newGrid);
                        boolean isValid = checkPosition(newGrid, transformedBlock, gridX, gridY, N, M);
                        if (isValid) {
                            count[0]++;
                            if (count[0]%521 == 0 || count[0] == 1){
                                Platform.runLater(() -> updateGridDisplay(newGrid,String.valueOf(count[0])));
                            }
                            List<List<char[][]>> tail = new ArrayList<>(listOfTransformedBlocks.subList(1, listOfTransformedBlocks.size()));
                            boolean success = backtrack(gridStack, tail, N, M, P, count);
                            if (success) return true;
                            gridStack.remove(gridStack.size() - 1);
                        } else {
                            gridStack.remove(gridStack.size() - 1);
                        }
                    }
                }
            }
            return false;
        }
    }

    public static char[][] copyGrid(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        char[][] newGrid = new char[rows][cols];
    
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, newGrid[i], 0, cols);
        }
    
        return newGrid;
    }

    private boolean checkPosition(char[][] grid, char[][] currentBlock, int gridX, int gridY, int N, int M){
        for (int i = 0; i < currentBlock.length; i++) {
            for (int j = 0; j < currentBlock[0].length; j++) {
                if (currentBlock[i][j] == '\0') continue;
                int positionInGridX = gridX + i;
                int positionInGridY = gridY + j;
                if (positionInGridX < 0 || positionInGridY < 0 || positionInGridX >= N || positionInGridY >= M) return false;
                if (grid[positionInGridX][positionInGridY] != '\0') return false;
                grid[positionInGridX][positionInGridY] = currentBlock[i][j];
            }
        }
       
        return true;
    }

    private char getCurrentChar(String line){
        if (line == null) return ' ';
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' '){
                return line.charAt(i);
            }
        }
        return ' ';
    }

    private static char[][] mirrorBlock(char[][] block) {
        int rows = block.length;
        int cols = block[0].length;
        char[][] mirrored = new char[rows][cols];
    
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mirrored[i][j] = block[rows - 1 - i][j];
            }
        }
        return mirrored;
    }

    private static char[][] rotateBlock90(char[][] block) {
        int rows = block.length;
        int cols = block[0].length;
        char[][] rotated = new char[cols][rows]; 
    
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = block[i][j];
            }
        }
        return rotated;
    }
   
    private void displayListOfBlocks(List<List<char[][]>> listOfTransformedBlocks) {
        for (int index = 0; index < listOfTransformedBlocks.size(); index++) {
            System.out.println("Block " + (index + 1) + ":");
            
            if (!listOfTransformedBlocks.get(index).isEmpty()) {
                displayBlock(listOfTransformedBlocks.get(index).get(0));
            } else {
                System.out.println("(No valid transformation found)");
            }
    
            System.out.println();
        }
    }
    
    private void displayBlock(char[][] block) {
        for (char[] row : block) {
            for (char cell : row) {
                if (cell == '\0') {
                    System.out.print(". "); 
                } else {
                    System.out.print(cell + " ");
                }
            }
            System.out.println();
        }
    }

    private int[] parseFirstLine(String line){
        String[] tokens = line.split("\\s+");
        int[] parsed = new int[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
           parsed[i] = Integer.parseInt(tokens[i]);

        }
        return parsed;
    }

    @FXML
    private void initialize() {
        solutionFoundLabel.setVisible(false);
        solutionFoundLabel.setManaged(false);  // Remove label from layout
    }
   
}
