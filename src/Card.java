import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Vector;

public class Card extends JPanel {
    enum Suit {
        Spades, Diamonds, Clubs, Hearts
    }

    private int rank;
    private Suit suit;
    private boolean isFaceUp, isSelected;
    private Image frontImage, backImage;
    Card child;
    private SpiderSolitaire spiderSolitaire;
    private Pile pile;

    public Card(Suit s, int r, SpiderSolitaire solitaire) {
        suit = s;
        rank = r;
        isFaceUp = false;
        isSelected = false;
        child = null;
        spiderSolitaire = solitaire;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isFaceUp) {
                    Card c = Card.this;
                    Vector<Card> cards = new Vector<>();
                    boolean alreadySelected = c.selected();

                    if (spiderSolitaire.hasSelectedCards()) {
                        if (spiderSolitaire.getCards().get(0) == c) {
                            while (c != null) {
                                c.deselect();
                                c = c.getChild();
                            }
                            spiderSolitaire.deselectCards();
                        } else if (spiderSolitaire.getCards().get(0).getSuit() == c.getSuit()
                                && spiderSolitaire.getCards().get(0).getRank() == (c.getRank() - 1)) {
                            if (!c.hasChild()) {
                                Card cardToAdd = spiderSolitaire.getCards().get(0);
                                Pile cPile = cardToAdd.getPile();
                                cardToAdd.take();
                                pile.addCard(cardToAdd);
                                while (cardToAdd != null) {
                                    cardToAdd.deselect();
                                    cardToAdd = cardToAdd.getChild();
                                }
                                spiderSolitaire.deselectCards();

                                if (!cPile.empty() && !cPile.bottom().faceUp()) {
                                    cPile.bottom().flip();
                                }
                            }
                        } else {
                            for (Card card : spiderSolitaire.getCards()) {
                                card.deselect();
                            }
                            spiderSolitaire.deselectCards();
                        }
                    } else if (!alreadySelected && c.isLegalStack()) {
                        while (c != null) {
                            c.select();
                            cards.add(c);
                            c = c.getChild();
                        }
                        spiderSolitaire.selectCards(cards);
                    }
                    pile.recalcSize();
                    spiderSolitaire.checkWin();
                }
            }
        });

        try {
            frontImage = ImageIO.read(getClass().getResourceAsStream(getImagePath()))
                            .getScaledInstance(95, 145, Image.SCALE_SMOOTH);
            backImage = ImageIO.read(getClass().getResourceAsStream("assets/red_back.png"))
                            .getScaledInstance(95, 145, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setOpaque(false);
        setPreferredSize(new Dimension(115, 145));
    }

    public boolean isLegalStack() {
        Card current = this;
        Card next = this.getChild();
        while (next != null) {
            if (current.getSuit() != next.getSuit() || current.getRank() != next.getRank() + 1) {
                return false;
            }
            current = next;
            next = next.getChild();
        }
        return true;
    }

    public void flip() {
        isFaceUp = !isFaceUp;
        repaint();
    }

    public boolean faceUp() {
        return isFaceUp;
    }

    public int getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setChild(Card c) {
        child = c;
    }

    public Card getChild() {
        return child;
    }

    public void take() {
        if (pile != null) {
            pile.take(this);
        }
    }

    public boolean hasChild() {
        return child != null;
    }

    public void select() {
        isSelected = true;
        repaint();
    }

    public void deselect() {
        isSelected = false;
        repaint();
    }

    public boolean selected() {
        return isSelected;
    }

    private String getImagePath() {
        return "assets/" + rank + suit.name().charAt(0) + ".png";
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = isSelected ? 20 : 0;
        g.drawImage(isFaceUp ? frontImage : backImage, x, 0, this);
    }

    Pile getPile() {
        return pile;
    }

    void setPile(Pile newPile) {
        pile = newPile;
    }
}
