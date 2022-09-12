package com.example.matchinggame;

import com.google.gson.Gson;

public class Matching {
    // defualt start board?

    private int gamesPlayed = 0;
    private int gamesWon = 0;
    private int lifeCount = 3;
    private int matches = 0;

    private int cardOne = 9;
    private int cardTwo = 9;

    private int cardOneTag;
    private int cardTwoTag;

    private int[] board = {1,1,2,2,3,3,4,4};

    private boolean[] flipped = new boolean[8];

    public final static int sMin_Pick = 1;
    public final static int sMax_Pick = 8;



    public Matching() {
        startGame();
    }

    public int[] getNewBoard(){
        int randNum;
        int idx;
        int last;
        int counter = 7;
        int[] newBoard = board;
        for(int i=0; i<newBoard.length; i++){
            randNum = (int) (Math.random()*(counter -1)+0);
            idx = newBoard[randNum];
            last = newBoard[counter];
            newBoard[counter] = idx;
            newBoard[randNum] = last;
            counter --;
        }
        return newBoard;
    }

    public void startGame() {
        gamesPlayed++;
        lifeCount = 3;
        matches = 0;
        board = getNewBoard();
        setFlipped();
    }

    public int getCardOneTag() {
        return cardOneTag;
    }

    public void setCardOneTag(int cardOneTag) {
        this.cardOneTag = cardOneTag;
    }

    public int getCardTwoTag() {
        return cardTwoTag;
    }

    public void setCardTwoTag(int cardTwoTag) {
        this.cardTwoTag = cardTwoTag;
    }

    private void setFlipped() {
        for (int b = 0; b < flipped.length; b++) {
            flipped[b] = false;
        }
    }
    public void setFlippedSlot(int slot, boolean val){
        flipped[slot] = val;
    }

    public boolean[] getFlipped() {
        return flipped;
    }

    public void setFlipped(boolean[] flipped) {
        this.flipped = flipped;
    }

    public int[] getBoard() {
        return board;
    }

    public void setBoard(int[] board) {
        this.board = board;
    }

    public int getCardOne() {return cardOne;}
    public void setCardOne(int cardOne) {this.cardOne = cardOne;}

    public int getCardTwo() {return cardTwo;}
    public void setCardTwo(int cardTwo) {this.cardTwo = cardTwo;}

    public int getGamesPlayed() {return gamesPlayed;}
    public void setGamesPlayed(int gamesPlayed) {this.gamesPlayed = gamesPlayed;}

    public int getGamesWon() {return gamesWon;}
    public void setGamesWon(int gamesWon) {this.gamesWon = gamesWon;}

    public int getLifeCount() {return lifeCount;}
    public void setLifeCount(int lifeCount) {this.lifeCount = lifeCount;}

    public int getMatches() {return matches;}
    public void setMatches(int matches) {this.matches = matches;}

    public boolean gameOver(){
        return matches == 4 || lifeCount == 0;
    }

    public void pickOne(int card){
        cardOne = board[card-1];
    }

    public void pickTwo(int card){
        cardTwo = board[card-1];
    }

    public boolean isMatch(int one, int two) {return board[one] == board[two];}

    /**
     * Reverses the game object's serialization as a String
     * back to a Matching game object
     *
     * @param json The serialized String of the game object
     * @return The game object
     */
    public static Matching getGameFromJSON (String json)
    {
        Gson gson = new Gson ();
        return gson.fromJson (json, Matching.class);
    }

    /**
     * Serializes the game object to a JSON-formatted String
     *
     * @param obj Game Object to serialize
     * @return JSON-formatted String
     */
    public static String getJSONFromGame (Matching obj)
    {
        Gson gson = new Gson ();
        return gson.toJson (obj);
    }

    public String getJSONFromCurrentGame()
    {
        return getJSONFromGame(this);
    }
}


