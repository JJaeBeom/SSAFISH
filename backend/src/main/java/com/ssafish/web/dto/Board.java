package com.ssafish.web.dto;

import com.ssafish.web.dto.Phase.ChoosePhase;
import com.ssafish.web.dto.Phase.GameOverPhase;
import com.ssafish.web.dto.Phase.GameStartPhase;
import com.ssafish.web.dto.Phase.Phase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Component
@Scope("prototype")
public class Board {
    private final GameStartPhase gameStartPhase;
    private final GameOverPhase gameOverPhase;
    private long userId;
    private long deckId;
    private long timeLimit;
    private long capacity;
    private boolean isStarted = false;
    private GameStatus gameStatus;
    private ChoosePhase currentPhase;


    public void play(GameData gameData) {
        isStarted = true;
        gameStartPhase.run(gameData, gameStatus);

        while (!gameStatus.isGameOver()) {
            currentPhase = gameStatus.getCurrentPhase();

            currentPhase.startTurnTimer(gameStatus);
        }

        gameOverPhase.run();
        isStarted = false;
    }

    public void handlePub(GameData gameData) {
        // pub 처리
        currentPhase.handlePub(gameData, gameStatus);
    }

    public void addPlayer(long userId, String nickname, boolean isBot) {
        gameStatus.addPlayer(userId, nickname, isBot);
    }

    public void removePlayer(long userId) {
        gameStatus.removePlayer(userId);
    }
}
