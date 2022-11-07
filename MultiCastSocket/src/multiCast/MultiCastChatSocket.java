/**
 * 
 */
package multiCast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

/**
 * @author aksel
 * @version Nov 4, 2022
 *
 */
public class MultiCastChatSocket extends MulticastSocket {
    int versio = 1;
    int viesti = 1;
    int day;
    int month;
    int year;
    String userName;
    String client = "TIEA322kankaazt";
    String group = "239.0.0.1";
    /**
     * @param args Not used
     * @throws IOException Error
     */
@SuppressWarnings("deprecation")
public static void main(String[] args) throws IOException {
    //IP address multicastingille
    Scanner input = new Scanner(System.in);
    Random rand = new Random();
    System.out.println("Enter username: ");
    String userName = input.nextLine(); //Käyttäjän nimi
    int day = rand.nextInt(15);
    int month = rand.nextInt(13);
    int year = rand.nextInt(2022 - 1950 + 1) + 1950;
    //Multicasting soketti
    MultiCastChatSocket ms = new MultiCastChatSocket(userName, day, month, year);
    ms.joinGroup(InetAddress.getByName(ms.group));
    //skanneri user inputin lukemiseksi
    
    
    String msg = "/join";
    byte[] buffer = asetaTavut(ms, msg);
    //port 42000
    DatagramPacket dp = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ms.group), 42000);
    
    ms.send(dp);
    System.out.println("Welcome!");
    kirjoitaViesti(ms, input);
}

@SuppressWarnings("deprecation")
private static void kirjoitaViesti(MultiCastChatSocket ms, Scanner input) throws IOException {
        System.out.println("Kirjoita vapaasti viestejä, jos haluat poistua kirjoita quit");
        System.out.println("viesti: ");
        String msg = input.nextLine();
        if (msg.equals("quit")) {
            msg = "/leave";
            byte[] buffer = asetaTavut(ms, msg);
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length, 
                                                    InetAddress.getByName(ms.group), 42000);
            ms.send(dp);
            ms.leaveGroup(InetAddress.getByName(ms.group));
            ms.close();
            System.out.println("closing");
            System.exit(0);
        }
        byte[] buffer = asetaTavut(ms, msg);
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, 
                                                InetAddress.getByName(ms.group), 42000);
        ms.send(dp);
        kirjoitaViesti(ms, input);
    }

private MultiCastChatSocket(String userName, int day, int month, int year) throws IOException{
    super(42000);
    this.userName = userName;
    this.day = day;
    this.month = month;
    this.year = year;
}


/**
 * @param versio protokollan versio
 * @param viesti viesti 
 * @param day käyttäjän syntymäpäivä
 * @param month käyttäjän syntymäkuukausi
 * @param year käyttäjän syntymävuosi
 * @param asiakasnimi asiakkaan nimi
 * @param usernimi käyttäjän nimi
 * @param teksti viesin teksti
 * @return byte arrayn, jossa lähetettävät tiedot
 */
private static byte[] asetaTavut(MultiCastChatSocket ms, String msg)
                                                                {
    // selvitä string-kenttien pituudet UTF-8 tavuina
    int clientLength = ms.client.length(); // UTF-8 koodattujen tavujen määrä
    int userLength = ms.userName.length(); // UTF-8 koodattujen tavujen määrä
    int dataLength = msg.length(); // UTF-8 koodattujen tavujen määrä
    // Otsikon vakiopituiset kentät on 7 tavua
    int constLength = 7;
    byte[] tavut = new byte[constLength + clientLength + userLength + dataLength];
    //asetetaan versio ja viesti protokollan mukaan
    tavut[0] = (byte)(ms.versio << 4);
    tavut[0] += (byte)(ms.viesti);
    //asetetaan syntymäaika
    tavut[1] = (byte)(ms.day << 3);
    tavut[1] += (byte)(ms.month >> 1);
    tavut[2] = (byte)((ms.month&1)<<7);
    tavut[2] += (byte)(ms.year >> 4);
    tavut[3] = (byte)((ms.year&15) << 4);
    //asetetaan client-nimi
    tavut[4] = (byte)(clientLength);
    int apu = 0;
    int asiakasIndex = clientLength+5;
    for (int i=5;i<asiakasIndex;i++) {
        tavut[i] = (byte)(ms.client.charAt(apu));
        apu++;  
    }
    //asetetaan viestin lähettäjän käyttäjänimi
    tavut[asiakasIndex] = (byte)(userLength);
    int userIndex = asiakasIndex+userLength+1;
    apu = 0;
    for (int i=asiakasIndex+1;i<userIndex;i++) {
        tavut[i] = (byte)(ms.userName.charAt(apu));
        apu++;
    }
    tavut[userIndex] = (byte)(dataLength);
    int dataIndex = userIndex+dataLength+1;
    apu = 0;
    //asetetaan viesti
    for (int i=userIndex+1;i<dataIndex;i++) {
        tavut[i] = (byte)(msg.charAt(apu));
        apu++;
}
return tavut;
}
}
