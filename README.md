# UnoComputo

UnoComputo is a program developed in 2023 following course 02148 Introduction to Coordination in Distributed Applications at DTU.
It enables you to play a game of uno with 2-8 friends via the terminal on different computers.

## Setup
Running the program will place you in the lobby
Here you have two options: To host a game or to join a game.
The host first creates a game by following the instructions given by the game.
After the host has created a game other players can join by following the instruction given by the game.
The game ends when a person has zero cards left on their hand.

## Game
One player has their turn.
They can either play a card, draw a card, say uno, object and end their turn.
- The UI only shows legal actions so if you cannot play any of your card a play option will not be shown.
Meanwhile other players will have the opportunity to object.
The other players will get an update of the new gamestate when the current player ends their turn.


## Chaining
UnoComputo enables you to chain cards. This means that you can play multiple cards in one turn if all cards share the same number/action.

## Streaking
Players can create a streak of draw 2 or draw 4 cards through their turns.
If player 1 plays a draw 2 card player 2 has the option of either drawing two cards or play another draw 2 card if their hand contains one.
- This means that player 3 will have to draw 4 cards on their turn if they cannot continue the streak.
