package com.example.assignment2;/*
 * PlayWriter.java
 *
 * PLayWriter class.
 * Creates the lovers, and writes the two lover's story (to an output text file).
 */


import java.io.*;
import java.net.*;

import javafx.util.Pair;


public class PlayWriter {

    private Romeo myRomeo  = null;
    private InetAddress RomeoAddress = null;
    private int RomeoPort = 0;
    private Socket RomeoMailbox = null;

    private Juliet myJuliet = null;
    private InetAddress JulietAddress = null;
    private int JulietPort = 0;
    private Socket JulietMailbox = null;

    double[][] theNovel = null;
    int novelLength = 0;

    public PlayWriter()
    {
        novelLength = 500; //Number of verses
        theNovel = new double[novelLength][2];
        theNovel[0][0] = 0;
        theNovel[0][1] = 1;
    }

    //Create the lovers
    public void createCharacters() {
        //Create the lovers
        System.out.println("PlayWriter: Romeo enters the stage.");
            

			myRomeo = new Romeo(theNovel[0][0]);
        System.out.println("PlayWriter: Juliet enters the stage.");
            

			myJuliet = new Juliet(theNovel[0][1]);
    }

    //Meet the lovers and start letter communication
    public void charactersMakeAcquaintances() {
            

        Pair<InetAddress, Integer> pair = myRomeo.getAcquaintance();
        myRomeo.start();
        myJuliet.start();
        RomeoAddress = pair.getKey();
        RomeoPort = pair.getValue();
        System.out.println("PlayWriter: I've made acquaintance with Romeo");

            

        Pair<InetAddress, Integer> pair2 = myJuliet.getAcquaintance();
        JulietAddress = pair2.getKey();
        JulietPort = pair2.getValue();
        System.out.println("PlayWriter: I've made acquaintance with Juliet");
    }


    //Request next verse: Send letters to lovers communicating the partner's love in previous verse
    public void requestVerseFromRomeo(int verse) {
        System.out.println("PlayWriter: Requesting verse " + verse + " from Romeo. -> (" + theNovel[verse-1][1] + ")");


        double JulietLove = theNovel[verse-1][1];
        try{
            RomeoMailbox = new Socket(RomeoAddress,RomeoPort);
            ObjectOutputStream Letter = new ObjectOutputStream(RomeoMailbox.getOutputStream());
            Letter.writeObject(JulietLove);

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        receiveLetterFromRomeo(verse);


    }


    //Request next verse: Send letters to lovers communicating the partner's love in previous verse
    public void requestVerseFromJuliet(int verse) {
        System.out.println("PlayWriter: Requesting verse " + verse + " from Juliet. -> (" + theNovel[verse-1][0] + ")");
            

        double RomeoLove = theNovel[verse-1][0];
        try{
            JulietMailbox = new Socket(JulietAddress,JulietPort);
            ObjectOutputStream Letter = new ObjectOutputStream(JulietMailbox.getOutputStream());
            Letter.writeObject(RomeoLove);

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        receiveLetterFromJuliet(verse);
    }


    //Receive letter from Romeo with renovated love for current verse
    public void receiveLetterFromRomeo(int verse) {
        //System.out.println("PlayWriter: Receiving letter from Romeo for verse " + verse + ".");

            

        try{
            ObjectInputStream response = new ObjectInputStream(RomeoMailbox.getInputStream());
            double message = (double) response.readObject();
            theNovel[verse][0]= message;
            RomeoMailbox.close();
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("PlayWriter: Romeo's verse " + verse + " -> " + theNovel[verse][0]);
    }

    //Receive letter from Juliet with renovated love fro current verse
    public void receiveLetterFromJuliet(int verse) {
            

        try{
            ObjectInputStream response = new ObjectInputStream(JulietMailbox.getInputStream());
            double message = (double) response.readObject();
            theNovel[verse][1] = message;
            JulietMailbox.close();
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("PlayWriter: Juliet's verse " + verse + " -> " + theNovel[verse][1]);
    }





    //Let the story unfold
    public void storyClimax() {
        for (int verse = 1; verse < novelLength; verse++) {
            //Write verse
            System.out.println("PlayWriter: Writing verse " + verse + ".");
            

			requestVerseFromRomeo(verse);
            requestVerseFromJuliet(verse);
            System.out.println("PlayWriter: Verse " + verse + " finished.");
        }
    }

    //Character's death
    public void charactersDeath() {
            
			
        try{


        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

			
    }


    //A novel consists of introduction, conflict, climax and denouement
    public void writeNovel() {
        System.out.println("PlayWriter: The Most Excellent and Lamentable Tragedy of Romeo and Juliet.");
        System.out.println("PlayWriter: A play in IV acts.");
        //Introduction,
        System.out.println("PlayWriter: Act I. Introduction.");
        this.createCharacters();
        //Conflict
        System.out.println("PlayWriter: Act II. Conflict.");
        this.charactersMakeAcquaintances();
        //Climax
        System.out.println("PlayWriter: Act III. Climax.");
        this.storyClimax();
        //Denouement
        System.out.println("PlayWriter: Act IV. Denouement.");
        this.charactersDeath();

    }


    //Dump novel to file
    public void dumpNovel() {
        FileWriter Fw = null;
        try {
            Fw = new FileWriter("RomeoAndJuliet.csv");
        } catch (IOException e) {
            System.out.println("PlayWriter: Unable to open novel file. " + e);
        }

        System.out.println("PlayWriter: Dumping novel. ");
        StringBuilder sb = new StringBuilder();
        for (int act = 0; act < novelLength; act++) {
            String tmp = theNovel[act][0] + ", " + theNovel[act][1] + "\n";
            sb.append(tmp);
            //System.out.print("PlayWriter [" + act + "]: " + tmp);
        }

        try {
            BufferedWriter br = new BufferedWriter(Fw);
            br.write(sb.toString());
            br.close();
        } catch (Exception e) {
            System.out.println("PlayWriter: Unable to dump novel. " + e);
        }
    }

    public static void main (String[] args) {
        PlayWriter Shakespeare = new PlayWriter();
        Shakespeare.writeNovel();
        Shakespeare.dumpNovel();
        System.exit(0);
    }


}
