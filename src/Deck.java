/*
The Deck class represents a deck of cards in the Solitaire game.
It manages the cards in the deck, including shuffling and drawing.
*/

import java.util.Stack;
import java.util.Collections;

public class Deck {
    private Stack<Card> cards;
    private int difficultyLevel;
    private Game associatedGame;

    // Constructor for creating a default deck with 1 suit
    public Deck(Game game) {
        this(1, game); // Default to 1 suit if not specified
    }

    // Constructor for creating a deck with a specified number of suits
    public Deck(int numSuits, Game game) {
        this.difficultyLevel = validateDifficultyLevel(numSuits);
        this.associatedGame = game;
        this.cards = new Stack<Card>();
        this.initializeDeck();
        this.shuffleDeck();
    }

    // Check if the deck is empty
    public boolean isEmpty() {
        return cards.empty();
    }

    // Peek at the top card of the deck without removing it
    public Card peekTopCard() {
        return cards.empty() ? null : cards.peek();
    }

    // Draw a card from the deck (removes and returns the top card)
    public Card drawCard() {
        return cards.empty() ? null : cards.pop();
    }

    // Validate and set the difficulty level based on the number of suits
    private int validateDifficultyLevel(int numSuits) {
        if (numSuits != 1 && numSuits != 2 && numSuits != 4) {
            numSuits = 1; // Default to 1 if an invalid number is provided
        }
        return numSuits;
    }

    // Initialize the deck with cards based on the difficulty level
    private void initializeDeck() {
        for (int suitIndex = 0; suitIndex < this.difficultyLevel; suitIndex++) {
            for (int rank = 0; rank < 104 / this.difficultyLevel; rank++) {
                Card.Suit suit = Card.Suit.values()[suitIndex];
                int cardRank = (rank % 13) + 1;
                cards.push(new Card(suit, cardRank, associatedGame));
            }
        }
    }

    // Shuffle the deck
    private void shuffleDeck() {
        Collections.shuffle(cards);
    }
}