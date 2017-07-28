import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Eugen on 7/13/2017.
 */
public class Skyline_Block_Nested_Loops {
    public static void main(String []args)  {
        //preparing Proxy Database for testing (caveat: I am using Array structure instead of Tree)
        int d = 7;
        int entryCount = 10000;
        double[][] dbProxy = new double[d][entryCount];
        //insert random double values in the range [0:1] into DB
        Random r = new Random();
        for (int i = 0; i <dbProxy.length ; i++) {
            for (int j = 0; j <dbProxy[i].length ; j++) {
                dbProxy[i][j] = 1*r.nextDouble();
            }
        }


/*        double[][] dbProxy = {{0.34275532109283147, 0.3265297652400506, 0.37209628351810764, 0.320352112893041, 0.7063610456055767},
        {0.8936868627720992, 0.23966284291608098, 0.7597478981757514, 0.2981303669193932, 0.895326844767608}};*/


        //print for visual
        /*for (int i = 0; i <dbProxy.length ; i++) {
            System.out.println(Arrays.toString(dbProxy[i]));
        }*/
        Instant startBNL = Instant.now(); // for measuring time
        // array list is created to contain potential skyline elements
        ArrayList<Integer> skyline = new ArrayList<>();
        skyline.add(0);
        for (int i = 1; i < dbProxy[0].length; i++) {
            ArrayList<Integer> listAdd = new ArrayList<>();
            ArrayList<Integer> listRemove = new ArrayList<>();
            int countDominatedSkylineElements = 0;
            for (int j = 0; j < skyline.size(); j++) {
                int countAdd = 0;
                int equal = 0;

                for (int k = 0; k < d; k++) {
                    //here we need to make sure dbElement is better than ALL skyline elements, not just one
                    if(dbProxy[k][i] > dbProxy[k][skyline.get(j)]){
                        countAdd++;
                    }else if(dbProxy[k][i] < dbProxy[k][skyline.get(j)]){

                    }else /*(dbProxy[k][i] == dbProxy[k][skyline.get(j)])*/{
                        equal++;
                    }
                }
                if(countAdd>0) countDominatedSkylineElements++;
                if((countAdd+equal==d) && countAdd !=0) listRemove.add(skyline.get(j));
//                System.out.println("{count: " + countAdd + "}, {equal: " +equal + " }," +"}, {i: " +i + " }," + "}, {j: " +j+ "}, {listRemove: " +listRemove + " }," +skyline);
            }
            int skylineSize = skyline.size();
            for (int j = 0; j < listRemove.size(); j++) skyline.remove(Integer.valueOf(listRemove.get(j)));
            if (countDominatedSkylineElements==skylineSize) skyline.add(i);
        }
        Instant endBNL = Instant.now(); // for measuring time


        System.out.println(skyline + "<-- BlockNestedLoops Skyline");



        Instant startNaive = Instant.now(); // for measuring time
        //naive algorithm for checking
        ArrayList<Integer> naiveSkyline = new ArrayList<>();
        for (int i = 0; i < dbProxy[0].length; i++) {
            int countTotal = 0;
            for (int j = 0; j < dbProxy[0].length; j++) {
                int count = 0;
                int equal = 0;
                //comparing each entry their corresponding attributes
                for (int k = 0; k < d; k++) {
                    if(dbProxy[k][i] > dbProxy[k][j]){
                        count++;
                    }else if(dbProxy[k][i] == dbProxy[k][j]){
                        equal++;
                    }
                }
                /*System.out.println("{count: " + count + "}, {equal: " +equal + " }," +"}, {totalCount: " +countTotal + " }," +naiveSkyline);
                System.out.println("index state:"+"{i:" + i + "}, {j: " +j + " }," +naiveSkyline);*/
                if (i != j & count == 0 ){
//                    System.out.println("breaking naive");
                    break;
                }else if((i != j) & (count > 0)){
                    countTotal++;
                }
                if(countTotal == (entryCount-1)){
                    naiveSkyline.add(i);
                }
            }
        }
        Instant endNaive = Instant.now(); // for measuring time
        System.out.println(naiveSkyline + "<--naiveSkyline");
        Long BNLDuration = Duration.between(startBNL, endBNL).getSeconds();
        Long naiveDuration = Duration.between(startNaive ,endNaive).getSeconds();
        System.out.printf("Block Nested Loops Skyline algorithm performed successfully. Number of tuples: %d, number of dimension %d", entryCount, d);
        System.out.println();
        System.out.printf("It took %d seconds to run BNL algorithm. Naive method performed in %d seconds", BNLDuration, naiveDuration);
    }

}
