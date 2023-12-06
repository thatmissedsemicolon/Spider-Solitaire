/*
The Card class represents a playing card in the Solitaire game.
It handles card interactions and rendering.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Stack;

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

    // Constructor for creating a Card object
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

    // Flips the card to change its face up or face down state
    public void flip() { this.isFaceUp = !this.isFaceUp; }

    // Checks if the card is face up
    public boolean isFaceUp() { return this.isFaceUp; }

    // Gets the rank of the card
    public int getRank() { return this.rank; }

    // Gets the suit of the card
    public Suit getSuit() { return this.suit; }

    // Sets the child card
    public void setChild(Card c) { this.child = c; }

    // Gets the child card
    public Card getChild() { return this.child; }

    // Takes the card from its current pile
    public void take() { getPile().takeStack(this); }

    // Checks if the card has a child card
    public boolean hasChild() { return this.child != null; }

    // Checks if the card is selected
    public boolean isSelected() { return this.isSelected; }

    // Selects the card and updates its appearance
    public void select() {
        this.isSelected = true;
        this.repaint();
    }

    // Deselects the card and updates its appearance
    public void deselect() {
        this.isSelected = false;
        this.repaint();
    }

    // Checks if the card is part of a legal stack
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

    // Initializes card images
    private void initializeImages() {
        try {
            frontImage = loadImage(getImagePath());
            backImage = loadImage("assets/yellow.png");
        } catch (IOException e) {
            handleImageLoadingError(e);
        }
        this.setOpaque(false);
    }

    // Loads an image from the specified path
    private Image loadImage(String imagePath) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(imagePath))
            .getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
    }

    // Handles errors during image loading
    private void handleImageLoadingError(IOException e) {
        e.printStackTrace();
    }

    // Mouse listener for card interaction
    private class CardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!isFaceUp) {
                return;
            }

            Card clickedCard = Card.this;
            Stack<Card> selectedCards = new Stack<>();

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

        // Handles selected cards when a card is clicked
        private void handleSelectedCards(Card clickedCard, Stack<Card> selectedCards) {
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

        // Handles a valid card stack when moving cards
        private void handleValidCardStack(Card firstSelectedCard) {
            if (!Card.this.hasChild()) {
                moveSelectedCardsToPile(firstSelectedCard);
            }
        }

        // Moves selected cards to the target pile
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

        // Deselects all selected cards
        private void deselectAllSelectedCards() {
            for (int i = 0; i < game.getCards().size(); i++) {
                game.getCards().get(i).deselect();
            }
            game.deselectCards();
        }

        // Selects a chain of cards starting from the clicked card
        private void selectCardChain(Card clickedCard, Stack<Card> selectedCards) {
            Card currentCard = clickedCard;
            while (currentCard != null) {
                currentCard.select();
                selectedCards.add(currentCard);
                currentCard = currentCard.getChild();
            }
        }

        // Deselects a chain of cards starting from the clicked card
        private void deselectCardChain(Card clickedCard) {
            Card currentCard = clickedCard;
            while (currentCard != null) {
                currentCard.deselect();
                currentCard = currentCard.getChild();
            }
        }
    }

    // Constructs the image path for the card based on its rank and suit
    private String getImagePath() {
        StringBuilder imgPath = new StringBuilder("assets/");
        imgPath.append(getRank());
        imgPath.append(getSuit().name().charAt(0));
        imgPath.append(".png");
        return imgPath.toString();
    }

    // Returns a string representation of the card
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

    // Paints the card component
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int x = isSelected ? 20 : 0;
        graphics.drawImage(isFaceUp ? frontImage : backImage, x, 0, this);
    }

    // Gets the pile to which the card belongs
    Pile getPile() { return pile; }

    // Sets the pile to which the card belongs
    void setPile(Pile newPile) { pile = newPile; }
}
