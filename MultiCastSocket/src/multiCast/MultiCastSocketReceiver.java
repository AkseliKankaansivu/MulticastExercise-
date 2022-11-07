/**
 * 
 */
package multiCast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


/**
 * @author aksel
 * @version Nov 4, 2022
 *
 */
public class MultiCastSocketReceiver {

    /**
     * @param args not used
     */
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        String group = "239.0.0.1";
        try {
            MulticastSocket ms = new MulticastSocket(42000);
            ms.joinGroup(InetAddress.getByName(group));
            
            for (int i=0;i<10;i++) {
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, 1024);
                ms.receive(dp);
                byte[] tmp = dp.getData();
                rakennaViesti(tmp);
            }
            
           // ms.leaveGroup(InetAddress.getByName(group));
           // System.out.println("closing");
           // ms.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void rakennaViesti(byte[] tmp) {
        //luetaan versio viestistä
        int versio = tmp[0] >> 4;
        //luetaan viesti
        int viesti = tmp[0]&15;
        if (versio > 1) {
           if (viesti != 1 || viesti != 2 || viesti != 3) {
               System.exit(0);
           }
        }    
        //luetaan syntymäaika
        int day = tmp[1] >> 3;
        int month = (tmp[1]&7) << 1;
        month += tmp[2] >> 7;
        int year = tmp[2]&127;
        year = year << 4;
        year += tmp[3] >> 4;
        
        //luetaan asiakassovellus
        int maxPituus = tmp[4]+5;
        StringBuilder sb = new StringBuilder(tmp.length);
        for (int i=5;i<maxPituus;i++) {
            sb.append((char)(tmp[i]));
        }
        String client = sb.toString();
        //luetaan lähettäjä
        StringBuilder sb2 = new StringBuilder(tmp.length);
        int lahtoIndex = tmp[4]+5;
        int ekaPituus = tmp[lahtoIndex]+1;
        for (int i=lahtoIndex+1;i<lahtoIndex+ekaPituus;i++) {
            sb2.append((char)(tmp[i]));
        }
        String nimi = sb2.toString();
        //luetaan tekstin sisältö
        StringBuilder sb3 = new StringBuilder(tmp.length);
        int tokaPituus = tmp[lahtoIndex+ekaPituus]+1;
        for (int j=lahtoIndex+ekaPituus+1;j<lahtoIndex+ekaPituus+tokaPituus;j++) {
            sb3.append((char)(tmp[j]));
        }
        String teksti = sb3.toString();
        System.out.println("Version: " + versio + "\n" +
                           "Message: " + viesti + "\n" +
                           "Day: " + day + "\n" +
                           "Month: " + month + "\n" +
                           "Year: " + year + "\n" +
                           "Client: " + client + "\n" +
                           "Name: " + nimi + "\n" +
                           "Data: " + teksti + "\n");
    }

}
