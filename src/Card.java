import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Vector;

public class Card extends JPanel {
    enum Suit {
        Spades,
        Diamonds,
        Clubs,
        Hearts
    }

    private int rank;
    private Suit suit;
    private boolean isFaceUp,
            isSelected;
    private Image frontImage,
            backImage;
    Card child;
    private SpiderSolitaire spiderSolitaire;
    private Pile pile = null;

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
                if(isFaceUp)
                {
                    Card c = Card.this;
                    Vector<Card> cards = new Vector<Card>();
                    boolean alreadySelected = c.selected();
                    if(spiderSolitaire.hasSelectedCards())
                    {
                        if(spiderSolitaire.getCards().get(0) == c)
                        // clicking the same card twice just deselects it
                        {
                            while(c != null)
                            {
                                c.deselect();
                                c = c.getChild();
                            }
                            spiderSolitaire.deselectCards();
                        }
                        else if(spiderSolitaire.getCards().get(0).getSuit() == c.getSuit()
                                && spiderSolitaire.getCards().get(0).getRank() == (c.getRank() - 1))
                            // if clicking on a card which the hand can be added to
                        {
                            if(c.hasChild() == false)
                            // only place cards on the bottom of a pile
                            {
                                Card cardToAdd = spiderSolitaire.getCards().get(0);
                                Pile cPile = cardToAdd.getPile();
                                cardToAdd.take();
                                pile.addCard(cardToAdd);
                                while (cardToAdd != null) {
                                    cardToAdd.deselect();
                                    cardToAdd = cardToAdd.getChild();
                                }
                                spiderSolitaire.deselectCards();

                                if (!cPile.empty() && !cPile.bottom().faceUp())
                                    cPile.bottom().flip();  // flip the bottom card if it's face-down
                            }
                        }
                        else
                        // upon attempting an illegal move
                        {
                            for(int i = 0; i < spiderSolitaire.getCards().size(); i++)
                            {
                                spiderSolitaire.getCards().get(i).deselect();
                            }
                            spiderSolitaire.deselectCards();
                        } // end of nested if-else
                    }
                    else{
                        // if the hand is empty
                        if (alreadySelected) {
                            while (c != null) {
                                c.deselect();
                                c = c.getChild();
                            }
                            spiderSolitaire.deselectCards();
                        }
                        else if (c.isLegalStack()) {  // don't pick up cards that aren't in decreasing order/matching suits
                            while (c != null) {
                                c.select();
                                cards.add(c);
                                c = c.getChild();
                            }
                            spiderSolitaire.selectCards(cards);
                        }
                    }
                    pile.recalcSize();
                    spiderSolitaire.checkWin();
                }
            }
        });

        try {
            Image cardImage = ImageIO.read(getClass().getResourceAsStream(getImagePath()));
            frontImage = cardImage.getScaledInstance(95, 145, Image.SCALE_SMOOTH);
            cardImage = ImageIO.read(getClass().getResourceAsStream("assets/red_back.png"));
            backImage = cardImage.getScaledInstance(95, 145, Image.SCALE_SMOOTH);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        setOpaque(false);
        setPreferredSize(new Dimension(115, 145));
    }

    public void flip() {
        isFaceUp = !isFaceUp;
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
        getPile().take(this);
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

    public boolean isLegalStack() {
        /*
            checks whether the card and all its children are
            in strictly decreasing order and have matching suits.
        */
        Card c = this,
                next = this.getChild();
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

    Pile getPile()
    {
        return pile;
    }

    void setPile(Pile newPile)
    {
        pile = newPile;
    }
}
