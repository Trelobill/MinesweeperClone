import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedWriter;
import java.io.*;
import java.nio.file.*;

public class Minesweeper extends JFrame implements ActionListener, MouseListener {
    private JButton smiley = new JButton();
    private JButton[][] button;

    private JPanel composite = new JPanel();
    private JPanel topPanel = new JPanel();
    private JPanel minespan = null;

    private ImageIcon smileyImageIcon = null;
    private ImageIcon coveredImageIcon = null;
    private ImageIcon wasmineImageIcon = null;
    private ImageIcon lossImageIcon = null;
    private ImageIcon cryImageIcon = null;
    private ImageIcon oneImageIcon = null;
    private ImageIcon twoImageIcon = null;
    private ImageIcon threeImageIcon = null;
    private ImageIcon fourImageIcon = null;
    private ImageIcon fiveImageIcon = null;
    private ImageIcon sixImageIcon = null;
    private ImageIcon sevenImageIcon = null;
    private ImageIcon eightImageIcon = null;
    private ImageIcon flagImageIcon = null;
    private ImageIcon winImageIcon = null;
    private ImageIcon wasnotmineImageIcon = null;
    private ImageIcon blankImageIcon = null;
    private ImageIcon wowImageIcon = null;
    private ImageIcon displayZero = null;
    private ImageIcon displayOne = null;
    private ImageIcon displayTwo = null;
    private ImageIcon displayThree = null;
    private ImageIcon displayFour = null;
    private ImageIcon displayFive = null;
    private ImageIcon displaySix = null;
    private ImageIcon displaySeven = null;
    private ImageIcon displayEight = null;
    private ImageIcon displayNine = null;
    private ImageIcon negative = null;

    private final JLabel timerHundreds = new JLabel(displayZero);
    private final JLabel timerTens = new JLabel(displayZero);
    private final JLabel timerOnes = new JLabel(displayZero);
    private final JLabel mineHundreds = new JLabel(displayZero);
    private final JLabel mineTens = new JLabel(displayZero);
    private final JLabel mineOnes = new JLabel(displayZero);
    private JLabel liveslabel;

    private Timer mineTimer = new Timer(true);

    private int width = 9;
    private int height = 9;
    private int mines = 10;
    private int seconds = 0;
    private int nFlags;
    private int rndmines[];
    private int[][] mineArray;
    private int lives = 1;
    private int lastSavedLives = lives;

    private Object[] options = {"Reset Scores", "Ok"};

    private File scoresFile;

    private final String path = "ScoresFile.txt";

    private boolean timerStarted = false;
    private boolean inprogress; // flag whether game is in progress or not
    private boolean isLeftPressed;
    private boolean isRightPressed;
    private boolean bothPressed;
    private boolean loseFromDoublePress = false;
    private boolean firstClick = true;

    public Minesweeper() {
        super("MineSweeper");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setSize(800, 600);
        setJMenuBar(createMenuBar());
        loadImages();

        composite.setLayout(new BorderLayout());
        smiley.setIcon(smileyImageIcon);
        smiley.setPreferredSize(new Dimension(25, 25));
        smiley.addActionListener(e -> startNewGame());

        nFlags = mines;
        final JPanel timer = new JPanel();
        final JPanel minesCount = new JPanel();

        timer.setLayout(new BorderLayout());
        timer.add(timerHundreds, BorderLayout.WEST);
        timer.add(timerTens, BorderLayout.CENTER);
        timer.add(timerOnes, BorderLayout.EAST);

        minesCount.setLayout(new BorderLayout());
        minesCount.add(mineHundreds, BorderLayout.WEST);
        minesCount.add(mineTens, BorderLayout.CENTER);
        minesCount.add(mineOnes, BorderLayout.EAST);

        updateMineCount();
        updateTimer();
        FlowLayout fl = new FlowLayout();
        fl.setVgap(2);
        JPanel flfs = new JPanel(fl);
        flfs.add(smiley);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(timer, BorderLayout.EAST);
        topPanel.add(flfs, BorderLayout.CENTER);
        topPanel.add(minesCount, BorderLayout.WEST);
        topPanel.setPreferredSize(new Dimension(50, 30));

        composite.add(topPanel, BorderLayout.NORTH);
        composite.add(new JSeparator(), BorderLayout.CENTER);
        if (lives > 1) {
            liveslabel = new JLabel("<html><h1 style='margin: 0;color: green'>" + lives + " lives</h1></html>");
        } else {
            liveslabel = new JLabel("<html><h1 style='margin: 0;color: green'>" + lives + " life</h1></html>");
        }
        composite.add(liveslabel, BorderLayout.SOUTH);
        smiley.addActionListener(this);
        smiley.addMouseListener(this);
        arrangeButtons();
        createScoresFile();
        add(composite, BorderLayout.CENTER);
        inprogress = true;
        validate();
        pack();
    }

