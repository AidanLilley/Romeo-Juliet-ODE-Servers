package com.example.assignment2;/*
 * Juliet.java
 *
 * Juliet class.  Implements the Juliet subsystem of the Romeo and Juliet ODE system.
 */



import javafx.util.Pair;

import java.io.*;
import java.lang.Thread;
import java.net.*;

public class Juliet extends Thread {

    private ServerSocket ownServerSocket = null; //Juliet's (server) socket
    private Socket serviceMailbox = null; //Juliet's (service) socket

    private double currentLove = 0;
    private double b = 0;

    //Class construtor
    public Juliet(double initialLove) {
        currentLove = initialLove;
        b = 0.01;
        try {
            

            int port = 7779;
            ownServerSocket = new ServerSocket(port);

            System.out.println("Juliet: Good pilgrim, you do wrong your hand too much, ...");
        } catch(Exception e) {
            System.out.println("Juliet: Failed to create own socket " + e);
            System.exit(1);
        }
    }

    //Get acquaintance with lover;
    // Receives lover's socket information and share's own socket
    public Pair<InetAddress,Integer> getAcquaintance() {
        System.out.println("Juliet: My bounty is as boundless as the sea,\n" +
                "       My love as deep; the more I give to thee,\n" +
                "       The more I have, for both are infinite.");
            

        Pair<InetAddress, Integer> pair = null;
        try {
            pair = new Pair<>(InetAddress.getLocalHost(), ownServerSocket.getLocalPort());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return pair;
    }



    //Retrieves the lover's love
    public double receiveLoveLetter()
    {
            

        double RomeoLove = 0;
        try{
            System.out.println("Juliet: Awaiting Letter");
            serviceMailbox = ownServerSocket.accept();
            ObjectInputStream Letter = new ObjectInputStream(serviceMailbox.getInputStream());
            RomeoLove = (double) Letter.readObject();
            if(RomeoLove==404.0){
                serviceMailbox.close();
                ownServerSocket.close();
            }

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Juliet: Letter Received");
        System.out.println("Juliet: Romeo, Romeo! Wherefore art thou Romeo? (<-" +RomeoLove+ ")"); //tmp
        return RomeoLove; //tmp
    }


    //Love (The ODE system)
    //Given the lover's love at time t, estimate the next love value for Romeo
    public double renovateLove(double partnerLove){
        System.out.println("Juliet: Come, gentle night, come, loving black-browed night,\n" +
                "       Give me my Romeo, and when I shall die,\n" +
                "       Take him and cut him out in little stars.");
        currentLove = currentLove+(-b*partnerLove);
        return currentLove;
    }


    //Communicate love back to playwriter
    public void declareLove(){
            

        try{
            System.out.println("Juliet: Good night, good night! Parting is such sweet sorrow,\n" +
                    "       That I shall say good night till it be morrow. (->"+currentLove+"J)");
            ObjectOutputStream oos = new ObjectOutputStream(serviceMailbox.getOutputStream());
            oos.writeObject(currentLove);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }



    //Execution
    public void run () {
        try {
            while (!this.isInterrupted()) {
                //Retrieve lover's current love
                double RomeoLove = this.receiveLoveLetter();

                //Estimate new love value
                this.renovateLove(RomeoLove);

                //Communicate back to lover, Romeo's love
                this.declareLove();
            }
        }catch (Exception e){
            System.out.println("Juliet: " + e);
        }
        if (this.isInterrupted()) {
            System.out.println("Juliet: I will kiss thy lips.\n" +
                    "Haply some poison yet doth hang on them\n" +
                    "To make me die with a restorative.");
        }

    }

}
