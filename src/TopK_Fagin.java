import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Created by Eugen on 7/13/2017.
 */
public class TopK_Fagin {
    public static void main(String []args)  {
        //preparing Proxy Database for testing (caveat: I am using Array structure instead of Tree)
        int d = 3;
        int entryCount = 10000;
        int topK = 3;
        double[][] dbProxy = new double[d][entryCount];
        //insert random double values in the range [0:1) into DB
        Random r = new Random();
        for (int i = 0; i <dbProxy.length ; i++) {
            for (int j = 0; j <dbProxy[i].length ; j++) {
                dbProxy[i][j] = 1*r.nextDouble();
            }
        }
        //print for visual
        /*for (int i = 0; i <dbProxy.length ; i++) {
            System.out.println(Arrays.toString(dbProxy[i]));
        }
        System.out.println("not sorted");*/

        //DB indexing stage
        //populate list of maps with columns of dbProxy to index them later. NB: "columns" actually are rows of the dbProxy array, but for convenience we refer to them as "columns"
        Instant startIndexing = Instant.now(); // for measuring time
        ArrayList<Map<Integer,Double>> dbColumns = new ArrayList<>();
        for (int i = 0; i <dbProxy.length ; i++) {
            Map<Integer, Double> tmpMap = new HashMap<>();
            for (int j = 0; j <dbProxy[i].length ; j++) {
                tmpMap.put(j,dbProxy[i][j]);
            }
            //sort map
            tmpMap = sortByValuesDesc(tmpMap);
//            System.out.println(tmpMap);
            dbColumns.add(tmpMap);
        }
        Instant endIndexing = Instant.now(); // for measuring time
        Instant startTopK = Instant.now(); // for measuring time
        ArrayList<Iterator> iterators = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            Iterator tmpIterator = dbColumns.get(i).entrySet().iterator();
            iterators.add(tmpIterator);
        }

        Set<Integer> setIndex = new HashSet<>();
        boolean flag = false;
        int[] count = new int[entryCount];
        int counter = topK;
        //here we are waiting to find topK elements that occur in all dimensions before we stop our algorithm
        while (counter>0) {
            for (int i = 0; i < iterators.size(); i++) {
                Map.Entry<Integer, Double> me = (Map.Entry)iterators.get(i).next();
                if(!setIndex.add(me.getKey())){
                    count[me.getKey()]=count[me.getKey()]+1;
                    if(count[me.getKey()]== (d-1)){
                        counter--;
                    }
                }
            }
        }
//        System.out.println(Arrays.toString(count));

        //calculating resulting map. Put element with aggregation function: min(a1, a2,...an)
        Map<Integer, Double> resultMap = new HashMap<>();

        Iterator<Integer> setIndexIterator = setIndex.iterator();
        while (setIndexIterator.hasNext()){
            int tmp = setIndexIterator.next();
            double min = Double.MAX_VALUE;
            for (int i = 0; i < dbColumns.size(); i++) {
                double tmpValue = dbColumns.get(i).get(tmp);
                min = min <= tmpValue ? min :tmpValue;
            }
            resultMap.put(tmp, min);
        }
//        System.out.println("system has sorted top-k...");
        resultMap = sortByValuesDesc(resultMap);
//        printMap(resultMap);
        Instant endTopK = Instant.now(); // for measuring time
        //checking our algorithm
        Instant startNaiveTopK = Instant.now(); // for measuring time
        Map<Integer, Double> naiveMap = new HashMap<>();
        for (int i = 0; i <dbProxy[0].length ; i++) {
            double min=Double.MAX_VALUE;
            for (int j = 0; j <dbProxy.length ; j++) {
                min = min <= dbProxy[j][i] ?  min : dbProxy[j][i];
            }
            naiveMap.put(i, min);
        }
//        System.out.println("output of naive algorithm...");
        naiveMap = sortByValuesDesc(naiveMap);
        Instant endNaiveTopK = Instant.now(); // for measuring time
//        printMap(naiveMap);
        /*String outputTex = checkResult(resultMap,naiveMap,topK)? "Result is correct" : "Something went wrong...";
        System.out.println(outputTex);*/

        Long indexingDuration = Duration.between(startIndexing,endIndexing).getSeconds();
        Long topKDuration = Duration.between(startTopK ,endTopK).getSeconds();
        Long naiveTopKDuration = Duration.between(startNaiveTopK ,endNaiveTopK).getSeconds();
        System.out.printf("TopK by Ronald Fagin and Naive algorithm performed successfully. Number of tuples: %d, number of dimension %d, and top-k to be returned: %d", entryCount, d, topK);
        System.out.println();
        System.out.printf("It took %d seconds to index, %d seconds to run topK algorithm. Naive method performed in %d seconds", indexingDuration, topKDuration, naiveTopKDuration);


    }
    //static class to sort dbColumns by value
    private static Map sortByValuesDesc(Map map) {
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });


        Map sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
    //utility class for printing map
    private static void printMap(Map map){
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry me = (Map.Entry)iterator.next();
            System.out.print(me.getKey() + ": ");
            System.out.println(me.getValue());
        }
        System.out.println("printed all key-values of map");
    }
    private static boolean checkResult (Map finaMap, Map naiveMap, int topK){
        Iterator finalMapIterator = finaMap.entrySet().iterator();
        Iterator naiveMapIterator = naiveMap.entrySet().iterator();
        int flag = 0;
        for (int i = 0; i < topK; i++) {
            int tmpFinalMap = ((Map.Entry<Integer,Double>)finalMapIterator.next()).getKey();
            int tmpNaiveMap = ((Map.Entry<Integer,Double>)naiveMapIterator.next()).getKey();
            if(tmpFinalMap == tmpNaiveMap){
                flag++;
            }
        }
        return flag == topK;
    }
}

