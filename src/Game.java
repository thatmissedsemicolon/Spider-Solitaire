/*
The Game class represents a game of Solitaire.
It contains the main logic and functionality of the game.
*/

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.net.URI;

class Game {
    Deck gameDeck;
    Pile gamePiles[];
    JFrame gameFrame;
    boolean isCardSelected = false;
    Vector<Card> cards = null;
    int difficultyLevel;

    // Constructor for the Game class
    public Game(int level) {
        this.difficultyLevel = level;

        // Create the main game window
        gameFrame = new JFrame("Solitaire");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.getContentPane().setBackground(new Color(25, 160, 15));
        gameFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = .8;
        gbc.weighty = 1;

        // Initialize the game deck and piles
        gameDeck = new Deck(difficultyLevel, this);
        gamePiles = new Pile[10];
        for (int i = 0; i < 10; i++) {
            gamePiles[i] = new Pile(gameDeck, i < 4 ? 6 : 5, this);
            gameFrame.add(gamePiles[i], gbc);
        }

        // Create the menu bar and add menus
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
    private JMenuItem createNewGame(String title, int difficultyLevel) {
        JMenuItem newGameItem = new JMenuItem(title);
        newGameItem.addActionListener(e -> {
            gameFrame.dispose();
            new Game(difficultyLevel);
        });
        return newGameItem;
    }

    // Deal new cards to the game piles
    private void dealNewCards() {
        boolean allSpacesFilled = true;

        // Check if all spaces are filled with cards
        for (Pile p : gamePiles) {
            if (p.isEmpty()) {
                allSpacesFilled = false;
                break;
            }
        }

        // Deal new cards if there are empty spaces
        if (allSpacesFilled) {
            for (Pile pile : gamePiles) {
                Card c = gameDeck.drawCard();
                if (c != null) {
                    c.flip();
                    pile.addCard(c);
                } else {
                    JOptionPane.showMessageDialog(null, "Unable to deal cards: The deck is empty!");
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Unable to deal cards: Please ensure all empty spaces are occupied.");
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
        if (this.gameDeck.isEmpty()) {
            for (Pile pile : gamePiles) {
                if (!pile.isEmpty()) return;
            }
            int playAgain = JOptionPane.showConfirmDialog(null, "You won!\nPlay again?", "You won!", JOptionPane.YES_NO_OPTION);
            if (playAgain == JOptionPane.YES_OPTION) {
                gameFrame.dispose();
                new Game(difficultyLevel);
            } else {
                System.exit(0);
            }
        }
    }

    // Main method to launch the game
    public static void main(String[] args) {
        launchMainMenu();
    }

    // Launch the main menu
    private static void launchMainMenu() {
        JFrame mainMenuFrame = new JFrame("Solitaire");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainMenuFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton playButton = new JButton("Play");
        JButton rulesButton = new JButton("Rules");
        JButton exitButton = new JButton("Exit");

        playButton.setFont(new Font("Arial", Font.PLAIN, 30));
        rulesButton.setFont(new Font("Arial", Font.PLAIN, 30));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 30));

        // Button actions
        playButton.addActionListener(e -> {
            mainMenuFrame.dispose();
            showDifficultySelection();
        });
        rulesButton.addActionListener(e -> displayRules(mainMenuFrame));
        exitButton.addActionListener(e -> System.exit(0));

        mainMenuFrame.add(playButton, gbc);
        mainMenuFrame.add(rulesButton, gbc);
        mainMenuFrame.add(exitButton, gbc);

        mainMenuFrame.setVisible(true);
    }

    // Display game rules using a web browser
    protected static void displayRules(JFrame mainMenuFrame) {
        try {
            Desktop.getDesktop().browse(new URI("https://solitaired.com/guides/how-to-play-spider-solitaire"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to open rules webpage: " + e.getMessage());
        }
    }

    // Show difficulty selection dialog and start a new game
    private static void showDifficultySelection() {
        String[] options = {"Beginner", "Intermediate", "Advanced"};

        int difficulty = JOptionPane.showOptionDialog(
            null, "Select Difficulty:", "Difficulty Selection",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]
        );

        int difficultyLevel;
        switch (difficulty) {
            case 0:
                difficultyLevel = 1;
                break;
            case 1:
                difficultyLevel = 2;
                break;
            case 2:
                difficultyLevel = 4;
                break;
            default:
                difficultyLevel = 1;
                break;
        }

        new Game(difficultyLevel);
    }
}