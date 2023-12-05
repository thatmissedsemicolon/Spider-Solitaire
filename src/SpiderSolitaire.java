import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

public class SpiderSolitaire {
    Deck deck;
    Pile piles[];
    JFrame frame;
    boolean cardsSelected = false;
    Vector<Card> cards = null;
    int numSuits;

    private SpiderSolitaire(int n) {
        numSuits = n;
        frame = new JFrame("Spider Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.getContentPane().setBackground(new Color(25, 160, 15));
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0.8;
        gbc.weighty = 1;

        deck = new Deck(numSuits, this);
        piles = new Pile[10];
        for (int i = 0; i < 10; i++) {
            if(i < 4)
                piles[i] = new Pile(deck, 6, this);
            else
                piles[i] = new Pile(deck, 5, this);
            frame.add(piles[i], gbc);
        }

        JMenuBar menuBar = new JMenuBar();

        JMenuItem dealMenu = new JMenuItem("Deal!") {
            public Dimension getMaximumSize() {
                Dimension d1 = super.getPreferredSize();
                Dimension d2 = super.getMaximumSize();
                d2.width = d1.width;
                return d2;
            }
        };
        dealMenu.setMnemonic('d');
        dealMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean allSpacesFilled = true;
                for (Pile p : piles)
                    if (p.empty())
                        allSpacesFilled = false;
                if (allSpacesFilled) {
                    Card c;
                    for (Pile pile : piles) {
                        c = deck.drawCard();
                        if (c != null) {
                            c.flip();
                            pile.addCard(c);
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "The deck is empty!");
                            break;
                        }
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "You may only deal when all empty spaces are occupied.");
                }
            }
        });

        JMenu restartMenu = new JMenu("Restart");
        restartMenu.setMnemonic('r');
        restartMenu.add(createMenuItem("1 suit", 1));
        restartMenu.add(createMenuItem("2 suits", 2));
        restartMenu.add(createMenuItem("4 suits", 4));

        menuBar.add(dealMenu);
        menuBar.add(restartMenu);
        frame.setJMenuBar(menuBar);

        frame.setSize(1250, 900);
        frame.setVisible(true);
    }

    private JMenuItem createMenuItem(String title, int suits) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new SpiderSolitaire(suits);
            }
        });
        return menuItem;
    }

    public boolean hasSelectedCards() {
        return cardsSelected;
    }

    public void selectCards(Vector<Card> selectCards) {
        cardsSelected = true;
        cards = new Vector<Card>();
        for(Card card : selectCards) {
            cards.addElement(card);
        }
    }

    public Vector<Card> getCards() {
        return cards;
    }

    public void deselectCards() {
        cardsSelected = false;
        cards = null;
    }

    public void checkWin() {
        if (deck.isEmpty()) {
            for (Pile pile : piles)
                if (!pile.empty())
                    return;
            int playAgain = JOptionPane.showConfirmDialog(null, "Congratulations, you won!\nPlay again?", "You won!", JOptionPane.YES_NO_OPTION);
            if (playAgain == JOptionPane.YES_OPTION) {
                frame.dispose();
                new SpiderSolitaire(numSuits);
            }
            else
                System.exit(0);
        }
    }

    private static void showMainMenu() {
        JFrame mainMenuFrame = new JFrame("Spider Solitaire Menu");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setLayout(new GridLayout(3, 1));

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainMenuFrame.dispose();
                showDifficultySelection();
            }
        });

        JButton rulesButton = new JButton("Rules");
        rulesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRules(mainMenuFrame);
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mainMenuFrame.add(startButton);
        mainMenuFrame.add(rulesButton);
        mainMenuFrame.add(exitButton);

        mainMenuFrame.setSize(400, 300);
        mainMenuFrame.setVisible(true);
    }

    private static void showRules(JFrame parentFrame) {
        JFrame rulesFrame = new JFrame("Game Rules");
        rulesFrame.setLayout(new BorderLayout());
        rulesFrame.setSize(600, 400);
        rulesFrame.setLocationRelativeTo(parentFrame);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rulesFrame.dispose();
                parentFrame.setVisible(true);
            }
        });

        JEditorPane rulesPane = new JEditorPane();
        rulesPane.setEditable(false);
        try {
            rulesPane.setPage(new URL("https://solitaired.com/guides/how-to-play-spider-solitaire"));
        } catch (IOException e) {
            rulesPane.setText("Failed to load rules.");
        }

        rulesFrame.add(backButton, BorderLayout.NORTH);
        rulesFrame.add(new JScrollPane(rulesPane), BorderLayout.CENTER);

        rulesFrame.setVisible(true);
    }

    private static void showDifficultySelection() {
        String[] options = new String[] {"1", "2", "4"};
        int numDecks = JOptionPane.showOptionDialog(null, "Choose difficulty (number of suits):", "Difficulty Selection",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);

        if (numDecks >= 0) {
            numDecks++;
            if (numDecks == 3) numDecks++;
            new SpiderSolitaire(numDecks);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showMainMenu();
            }
        });
    }
}