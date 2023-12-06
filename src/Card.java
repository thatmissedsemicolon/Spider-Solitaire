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
    private static final int CARD_WIDTH = 95;
    private static final int CARD_HEIGHT = 145;

    enum Suit { Spades, Diamonds, Clubs, Hearts }

    private int rank;
    private Suit suit;
    private boolean isFaceUp, isSelected;
    private Image frontImage, backImage;
    private Card child;
    private Game game;
    private Pile pile = null;

    public Card(Suit suit, int rank, Game game) {
        this.suit = suit;
        this.rank = rank;
        this.isFaceUp = false;
        this.isSelected = false;
        this.child = null;
        this.game = game;

        this.initializeImages();

        this.addMouseListener(new CardMouseListener());
        this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
    }

    public void flip() { isFaceUp = !isFaceUp; }
    public boolean isFaceUp() { return isFaceUp; }
    public int getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public void setChild(Card c) { child = c; }
    public Card getChild() { return child; }
    public void take() { getPile().takeStack(this); }
    public boolean hasChild() { return child != null; }
    public boolean isSelected() { return isSelected; }

    public void select() {
        this.isSelected = true;
        this.repaint();
    }

    public void deselect() {
        this.isSelected = false;
        this.repaint();
    }

    public boolean isLegalStack() {
        Card c = this;
        Card next = this.getChild();
        while (next != null) {
            if (c.getSuit() != next.getSuit())
                return false;
            else if (c.getRank() != next.getRank() + 1)
                return false;
            c = next;
            next = next.getChild();
        }
        return true;
    }

    private void initializeImages() {
        try {
            frontImage = loadImage(getImagePath());
            backImage = loadImage("assets/yellow.png");
        } catch (IOException e) {
            handleImageLoadingError(e);
        }
        this.setOpaque(false);
    }

    private Image loadImage(String imagePath) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(imagePath))
            .getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
    }

    private void handleImageLoadingError(IOException e) {
        e.printStackTrace();
    }

    private class CardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!isFaceUp) {
                return;
            }

            Card clickedCard = Card.this;
            Vector<Card> selectedCards = new Vector<>();

            if (isSelected) {
                deselectCardChain(clickedCard);
                game.deselectCards();
            } else if (game.hasSelectedCards()) {
                handleSelectedCards(clickedCard, selectedCards);
            } else {
                selectCardChain(clickedCard, selectedCards);
                game.selectCards(selectedCards);
            }

            pile.recalculateSize();
            game.isWinner();
        }

        private void handleSelectedCards(Card clickedCard, Vector<Card> selectedCards) {
            Card firstSelectedCard = game.getCards().get(0);

            if (firstSelectedCard == clickedCard) {
                deselectCardChain(clickedCard);
                game.deselectCards();
            } else if (firstSelectedCard.getSuit() == clickedCard.getSuit()
                    && firstSelectedCard.getRank() == (clickedCard.getRank() - 1)) {
                handleValidCardStack(firstSelectedCard);
            } else {
                deselectAllSelectedCards();
            }
        }

        private void handleValidCardStack(Card firstSelectedCard) {
            if (!Card.this.hasChild()) {
                moveSelectedCardsToPile(firstSelectedCard);
            }
        }

        private void moveSelectedCardsToPile(Card firstSelectedCard) {
            Card cardToAdd = firstSelectedCard;
            Pile cPile = cardToAdd.getPile();
            cardToAdd.take();
            pile.addCard(cardToAdd);
            deselectAllSelectedCards();
            if (!cPile.isEmpty() && !cPile.getBottomCard().isFaceUp()) {
                cPile.getBottomCard().flip();
            }
        }

        private void deselectAllSelectedCards() {
            for (int i = 0; i < game.getCards().size(); i++) {
                game.getCards().get(i).deselect();
            }
            game.deselectCards();
        }

        private void selectCardChain(Card clickedCard, Vector<Card> selectedCards) {
            Card currentCard = clickedCard;
            while (currentCard != null) {
                currentCard.select();
                selectedCards.add(currentCard);
                currentCard = currentCard.getChild();
            }
        }

        private void deselectCardChain(Card clickedCard) {
            Card currentCard = clickedCard;
            while (currentCard != null) {
                currentCard.deselect();
                currentCard = currentCard.getChild();
            }
        }
    }

    private String getImagePath() {
        StringBuilder imgPath = new StringBuilder("assets/");
        imgPath.append(getRank());
        imgPath.append(getSuit().name().charAt(0));
        imgPath.append(".png");
        return imgPath.toString();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        switch (getRank()) {
            case 1:
                result.append("Ace");
                break;
            case 11:
                result.append("Jack");
                break;
            case 12:
                result.append("Queen");
                break;
            case 13:
                result.append("King");
                break;
            default:
                result.append(getRank());
                break;
        }
        result.append(" of ");
        result.append(getSuit());
        if (isFaceUp)
            result.append(" (face-up)");
        return result.toString();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = isSelected ? 20 : 0;
        g.drawImage(isFaceUp ? frontImage : backImage, x, 0, this);
    }

    Pile getPile() { return pile; }
    void setPile(Pile newPile) { pile = newPile; }
}
