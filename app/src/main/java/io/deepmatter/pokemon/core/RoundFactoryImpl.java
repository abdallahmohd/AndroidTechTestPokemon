package io.deepmatter.pokemon.core;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.deepmatter.pokemon.model.Card;
import io.deepmatter.pokemon.model.Rarity;
import io.deepmatter.pokemon.util.random.Randomiser;
import io.deepmatter.pokemon.viewmodel.Round;

public class RoundFactoryImpl implements RoundFactory {

    private Randomiser randomiser;
    private final int NR_CARDS_IN_ROUND = 2;

    public RoundFactoryImpl(Randomiser randomiser) {
        this.randomiser = randomiser;
    }


    @NotNull
    @Override
    public Round buildRound(@NotNull List<Card> cards) {
        if (cards.size() < NR_CARDS_IN_ROUND || isAllSameRarity(cards)) {
            return new Round();
        }
        boolean tempDifferentRarity = false;
        Card cardOne = cards.get(randomiser.getIntInRange(0, cards.size()));
        Card cardTwo = new Card();
        while (!tempDifferentRarity) {
           Card temp = cards.get(randomiser.getIntInRange(0, cards.size()));
           if (cardOne.getRarity().ordinal() != temp.getRarity().ordinal()
                && !cardOne.getImage().equals(temp.getImage())) {
               cardTwo = temp;
               tempDifferentRarity = true;
           } else {
               cards.remove(temp);
           }
        }
        Card winningCard = cardOne.getRarity().ordinal() > cardTwo.getRarity().ordinal() ? cardOne : cardTwo;
        return new Round(Arrays.asList(cardOne, cardTwo), winningCard);
    }

    private boolean isAllSameRarity(List<Card> cards) {
        Set<Rarity> differentRarity = new HashSet<>();

        for (Card card : cards) {
            differentRarity.add(card.getRarity());
            if (differentRarity.size() > 1) {
                return false;
            }
        }
        return true;
    }
}
