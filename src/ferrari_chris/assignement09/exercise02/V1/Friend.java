package assignement09.exercise02.V1;

import java.util.ArrayList;

class Friend {
    private int id;
    private ArrayList<Letter> inbox = new ArrayList<>();
    private int numOfLetters;

    public Friend(int id, int numOfLetters) {
        this.id = id;
        this.numOfLetters = numOfLetters;
    }
    private void sendLetter(){
        
    }
}

class Letter {
    private final int letterNum;

    public Letter(int letterNum) {
        this.letterNum = letterNum;
    }

    public int getLetterNum() {
        return letterNum;
    }
}

class PenFriendSimulation {
    public static void main(String[] args) {

    }
}

