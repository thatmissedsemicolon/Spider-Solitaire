/*
The Pile class represents a pile of cards in the Solitaire game.
It manages the cards in the pile and handles interactions with the pile.
*/

import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class Pile extends JPanel {
    private Vector<Card> cards;
    private JLayeredPane layeredPane;
    private static final int OFFSET = 35;
    private Game game;

    // Constructor for creating a pile from the deck
    public Pile(Deck deck, int numCards, Game game) {
        this.game = game;
        this.cards = new Vector<Card>();
        this.configureLayout();

        for (int depth = 0; depth < numCards; depth++) {
            Card card = deck.drawCard();
            if (depth > 0) {
                cards.get(depth - 1).setChild(card);
            }
            if (depth == numCards - 1) {
                card.flip();
            }
            card.setBounds(0, OFFSET * depth, 115, 145);
            cards.add(card);
            card.setPile(this);
            layeredPane.add(card, Integer.valueOf(depth));
        }
        addMouseListener(new PileMouseListener());
        recalculateSize();
    }

    private class PileMouseListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isEmpty() && game.hasSelectedCards()) {
                Card cardToAdd = game.getCards().get(0);
                cardToAdd.take();
                addCard(cardToAdd); // Adding card to an empty space
                deselectStack(cardToAdd);
                game.deselectCards();
                game.updateNumMoves();
            }
        }
    }

    // Configure the layout and layered pane
    private void configureLayout() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        layeredPane = new JLayeredPane();
        add(layeredPane);
    }

    // Check if the pile is empty
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // Get the top card in the pile
    public Card getTopCard() {
        return cards.isEmpty() ? null : cards.firstElement();
    }

    // Get the bottom card in the pile
    public Card getBottomCard() {
        return cards.isEmpty() ? null : cards.lastElement();
    }

    // Check and resolve a stack if necessary
    public void checkAndResolveStack() {
        Card cardToCheck = this.findLastFaceUpKing();

        if (cardToCheck != null && cardToCheck.isLegalStack()) {
            Card lastCard = this.findLastCardInStack(cardToCheck);
            if (lastCard.getValue() == 1) {
                removeStack(cardToCheck);
                game.updateNumStacks();
            }
        }
    }

    // Finds the last king in the pile
    private Card findLastFaceUpKing() {
        Card card = null;
        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i).getValue() == 13 && cards.get(i).faceUp())
                card = cards.get(i); // Found a face-up King card
        return card; // No face-up King card found
    }    

    // Find the last card in a stack starting from a given card
    private Card findLastCardInStack(Card card) {
        while (card.getChild() != null)
            card = card.getChild();
        return card;
    }

    // Add a card or a stack of cards to the pile
    public void addCard(Card card) {
        while (card != null) {
            card.setPile(this);
            if (!cards.isEmpty())
                getBottomCard().setChild(card);
            card.setBounds(0, OFFSET * cards.size(), 115, 145);
            cards.add(card);
            layeredPane.add(card, Integer.valueOf(cards.size()));
            card = card.getChild();
        }

        this.checkAndResolveStack();
        this.recalculateSize();

        // Added revalidate and repaint to ensure updates are reflected
        this.revalidate();
        this.repaint();
    }

    // Take a stack of cards from the pile
    public void removeStack(Card card) {
        int index = cards.indexOf(card);
        if (index < 0)
            return;

        Card newBottomCard = index > 0 ? cards.get(index - 1) : null;
        if (newBottomCard != null) {
            newBottomCard.setChild(null);
            if (!newBottomCard.faceUp())
                newBottomCard.flip();
        }

        this.removeCardsFromLayer(card);
        cards.subList(index, cards.size()).clear();
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
        int newHeight = (OFFSET * (cards.size() - 1)) + 145;
        layeredPane.setPreferredSize(new Dimension(115, newHeight));
        setPreferredSize(new Dimension(115, newHeight)); // Update Pile's preferred size as well

        // Added revalidate and repaint here too
        revalidate();
        repaint();
    }

    // Deselect a stack of cards
    private void deselectStack(Card card) {
        while (card != null) {
            card.deselect();
            card = card.getChild();
        }
    }
}