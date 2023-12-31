/*
The Game class represents a game of Spider Solitaire.
It contains the main logic and functionality of the game.
*/

// ------------------- Fix This ------------------- //
// Change function names
// Possibly add new functions to call other functions

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.net.URI;

class Game {
    private Deck gameDeck;
    private Pile gamePiles[];
    private static JFrame gameFrame;
    private JMenu gameStats;
    private static JButton playButton, rulesButton, exitButton;
    private boolean cardSelected = false;
    private Vector<Card> cards = null;
    private static int numSuits;
    private int numStacks = 0, numDeals = 5, numMoves = 0;
    private static final Color BGCOLOR = new Color(0, 0, 240);
    private static final ImageIcon icon = new ImageIcon("assets/icon.png");

    // Main method to launch the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> startMenu());
    }

    // Launch the main menu
    private static void startMenu() {
        setUpGameFrame();
        initializeButtons();
        setButtonColors();
        modifyButtonFonts();
        addActionListeners();
        addButtons();
        gameFrame.setVisible(true);
    }

    // Constructor for the Game class
    public Game(int suits) {
        numSuits = suits;
        setUpGameFrame();

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

        for (int i = 0; i < gamePiles.length; i++) {

            int numOfCards = 5;
            if (i < 4)
                numOfCards = 6;

            gamePiles[i] = new Pile(gameDeck, numOfCards, this);

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
        rulesItem.addActionListener(new rulesButtonListener());
        helpMenu.add(rulesItem);
        menuBar.add(helpMenu);

        JMenu exitMenu = new JMenu("Quit");
        JMenuItem exitMenuItem = new JMenuItem("Quit Game!");
        exitMenuItem.addActionListener(new exitButtonListener());
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

    private static class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            gameFrame.dispose();
            showSuitSelection();
        }
    }

    private static class rulesButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayRules(gameFrame);
        }
    }

    private static class exitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private static void setUpGameFrame() {
        gameFrame = new JFrame("Spider Solitaire");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.getContentPane().setBackground(BGCOLOR);
        gameFrame.setIconImage(icon.getImage());
        gameFrame.setLayout(new GridBagLayout());
    }

    private static void initializeButtons() {
        playButton = new JButton("Play");
        rulesButton = new JButton("Rules");
        exitButton = new JButton("Exit");
    }

    private static void setButtonColors() {
        playButton.setBackground(Color.GREEN);
        rulesButton.setBackground(Color.RED);
        exitButton.setBackground(Color.YELLOW);
    }

    private static void modifyButtonFonts() {
        playButton.setFont(new Font("Arial", Font.PLAIN, 30));
        rulesButton.setFont(new Font("Arial", Font.PLAIN, 30));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 30));
    }

    private static void addActionListeners() {
        playButton.addActionListener(new playButtonListener());
        rulesButton.addActionListener(new rulesButtonListener());
        exitButton.addActionListener(new exitButtonListener());
    }

    private static void addButtons() {
        gameFrame.add(playButton, gbc());
        gameFrame.add(rulesButton, gbc());
        gameFrame.add(exitButton, gbc());
    }

    private static GridBagConstraints gbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        return gbc;
    }

    // Create the "Deal" menu item
    private JMenuItem createDealMenu() {
        JMenuItem dealMenu = new JMenuItem("Deal");
        dealMenu.addActionListener(new DealButtonListener());
        return dealMenu;
    }

    private class DealButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            dealNewCards();
        }
    }

    // Create the "Restart" menu with difficulty options
    private JMenu createRestartMenu() {
        JMenu restartMenu = new JMenu("Restart");
        restartMenu.add(createNewGame("1 Suit"));
        restartMenu.add(createNewGame("2 Suit"));
        restartMenu.add(createNewGame("4 Suit"));
        return restartMenu;
    }

    // Creates menu item to start a new game with a specific number of cards
    private JMenuItem createNewGame(String title) {
        JMenuItem newGameItem = new JMenuItem(title);
        newGameItem.addActionListener(new NewGameButtonListener());
        return newGameItem;
    }

    private class NewGameButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            numSuits = Character.getNumericValue(
                                    e.getActionCommand().charAt(0));
            newGameHelper();
        }
    }

    private void newGameHelper() {
        gameFrame.dispose();
        new Game(numSuits);
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
        if(numDeals != 0) {
            boolean allSpacesFilled = true;

            // Check if all spaces are filled with cards
            for(int i = 0; i < 10; i++) {
                if (gamePiles[i].isEmpty()) {
                    allSpacesFilled = false;
                    highlightPiles();
                    i = 10;
                } 
            }

            // Deal new cards if there are no empty spaces
            if (allSpacesFilled) {
                for(int i = 0; i < 10; i++) {
                    Card card = gameDeck.drawCard();
                    if(card != null) {
                        card.flipOver();
                        gamePiles[i].addCard(card);
                    }
                    else
                        i = 10;
                }
                updateNumDeals();
                updateNumMoves();
            } 
            else
            JOptionPane.showMessageDialog(null,"Please Fill All Empty Spaces.");
        }
    }

    // Check if there are selected cards
    public boolean getCardSelected() {
        return cardSelected;
    }

    // Select a group of cards
    public void setSelectedCards(Vector<Card> selectedCards) {
        cardSelected = true;
        cards = new Vector<>(selectedCards);
    }

    // Get the selected card(s)
    public Vector<Card> getCard() {
        return this.cards;
    }

    // Deselect the selected card(s)
    public void deselectCard() {
        cardSelected = false;
        cards = null;
    }

    // Check if the player has won the game
    public void isWinner() {
        if (gameDeck.isEmpty()) {
            for (int i = 0; i < 10; i++)
                if (!gamePiles[i].isEmpty()) 
                    return;
            int playAgain = JOptionPane.showConfirmDialog(null,
                    "You won!\nTotal Moves: " + numMoves + "\nPlay again?",
                      "You won!", JOptionPane.YES_NO_OPTION);
            if (playAgain == JOptionPane.YES_OPTION)
                newGameHelper();
            else
                System.exit(0);
        }
    }

    protected void highlightPiles(){
        for(int i = 0; i < gamePiles.length; i++)
            if(gamePiles[i].isEmpty())
                gamePiles[i].highlightPile();
    }

    protected void unhighlightPiles(){
        for(int i = 0; i < gamePiles.length; i++)
            gamePiles[i].unhighlightPile();
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
    private static void showSuitSelection() {
        String[] options = {"1 Suit", "2 Suit", "4 Suit"};

        int suits = JOptionPane.showOptionDialog(
            null, "Select Number of Suits:", "Suit Selection",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            new ImageIcon("assets/iconn.png"), options, options[0]);

        switch (suits) {
            case 0:
                numSuits = 1;
                break;
            case 1:
                numSuits = 2;
                break;
            case 2:
                numSuits = 4;
                break;
            default:
                numSuits = 0;
                System.exit(0);
                break;
        }

        SwingUtilities.invokeLater(() -> new Game(numSuits));
    }
}