import java.util.Stack;
import java.util.Collections;

public class Deck {
    private Stack<Card> deck = new Stack<Card>();
    private int numSuits;
    private SpiderSolitaire game;

    // Constructor for Deck with default one suit
    public Deck(SpiderSolitaire g) {
        this(1, g);
    }

    // Constructor for Deck with specified number of suits
    public Deck(int n, SpiderSolitaire g) {
        // Ensure numSuits is one of the allowed values
        if (n != 1 && n != 2 && n != 4)
            n = 1;
        numSuits = n;
        game = g;
        populateDeck(); // Fill the deck with cards
        shuffle(); // Shuffle the deck
    }

    // Check if the deck is empty
    public boolean isEmpty() {
        return deck.empty();
    }

    // Get the top card without removing it
    public Card top() {
        Card topCard;
        try {
            topCard = deck.peek();
        } catch (Exception e) {
            topCard = null; // If the deck is empty, return null
        }
        return topCard;
    }

    // Draw a card from the deck
    public Card drawCard() {
        if (deck.empty()) {
            return null; // Return null if the deck is empty
        }
        else
            return deck.pop(); // Remove and return the top card
    }

    // Populate the deck with cards
    private void populateDeck() {
        for (int suit = 0; suit < numSuits; suit++) {
            for (int card = 0; card < 104 / numSuits; card++) {
                Card.Suit s = Card.Suit.values()[suit]; // Choose a suit
                int r = (card % 13) + 1;  // Cycle through ranks 1-13
                deck.push(new Card(s, r, game)); // Add the card to the deck
            }
        }
    }

    // Shuffle the deck
    private void shuffle() {
        Collections.shuffle(deck); // Randomly shuffle the deck
    }
}
