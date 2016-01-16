package uk.co.adeveloperabroad;

/**
 * Created by snow on 16/01/16.
 */
public class BinaryDisplay {

    public String getTrack(int track, int lastTrack) {
        return Integer.toBinaryString(track).replace("0","_")
                + " / " +
                Integer.toBinaryString(lastTrack).replace("0","_");
    }

   public String getScore(int score) {
       return Integer.toBinaryString(score).replace("0","_");
   }

}
