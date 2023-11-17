# Betting data processing test assignment
Author: Taeri Saar

## Prerequisites
* This project requires JDK 17 or later
* Lombok is used in this project
    * Ensure that your IDE supports it and make sure annotation processing is enabled
* Git

## Installation
* No specific installation guidelines
* Clone the repo and run it in your preferred IDE

## Tests

Go into the project directory and execute gradle test
```bash
./gradlew test
```

## Features
* Parses player data and match data files using dedicated custom file parsers
* Processes player transactions - deposits, withdrawals and bets
* Calculates wins and losses of bets based on match outcomes and bet amounts
* Handles player data, tracking balances, profits, losses and number of matches won
* Manages match data, supporting three match outcome types - A, B, or DRAW
* Generates a result file providing details about legal and illegal players and the final casino balance
* Includes an assortment of JUnit tests to ensure the correctness and reliability of the program