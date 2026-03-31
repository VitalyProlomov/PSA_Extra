/**
 * Poker Hand Replay - Step-through controller
 * Fixes: next button triggers action, restart goes to /hands
 */
(function() {
    'use strict';

    // ===== STATE =====
    let currentStep = 0;
    let actions = [];
    let players = {};
    let board = [];
    let isComplete = false;

    // ===== DOM ELEMENTS =====
    const elements = {
        nextBtn: null,
        potLabel: null,
        hero: {
            balance: null,
            actionBadge: null,
            button: null,
            cards: {
                left: { rect: null, rank: null, back: null },
                right: { rect: null, rank: null, back: null }
            }
        },
        community: {
            flop: [
                { rect: null, rank: null },
                { rect: null, rank: null },
                { rect: null, rank: null }
            ],
            turn: { rect: null, rank: null },
            river: { rect: null, rank: null }
        }
    };

    // ===== INITIALIZATION =====
    function init() {
        console.log('🎴 Replay initializing...');

        if (!window.gameData || !window.gameData.actions) {
            console.warn('⚠️ No actions in game data - static display only');
            renderStaticBoard();
            return;
        }

        // Load data
        actions = window.gameData.actions || [];
        board = window.gameData.board || window.gameData.communityCards || [];

        // Build player map
        if (window.gameData.players) {
            window.gameData.players.forEach(p => {
                if (p && p.id) players[p.id] = p;
            });
        }

        // Cache DOM elements
        cacheElements();

        // Setup event listeners
        setupEventListeners();

        // Render initial state
        renderInitial();

        console.log('✅ Replay ready. Press Space or click ▶ to advance.');
    }

    function cacheElements() {
        elements.nextBtn = document.getElementById('nextActionBtn');
        elements.potLabel = document.getElementById('potLabel');

        // Hero
        elements.hero.balance = document.getElementById('heroBalanceLabel');
        elements.hero.actionBadge = document.getElementById('heroActionBadge');
        elements.hero.button = document.getElementById('heroButtonIcon');
        elements.hero.cards.left.rect = document.getElementById('heroLeftCardRect');
        elements.hero.cards.left.rank = document.getElementById('heroLeftCardRank');
        elements.hero.cards.left.back = document.getElementById('heroLeftCardBack');
        elements.hero.cards.right.rect = document.getElementById('heroRightCardRect');
        elements.hero.cards.right.rank = document.getElementById('heroRightCardRank');
        elements.hero.cards.right.back = document.getElementById('heroRightCardBack');

        // Community cards
        ['flopCard1', 'flopCard2', 'flopCard3'].forEach((id, i) => {
            elements.community.flop[i].rect = document.getElementById(id + 'Rect');
            elements.community.flop[i].rank = document.getElementById(id + 'Rank');
        });
        elements.community.turn.rect = document.getElementById('turnCardRect');
        elements.community.turn.rank = document.getElementById('turnCardRank');
        elements.community.river.rect = document.getElementById('riverCardRect');
        elements.community.river.rank = document.getElementById('riverCardRank');
    }

    function setupEventListeners() {
        // ✅ Next action button - THIS WAS MISSING
        if (elements.nextBtn) {
            elements.nextBtn.addEventListener('click', function(e) {
                e.preventDefault();
                if (!isComplete) {
                    processNextAction();
                }
            });
        }

        // Keyboard: Space = next action
        document.addEventListener('keydown', function(e) {
            if ((e.code === 'Space' || e.key === ' ') && !isComplete) {
                e.preventDefault();
                processNextAction();
            }
        });

        // ✅ Restart button - Just let the href handle navigation to /hands
        // No JS needed - the <a th:href="@{/hands}"> handles it
    }

    // ===== RENDERING =====
    function renderStaticBoard() {
        // Show Hero's cards if available
        const hero = players['Hero'];
        if (hero && hero.cards && hero.cards.length >= 2) {
            renderCard(elements.hero.cards.left, hero.cards[0]);
            renderCard(elements.hero.cards.right, hero.cards[1]);
        }

        // Show dealer button for Hero if applicable
        if (hero && hero.isDealer && elements.hero.button) {
            elements.hero.button.style.display = 'block';
        }

        // Set initial balances
        if (hero && elements.hero.balance) {
            elements.hero.balance.textContent = '$' + (hero.balance || 0).toFixed(2);
        }

        // Set initial pot
        if (elements.potLabel && window.gameData.finalPot !== undefined) {
            elements.potLabel.textContent = 'POT: $' + window.gameData.finalPot.toFixed(2);
        }
    }

    function renderInitial() {
        renderStaticBoard();

        // If we have actions, start with pot at 0 and build up
        if (actions.length > 0 && elements.potLabel) {
            elements.potLabel.textContent = 'POT: $0.00';
        }
    }

    function renderCard(cardEl, card) {
        if (!cardEl || !card || !card.rank || !card.suit) return;

        // Set suit color class
        if (cardEl.rect && window.suitClasses[card.suit]) {
            cardEl.rect.className = 'card-rect ' + window.suitClasses[card.suit];
        }

        // Set rank text (abbreviate)
        if (cardEl.rank) {
            const abbrev = { TEN: 'T', JACK: 'J', QUEEN: 'Q', KING: 'K', ACE: 'A' };
            cardEl.rank.textContent = abbrev[card.rank] || card.rank;
        }

        // Hide card back (show face-up)
        if (cardEl.back) {
            cardEl.back.style.display = 'none';
        }
    }

    function renderBoardCard(slot, card) {
        if (!slot || !card) return;

        if (slot.rect && window.suitClasses[card.suit]) {
            slot.rect.className = 'card-rect ' + window.suitClasses[card.suit];
            slot.rect.style.animation = 'cardReveal 0.3s ease-out';
        }
        if (slot.rank) {
            const abbrev = { TEN: 'T', JACK: 'J', QUEEN: 'Q', KING: 'K', ACE: 'A' };
            slot.rank.textContent = abbrev[card.rank] || card.rank;
        }
    }

    // ===== ACTION PROCESSING =====
    function processNextAction() {
        if (currentStep >= actions.length) {
            isComplete = true;
            if (elements.nextBtn) {
                elements.nextBtn.style.opacity = '0.5';
                elements.nextBtn.style.cursor = 'default';
                elements.nextBtn.title = 'Replay Complete';
            }
            return;
        }

        const action = actions[currentStep++];
        executeAction(action);

        // Button feedback
        if (elements.nextBtn) {
            elements.nextBtn.style.transform = 'scale(0.95)';
            setTimeout(() => elements.nextBtn.style.transform = '', 100);
        }
    }

    function executeAction(action) {
        if (!action) return;

        const { actor, actionType, amount, street, potBeforeAction } = action;
        const player = players[actor];

        // Update pot display
        if (elements.potLabel && potBeforeAction !== undefined) {
            elements.potLabel.textContent = 'POT: $' + potBeforeAction.toFixed(2);
        }

        // Update player balance if available
        if (player && player.balance !== undefined && amount !== undefined) {
            if (['BET', 'CALL', 'RAISE', 'BLIND', 'ANTE', 'STRADDLE'].includes(actionType)) {
                // Balance decreases when betting
                const balanceEl = actor === 'Hero' ? elements.hero.balance :
                    document.getElementById('player' + getPlayerNumber(actor) + 'BalanceLabel');
                if (balanceEl) {
                    balanceEl.textContent = '$' + (player.balance - amount).toFixed(2);
                }
            } else if (actionType === 'WIN') {
                // Balance increases when winning
                const balanceEl = actor === 'Hero' ? elements.hero.balance :
                    document.getElementById('player' + getPlayerNumber(actor) + 'BalanceLabel');
                if (balanceEl) {
                    balanceEl.textContent = '$' + (player.balance + amount).toFixed(2);
                }
            }
        }

        // Show action badge
        showActionBadge(actor, actionType, amount);

        // Reveal cards at showdown for winners or all-in players
        if (actionType === 'WIN' || street === 'SHOWDOWN') {
            revealPlayerCards(actor);
        }

        // Deal community cards by street
        if (street && board.length > 0) {
            dealStreetCards(street);
        }
    }

    function showActionBadge(playerId, actionType, amount) {
        const badgeId = playerId === 'Hero' ? 'heroActionBadge' :
            'player' + getPlayerNumber(playerId) + 'ActionBadge';
        const badge = document.getElementById(badgeId);
        if (!badge) return;

        // Format action text
        let text = actionType;
        if (amount !== undefined && ['BET', 'CALL', 'RAISE', 'WIN'].includes(actionType)) {
            text = actionType + ' $' + amount.toFixed(2);
        }

        badge.textContent = text;
        badge.className = 'action-badge action-' + actionType.toLowerCase();
        badge.style.display = 'flex';

        // Auto-hide after 2 seconds
        setTimeout(() => {
            badge.style.display = 'none';
        }, 2000);
    }

    function revealPlayerCards(playerId) {
        const player = players[playerId];
        if (!player || !player.cards || player.cards.length < 2) return;

        const prefix = playerId === 'Hero' ? 'hero' : 'player' + getPlayerNumber(playerId);
        renderCard({
            rect: document.getElementById(prefix + 'LeftCardRect'),
            rank: document.getElementById(prefix + 'LeftCardRank'),
            back: document.getElementById(prefix + 'LeftCardBack')
        }, player.cards[0]);

        renderCard({
            rect: document.getElementById(prefix + 'RightCardRect'),
            rank: document.getElementById(prefix + 'RightCardRank'),
            back: document.getElementById(prefix + 'RightCardBack')
        }, player.cards[1]);
    }

    function dealStreetCards(street) {
        // Count already-dealt cards
        let dealtCount = 0;
        elements.community.flop.forEach(slot => {
            if (slot.rect && slot.rect.className.includes('card-rect')) dealtCount++;
        });
        if (elements.community.turn.rect && elements.community.turn.rect.className.includes('card-rect')) dealtCount++;
        if (elements.community.river.rect && elements.community.river.rect.className.includes('card-rect')) dealtCount++;

        // Deal based on street
        if (street === 'FLOP' && dealtCount < 3) {
            for (let i = 0; i < 3 && dealtCount + i < board.length; i++) {
                renderBoardCard(elements.community.flop[i], board[dealtCount + i]);
            }
        } else if (street === 'TURN' && dealtCount < 4 && board.length >= 4) {
            renderBoardCard(elements.community.turn, board[3]);
        } else if (street === 'RIVER' && dealtCount < 5 && board.length >= 5) {
            renderBoardCard(elements.community.river, board[4]);
        }
    }

    function getPlayerNumber(playerId) {
        // Simple mapping: Hero = 0, others = 1-8 based on order
        if (playerId === 'Hero') return 0;
        const ids = Object.keys(players).filter(id => id !== 'Hero');
        const idx = ids.indexOf(playerId);
        return idx >= 0 ? idx + 1 : 1;
    }

    // ===== PUBLIC API (for debugging) =====
    window.PokerReplay = {
        next: processNextAction,
        getState: () => ({ currentStep, total: actions.length, complete: isComplete })
    };

    // Auto-init
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();