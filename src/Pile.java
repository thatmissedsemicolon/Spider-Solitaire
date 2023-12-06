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

    // Constructor for creating a pile with an initial card
    public Pile(Card initialCard, Game game) {
        this.game = game;
        this.cards = new Vector<Card>();
        this.configureLayout();

        if (initialCard != null) {
            this.addCard(initialCard);
        }

        this.recalculateSize();
    }

    // Constructor for creating a pile from a deck
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

        // addMouseListener(new MouseAdapter() {
        //     @Override
        //     public void mouseClicked(MouseEvent e) {
        //         handlePileClick();
        //     }
        // });

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
        }
        }
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
        return cards.isEmpty() ? null : cards.firstElement();
    }

    // Get the bottom card in the pile
    public Card getBottomCard() {
        return cards.isEmpty() ? null : cards.lastElement();
    }

    // Check and resolve a stack if necessary
    public void checkAndResolveStack() {
        Card cardToCheck = this.findFirstFaceUpKing();
        if (cardToCheck != null && cardToCheck.isLegalStack()) {
            Card lastCard = this.findLastCardInStack(cardToCheck);
            if (lastCard.getRank() == 1)
                this.takeStack(cardToCheck);
        }
    }

    private Card findFirstFaceUpKing() {
        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i).getRank() == 13 && cards.get(i).faceUp())
                return cards.get(i); // Found a face-up King card
        return null; // No face-up King card found
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
            // System.out.println("Card bounds: " + card.getBounds()); // Debugging
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
    public void takeStack(Card card) {
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
        // System.out.println("New pile size: " + layeredPane.getPreferredSize());
        this.setPreferredSize(new Dimension(115, newHeight)); // Update Pile's preferred size as well

        // Added revalidate and repaint here too
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