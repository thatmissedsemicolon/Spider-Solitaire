/*
The Deck class represents a deck of cards in the Solitaire game.
It manages the cards in the deck, including shuffling and drawing.
*/

import java.util.Vector;
import java.util.Random;

public class Deck {
    private Vector<Card> cards;
    private static int currentCard;
    private Game game;

    // Constructor for creating a deck with a specified number of suits
    public Deck(int suits, Game games) {
        game = games;
        currentCard = 0;
        cards = new Vector<>();
        initializeDeck(suits);

        // Shuffling the deck 10 times
        for (int i = 0; i < 10; i++)
            shuffleDeck();
    }

    // Check if the deck is empty
    public boolean isEmpty() {
        return currentCard == cards.size();
    }

    // Draw a card from the deck (removes and returns the current card)
    public Card drawCard() {
        return currentCard == cards.size()?null:cards.elementAt(currentCard++);
    }

    // Initialize the deck with cards based on the number of suits
    private void initializeDeck(int numSuits) {
        for (int i = 0; i < numSuits; i++)
            for (int j = 0; j < 104 / numSuits; j++)
                cards.add(new Card(Card.Suit.values()[i], (j % 13) + 1, game));
    }

    // Shuffle all cards randomly
    public void shuffleDeck() {
        // Creating a Random object
        Random random = new Random();

        // Running through all the cards and switching their places with other
        // cards randomly
        for (int i = 0; i < cards.size(); i++) {
            // Generating the random location of the card to switch with
            int loc = random.nextInt(cards.size());
            // Switching the cards
            Card card = cards.elementAt(i);
            cards.set(i, cards.elementAt(loc));
            cards.set(loc, card);
        }
    }
}
