/*
The Card class represents a playing card in the Solitaire game.
It handles card interactions and rendering.
*/

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Card extends JPanel {
    protected enum Suit {Spades, Diamonds, Clubs, Hearts}

    private int value;
    private Suit suit;
    private Card child;
    private Game game;
    private Pile pile = null;
    private boolean faceUp, selected;
    private Image frontImage, backImage;

    // Constructor for Card class
    public Card(int val, Suit suit, Game game) {
        value = val;
        this.suit = suit;
        this.game = game;
        child = null;
        faceUp = selected = false;
        setUpCard();
    }

    private void setUpCard() {
        tryImage();
        addMouseListener(new CardMouseListener());
        setOpaque(false);
        setPreferredSize(new Dimension(120, 150));
    }

    private void tryImage() {
        // Load card images
        try {
            setUpImages();
        } 
        catch (IOException e) { 
            JOptionPane.showMessageDialog(null, 
            "Card Images Exceptio: " + e.getMessage());
        }
    }

    private void setUpImages() throws IOException {
        frontImage = ImageIO.read(getClass()
                              .getResource(getImagePath()))
                              .getScaledInstance(120, 150, Image.SCALE_SMOOTH);
        backImage = ImageIO.read(getClass()
                              .getResource("assets/uno.png"))
                              .getScaledInstance(120, 150, Image.SCALE_SMOOTH);
    }

    private class CardMouseListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            // Checks if the card is face up
            if (faceUp) {
                Card card = Card.this;
                Vector<Card> cards = new Vector<Card>();
                // Checks if card is already selected
                if (game.getCardSelected()) {
                    // Card is selected and clicked again, deselect all
                    if (card == game.getCard().get(0))
                        deselectAll(card, false);
                    
                    // Card(s) already and another card is selected that can
                    // be latched on to
                    else if ((card.getValue() - 1)==game.getCard().get(0).getValue()
                          && card.getSuit()==game.getCard().get(0).getSuit()) {
                        if (!card.hasChild()) {
                            Card addCard = game.getCard().get(0);
                            Pile cardPile = addCard.getPile();
                            addCard.grabStack();
                            pile.addCard(addCard);

                            while (addCard != null) { 
                                addCard.deselect(); 
                                addCard = addCard.getChild(); 
                            }

                            game.deselectCard();

                            if (!cardPile.isEmpty() 
                                && !cardPile.getBottomCard().getFaceUp()) 
                                cardPile.getBottomCard().flipOver();
                        }
                        game.updateNumMoves();
                    }
                    // Card(s) already selected & another is chosen that cannot
                    // be latched on to, deselect the other card(s)
                    else {
                        for (int i = 0; i < game.getCard().size(); i++)
                            game.getCard().get(i).deselect();
                        game.deselectCard();
                    }
                    game.unhighlightPiles();
                }
                else {
                    // Deselect all cards if already selected
                    if (card.getSelected())
                        deselectAll(card, true);
                    // No card is alrady selected, select all its cards
                    else if (card.isStackGood()) { 
                        while (card != null) { 
                            card.select();
                            cards.add(card);
                            card = card.getChild();
                        }
                        game.setSelectedCards(cards);
                        game.highlightPiles();
                    }
                }
                pile.recalculateSize();
                game.isWinner();
            }
        }
    }

    private void deselectAll(Card card, boolean unhighlight) {
        while (card != null) { 
            card.deselect();
            card = card.getChild();
        }
        game.deselectCard();
        if(unhighlight)
            game.unhighlightPiles();
    }

    // Check if the card and its children form a legal stack
    public boolean isStackGood() {
        Card c = this, kid = this.getChild();
        while (kid != null) {
            if (c.getValue() != (kid.getValue() + 1)
                || c.getSuit() != kid.getSuit()) 
                return false;
            c = kid;
            kid = kid.getChild();
        }
        return true;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y = 0;
        if (selected) 
            y = 18;

        if(faceUp) 
            g.drawImage(frontImage, 0, y, this);
        else
            g.drawImage(backImage, 0, y, this);
    }

    // Setters
    public void flipOver() { 
        faceUp = !faceUp; 
    }

    public void setChild(Card c) { 
        child = c; 
    }

    void setPile(Pile newPile) { 
        pile = newPile; 
    }

    public void grabStack() { 
        getPile().removeStack(this); 
    }

    public void select() { 
        selected = true; 
        repaint(); 
    }

    protected void deselect() { 
        selected = false; 
        repaint(); 
    }

    // Getters
    public boolean getFaceUp() { 
        return faceUp; 
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

    public boolean getSelected() { 
        return selected; 
    }

    Pile getPile() { 
        return pile; 
    }

    
    // Get the image file path for the card
    private String getImagePath() {
        return "assets/" + getValue() + " " + getSuit().name() + ".png";
    }
}
