/*
The Deck class represents a deck of cards in the Solitaire game.
It manages the cards in the deck, including shuffling and drawing.
*/

import java.util.Stack;
import java.util.Vector;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private Stack<Card> cards;
    private Vector<Card> c;
    private static int currentCard = 0;
    private Game game;

    // Constructor for creating a deck with a specified number of suits
    public Deck(int suits, Game games) {
        game = games;
        // cards = new Stack<Card>();
        c = new Vector<>();
        initializeDeck(suits);

        // Shuffling the deck 10 times
        for (int i = 0; i < 10; i++) {
            shuffleDeck();
        }
        // shuffleDeck();
    }

    // Check if the deck is empty
    public boolean isEmpty() {
        // return cards.empty();
        return currentCard == c.size();
    }

    // Draw a card from the deck (removes and returns the top card)
    public Card drawCard() {
        // return cards.empty() ? null : cards.pop();
        return currentCard == c.size() ? null : c.elementAt(currentCard++);
    }

    // Initialize the deck with cards based on the number of suits
    // private void initializeDeck(int numSuits) {
    //     for (int suitIndex = 0; suitIndex < numSuits; suitIndex++) {
    //         for (int value = 0; value < 104 / numSuits; value++) {
    //             Card.Suit suit = Card.Suit.values()[suitIndex];
    //             int cardValue = (value % 13) + 1;
    //             cards.push(new Card(suit, cardValue, game));
    //         }
    //     }
    // }

    // When Restart is chosen in menu this loop causes the program to crash
    private void initializeDeck(int numSuits) {
        for (int i = 0; i < numSuits; i++)
            for (int j = 0; j < 104 / numSuits; j++)
                c.add(new Card(Card.Suit.values()[i], (j % 13) + 1, game));
    }

    // Shuffle the deck
    // private void shuffleDeck() {
    //     Collections.shuffle(cards);
    // }

    // Shuffle all cards randomly
    public void shuffleDeck() {
        // Creating a Random object
        Random random = new Random();

        // Running through all the cards and switching their places with other
        // cards randomly
        for (int i = 0; i < c.size(); i++) {
            // Generating the random location of the card to switch with
            int loc = random.nextInt(c.size());
            // Switching the cards
            Card card = c.elementAt(i);
            c.set(i, c.elementAt(loc));
            c.set(loc, card);
        }
    }
}