    private void createScoresFile() {
        try {
            scoresFile = new File(path);
            if (scoresFile.createNewFile()) {
                System.out.println("File created: " + scoresFile.getName());
                Files.write(Paths.get(path), "-\n-\n-".getBytes(), StandardOpenOption.APPEND);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void resetDifficulty(int newWidth, int newHeight, int newMines) {
        width = newWidth;
        height = newHeight;
        mines = newMines;
        startNewGame();
    }

    private void startNewGame() {
        lives = lastSavedLives;
        if (lives > 1) {
            liveslabel.setText("<html><h1 style='margin: 0;color: green'>" + lives + " lives</h1></html>");
        } else {
            liveslabel.setText("<html><h1 style='margin: 0;color: green'>" + lives + " life</h1></html>");
        }
        firstClick = true;
        loseFromDoublePress = false;
        mineTimer.cancel();
        mineTimer = new Timer(false);
        nFlags = mines;
        seconds = 0;
        updateTimer();
        updateMineCount();
        arrangeButtons();
        minesFormat();
        smiley.setIcon(smileyImageIcon);
        timerStarted = false;
        inprogress = true;
        validate();
        repaint();
        pack();

    }

    private JMenuBar createMenuBar() {

        JMenuBar mBar = new JMenuBar();
        JMenu game = new JMenu("Game");

        JMenu help = new JMenu("Help");
        JMenu about = new JMenu("About");

        final JMenuItem miNew = new JMenuItem("New");
        final JMenuItem miBeg = new JMenuItem("Beginner");
        final JMenuItem miInter = new JMenuItem("Intermediate");
        final JMenuItem miExp = new JMenuItem("Expert");
        final JMenuItem chooseLives = new JMenuItem("Choose lives");
        final JMenuItem miExit = new JMenuItem("Exit");
        final JMenuItem scores = new JMenuItem("Scores");

        final JMenuItem aboutMenu = new JMenuItem("About Me");

        game.add(miNew);
        game.add(miBeg);
        game.add(miInter);
        game.add(miExp);
        game.add(chooseLives);
        game.add(miExit);
        help.add(scores);
        about.add(aboutMenu);

        ActionListener CHOOSELIVES = ae -> {
            setEnabled(false);
            JFrame inputFrame = new JFrame("Choose Lives");
            inputFrame.setSize(250, 150);
            inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            inputFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());

            JLabel titleLabel = new JLabel("Choose Number of Lives");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(titleLabel, BorderLayout.NORTH);

            Integer[] numbers = new Integer[10];
            for (int i = 1; i <= 10; i++) {
                numbers[i - 1] = i;
            }

            JComboBox<Integer> comboBox = new JComboBox<>(numbers);
            comboBox.setPreferredSize(new Dimension(100, 30));
            comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(comboBox, BorderLayout.CENTER);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Integer selectedValue = (Integer) comboBox.getSelectedItem();
                    if (selectedValue != null) {
                        lives = selectedValue;
                        lastSavedLives = lives;
                        setEnabled(true);
                        inputFrame.dispose();
                        resetDifficulty(width, height, mines);
                    } else {
                        JOptionPane.showMessageDialog(inputFrame, "Please select a valid number of lives.");
                    }
                }
            });
            panel.add(okButton, BorderLayout.SOUTH);

            inputFrame.add(panel);
            inputFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    setEnabled(true);
                    inputFrame.dispose(); // Close the window
                }
            });
            inputFrame.setVisible(true);
        };

        ActionListener MENULSTNR = ae -> {
            if (scores == ae.getSource()) {
                List<String> list = new LinkedList<>();
                Scanner s;
                JPanel panel = new JPanel(new BorderLayout());
                panel.setSize(500, 500);

                try {
                    s = new Scanner(new File(path));
                    while (s.hasNextLine())
                        list.add(s.nextLine());
                    String tempText = "<html><h2>Beginner:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                            + list.get(0)
                            + "&nbsp;&nbsp;&nbsp;&nbsp;seconds<br/><br/>Intermediate:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                            + list.get(1)
                            + "&nbsp;&nbsp;&nbsp;&nbsp;seconds<br/><br/>Expert:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                            + list.get(2) + "&nbsp;&nbsp;&nbsp;&nbsp;seconds</h2></html>";
                    panel.add(new JLabel(tempText));
                    int result = JOptionPane.showOptionDialog(null, panel, "Best Scores", JOptionPane.YES_NO_OPTION,
                            JOptionPane.PLAIN_MESSAGE, null, options, null);
                    if (result == JOptionPane.YES_OPTION) {
                        resetScores();
                        JOptionPane.showMessageDialog(null, "Scores reseted!");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (miNew == ae.getSource()) {
                resetDifficulty(width, height, mines);
            }
            if (miBeg == ae.getSource()) {
                resetDifficulty(9, 9, 10);

            }
            if (miInter == ae.getSource()) {
                resetDifficulty(16, 16, 40);

            }
            if (miExp == ae.getSource()) {
                resetDifficulty(16, 30, 99);
            }
            if (miExit == ae.getSource()) {
                dispose();
                System.exit(0);
            }

            if (aboutMenu == ae.getSource()) {
                JOptionPane.showMessageDialog(null, "First Java Project \nGkagkakis Vasilis");
            }
        };
        miNew.addActionListener(MENULSTNR);
        miBeg.addActionListener(MENULSTNR);
        miInter.addActionListener(MENULSTNR);
        miExp.addActionListener(MENULSTNR);
        chooseLives.addActionListener(CHOOSELIVES);
        miExit.addActionListener(MENULSTNR);
        about.addActionListener(MENULSTNR);
        aboutMenu.addActionListener(MENULSTNR);
        scores.addActionListener(MENULSTNR);
        mBar.add(game);
        mBar.add(help);
        mBar.add(about);
        return mBar;
    }

    private void resetScores() {
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(path));
            // gia na ka8arisw to arxeio
            PrintWriter pr = new PrintWriter(path);
            pr.print("");
            wr.write("-\n-\n-\n");
            wr.close();
            pr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {
        smileyImageIcon = getScaledImage("images\\smiley.png", 25, 25);
        coveredImageIcon = getScaledImage("images\\covered.png", 20, 20);
        wasmineImageIcon = getScaledImage("images\\greymine.png", 20, 20);
        lossImageIcon = getScaledImage("images\\mine.png", 20, 20);
        cryImageIcon = getScaledImage("images\\deadsmiley.png", 25, 25);
        oneImageIcon = getScaledImage("images\\one.png", 20, 20);
        twoImageIcon = getScaledImage("images\\two.png", 20, 20);
        threeImageIcon = getScaledImage("images\\three.png", 20, 20);
        fourImageIcon = getScaledImage("images\\four.png", 20, 20);
        fiveImageIcon = getScaledImage("images\\five.png", 20, 20);
        sixImageIcon = getScaledImage("images\\six.png", 20, 20);
        sevenImageIcon = getScaledImage("images\\seven.png", 20, 20);
        eightImageIcon = getScaledImage("images\\eight.png", 20, 20);
        flagImageIcon = getScaledImage("images\\flagged.png", 20, 20);
        winImageIcon = getScaledImage("images\\sunglasses.png", 20, 20);
        wasnotmineImageIcon = getScaledImage("images\\nomine.png", 20, 20);
        blankImageIcon = getScaledImage("images\\blank.png", 20, 20);
        wowImageIcon = getScaledImage("images\\wow.png", 30, 40);
        displayZero = getScaledImage("images/displayzero.png", 20, 30);
        displayOne = getScaledImage("images/displayone.png", 20, 30);
        displayTwo = getScaledImage("images/displaytwo.png", 20, 30);
        displayThree = getScaledImage("images/displaythree.png", 20, 30);
        displayFour = getScaledImage("images/displayfour.png", 20, 30);
        displayFive = getScaledImage("images/displayfive.png", 20, 30);
        displaySix = getScaledImage("images/displaysix.png", 20, 30);
        displaySeven = getScaledImage("images/displayseven.png", 20, 30);
        displayEight = getScaledImage("images/displayeight.png", 20, 30);
        displayNine = getScaledImage("images/displaynine.png", 20, 30);
        negative = getScaledImage("images/negative.png", 20, 30);
    }

    private ImageIcon getScaledImage(String imageString, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(imageString);
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT);
        imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }

    private boolean checkWon() {
        if (!inprogress)
            return false;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!(mineArray[x][y] == 9) && button[x][y].getIcon() == coveredImageIcon) {
                    return false;
                }
            }
        }
        return true;
    }

    private void win() {
        inprogress = false;
        mineTimer.cancel();
        smiley.setIcon(winImageIcon);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                button[i][j].removeActionListener(this);
                button[i][j].removeMouseListener(this);
                if (mineArray[i][j] == 9 && button[i][j].getIcon() != flagImageIcon) {
                    button[i][j].setIcon(flagImageIcon);
                }
            }
        int winSeconds = updateTimer();
        try {
            // gia na diabasw ta score kai na ta kratisw
            List<String> list = new LinkedList<>();
            Scanner s = new Scanner(new File(path));
            while (s.hasNextLine())
                list.add(s.nextLine());
            // gia na grapsw sto arxeio
            BufferedWriter wr = new BufferedWriter(new FileWriter(path));
            // gia na ka8arisw to arxeio
            PrintWriter pr = new PrintWriter(path);
            // krataw ta 3 score gia ka8e diskolia
            String easy = list.get(0);
            String intermediate = list.get(1);
            String expert = list.get(2);

            System.out.println(easy);
            System.out.println(intermediate);
            System.out.println(expert);
            // ka8arizw to arxeio
            pr.print("");
            // sto switch ananewnw ta scores
            switch (mines) {
                // an eimaste sto easy
                case 10:
                    wr.write(winSeconds + "\n" + intermediate + "\n" + expert);
                    pr.close();
                    wr.close();

                    break;
                // an eimaste sto intermediate
                case 40:
                    wr.write(easy + "\n" + winSeconds + "\n" + expert);
                    pr.close();
                    wr.close();
                    break;
                // an eimaste sto expert
                case 99:
                    wr.write(easy + "\n" + intermediate + "\n" + winSeconds);
                    pr.close();
                    wr.close();
                    break;
            }
        } catch (Exception ignored) {
        }
    }

    // an brei mine sto prwto click tote to kanei ari8mo kai metaferei to mine sto
    // prwto cell pou den exei mine apo aristera panw pros deksia katw
    private void firstNoMine(int i, int j) {
        if (mineArray[i][j] == 9) {
            mineArray[i][j] = 0;
            for (int a = 0; a < width; a++) {
                for (int b = 0; b < height; b++) {
                    // to i !=a && j !=b to bazw gia na min balei to mine sto idio pou tin brike
                    if (mineArray[a][b] == 0 && i != a && j != b) {
                        mineArray[a][b] = 9;
                        System.out.println("neo array");
                        for (int a1 = 0; a1 < width; a1++) {
                            for (int b1 = 0; b1 < height; b1++) {
                                System.out.print(mineArray[a1][b1] + "");
                            }
                            System.out.print("\n");
                        }
                        System.out.println();
                        return;
                    }
                }
            }
        }

    }

    private void lose(int a, int b) {
        smiley.setIcon(cryImageIcon);
        mineTimer.cancel();
        inprogress = false;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                button[i][j].removeActionListener(this);
                button[i][j].removeMouseListener(this);
                if (mineArray[i][j] == 9 && button[i][j].getIcon() != flagImageIcon) {
                    button[i][j].setIcon(wasmineImageIcon);
                }
                if (mineArray[i][j] != 9 && button[i][j].getIcon() == flagImageIcon) {
                    button[i][j].setIcon(wasnotmineImageIcon);
                }
            }
        // an xasw kanonika kai oxi apo double press deixnw se poia narki exasa
        if (!loseFromDoublePress)
            button[a][b].setIcon(lossImageIcon);

    }

    private void setCorrectIcon(int i, int j, int mines) {
        switch (mines) {
            case 1:
                button[i][j].setIcon(oneImageIcon);
                break;
            case 2:
                button[i][j].setIcon(twoImageIcon);
                break;
            case 3:
                button[i][j].setIcon(threeImageIcon);
                break;
            case 4:
                button[i][j].setIcon(fourImageIcon);
                break;
            case 5:
                button[i][j].setIcon(fiveImageIcon);
                break;
            case 6:
                button[i][j].setIcon(sixImageIcon);
                break;
            case 7:
                button[i][j].setIcon(sevenImageIcon);
                break;
            case 8:
                button[i][j].setIcon(eightImageIcon);
                break;
        }
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent me) {
        JButton jb = (JButton) me.getSource();
        int x = jb.getY() / 20;
        int y = jb.getX() / 20;
        if (SwingUtilities.isLeftMouseButton(me)) {
            isLeftPressed = true;
            // an einai to prwto click checkarw gia to an einai mine
            if (firstClick) {
                firstNoMine(x, y);
                if (checkWon())
                    win();
            }
        }
        if (SwingUtilities.isRightMouseButton(me)) {
            isRightPressed = true;
        }
        if (SwingUtilities.isRightMouseButton(me) && !isLeftPressed) {
            if (inprogress) {
                if (jb.getIcon() == coveredImageIcon) {

                    jb.setIcon(flagImageIcon);
                    nFlags--;
                } else if (jb.getIcon() == flagImageIcon) {
                    jb.setIcon(coveredImageIcon);
                    nFlags++;
                }
                updateMineCount();
            }
        }

        if (jb == me.getSource()) {
            if (SwingUtilities.isLeftMouseButton(me)) {
                smiley.setIcon(wowImageIcon);
            }
        }
        if (isLeftPressed && isRightPressed && button[x][y].getIcon() != flagImageIcon) {
            checkBlankDoublePress(x, y);
            if (checkWon())
                win();
            bothPressed = true;
        }
        firstClick = false;
    }

    public void mouseReleased(MouseEvent me) {
        JButton jb = (JButton) me.getSource();
        // blepw poio button pati8ike
        int x = jb.getY() / 20;
        int y = jb.getX() / 20;
        smiley.setIcon(smileyImageIcon);
        // an pati8ei to aristero kai den patame kai ta 2 koumpia ksekinaei to timer(an
        // den exei ksekinisei idi)
        if (SwingUtilities.isLeftMouseButton(me) && !bothPressed) {
            isLeftPressed = false;

            // an to koumpi einai covered mpainei
            if (jb.getIcon() == coveredImageIcon) {
                if (inprogress) {
                    if (!timerStarted) {
                        timerStarted = true;
                        mineTimer.scheduleAtFixedRate(new updateTimerTask(), 1000, 1000);// start timer updater

                    }
                }
                // an brei bomba xanoume mia zwi

                if (mineArray[x][y] == 9) {
                    lives--;
                    if (lives > 1) {
                        liveslabel.setText("<html><h1 style='margin: 0;color: green'>" + lives + " lives</h1></html>");
                        // apokaliptoume tin narki
                        button[x][y].setIcon(flagImageIcon);
                        revealAllWrongNeighbors(x, y);
                        if (checkWon())
                            win();
                    } else if (lives == 1) {
                        liveslabel.setText("<html><h1 style='margin: 0;color: green'>" + lives + " life</h1></html>");
                        // apokaliptoume tin narki
                        button[x][y].setIcon(flagImageIcon);
                        revealAllWrongNeighbors(x, y);
                        if (checkWon())
                            win();
                    } else if (lives == 0) {
                        liveslabel.setText("<html><h1 style='margin: 0;color: green'>You died!</h1></html>");
                        lose(x, y);
                        revealAllWrongNeighbors(x, y);
                        if (checkWon())
                            win();
                    }
                } else {
                    checkBlank(x, y);
                    if (checkWon())
                        win();
                }
            }
        }
        isRightPressed = false;
        isLeftPressed = false;
        bothPressed = false;
    }

    public void mouseEntered(MouseEvent arg0) {

    }

    public void mouseExited(MouseEvent arg0) {

    }

    public void arrangeButtons() {
        mineArray = new int[width][height];
        button = new JButton[width][height];
        boolean starting = true;
        if (minespan != null) {
            composite.remove(minespan);
            minespan = null;
            starting = false;

        }
        minespan = new JPanel();
        minespan.setLayout(new GridLayout(width, height));

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mineArray[i][j] = 0;
                button[i][j] = new JButton("");
                button[i][j].setIcon(coveredImageIcon);
                button[i][j].setPreferredSize(
                        new Dimension(coveredImageIcon.getIconWidth(), coveredImageIcon.getIconWidth()));
                button[i][j].addActionListener(this);
                button[i][j].addMouseListener(this);
                // gia na min exoun hover animation ta button
                button[i][j].setFocusPainted(false);
                button[i][j].setBorderPainted(false);
                button[i][j].setContentAreaFilled(false);

                minespan.add(button[i][j]);
            }
        }

        minespan.setVisible(true);
        composite.add(minespan, BorderLayout.CENTER);
        if (starting) {
            minesFormat();
        }
        pack();
    }

    private int[] getRndmNos(int width, int height, int mines) {
        Random rand = new Random();
        rndmines = new int[mines];
        boolean in;
        int count = 0;
        while (count < mines) {
            int rndno = (int) ((width * height) * (rand.nextDouble())) + 1;
            in = false;
            for (int i = 0; i < count; i++) {
                if (rndmines[i] == rndno) {
                    in = true;
                    break;
                }
            }
            if (!in) {
                rndmines[count++] = rndno;
            }

        }
        return rndmines;
    }

    private void minesFormat() {
        int mine[] = getRndmNos(width, height, mines);
        int count = 1;
        // edw orizw pou einai ta mines sto minearray
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                for (int k = 0; k < mine.length && mine[k] != 0; k++) {
                    if (count == mine[k]) {
                        mineArray[i][j] = 9;
                    }
                }

                count++;
            }
        }
        for (int a = 0; a < width; a++) {
            for (int b = 0; b < height; b++) {
                System.out.print(mineArray[a][b] + "");
            }
            System.out.print("\n");
        }
        System.out.println();
    }

    private void checkBlankDoublePress(int i, int j) {
        bothPressed = false;
        int bombsAround = 0;
        int flagsAround = 0;
        int correct = 0;
        // koitaei aristera panw
        if (i > 0 && j > 0) {
            if (mineArray[i - 1][j - 1] == 9)
                bombsAround++;
            if (button[i - 1][j - 1].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i - 1][j - 1] == 9 && button[i - 1][j - 1].getIcon() == flagImageIcon)
                correct++;
        }
        // koitaei aristera
        if (i > 0) {
            if (mineArray[i - 1][j] == 9)
                bombsAround++;
            if (button[i - 1][j].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i - 1][j] == 9 && button[i - 1][j].getIcon() == flagImageIcon)
                correct++;
        }
        // koitaei aristera katw
        if (i > 0 && j < height - 1) {
            if (mineArray[i - 1][j + 1] == 9)
                bombsAround++;
            if (button[i - 1][j + 1].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i - 1][j + 1] == 9 && button[i - 1][j + 1].getIcon() == flagImageIcon)
                correct++;
        }
        // koitaei deksia panw
        if (i < width - 1 && j > 0) {
            if (mineArray[i + 1][j - 1] == 9)
                bombsAround++;
            if (button[i + 1][j - 1].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i + 1][j - 1] == 9 && button[i + 1][j - 1].getIcon() == flagImageIcon)
                correct++;
        }
        // koitaei deksia
        if (i < width - 1) {
            if (mineArray[i + 1][j] == 9)
                bombsAround++;
            if (button[i + 1][j].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i + 1][j] == 9 && button[i + 1][j].getIcon() == flagImageIcon)
                correct++;
        }
        // koitaei deksia katw
        if (i < width - 1 && j < height - 1) {
            if (mineArray[i + 1][j + 1] == 9)
                bombsAround++;
            if (button[i + 1][j + 1].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i + 1][j + 1] == 9 && button[i + 1][j + 1].getIcon() == flagImageIcon)
                correct++;
        }
        // koitaei panw
        if (j > 0) {
            if (mineArray[i][j - 1] == 9)
                bombsAround++;
            if (button[i][j - 1].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i][j - 1] == 9 && button[i][j - 1].getIcon() == flagImageIcon)
                correct++;
        }
        // koitaei katw
        if (j < height - 1) {
            if (mineArray[i][j + 1] == 9)
                bombsAround++;
            if (button[i][j + 1].getIcon() == flagImageIcon)
                flagsAround++;
            if (mineArray[i][j + 1] == 9 && button[i][j + 1].getIcon() == flagImageIcon)
                correct++;
        }
        // an exoume idio ari8mo simaiwn me bombes kai einai oi swstes tote kane reveal
        // tous geitones
        if (bombsAround == flagsAround && correct == flagsAround) {
            revealNeighbors(i, j);
        }
        // an exoume idio ari8mo simaiwn me bombes alla einai la8os tote xanw mia zwi
        // kai dior8wnetai to la8os
        if (bombsAround == flagsAround && correct != flagsAround) {
            lives--;
            if (lives > 1) {
                liveslabel.setText("<html><h1 style='margin: 0;color: green'>" + lives + " lives</h1></html>");
            } else if (lives == 1) {
                liveslabel.setText("<html><h1 style='margin: 0;color: green'>" + lives + " life</h1></html>");
            } else if (lives == 0) {
                loseFromDoublePress = true;
                liveslabel.setText("<html><h1 style='margin: 0;color: green'>You died!</h1></html>");
                lose(i, j);
            }
            revealWrongNeighbors(i, j);
        }

    }

    private void revealAllWrongNeighbors(int i, int j) {
        if (i > 0 && j > 0)
            setCorrectIcon(i - 1, j - 1, checkBlank(i - 1, j - 1));
        if (i > 0)
            setCorrectIcon(i - 1, j, checkBlank(i - 1, j));
        if (i > 0)
            setCorrectIcon(i - 1, j + 1, checkBlank(i - 1, j + 1));
        if (i > 0 && j < height - 1)
            setCorrectIcon(i + 1, j - 1, checkBlank(i + 1, j - 1));
        if (i < width - 1 && j > 0)
            setCorrectIcon(i + 1, j, checkBlank(i + 1, j));
        if (i < width - 1)
            setCorrectIcon(i + 1, j + 1, checkBlank(i + 1, j + 1));
        if (i < width - 1 && j < height - 1)
            setCorrectIcon(i, j - 1, checkBlank(i, j - 1));
        if (j < height - 1)
            setCorrectIcon(i, j + 1, checkBlank(i, j + 1));
    }

    private void revealWrongNeighbors(int i, int j) {
        // koitame pali olous tous geitones
        if (i > 0 && j > 0) {
            // an exoume balei flag kai einai la8os narki tote kanei reveal olous tous
            // geitones kai xanei mia zwi
            if (button[i - 1][j - 1].getIcon() == flagImageIcon && mineArray[i - 1][j - 1] != 9)
                revealAllWrongNeighbors(i, j);
            // an den exoume balei flag kai einai narki tote kanei to cell tis narkis iso me
            // flag
            if (button[i - 1][j - 1].getIcon() == coveredImageIcon && mineArray[i - 1][j - 1] == 9)
                button[i - 1][j - 1].setIcon(flagImageIcon);

        }
        if (i > 0) {
            if (button[i - 1][j].getIcon() == flagImageIcon && mineArray[i - 1][j] != 9)
                revealAllWrongNeighbors(i, j);
            if (button[i - 1][j].getIcon() == coveredImageIcon && mineArray[i - 1][j] == 9)
                button[i - 1][j].setIcon(flagImageIcon);
        }
        if (i > 0 && j < height - 1) {
            if (button[i - 1][j + 1].getIcon() == flagImageIcon && mineArray[i - 1][j + 1] != 9)
                revealAllWrongNeighbors(i, j);
            if (button[i - 1][j + 1].getIcon() == coveredImageIcon && mineArray[i - 1][j + 1] == 9)
                button[i - 1][j + 1].setIcon(flagImageIcon);
        }
        if (i < width - 1 && j > 0) {
            if (button[i + 1][j - 1].getIcon() == flagImageIcon && mineArray[i + 1][j - 1] != 9)
                revealAllWrongNeighbors(i, j);
            if (button[i + 1][j - 1].getIcon() == coveredImageIcon && mineArray[i + 1][j - 1] == 9)
                button[i + 1][j - 1].setIcon(flagImageIcon);
        }
        if (i < width - 1) {
            if (button[i + 1][j].getIcon() == flagImageIcon && mineArray[i + 1][j] != 9)
                revealAllWrongNeighbors(i, j);
            if (button[i + 1][j].getIcon() == coveredImageIcon && mineArray[i + 1][j] == 9)
                button[i + 1][j].setIcon(flagImageIcon);
        }
        if (i < width - 1 && j < height - 1) {
            if (button[i + 1][j + 1].getIcon() == flagImageIcon && mineArray[i + 1][j + 1] != 9)
                revealAllWrongNeighbors(i, j);
            if (button[i + 1][j + 1].getIcon() == coveredImageIcon && mineArray[i + 1][j + 1] == 9)
                button[i + 1][j + 1].setIcon(flagImageIcon);
        }
        if (j > 0) {
            if (button[i][j - 1].getIcon() == flagImageIcon && mineArray[i][j - 1] != 9)
                revealAllWrongNeighbors(i, j);
            if (button[i][j - 1].getIcon() == coveredImageIcon && mineArray[i][j - 1] == 9)
                button[i][j - 1].setIcon(flagImageIcon);
        }
        if (j < height - 1) {
            if (button[i][j + 1].getIcon() == flagImageIcon && mineArray[i][j + 1] != 9)
                revealAllWrongNeighbors(i, j);
            if (button[i][j + 1].getIcon() == coveredImageIcon && mineArray[i][j + 1] == 9)
                button[i][j + 1].setIcon(flagImageIcon);
        }
    }

    private void revealNeighbors(int i, int j) {
        if (i > 0 && j > 0) {
            if (button[i - 1][j - 1].getIcon() == coveredImageIcon)
                setCorrectIcon(i - 1, j - 1, checkBlank(i - 1, j - 1));
        }
        if (i > 0) {
            if (button[i - 1][j].getIcon() == coveredImageIcon)
                setCorrectIcon(i - 1, j, checkBlank(i - 1, j));
        }
        if (i > 0 && j < height - 1) {
            if (button[i - 1][j + 1].getIcon() == coveredImageIcon)
                setCorrectIcon(i - 1, j + 1, checkBlank(i - 1, j + 1));
        }
        if (i < width - 1 && j > 0) {
            if (button[i + 1][j - 1].getIcon() == coveredImageIcon)
                setCorrectIcon(i + 1, j - 1, checkBlank(i + 1, j - 1));
        }
        if (i < width - 1) {
            if (button[i + 1][j].getIcon() == coveredImageIcon)
                setCorrectIcon(i + 1, j, checkBlank(i + 1, j));
        }
        if (i < width - 1 && j < height - 1) {
            if (button[i + 1][j + 1].getIcon() == coveredImageIcon)
                setCorrectIcon(i + 1, j + 1, checkBlank(i + 1, j + 1));
        }
        if (j > 0) {
            if (button[i][j - 1].getIcon() == coveredImageIcon)
                setCorrectIcon(i, j - 1, checkBlank(i, j - 1));
        }
        if (j < height - 1) {
            if (button[i][j + 1].getIcon() == coveredImageIcon)
                setCorrectIcon(i, j + 1, checkBlank(i, j + 1));
        }

    }

    private int checkBlank(int i, int j) {
        int boxcount = 0;
        if (i >= 0 && i < width && j >= 0 && j < height) {
            if (mineArray[i][j] != 9) {
                // koitaei aristera panw
                if (i > 0 && j > 0) {
                    if (mineArray[i - 1][j - 1] == 9)
                        boxcount++;
                }
                // koitaei aristera
                if (i > 0) {
                    if (mineArray[i - 1][j] == 9)
                        boxcount++;
                }
                // koitaei aristera katw
                if (i > 0 && j < height - 1) {
                    if (mineArray[i - 1][j + 1] == 9)
                        boxcount++;
                }
                // koitaei deksia panw
                if (i < width - 1 && j > 0) {
                    if (mineArray[i + 1][j - 1] == 9)
                        boxcount++;
                }
                // koitaei deksia
                if (i < width - 1) {
                    if (mineArray[i + 1][j] == 9)
                        boxcount++;
                }
                // koitaei deksia katw
                if (i < width - 1 && j < height - 1) {
                    if (mineArray[i + 1][j + 1] == 9)
                        boxcount++;
                }
                // koitaei panw
                if (j > 0) {
                    if (mineArray[i][j - 1] == 9)
                        boxcount++;
                }
                // koitaei katw
                if (j < height - 1) {
                    if (mineArray[i][j + 1] == 9)
                        boxcount++;
                }
                mineArray[i][j] = boxcount;
                setCorrectIcon(i, j, boxcount);
                // an oute o geitonas exei bomba stous geitones tou
                if (boxcount == 0) {
                    checkNeighbors(i, j);
                }
            }
        }
        return boxcount;
    }

    private void checkNeighbors(int i, int j) {
        button[i][j].setIcon(blankImageIcon);
        if (i > 0 && j > 0) {
            // aristera panw
            if (button[i - 1][j - 1].getIcon() == coveredImageIcon) {
                checkBlank(i - 1, j - 1);
            }
        }
        if (i > 0) {
            // aristera
            if (button[i - 1][j].getIcon() == coveredImageIcon) {
                checkBlank(i - 1, j);
            }
        }
        if (i > 0 && j < height - 1) {
            // aristera katw
            if (button[i - 1][j + 1].getIcon() == coveredImageIcon) {
                checkBlank(i - 1, j + 1);
            }
        }
        if (i < width - 1 && j > 0) {
            // deksia panw
            if (button[i + 1][j - 1].getIcon() == coveredImageIcon) {
                checkBlank(i + 1, j - 1);
            }
        }
        if (i < width - 1) {
            // deksia
            if (button[i + 1][j].getIcon() == coveredImageIcon) {
                checkBlank(i + 1, j);
            }
        }
        if (i < width - 1 && j < height - 1) {
            // deksia katw
            if (button[i + 1][j + 1].getIcon() == coveredImageIcon) {
                checkBlank(i + 1, j + 1);
            }
        }
        if (j > 0) {
            // panw
            if (button[i][j - 1].getIcon() == coveredImageIcon) {
                checkBlank(i, j - 1);
            }
        }
        if (j < height - 1) {
            // katw
            if (button[i][j + 1].getIcon() == coveredImageIcon) {
                checkBlank(i, j + 1);
            }
        }
    }

    private void updateMineCount() {
        if (nFlags >= 0) {
            char hundreds = (char) (((nFlags / 100) % 10) + 30);
            char tens = (char) (((nFlags / 10) % 10) + 30);
            char ones = (char) ((nFlags % 10) + 30);
            setDisplayLabel(mineHundreds, hundreds);
            setDisplayLabel(mineTens, tens);
            setDisplayLabel(mineOnes, ones);
        } else {
            int tmpFlags = nFlags * -1;
            char tens = (char) (((tmpFlags / 10) % 10) + 30);
            char ones = (char) ((tmpFlags % 10) + 30);
            mineHundreds.setIcon(negative);
            setDisplayLabel(mineTens, tens);
            setDisplayLabel(mineOnes, ones);
        }

    }

    private void setDisplayLabel(JLabel label, char num) {
        switch (num) {
            case 30:
                label.setIcon(displayZero);
                break;
            case 31:
                label.setIcon(displayOne);
                break;
            case 32:
                label.setIcon(displayTwo);
                break;
            case 33:
                label.setIcon(displayThree);
                break;
            case 34:
                label.setIcon(displayFour);
                break;
            case 35:
                label.setIcon(displayFive);
                break;
            case 36:
                label.setIcon(displaySix);
                break;
            case 37:
                label.setIcon(displaySeven);
                break;
            case 38:
                label.setIcon(displayEight);
                break;
            default:
                label.setIcon(displayNine);
                break;

        }
    }

    private class updateTimerTask extends TimerTask {
        public void run() {
            seconds += 1;
            updateTimer();
        }
    }

    private int updateTimer() // update timer graphically
    {
        char hundreds, tens, ones;
        if (seconds < 999) {
            hundreds = (char) (((seconds / 100) % 10) + 30);
            tens = (char) (((seconds / 10) % 10) + 30);
            ones = (char) ((seconds % 10) + 30);
        } else {
            hundreds = tens = ones = '9';
        }

        setDisplayLabel(timerHundreds, hundreds);
        setDisplayLabel(timerTens, tens);
        setDisplayLabel(timerOnes, ones);

        return seconds;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new Minesweeper();
    }
}