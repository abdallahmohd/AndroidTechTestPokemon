package io.deepmatter.pokemon.core;

import io.deepmatter.pokemon.model.Card;
import io.deepmatter.pokemon.model.Rarity;
import io.deepmatter.pokemon.util.random.Randomiser;
import io.deepmatter.pokemon.viewmodel.Round;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoundFactoryImplTest {
    private RoundFactory factory;
    @Mock
    private Randomiser randomiser;

    @Before
    public void setup() {
        factory = new RoundFactoryImpl(randomiser);
    }

    @Test
    public void generatesARoundWithCardsFromTwoDifferentRarities() {
        List<Card> cards = new ArrayList<>();
        cards.add(this.common("A"));
        cards.add(this.uncommon("B"));
        cards.add(this.common("C"));
        cards.add(this.common("D"));

        List<Card> expected = new ArrayList<>();
        expected.add(this.common("A"));
        expected.add(this.uncommon("B"));

        when(randomiser.getIntInRange(anyInt(), anyInt())).thenReturn(0, 1);

        assertThat(factory.buildRound(cards).getCards()).isEqualTo(expected);
    }

    @Test
    public void uncommonWinsOverCommon() {
        List<Card> cards = new ArrayList<>();
        cards.add(this.common("A"));
        cards.add(this.uncommon("B"));

       Card expected = this.uncommon("B");

        when(randomiser.getIntInRange(anyInt(), anyInt())).thenReturn(0, 1);

        assertThat(factory.buildRound(cards).getWinner()).isEqualTo(expected);
    }

    @Test
    public void buildsEmptyRoundIfCardsOfOnlyOneRarityAreSupplied() {
        List<Card> cards = new ArrayList<>();
        cards.add(this.common("A"));
        cards.add(this.common("B"));

        Round expected = new Round();

        assertThat(factory.buildRound(cards)).isEqualTo(expected);
    }

    /*
    Ensuring that when the randomiser provides two cards with the same rarity,
    a different second card is selected until the rarity is different.
     */
    @Test
    public void continuesToSelectDifferentSecondCardUntilBothCardsSelectedHaveSameRarity() {
        List<Card> cards = new ArrayList<>();
        cards.add(this.common("A"));
        cards.add(this.common("B"));
        cards.add(this.uncommon("C"));
        cards.add(this.common("D"));

        List<Card> expected = new ArrayList<>();
        expected.add(this.common("A"));
        expected.add(this.uncommon("C"));

        when(randomiser.getIntInRange(anyInt(), anyInt())).thenReturn(0, 1, 1);

        assertThat(factory.buildRound(cards).getCards()).isEqualTo(expected);
    }

    /*
    If there are either no cards or one card in the deck, we cannot create a round,
    and thus should return an empty round.
     */
    @Test
    public void buildsEmptyRoundIfLessThanTwoCardsAreInDeck() {
        List<Card> cards = Collections.singletonList(this.common("A"));

        Round expected = new Round();

        assertThat(factory.buildRound(cards)).isEqualTo(expected);
    }

    /*
    Given that each card selection is random, it is likely that the same pair that contain the same
    rarity are drawn multiple times, particularly in small decks. Removing the card from the list
    for the particular round improves the efficiency of the App.
     */
    @Test
    public void removeCardFromListWhenRarityIsSameAsFirstCard() {
        List<Card> cards = new ArrayList<>();
        cards.add(this.common("A"));
        cards.add(this.common("B"));
        cards.add(this.uncommon("C"));
        cards.add(this.common("D"));


        List<Card> spyList = spy(cards);

        when(randomiser.getIntInRange(anyInt(), anyInt())).thenReturn(0, 1, 1);

        factory.buildRound(cards);
        assertThat(spyList).containsExactly(this.common("A"),
                this.uncommon("C"),
                this.common("D"),
                null);
    }

    /*
    Two cards could have the same image but a different rarity. Need to ensure they are
    not presented to the user as it would affect gameplay. The user cannot guess which card
    is more rare if they have the same image.
     */
    @Test
    public void removeSecondCardFromListIfTwoCardsDrawnContainSameImage() {
        List<Card> cards = new ArrayList<>();
        cards.add(this.common("A"));
        cards.add(this.uncommon("A"));
        cards.add(this.uncommon("C"));
        cards.add(this.common("D"));


        List<Card> spyList = spy(cards);

        when(randomiser.getIntInRange(anyInt(), anyInt())).thenReturn(0, 1, 1);

        factory.buildRound(cards);
        assertThat(spyList).containsExactly(this.common("A"),
                this.uncommon("C"),
                this.common("D"),
                null);
    }

    private Card common(String image) {
        return new Card(image, Rarity.Common);
    }

    private Card uncommon(String image) {
        return new Card(image, Rarity.Uncommon);
    }
}

