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
    private static final int OFFSET = 30;
    private Game game;

    // Constructor for creating a pile from the deck
    public Pile(Deck deck, int numCards, Game game) {
        this.game = game;
        cards = new Vector<Card>();
        configureLayout();

        for (int i = 0; i < numCards; i++) {
            Card card = deck.drawCard();
            if (i > 0)
                cards.get(i - 1).setChild(card);
            if (i == numCards - 1)
                card.flipOver();
            card.setBounds(0, OFFSET * i, 120, 150);
            cards.add(card);
            card.setPile(this);
            layeredPane.add(card, Integer.valueOf(i));
        }
        addMouseListener(new PileMouseListener());
        recalculateSize();
    }

    private class PileMouseListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isEmpty() && game.getCardSelected()) {
                Card cardToAdd = game.getCard().get(0);
                cardToAdd.grabStack();
                addCard(cardToAdd); // Adding card to an empty space
                deselectStack(cardToAdd);
                game.deselectCard();
                game.updateNumMoves();
                game.unhighlightPiles();
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
    public void checkForStack() {
        Card lastKing = findLastFaceUpKing();

        if (lastKing != null && lastKing.isStackGood()) {
            Card lastCard = findLastCardInStack(lastKing);
            if (lastCard.getValue() == 1) {
                removeStack(lastKing);
                game.updateNumStacks();
            }
        }
    }

    // Finds the last king in the pile
    private Card findLastFaceUpKing() {
        Card card = null;
        // Find the last face-up King card
        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i).getValue() == 13 && cards.get(i).getFaceUp())
                card = cards.get(i);
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
            card.setBounds(0, OFFSET * cards.size(), 120, 150);
            cards.add(card);
            layeredPane.add(card, Integer.valueOf(cards.size()));
            card = card.getChild();
        }
        checkForStack();
        recalculateSize();
        // Ensures updates are shown in GUI
        revalidate();
        repaint();
    }

    // Take a stack of cards from the pile
    public void removeStack(Card card) {
        int loc = cards.indexOf(card);
        if (loc < 0)
            return;

        Card newBottomCard = loc > 0 ? cards.get(loc - 1) : null;
        if (newBottomCard != null) {
            newBottomCard.setChild(null);
            if (!newBottomCard.getFaceUp())
                newBottomCard.flipOver();
        }

        removeCardsFromLayer(card);
        cards.subList(loc, cards.size()).clear();
        recalculateSize();
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
        int newHeight = ((cards.size() - 1) * OFFSET) + 150;
        // setPreferredSize(new Dimension(120, newHeight));
        setSize(new Dimension(120, newHeight));

        // Ensures updates are show in GUI
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

    protected void highlightPile(){
        layeredPane.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 10, true));
    }

    protected void unhighlightPile(){
        layeredPane.setBorder(BorderFactory.createEmptyBorder());
    }
}