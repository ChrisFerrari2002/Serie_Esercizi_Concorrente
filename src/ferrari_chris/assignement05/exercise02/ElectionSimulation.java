package ferrari_chris.assignement05.exercise02;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

class Citizens implements Runnable{
    private final List<Candidate> candidates;
    private static final int NUM_OF_CITIZENS = 10000000;

    public Citizens(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    @Override
    public void run() {
        int numElector;
        for (int i = 0; i < NUM_OF_CITIZENS; i++) {
            numElector = ThreadLocalRandom.current().nextInt(0, candidates.size());
            candidates.get(numElector).totalOfVotes.getAndIncrement();
        }
    }
}

class Candidate {
    private final int id;
    public AtomicInteger totalOfVotes = new AtomicInteger(0);

    public Candidate(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

public class ElectionSimulation {
    private static boolean weHaveAWinner = false;
    private final static int NUM_CANDIDATES = 15;
    private final static int NUM_THREADS = 10;

    public static void main(String[] args) {

        List<Candidate> candidateList = new ArrayList<>();
        for (int i = 0; i < NUM_CANDIDATES; i++) {
            candidateList.add(new Candidate(i));
        }

        List<Thread> citizens = new ArrayList<>();
        while (!weHaveAWinner){
            for (int i = 0; i < NUM_THREADS; i++) {
                citizens.add(new Thread(new Citizens(candidateList)));
            }

            citizens.forEach(Thread::start);

            try {
                for (Thread thread : citizens) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int maxVotes = 0;
            Candidate currentCandidate;
            for (int i = 0; i < candidateList.size(); i++) {
                currentCandidate = candidateList.get(i);
                if (currentCandidate.totalOfVotes.get() > maxVotes){
                    maxVotes = currentCandidate.totalOfVotes.get();
                }
            }
            for (Candidate candidate : candidateList){
                System.out.println(candidate.getId() + ": " + candidate.totalOfVotes.get());
            }
            List<Candidate> temp = new ArrayList<>();
            for (Candidate candidate : candidateList){
                if (candidate.totalOfVotes.get() == maxVotes){
                    temp.add(candidate);
                }
            }
            candidateList.clear();
            candidateList = temp;
            if (candidateList.size() == 1)
                weHaveAWinner = true;

            citizens.clear();
            System.out.println("Election finished");
        }
        System.out.println("Winner: " + candidateList.get(0).getId() + " with " + candidateList.get(0).totalOfVotes.get() + " votes");
    }
}

