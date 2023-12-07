/*
The Game class represents a game of Spider Solitaire.
The Game class represents a game of Spider Solitaire.
It contains the main logic and functionality of the game.
*/


// ------------------- Fix This ------------------- //
// Change back of card image
// Remove the anonymous classes and change them private inner classes in the Game.java file
// Change hard coded values
// Change function names
// Possibly add new functions to call other functions
// Fix loops in Deck file line 54 - 61

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.net.URI;

class Game {
    private Deck gameDeck;
    private Pile gamePiles[];
    private static JFrame gameFrame;
    private static JFrame gameFrame;
    private JMenu gameStats;
    private static JButton playButton, rulesButton, exitButton;
    private static JButton playButton, rulesButton, exitButton;
    private boolean isCardSelected = false;
    private Vector<Card> cards = null;
    private int numSuits, numStacks = 0, numDeals = 5, numMoves = 0;
    private static final Color BGCOLOR = new Color(31, 164, 22);
    private static final ImageIcon icon = new ImageIcon("assets/icon.png");

    // Main method to launch the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> launchStartMenu());
    }

    // Launch the main menu
    private static void launchStartMenu() {
        gameFrame = new JFrame("Spider Solitaire");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setIconImage(icon.getImage());
        gameFrame.setLayout(new GridBagLayout());
        gameFrame = new JFrame("Spider Solitaire");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setIconImage(icon.getImage());
        gameFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        playButton = new JButton("Play");
        rulesButton = new JButton("Rules");
        exitButton = new JButton("Exit");

        playButton.setBackground(BGCOLOR);
        rulesButton.setBackground(Color.RED);
        exitButton.setBackground(Color.YELLOW);
        playButton = new JButton("Play");
        rulesButton = new JButton("Rules");
        exitButton = new JButton("Exit");

        playButton.setBackground(BGCOLOR);
        rulesButton.setBackground(Color.RED);
        exitButton.setBackground(Color.YELLOW);

        playButton.setFont(new Font("Arial", Font.PLAIN, 30));
        rulesButton.setFont(new Font("Arial", Font.PLAIN, 30));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 30));

        // Button actions
        playButton.addActionListener(e -> {
            gameFrame.dispose();
            gameFrame.dispose();
            showDifficultySelection();
        });
        rulesButton.addActionListener(e -> displayRules(gameFrame));
        rulesButton.addActionListener(e -> displayRules(gameFrame));
        exitButton.addActionListener(e -> System.exit(0));

        gameFrame.add(playButton, gbc);
        gameFrame.add(rulesButton, gbc);
        gameFrame.add(exitButton, gbc);
        gameFrame.add(playButton, gbc);
        gameFrame.add(rulesButton, gbc);
        gameFrame.add(exitButton, gbc);

        gameFrame.setVisible(true);
        gameFrame.setVisible(true);
    }

    // Constructor for the Game class
    public Game(int suits) {
        numSuits = suits;

        // Create the main game window
        gameFrame = new JFrame("Spider Solitaire");
        gameFrame = new JFrame("Spider Solitaire");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.getContentPane().setBackground(BGCOLOR);
        gameFrame.setIconImage(icon.getImage());
        gameFrame.setIconImage(icon.getImage());
        gameFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = .8;
        gbc.weighty = 1;

        // Initialize the game deck and piles
        gameDeck = new Deck(numSuits, this);
        gamePiles = new Pile[10];

        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;

        for (int i = 0; i < 10; i++) {
            gamePiles[i] = new Pile(gameDeck, i < 4 ? 6 : 5, this);

            JScrollPane pileScrollPane = new JScrollPane(gamePiles[i]);
            pileScrollPane.setPreferredSize(new Dimension(120, screenHeight - 100)); // Set a preferred size for the scroll pane
            pileScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            pileScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // Set the background color for the JScrollPane and its viewport
            pileScrollPane.getViewport().setBackground(BGCOLOR);
            pileScrollPane.setBackground(BGCOLOR);

            // Remove the border of the JScrollPane
            pileScrollPane.setBorder(BorderFactory.createEmptyBorder());

            // Add the JScrollPane to the game frame instead of the Pile directly
            gameFrame.add(pileScrollPane, gbc);
        }

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(createDealMenu());
        gameMenu.add(createRestartMenu());
        menuBar.add(gameMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem rulesItem = new JMenuItem("Rules");
        rulesItem.addActionListener(e -> displayRules(gameFrame));
        helpMenu.add(rulesItem);
        menuBar.add(helpMenu);

        JMenu exitMenu = new JMenu("Quit");
        JMenuItem exitMenuItem = new JMenuItem("Quit Game!");
        exitMenuItem.addActionListener(e -> System.exit(0));
        exitMenu.add(exitMenuItem);
        menuBar.add(exitMenu);

        gameStats = new JMenu("Moves: " + numMoves + "  |  Stacks: " 
                             + numStacks + "/8  |  Deals: " + numDeals + "/5");
        gameStats.setOpaque(true);
        gameStats.setBackground(Color.WHITE);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(gameStats);

        gameFrame.setJMenuBar(menuBar);
        gameFrame.setVisible(true);
    }

    // Create the "Deal" menu item
    private JMenuItem createDealMenu() {
        JMenuItem dealMenu = new JMenuItem("Deal");
        dealMenu.setMnemonic('d');
        dealMenu.addActionListener(e -> dealNewCards());
        return dealMenu;
    }

    // Create the "Restart" menu with difficulty options
    private JMenu createRestartMenu() {
        JMenu restartMenu = new JMenu("Restart");
        restartMenu.setMnemonic('r');
        restartMenu.add(createNewGame("Beginner", 1));
        restartMenu.add(createNewGame("Intermediate", 2));
        restartMenu.add(createNewGame("Advanced", 4));
        return restartMenu;
    }

    // Create a menu item for starting a new game with a specific difficulty
    private JMenuItem createNewGame(String title, int numSuits) {
        JMenuItem newGameItem = new JMenuItem(title);
        newGameItem.addActionListener(e -> {
            gameFrame.dispose();
            new Game(numSuits);
        });
        return newGameItem;
    }

    // Update game stats text
    private void updateGameStats(){
        gameStats.setText("Moves: " + numMoves + "  |  Stacks: " 
                          + numStacks + "/8  |  Deals: " + numDeals + "/5");
    }

    public void updateNumMoves(){
        numMoves++;
        updateGameStats();
    }

    public void updateNumStacks(){
        numStacks++;
        updateGameStats();
    }

    public void updateNumDeals(){
        numDeals--;
        updateGameStats();
    }

    // Deal new cards to the game piles
    private void dealNewCards() {
        boolean allSpacesFilled = true;

        // Check if all spaces are filled with cards
        for(int i = 0; i < 10; i++) {
            if (gamePiles[i].isEmpty()) {
                allSpacesFilled = false;
                i = 10;
            }    
        }

        // Deal new cards if there are no empty spaces
        if (allSpacesFilled) {
            for(int i = 0; i < 10; i++) {
                Card card = gameDeck.drawCard();
                if(card != null) {
                    card.flip();
                    gamePiles[i].addCard(card);
                }
                else
                    i = 10;
            }
            if(numDeals != 0) {
                updateNumDeals();
                updateNumMoves();
            }
        } 
        else {
            JOptionPane.showMessageDialog(null, "Please Fill All Empty Spaces.");
        }
    }

    // Check if there are selected cards
    public boolean hasSelectedCards() {
        return this.isCardSelected;
    }

    // Select a group of cards
    public void selectCards(Vector<Card> selectCards) {
        this.isCardSelected = true;
        this.cards = new Vector<>(selectCards);
    }

    // Get the selected cards
    public Vector<Card> getCards() {
        return this.cards;
    }

    // Deselect the selected cards
    public void deselectCards() {
        this.isCardSelected = false;
        this.cards = null;
    }

    // Check if the player has won the game
    public void isWinner() {
        if (gameDeck.isEmpty()) {
            for (int i = 0; i < 10; i++)
                if (!gamePiles[i].isEmpty()) 
                    return;
            int playAgain = JOptionPane.showConfirmDialog(null,
                    "You won!\nTotal Moves: " + numMoves + "\nPlay again?",
                    "You won!\nTotal Moves: " + numMoves + "\nPlay again?",
                      "You won!", JOptionPane.YES_NO_OPTION);
            if (playAgain == JOptionPane.YES_OPTION) {
                gameFrame.dispose();
                new Game(numSuits);
            } 
            else {
                System.exit(0);
            }
        }
    }

    // Display game rules using a web browser
    protected static void displayRules(JFrame mainMenuFrame) {
        try {
            Desktop.getDesktop().browse(new URI("https://solitaired.com/guides/how-to-play-spider-solitaire"));
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to open rules webpage: " + e.getMessage());
        }
    }

    // Show suit selection dialog and start a new game
    // Show suit selection dialog and start a new game
    private static void showDifficultySelection() {
        String[] options = {"1 Suit", "2 Suit", "4 Suit"};
        String[] options = {"1 Suit", "2 Suit", "4 Suit"};

        int numSuits = JOptionPane.showOptionDialog(
            null, "Select Number of Suits:", "Suit Selection",
        int numSuits = JOptionPane.showOptionDialog(
            null, "Select Number of Suits:", "Suit Selection",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            new ImageIcon("assets/iconn.png"), options, options[0]);
            new ImageIcon("assets/iconn.png"), options, options[0]);

        int suit;
        switch (numSuits) {
        int suit;
        switch (numSuits) {
            case 0:
                suit = 1;
                suit = 1;
                break;
            case 1:
                suit = 2;
                suit = 2;
                break;
            case 2:
                suit = 4;
                suit = 4;
                break;
            default:
                suit = 0;
                System.exit(0);
                suit = 0;
                System.exit(0);
                break;
        }

        SwingUtilities.invokeLater(() -> new Game(suit));
        SwingUtilities.invokeLater(() -> new Game(suit));
    }
}