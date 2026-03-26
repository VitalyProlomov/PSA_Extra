class PokerReplayViewer {
    constructor() {
        this.game = window.gameData || null;
        this.curStreetStr = "preflop";
        this.curActionIndex = -1;
        this.prevActionLabel = null;
        this.hashPlayerIndexMap = {};

        // Maps matching JavaFX fillMaps()
        this.suitColorMap = {
            "SPADES": "spades",
            "HEARTS": "hearts",
            "DIAMONDS": "diamonds",
            "CLUBS": "clubs"
        };

        this.rankCharMap = {
            "TWO": "2", "THREE": "3", "FOUR": "4", "FIVE": "5",
            "SIX": "6", "SEVEN": "7", "EIGHT": "8", "NINE": "9",
            "TEN": "T", "JACK": "J", "QUEEN": "Q", "KING": "K", "ACE": "A"
        };

        this.actionStringMap = {
            "CHECK": "Check", "BET": "Bet", "FOLD": "Fold",
            "CALL": "Call", "RAISE": "Raise", "BLIND": "Blind",
            "ANTE": "Ante", "STRADDLE": "Straddle", "MISSED_BLIND": "Missed Blind"
        };

        if (this.game) {
            this.initListeners();
            this.initialize();
        }
    }

    initListeners() {
        document.getElementById('nextActionBtn').addEventListener('click', () => this.showNextAction());
    }

    formatMoney(amount) {
        return parseFloat(amount).toFixed(2).replace(',', '.');
    }

    initialize() {
        this.updateTableInfo();
        this.initializeLabels();
    }

    updateTableInfo() {
        const hero = this.game.players["Hero"];
        if (!hero) return;

        // Hero Cards (visible from start)
        this.setPlayerCard("hero", "left", hero.hand.card1);
        this.setPlayerCard("hero", "right", hero.hand.card2);

        document.getElementById('heroPositionLabel').textContent = `${hero.position}: Hero`;
        const initBalance = this.game.initialBalances["Hero"];
        document.getElementById('heroBalanceLabel').textContent = `${this.formatMoney(initBalance)}$`;

        if (hero.position === "BTN") {
            document.getElementById('heroButtonIcon').style.display = 'block';
        }

        // Opponent Seats Mapping (matching JavaFX logic exactly)
        const heroSeatNumber = hero.seatNumber;

        for (const playerId in this.game.players) {
            if (playerId === "Hero") continue;

            const p = this.game.players[playerId];
            let index = p.seatNumber - heroSeatNumber;
            if (index < 0) index += 9; // MAX_PLAYERS = 9
            if (index === 0) index = 9;
            if (index > 8) index = index % 8;
            if (index === 0) index = 8;

            this.hashPlayerIndexMap[playerId] = index;

            const balanceEl = document.getElementById(`player${index}BalanceLabel`);
            const posEl = document.getElementById(`player${index}PositionLabel`);
            const btnIcon = document.getElementById(`player${index}ButtonIcon`);

            const balance = this.game.initialBalances[playerId];
            balanceEl.textContent = `${this.formatMoney(balance)}$`;

            const userName = p.ref ? p.ref.userName : p.id;
            posEl.textContent = `${p.position}: ${userName}`;

            if (p.position === "BTN") {
                btnIcon.style.display = 'block';
            }

            // Show card backs for opponents
            this.setCardBack(`player${index}`, "left", true);
            this.setCardBack(`player${index}`, "right", true);
        }
    }

    setPlayerCard(playerPrefix, side, card) {
        const rectEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardRect`);
        const rankEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardRank`);
        const backEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardBack`);

        rectEl.className = `card-rect ${this.suitColorMap[card.suit]}`;
        rectEl.style.display = 'block';
        rankEl.textContent = this.rankCharMap[card.rank];
        rankEl.style.display = 'flex';
        if (backEl) backEl.style.display = 'none';
    }

    setCardBack(playerPrefix, side, visible) {
        const backEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardBack`);
        if (backEl) backEl.style.display = visible ? 'block' : 'none';
    }

    initializeLabels() {
        // Info panel already populated by Thymeleaf
        // Just set initial pot
        document.getElementById('potLabel').textContent = "POT: ";
    }

    showNextAction() {
        this.curActionIndex++;
        let curStreet = null;

        // Street Transition Logic (matching JavaFX exactly)
        if (this.curStreetStr === "preflop") {
            curStreet = this.game.preFlop;
            if (!curStreet || !curStreet.allActions || curStreet.allActions.length <= this.curActionIndex) {
                this.curStreetStr = "flop";
                this.curActionIndex = 0;
                if (this.game.flop) {
                    this.renderCommunityCards(this.game.flop.board.cards, "flop");
                    this.updatePot(this.game.preFlop.potAfterBetting);
                    this.clearPrevAction();
                    this.curActionIndex = -1;
                    return;
                }
            }
        }

        if (this.curStreetStr === "flop") {
            curStreet = this.game.flop;
            if (!curStreet) {
                this.updatePot(this.game.preFlop.potAfterBetting);
                if (this.game.preFlop.playersAfterBetting && this.game.preFlop.playersAfterBetting.length > 1) {
                    this.displayShownCards(this.game.preFlop);
                }
                return;
            } else if (curStreet.allActions.length <= this.curActionIndex) {
                this.curStreetStr = "turn";
                this.curActionIndex = 0;
                if (this.game.turn) {
                    this.renderCommunityCards(this.game.turn.board.cards, "turn");
                    this.updatePot(this.game.flop.potAfterBetting);
                    this.clearPrevAction();
                    this.curActionIndex = -1;
                    return;
                }
            }
        }

        if (this.curStreetStr === "turn") {
            curStreet = this.game.turn;
            if (!curStreet) {
                this.updatePot(this.game.flop.potAfterBetting);
                if (this.game.flop.playersAfterBetting && this.game.flop.playersAfterBetting.length > 1) {
                    this.displayShownCards(this.game.flop);
                }
                return;
            } else if (curStreet.allActions.length <= this.curActionIndex) {
                this.curStreetStr = "river";
                this.curActionIndex = 0;
                if (this.game.river) {
                    this.renderCommunityCards(this.game.river.board.cards, "river");
                    this.updatePot(this.game.turn.potAfterBetting);
                    this.clearPrevAction();
                    this.curActionIndex = -1;
                    return;
                }
            }
        }

        if (this.curStreetStr === "river") {
            curStreet = this.game.river;
            if (!curStreet) {
                this.updatePot(this.game.turn.potAfterBetting);
                if (this.game.turn.playersAfterBetting && this.game.turn.playersAfterBetting.length > 1) {
                    this.displayShownCards(this.game.turn);
                }
                return;
            }
            if (curStreet.allActions.length <= this.curActionIndex) {
                if (curStreet.playersAfterBetting && curStreet.playersAfterBetting.length > 1) {
                    this.displayShownCards(curStreet);
                }
                return;
            }
        }

        // Process Action
        const nextAction = curStreet.allActions[this.curActionIndex];
        const isHero = nextAction.playerId === "Hero";
        const seatIndex = isHero ? "hero" : this.hashPlayerIndexMap[nextAction.playerId];

        const curActionLabel = document.getElementById(`${isHero ? 'hero' : 'player' + seatIndex}ActionLabel`);

        this.clearPrevAction();

        let actionText = this.actionStringMap[nextAction.actionType] || nextAction.actionType;
        if (!["CHECK", "FOLD", "CALL"].includes(nextAction.actionType)) {
            actionText += ` ${this.formatMoney(nextAction.amount)}$`;
        }

        curActionLabel.textContent = actionText;
        this.prevActionLabel = curActionLabel;

        // Handle Fold Visuals
        if (nextAction.actionType === "FOLD") {
            if (isHero) {
                document.getElementById('heroLeftCardRect').style.opacity = 0.3;
                document.getElementById('heroRightCardRect').style.opacity = 0.3;
            } else {
                const leftBack = document.getElementById(`player${seatIndex}LeftCardBack`);
                const rightBack = document.getElementById(`player${seatIndex}RightCardBack`);
                if (leftBack) leftBack.style.opacity = 0.1;
                if (rightBack) rightBack.style.opacity = 0.1;
            }
        }
    }

    renderCommunityCards(cards, street) {
        if (street === "flop") {
            this.renderCardSlot("flopCard1", cards[0]);
            this.renderCardSlot("flopCard2", cards[1]);
            this.renderCardSlot("flopCard3", cards[2]);
        } else if (street === "turn") {
            this.renderCardSlot("turnCard", cards[3]);
        } else if (street === "river") {
            this.renderCardSlot("riverCard", cards[4]);
        }
    }

    renderCardSlot(elementId, card) {
        const rectEl = document.getElementById(`${elementId}Rect`);
        const rankEl = document.getElementById(`${elementId}Rank`);

        rectEl.className = `card-rect ${this.suitColorMap[card.suit]}`;
        rectEl.style.display = 'block';
        rankEl.textContent = this.rankCharMap[card.rank];
        rankEl.style.display = 'flex';
    }

    displayShownCards(street) {
        if (!street.playersAfterBetting) return;

        street.playersAfterBetting.forEach(p => {
            if (p.id === "Hero") return;
            const index = this.hashPlayerIndexMap[p.id];
            const playerObj = this.game.players[p.id];

            if (playerObj && playerObj.hand) {
                this.setPlayerCard(`player${index}`, "left", playerObj.hand.card1);
                this.setPlayerCard(`player${index}`, "right", playerObj.hand.card2);
            }
        });
    }

    updatePot(amount) {
        document.getElementById('potLabel').textContent = `POT: ${this.formatMoney(amount)}$`;
    }

    clearPrevAction() {
        if (this.prevActionLabel) {
            this.prevActionLabel.textContent = "";
        }
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new PokerReplayViewer();
});