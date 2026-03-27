class PokerReplayViewer {
    constructor() {
        console.log("=== PokerReplayViewer Constructor ===");
        console.log("window.gameData exists:", window.gameData !== undefined && window.gameData !== null);

        this.game = window.gameData || null;
        this.curStreetStr = "preflop";
        this.curActionIndex = -1;
        this.prevActionBadge = null;
        this.hashPlayerIndexMap = {};
        this.shownCardsStreet = null;  //  Track which street cards were shown

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

        this.actionClassMap = {
            "CHECK": "action-check",
            "BET": "action-bet",
            "FOLD": "action-fold",
            "CALL": "action-call",
            "RAISE": "action-raise",
            "BLIND": "action-blind",
            "ANTE": "action-ante",
            "STRADDLE": "action-straddle",
            "MISSED_BLIND": "action-blind"
        };

        if (this.game) {
            console.log("✅ Game data loaded, initializing...");
            this.initListeners();
            this.initialize();
            this.setupResponsiveScaling();
        } else {
            console.error("❌ NO GAME DATA FOUND!");
        }
    }

    initListeners() {
        const nextBtn = document.getElementById('nextActionBtn');
        if (nextBtn) {
            nextBtn.addEventListener('click', () => this.showNextAction());
            console.log("✅ Next action button listener attached");
        }

        const restartBtn = document.getElementById('restartBtn');
        if (restartBtn) {
            restartBtn.addEventListener('click', () => {
                console.log("=== Restart button clicked ===");
                this.resetAllStates();
            });
            console.log("✅ Restart button listener attached");
        }
    }

    formatMoney(amount) {
        return parseFloat(amount).toFixed(2).replace(',', '.');
    }

    initialize() {
        console.log("=== initialize() ===");

        if (!this.game || !this.game.players) {
            console.error("❌ CRITICAL: Game or players data is missing!");
            return;
        }

        this.updateTableInfo();
    }

    setupResponsiveScaling() {
        const tableContainer = document.querySelector('.table-container');
        const tableWrapper = document.querySelector('.table-wrapper');

        if (!tableContainer || !tableWrapper) return;

        const scaleTable = () => {
            const wrapperWidth = tableWrapper.offsetWidth;
            const containerWidth = tableContainer.offsetWidth;
            const containerHeight = tableContainer.offsetHeight;
            const wrapperHeight = tableWrapper.offsetHeight;

            const scaleX = wrapperWidth / containerWidth;
            const scaleY = wrapperHeight / containerHeight;
            const scale = Math.min(scaleX, scaleY, 1);

            if (scale < 1) {
                tableContainer.style.transform = `scale(${scale})`;
            } else {
                tableContainer.style.transform = 'scale(1)';
            }
        };

        window.addEventListener('resize', scaleTable);
        window.addEventListener('load', scaleTable);
        scaleTable();
    }

    updateTableInfo() {
        console.log("=== updateTableInfo() ===");
        const hero = this.game.players["Hero"];

        if (!hero) {
            console.error("❌ Hero not found in game!");
            console.log("Available players:", Object.keys(this.game.players));
            return;
        }

        // Hero Cards
        if (hero.hand && hero.hand.card1 && hero.hand.card2) {
            this.setPlayerCard("hero", "left", hero.hand.card1);
            this.setPlayerCard("hero", "right", hero.hand.card2);
        }

        document.getElementById('heroPositionLabel').textContent = `${hero.position}: Hero`;
        const initBalance = this.game.initialBalances["Hero"];
        document.getElementById('heroBalanceLabel').textContent = `${this.formatMoney(initBalance)}$`;

        if (hero.position === "BTN") {
            document.getElementById('heroButtonIcon').style.display = 'block';
        }

        // Opponent Seats
        const heroSeatNumber = hero.seatNumber;

        // First, hide ALL opponent seats
        for (let i = 1; i <= 8; i++) {
            const seat = document.getElementById(`seat-${i}`);
            if (seat) {
                seat.style.display = 'none';
            }
        }

        // Then, only show seats that have players
        for (const playerId in this.game.players) {
            if (playerId === "Hero") continue;

            const p = this.game.players[playerId];
            let index = p.seatNumber - heroSeatNumber;
            if (index < 0) index += 9;
            if (index === 0) index = 9;
            if (index > 8) index = index % 8;
            if (index === 0) index = 8;

            this.hashPlayerIndexMap[playerId] = index;

            const seat = document.getElementById(`seat-${index}`);
            if (seat) {
                seat.style.display = 'block';
            }

            const balanceEl = document.getElementById(`player${index}BalanceLabel`);
            const posEl = document.getElementById(`player${index}PositionLabel`);
            const btnIcon = document.getElementById(`player${index}ButtonIcon`);

            const balance = this.game.initialBalances[playerId];
            balanceEl.textContent = `${this.formatMoney(balance)}$`;

            const userName = p.userName || p.id;
            posEl.textContent = `${p.position}: ${userName}`;
            posEl.title = userName;

            if (p.position === "BTN") {
                btnIcon.style.display = 'block';
            }

            this.setCardBack(`player${index}`, "left", true);
            this.setCardBack(`player${index}`, "right", true);
        }
    }

    setPlayerCard(playerPrefix, side, card) {
        const rectEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardRect`);
        const rankEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardRank`);
        const backEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardBack`);

        if (rectEl && rankEl && card && card.suit && card.rank) {
            rectEl.className = `card-rect ${this.suitColorMap[card.suit]}`;
            rectEl.style.display = 'block';
            rankEl.textContent = this.rankCharMap[card.rank];
            rankEl.style.display = 'flex';
            if (backEl) backEl.style.display = 'none';
        } else {
            if (backEl) backEl.style.display = 'block';
        }
    }

    setCardBack(playerPrefix, side, visible) {
        const backEl = document.getElementById(`${playerPrefix}${side === 'left' ? 'Left' : 'Right'}CardBack`);
        if (backEl) backEl.style.display = visible ? 'block' : 'none';
    }

    resetAllStates() {
        console.log("=== resetAllStates() ===");

        this.curStreetStr = "preflop";
        this.curActionIndex = -1;
        this.prevActionBadge = null;
        this.shownCardsStreet = null;  // ✅ Reset all-in tracking

        document.getElementById('potLabel').textContent = "POT: ";

        // Reset Hero cards
        document.getElementById('heroLeftCardRect').style.opacity = 1;
        document.getElementById('heroRightCardRect').style.opacity = 1;
        this.hideActionBadge("hero");

        // Reset all opponent seats
        for (let i = 1; i <= 8; i++) {
            this.hideActionBadge(`player${i}`);
            const leftCardBack = document.getElementById(`player${i}LeftCardBack`);
            const rightCardBack = document.getElementById(`player${i}RightCardBack`);
            if (leftCardBack) {
                leftCardBack.style.display = 'block';
                leftCardBack.style.opacity = 1;
            }
            if (rightCardBack) {
                rightCardBack.style.display = 'block';
                rightCardBack.style.opacity = 1;
            }
        }

        // Reset community cards
        ['flopCard1', 'flopCard2', 'flopCard3', 'turnCard', 'riverCard'].forEach(id => {
            const rectEl = document.getElementById(`${id}Rect`);
            const rankEl = document.getElementById(`${id}Rank`);
            if (rectEl) rectEl.style.display = 'none';
            if (rankEl) rankEl.style.display = 'none';
        });

        console.log("✅ All states reset to beginning of hand");
    }

    showActionBadge(playerPrefix, actionType, amount) {
        const badgeEl = document.getElementById(`${playerPrefix}ActionBadge`);
        if (!badgeEl) return;

        if (this.prevActionBadge && this.prevActionBadge !== badgeEl) {
            this.prevActionBadge.classList.remove('show');
        }

        let actionText = this.actionStringMap[actionType] || actionType;
        if (!["CHECK", "FOLD", "CALL"].includes(actionType)) {
            actionText += ` ${this.formatMoney(amount)}$`;
        }

        badgeEl.textContent = actionText;
        badgeEl.className = `action-badge show ${this.actionClassMap[actionType] || 'action-check'}`;

        this.prevActionBadge = badgeEl;
    }

    hideActionBadge(playerPrefix) {
        const badgeEl = document.getElementById(`${playerPrefix}ActionBadge`);
        if (badgeEl) {
            badgeEl.classList.remove('show');
            badgeEl.textContent = '';
        }
    }

    showNextAction() {
        console.log("=== showNextAction() ===");
        console.log("curStreetStr:", this.curStreetStr);
        console.log("curActionIndex before:", this.curActionIndex);

        this.curActionIndex++;
        let curStreet = null;

        // ========== PREFLOP ==========
        if (this.curStreetStr === "preflop") {
            curStreet = this.game.preFlop;
            if (!curStreet || !curStreet.allActions || curStreet.allActions.length <= this.curActionIndex) {
                // ✅ CHECK ALL-IN ON PREFLOP
                if (curStreet && curStreet.isAllIn && curStreet.playersAfterBetting &&
                 curStreet.playersAfterBetting.length > 1 && !this.shownCardsStreet) {
                    console.log("🎯 PREFLOP ALL-IN - Showing cards immediately");
                    this.updatePot(curStreet.potAfterBetting);
                    this.displayShownCards(curStreet);
                    this.shownCardsStreet = "preflop";

                    return;
                    // Continue to show community cards if they exist
                }

                // End of preflop actions
                this.curStreetStr = "flop";
                this.curActionIndex = 0;



                if (this.game.flop && this.game.flop.board && this.game.flop.board.cards && this.game.flop.board.cards.length > 0) {
                    this.renderCommunityCards(this.game.flop.board.cards, "flop");
                    if (!this.shownCardsStreet) {
                        this.updatePot(this.game.preFlop.potAfterBetting);
                    }
                    this.hideActionBadge("hero");
                    for (let i = 1; i <= 8; i++) {
                        this.hideActionBadge(`player${i}`);
                    }
                    this.curActionIndex = -1;
                    return;
                } else {
                    // No flop - hand ended preflop
                    this.updatePot(this.game.preFlop.potAfterBetting);
                    if (!this.shownCardsStreet && this.game.preFlop && this.game.preFlop.playersAfterBetting && this.game.preFlop.playersAfterBetting.length > 1) {
                        console.log("Preflop showdown - showing cards");
                        this.displayShownCards(this.game.preFlop);
                    }
                    return;
                }
            }
        }

        // ========== FLOP ==========
        if (this.curStreetStr === "flop") {
            curStreet = this.game.flop;

            if (!curStreet) {
                this.updatePot(this.game.preFlop.potAfterBetting);
                if (!this.shownCardsStreet && this.game.preFlop && this.game.preFlop.playersAfterBetting && this.game.preFlop.playersAfterBetting.length > 1) {
                    this.displayShownCards(this.game.preFlop);
                }
                return;
            }

            if (curStreet.allActions.length <= this.curActionIndex) {


                // ✅ CHECK ALL-IN ON FLOP
                if (curStreet.isAllIn && curStreet.playersAfterBetting &&
                 curStreet.playersAfterBetting.length > 1 && !this.shownCardsStreet) {
                    console.log("🎯 FLOP ALL-IN - Showing cards immediately");
                    this.updatePot(curStreet.potAfterBetting);
                    this.displayShownCards(curStreet);
                    this.shownCardsStreet = "flop";
                    return;
                    // Continue to show turn/river cards if they exist
                }

                // End of flop actions
                this.curStreetStr = "turn";
                this.curActionIndex = 0;

                if (this.game.turn && this.game.turn.board && this.game.turn.board.cards && this.game.turn.board.cards.length > 0) {
                    this.renderCommunityCards(this.game.turn.board.cards, "turn");
                    if (!this.shownCardsStreet) {
                        this.updatePot(this.game.flop.potAfterBetting);
                    }
                    this.hideActionBadge("hero");
                    for (let i = 1; i <= 8; i++) {
                        this.hideActionBadge(`player${i}`);
                    }
                    this.curActionIndex = -1;
                    return;
                } else {
                    // No turn - hand ended on flop
                    this.updatePot(this.game.flop.potAfterBetting);
                    if (!this.shownCardsStreet && curStreet.playersAfterBetting && curStreet.playersAfterBetting.length > 1) {
                        console.log("Flop ended - showing cards");
                        this.displayShownCards(curStreet);
                    }
                    return;
                }
            }
        }

        // ========== TURN ==========
        if (this.curStreetStr === "turn") {
            curStreet = this.game.turn;

            if (!curStreet) {
                this.updatePot(this.game.flop.potAfterBetting);
                if (!this.shownCardsStreet && this.game.flop && this.game.flop.playersAfterBetting && this.game.flop.playersAfterBetting.length > 1) {
                    this.displayShownCards(this.game.flop);
                }
                return;
            }

            if (curStreet.allActions.length <= this.curActionIndex) {
                // ✅ CHECK ALL-IN ON TURN
                if (curStreet.isAllIn && curStreet.playersAfterBetting &&
                 curStreet.playersAfterBetting.length > 1 && !this.shownCardsStreet) {
                    console.log("🎯 TURN ALL-IN - Showing cards immediately");
                    this.updatePot(curStreet.potAfterBetting);
                    this.displayShownCards(curStreet);
                    this.shownCardsStreet = "turn";
                    return;
                    // Continue to show river cards if they exist
                }

                // End of turn actions
                this.curStreetStr = "river";
                this.curActionIndex = 0;

                if (this.game.river && this.game.river.board && this.game.river.board.cards && this.game.river.board.cards.length > 0) {
                    this.renderCommunityCards(this.game.river.board.cards, "river");
                    if (!this.shownCardsStreet) {
                        this.updatePot(this.game.turn.potAfterBetting);
                    }
                    this.hideActionBadge("hero");
                    for (let i = 1; i <= 8; i++) {
                        this.hideActionBadge(`player${i}`);
                    }
                    this.curActionIndex = -1;
                    return;
                } else {
                    // No river - hand ended on turn
                    this.updatePot(this.game.turn.potAfterBetting);
                    if (!this.shownCardsStreet && curStreet.playersAfterBetting && curStreet.playersAfterBetting.length > 1) {
                        console.log("Turn ended - showing cards");
                        this.displayShownCards(curStreet);
                    }
                    return;
                }
            }
        }

        // ========== RIVER ==========
        if (this.curStreetStr === "river") {
            curStreet = this.game.river;

            if (!curStreet) {
                this.updatePot(this.game.turn.potAfterBetting);
                if (!this.shownCardsStreet && this.game.turn && this.game.turn.playersAfterBetting && this.game.turn.playersAfterBetting.length > 1) {
                    this.displayShownCards(this.game.turn);
                }
                return;
            }

            if (curStreet.allActions.length <= this.curActionIndex) {
                // End of river actions - FINAL SHOWDOWN
                this.updatePot(curStreet.potAfterBetting);

                // ✅ FINAL SHOWDOWN (only if cards haven't been shown yet)
                if (!this.shownCardsStreet) {
                    if (curStreet.playersAfterBetting && curStreet.playersAfterBetting.length > 1) {
                        console.log("🎯 RIVER SHOWDOWN - showing all cards");
                        this.displayShownCards(curStreet);
                    } else if (curStreet.playersAfterBetting && curStreet.playersAfterBetting.length === 1) {
                        console.log("🎯 RIVER WINNER - showing winner cards");
                        this.displayWinnerCards(curStreet);
                    }
                } else {
                    console.log("Cards already shown on street:", this.shownCardsStreet);
                }
                return;
            }
        }

        // ========== PROCESS ACTION ==========
        const nextAction = curStreet.allActions[this.curActionIndex];
        console.log("Processing action:", nextAction);

        const isHero = nextAction.playerId === "Hero";
        const seatIndex = isHero ? "hero" : this.hashPlayerIndexMap[nextAction.playerId];

        this.showActionBadge(isHero ? 'hero' : `player${seatIndex}`, nextAction.actionType, nextAction.amount);

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

        console.log("curActionIndex after:", this.curActionIndex);
    }

    renderCommunityCards(cards, street) {
        console.log("=== renderCommunityCards() ===");
        console.log("street:", street);
        console.log("cards count:", cards?.length);

        if (street === "flop" && cards && cards.length >= 3) {
            this.renderCardSlot("flopCard1", cards[0]);
            this.renderCardSlot("flopCard2", cards[1]);
            this.renderCardSlot("flopCard3", cards[2]);
        } else if (street === "turn" && cards && cards.length >= 4) {
            this.renderCardSlot("turnCard", cards[3]);
        } else if (street === "river" && cards && cards.length >= 5) {
            this.renderCardSlot("riverCard", cards[4]);
        }
    }

    renderCardSlot(elementId, card) {
        console.log("renderCardSlot:", elementId, card);
        const rectEl = document.getElementById(`${elementId}Rect`);
        const rankEl = document.getElementById(`${elementId}Rank`);

        if (rectEl && rankEl && card && card.suit && card.rank) {
            rectEl.className = `card-rect ${this.suitColorMap[card.suit]}`;
            rectEl.style.display = 'block';
            rankEl.textContent = this.rankCharMap[card.rank];
            rankEl.style.display = 'flex';
            console.log("✅ Card rendered:", elementId);
        } else {
            console.error("❌ Cannot render card - missing elements or card data");
        }
    }

    // ✅ SHOWDOWN: Show cards for ALL players who reached this street (2+ players)
    displayShownCards(street) {
        console.log("=== displayShownCards() ===");
        if (!street || !street.playersAfterBetting) {
            console.log("No street or playersAfterBetting data");
            return;
        }

        console.log("Players after betting:", street.playersAfterBetting.length);
        console.log("Street isAllIn:", street.isAllIn);

        street.playersAfterBetting.forEach(p => {
            if (p.id === "Hero") {
                return;
            }

            const index = this.hashPlayerIndexMap[p.id];
            console.log(`Player ${p.id} mapped to seat index ${index}`);

            const playerObj = this.game.players[p.id];
            console.log(`Player object:`, playerObj);

            if (playerObj && playerObj.hand && playerObj.hand.card1 && playerObj.hand.card2) {
                console.log(`Showing cards for player ${p.id} at seat ${index}`);
                this.setPlayerCard(`player${index}`, "left", playerObj.hand.card1);
                this.setPlayerCard(`player${index}`, "right", playerObj.hand.card2);
            } else {
                console.log(`⚠️ Player ${p.id} has no cards data (folded before showdown or data missing)`);
            }
        });

        console.log("=== displayShownCards() complete ===");
    }

    // ✅ WINNER: Show cards for the single remaining player (others folded)
    displayWinnerCards(street) {
        console.log("=== displayWinnerCards() ===");
        if (!street || !street.playersAfterBetting || street.playersAfterBetting.length !== 1) {
            console.log("Not exactly 1 player");
            return;
        }

        const winner = street.playersAfterBetting[0];
        console.log("Winner:", winner);

        if (winner.id === "Hero") {
            console.log("Hero is winner (cards already visible)");
            return;
        }

        const index = this.hashPlayerIndexMap[winner.id];
        const playerObj = this.game.players[winner.id];

        if (playerObj && playerObj.hand && playerObj.hand.card1 && playerObj.hand.card2) {
            console.log(`Showing winner cards for ${winner.id} at seat ${index}`);
            this.setPlayerCard(`player${index}`, "left", playerObj.hand.card1);
            this.setPlayerCard(`player${index}`, "right", playerObj.hand.card2);
        } else {
            console.log(`⚠️ Winner ${winner.id} has no cards data`);
        }
    }

    updatePot(amount) {
        document.getElementById('potLabel').textContent = `POT: ${this.formatMoney(amount)}$`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    console.log("=== DOMContentLoaded ===");
    new PokerReplayViewer();
});