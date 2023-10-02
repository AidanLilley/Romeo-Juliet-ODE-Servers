package com.example.assignment2;/*
 * Romeo.java
 *
 * Romeo class.  Implements the Romeo subsystem of the Romeo and Juliet ODE system.
 */


import java.io.*;
import java.lang.Thread;
import java.net.*;

import javafx.util.Pair;

public class Romeo extends Thread {

    private ServerSocket ownServerSocket = null; //Romeo's (server) socket
    private Socket serviceMailbox = null; //Romeo's (service) socket


    private double currentLove = 0;
    private double a = 0; //The ODE constant

    //Class construtor
    public Romeo(double initialLove) {
        currentLove = initialLove;
        a = 0.02;
        try {
            


            int port = 7778;
			ownServerSocket = new ServerSocket(port);
            System.out.println("Romeo: What lady is that, which doth enrich the hand\n" +
                    "       Of yonder knight?");
        } catch(Exception e) {
            System.out.println("Romeo: Failed to create own socket " + e);
            System.exit(1);
        }
   }

    //Get acquaintance with lover;
    public Pair<InetAddress,Integer> getAcquaintance() {
        System.out.println("Romeo: Did my heart love till now? forswear it, sight! For I ne'er saw true beauty till this night.");


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

        double JulietLove = 0;
        try{
            System.out.println("Entering Service Iteration");
            System.out.println("Romeo: Awaiting Letter");
            serviceMailbox = ownServerSocket.accept();
            ObjectInputStream Letter = new ObjectInputStream(serviceMailbox.getInputStream());
            JulietLove = (double) Letter.readObject();
            if(JulietLove==404.0){
                serviceMailbox.close();
                ownServerSocket.close();
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Romeo: Letter Received");
        System.out.println("Romeo: O sweet Juliet... (<-"  +JulietLove+  ")");//tmp
        return JulietLove; //tmp
    }


    //Love (The ODE system)
    //Given the lover's love at time t, estimate the next love value for Romeo
    public double renovateLove(double partnerLove){
        System.out.println("Romeo: But soft, what light through yonder window breaks?\n" +
                "       It is the east, and Juliet is the sun.");
        currentLove = currentLove+(a*partnerLove);
        return currentLove;
    }


    //Communicate love back to playwriter
    public void declareLove(){

        try{
            System.out.println("Romeo: I would I were thy bird. (->"+currentLove+"R)");
            ObjectOutputStream oos = new ObjectOutputStream(serviceMailbox.getOutputStream());
            System.out.println("this is service port: "+serviceMailbox.getPort()+ "  this is server port: "+ownServerSocket.getLocalPort());
            System.out.println("this is service IP: "+serviceMailbox.getInetAddress()+ "  this is server IP: "+ownServerSocket);
            oos.writeObject(currentLove);
            System.out.println("Exiting Service Iteration");
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
                double JulietLove = this.receiveLoveLetter();

                //Estimate new love value
                this.renovateLove(JulietLove);

                //Communicate love back to playwriter
                this.declareLove();
            }
        }catch (Exception e){
            System.out.println("Romeo: " + e);
        }
        if (this.isInterrupted()) {
            System.out.println("Romeo: Here's to my love. O true apothecary,\n" +
                    "Thy drugs are quick. Thus with a kiss I die." );
        }
    }

}
