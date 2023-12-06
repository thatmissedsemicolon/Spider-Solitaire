/*
The Card class represents a playing card in the Solitaire game.
It handles card interactions and rendering.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Vector;

public class Card extends JPanel {
    enum Suit { Spades, Diamonds, Clubs, Hearts }

    private int rank;
    private Suit suit;
    private boolean isFaceUp, isSelected;
    private Image frontImage, backImage;
    Card child;
    private Game Game;
    private Pile pile = null;

    // Constructor for Card class
    public Card(Suit suit, int rank, Game game) {
        this.suit = suit;
        this.rank = rank;
        this.isFaceUp = false;
        this.isSelected = false;
        this.child = null;
        this.Game = game;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isFaceUp) {
                    Card c = Card.this;
                    Vector<Card> cards = new Vector<Card>();
                    boolean alreadySelected = c.selected();
                    if (Game.hasSelectedCards()) {
                        if (Game.getCards().get(0) == c) {
                            while (c != null) { c.deselect(); c = c.getChild(); }
                            Game.deselectCards();
                        } else if (Game.getCards().get(0).getSuit() == c.getSuit()
                                && Game.getCards().get(0).getRank() == (c.getRank() - 1)) {
                            if (!c.hasChild()) {
                                Card cardToAdd = Game.getCards().get(0);
                                Pile cPile = cardToAdd.getPile();
                                cardToAdd.take();
                                pile.addCard(cardToAdd);
                                while (cardToAdd != null) { cardToAdd.deselect(); cardToAdd = cardToAdd.getChild(); }
                                Game.deselectCards();
                                if (!cPile.isEmpty() && !cPile.getBottomCard().faceUp()) cPile.getBottomCard().flip();
                            }
                        } else {
                            for (int i = 0; i < Game.getCards().size(); i++) { Game.getCards().get(i).deselect(); }
                            Game.deselectCards();
                        }
                    } else {
                        if (alreadySelected) { while (c != null) { c.deselect(); c = c.getChild(); } Game.deselectCards(); }
                        else if (c.isLegalStack()) { while (c != null) { c.select(); cards.add(c); c = c.getChild(); } Game.selectCards(cards); }
                    }
                    pile.recalculateSize();
                    Game.isWinner();
                }
            }
        });
        
        // Load card images
        try {
            Image cardImage = ImageIO.read(getClass().getResourceAsStream(getImagePath()));
            frontImage = cardImage.getScaledInstance(95, 145, Image.SCALE_SMOOTH);
            cardImage = ImageIO.read(getClass().getResourceAsStream("assets/yellow.png"));
            backImage = cardImage.getScaledInstance(95, 145, Image.SCALE_SMOOTH);
        } catch (IOException e) { e.printStackTrace(); }
        setOpaque(false);
        setPreferredSize(new Dimension(115, 145));
    }

    public void flip() { isFaceUp = !isFaceUp; }
    public boolean faceUp() { return isFaceUp; }
    public int getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public void setChild(Card c) { child = c; }
    public Card getChild() { return child; }
    public void take() { getPile().takeStack(this); }
    public boolean hasChild() { return child != null; }
    public void select() { isSelected = true; repaint(); }
    public void deselect() { isSelected = false; repaint(); }
    public boolean selected() { return isSelected; }
    void setPile(Pile newPile) { pile = newPile; }
    Pile getPile() { return pile; }

    // Check if the card and its children form a legal stack
    public boolean isLegalStack() {
        Card c = this, next = this.getChild();
        while (next != null) {
            if (c.getSuit() != next.getSuit()) return false;
            else if (c.getRank() != next.getRank() + 1) return false;
            c = next;
            next = next.getChild();
        }
        return true;
    }

    // Get the image file path for the card
    private String getImagePath() {
        return "assets/" + getRank() + getSuit().name().charAt(0) + ".png";
    }

    // Get a string representation of the card
    public String toString() {
        StringBuilder result = new StringBuilder();
        switch (getRank()) {
            case 1: result.append("Ace"); break;
            case 11: result.append("Jack"); break;
            case 12: result.append("Queen"); break;
            case 13: result.append("King"); break;
            default: result.append(getRank()); break;
        }
        result.append(" of ").append(getSuit());
        if (isFaceUp) result.append(" (face-up)");
        return result.toString();
    }

    // Paint the card component
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int x = isSelected ? 20 : 0;
        graphics.drawImage(isFaceUp ? frontImage : backImage, x, 0, this);
    }
}
