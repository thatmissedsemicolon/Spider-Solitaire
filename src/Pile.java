/*
The Pile class represents a pile of cards in the Solitaire game.
It manages the cards in the pile and handles interactions with the pile.
*/

import java.util.Stack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class Pile extends JPanel {
    private Stack<Card> cards;
    private JLayeredPane layeredPane;
    private static final int OFFSET = 35;
    private Game game;

    // Constructor for creating a pile with an initial card
    public Pile(Card initialCard, Game game) {
        this.game = game;
        this.cards = new Stack<>();
        this.configureLayout();

        if (initialCard != null) {
            this.addCard(initialCard);
        }

        this.recalculateSize();
    }

    // Constructor for creating a pile from a deck
    public Pile(Deck deck, int numCards, Game game) {
        this.game = game;
        this.cards = new Stack<>();
        this.configureLayout();

        for (int depth = 0; depth < numCards; depth++) {
            Card card = deck.drawCard();
            if (depth > 0) {
                cards.peek().setChild(card);
            }
            if (depth == numCards - 1) {
                card.flip();
            }
            card.setBounds(0, OFFSET * depth, 115, 145);
            cards.push(card);
            card.setPile(this);
            layeredPane.add(card, Integer.valueOf(depth));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handlePileClick();
            }
        });

        recalculateSize();
    }

    // Configure the layout and layered pane
    private void configureLayout() {
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.layeredPane = new JLayeredPane();
        this.add(this.layeredPane);
    }

    // Handle the click on the pile
    private void handlePileClick() {
        if (this.isEmpty() && this.game.hasSelectedCards()) {
            Card cardToAdd = this.game.getCards().get(0);
            cardToAdd.take();
            this.addCard(cardToAdd); // Adding card to an empty space
            this.deselectStack(cardToAdd);
            this.game.deselectCards();
        }
    }

    // Check if the pile is empty
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // Get the top card in the pile
    public Card getTopCard() {
        return cards.isEmpty() ? null : cards.peek();
    }

    // Get the bottom card in the pile
    public Card getBottomCard() {
        return cards.isEmpty() ? null : cards.firstElement();
    }

    // Check and resolve a stack if necessary
    public void checkAndResolveStack() {
        Card cardToCheck = this.findFirstFaceUpKing();
        if (cardToCheck != null && cardToCheck.isLegalStack()) {
            Card lastCard = this.findLastCardInStack(cardToCheck);
            if (lastCard.getRank() == 1) {
                this.takeStack(cardToCheck);
            }
        }
    }

    // Find the first face-up King card in the pile
    private Card findFirstFaceUpKing() {
        for (Card card : cards) {
            if (card.getRank() == 13 && card.isFaceUp()) {
                return card; // King card to start check
            }
        }
        return null;
    }

    // Find the last card in a stack starting from a given card
    private Card findLastCardInStack(Card card) {
        while (card.getChild() != null) {
            card = card.getChild();
        }
        return card;
    }

    // Add a card or a stack of cards to the pile
    public void addCard(Card card) {
        while (card != null) {
            card.setPile(this);
            if (!cards.isEmpty()) {
                cards.peek().setChild(card);
            }
            card.setBounds(0, OFFSET * cards.size(), 115, 145);
            cards.push(card);
            layeredPane.add(card, Integer.valueOf(cards.size()));
            card = card.getChild();
        }

        this.checkAndResolveStack();
        this.recalculateSize();
    }

    // Take a stack of cards from the pile
    public void takeStack(Card card) {
        Stack<Card> tempStack = new Stack<>();
        while (!cards.isEmpty() && cards.peek() != card) {
            tempStack.push(cards.pop());
        }

        if (!cards.isEmpty()) {
            cards.pop(); // Pop the card we're taking
        }

        while (!tempStack.isEmpty()) {
            Card tempCard = tempStack.pop();
            tempCard.setChild(cards.peek()); // Set the child of the new top card
            cards.push(tempCard);
        }

        this.removeCardsFromLayer(card);
        this.recalculateSize();
    }

    // Remove cards from the layered pane
    private void removeCardsFromLayer(Card card) {
        while (card != null) {
            layeredPane.remove(card);
            card = card.getChild();
        }
    }

    // Recalculate the size of the pile
    public void recalculateSize() {
        layeredPane.setPreferredSize(new Dimension(115, (OFFSET * (cards.size() + 1)) + (145 - OFFSET)));
        this.revalidate();
        this.repaint();
    }

    // Deselect a stack of cards
    private void deselectStack(Card card) {
        while (card != null) {
            card.deselect();
            card = card.getChild();
        }
    }
}
