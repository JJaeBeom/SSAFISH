package com.ssafish.service;

import com.ssafish.domain.deck.CardDeck;
import com.ssafish.domain.deck.CardDeckRepository;
import com.ssafish.domain.deck.Deck;
import com.ssafish.domain.deck.DeckRepository;
import com.ssafish.web.dto.CardDto;
import com.ssafish.web.dto.DeckDto;
import com.ssafish.web.dto.DeckRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardDeckRepository cardDeckRepository;

    public DeckRequestDto create(DeckRequestDto deckRequestDto) {
        // DB에 deck 저장
        DeckDto deckDto = deckRequestDto.getDeck();

        // 덱 생성한 사람 id 없거나 덱 이름 및 설명이 없는 경우
        if (deckDto.getUserId() <= 0 || deckDto.getDeckDescription() == null || deckDto.getDeckName() == null) {
            throw new IllegalArgumentException("Invalid deck condition");
        }

        // 대표 카드가 카드리스트 안에 없는 경우
        if (deckDto.getCardId() <= 0 || !deckRequestDto.getCardIdList().contains(deckDto.getCardId())) {
            throw  new IllegalArgumentException("Main card is not in cardIdList");
        }

        // cardIdList 길이가 25가 아닌 경우
        List<Long> cardIdList = deckRequestDto.getCardIdList();
        if (cardIdList.size() != 25) {
            throw new IllegalArgumentException("cardIdList should have 25 card IDs");
        }

        Deck deck = Deck.builder()
                .userId(deckDto.getUserId())
                .cardId(deckDto.getCardId())
                .deckName(deckDto.getDeckName())
                .deckDescription(deckDto.getDeckDescription())
                .deckUsageCount(0)
                .isPublic(true)
                .build();

        Deck createdDeck = deckRepository.save(deck);

        // DB에 card_deck 저장
        long deckId = createdDeck.getDeckId();

        for (int i=0;i<25;i++) {
            CardDeck card_decks = CardDeck.builder()
                    .cardId(cardIdList.get(i))
                    .deckId(deckId)
                    .build();
            cardDeckRepository.save(card_decks);
        }
        deckRequestDto.setDeck(createdDeck.toDto());
        return deckRequestDto;
    }
}
