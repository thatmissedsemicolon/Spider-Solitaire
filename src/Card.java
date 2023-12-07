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
    protected enum Suit {Spades, Diamonds, Clubs, Hearts}

    private int value;
    private Suit suit;
    private boolean isFaceUp, isSelected;
    private Image frontImage, backImage;
    private Card child;
    private Game game;
    private Pile pile = null;

    // Constructor for Card class
    public Card(Suit suit, int val, Game game) {
        this.suit = suit;
        this.game = game;
        value = val;
        isFaceUp = isSelected = false;
        child = null;

        // Change this to a seperate class
        addMouseListener(new CardMouseListener());
        
        // Load card images
        try {
            Image cardImage = ImageIO.read(getClass().getResourceAsStream(getImagePath()));
            frontImage = cardImage.getScaledInstance(95, 145, Image.SCALE_SMOOTH);
            cardImage = ImageIO.read(getClass().getResourceAsStream("assets/magic.png"));
            backImage = cardImage.getScaledInstance(95, 145, Image.SCALE_SMOOTH);
        } 
        catch (IOException e) { 
            JOptionPane.showMessageDialog(null, 
            "Error loading card images: " + e.getMessage());
        }
        setOpaque(false);
        setPreferredSize(new Dimension(115, 145));
    }

    private class CardMouseListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isFaceUp) {
                Card c = Card.this;
                Vector<Card> cards = new Vector<Card>();
                boolean alreadySelected = c.selected();
                if (game.hasSelectedCards()) {
                    if (game.getCards().get(0) == c) {
                        while (c != null) { 
                            c.deselect(); 
                            c = c.getChild();
                        }
                        game.deselectCards();
                    } 
                    else if (game.getCards().get(0).getSuit() 
                            == c.getSuit()
                            && game.getCards().get(0).getValue() 
                            == (c.getValue() - 1)) {
                        if (!c.hasChild()) {
                            Card cardToAdd = game.getCards().get(0);
                            Pile cPile = cardToAdd.getPile();
                            cardToAdd.take();
                            pile.addCard(cardToAdd);

                            while (cardToAdd != null) { 
                                cardToAdd.deselect(); 
                                cardToAdd = cardToAdd.getChild(); 
                            }

                            game.deselectCards();
                            if (!cPile.isEmpty() && 
                                !cPile.getBottomCard().faceUp()) 
                                cPile.getBottomCard().flip();
                        }
                        game.updateNumMoves();
                    } 
                    else {
                        for (int i = 0; i < game.getCards().size(); i++)
                            game.getCards().get(i).deselect();
                        game.deselectCards();
                    }
                } 
                else {
                    if (alreadySelected) { 
                        while (c != null) { 
                            c.deselect(); 
                            c = c.getChild(); 
                        } 
                        game.deselectCards(); 
                    }
                    else if (c.isLegalStack()) { 
                        while (c != null) { 
                            c.select(); 
                            cards.add(c); 
                            c = c.getChild(); 
                        }
                        game.selectCards(cards); 
                    }
                }
                pile.recalculateSize();
                game.isWinner();
            }
        }
    }

    // Setters
    public void flip() { 
        isFaceUp = !isFaceUp; 
    }

    public void setChild(Card c) { 
        child = c; 
    }

    void setPile(Pile newPile) { 
        pile = newPile; 
    }

    public void take() { 
        getPile().removeStack(this); 
    }

    public void select() { 
        isSelected = true; 
        repaint(); 
    }

    public void deselect() { 
        isSelected = false; 
        repaint(); 
    }

    // Getters
    public boolean faceUp() { 
        return isFaceUp; 
    }

    public int getValue() { 
        return value; 
    }

    public Suit getSuit() { 
        return suit; 
    }

    public Card getChild() { 
        return child; 
    }

    public boolean hasChild() { 
        return child != null; 
    }

    public boolean selected() { 
        return isSelected; 
    }

    Pile getPile() { 
        return pile; 
    }

    // Check if the card and its children form a legal stack
    public boolean isLegalStack() {
        Card c = this, next = this.getChild();
        while (next != null) {
            if (c.getSuit() != next.getSuit()) 
                return false;
            else if (c.getValue() != next.getValue() + 1) 
                return false;
            c = next;
            next = next.getChild();
        }
        return true;
    }

    // Get the image file path for the card
    private String getImagePath() {
        return "assets/" + getValue() + getSuit().name().charAt(0) + ".png";
    }

    // Paint the card component
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int x = isSelected ? 20 : 0;
        graphics.drawImage(isFaceUp ? frontImage : backImage, x, 0, this);
    }
}
