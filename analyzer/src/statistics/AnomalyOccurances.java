package results;

import java.util.HashSet;

public class AnomalyOccurances {
    private long occurences;
    private HashSet<String> anomalyTypes;

    public AnomalyOccurances(){
        this.occurences = 0;
        this.anomalyTypes = new HashSet<String>();
    }

    public void addAnomaly(String anomalyType){
        this.occurences++;
        this.anomalyTypes.add(anomalyType);
    }

    public long getOccurences(){
        return occurences;
    }

    public HashSet<String> getAnomalyTypes(){
        return anomalyTypes;
    }
}