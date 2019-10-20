package it.unisa.ascetic.analysis.code_smell_detection.smellynessMetricProcessing;

import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;

import java.io.IOException;
import java.util.ArrayList;

public class SmellynessMetric {

    //apache.commons.math

    public double computeSmellyness(String pTestCase) throws IOException {
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        String[] blocks = pTestCase.split("_____");
        ArrayList<String> filteredBlocks = new ArrayList<String>();
        Double smellyness = 0.0;


        for (int k = 0; k < blocks.length; k++) {
            if (blocks[k].split("\n").length > 3) {
                filteredBlocks.add(blocks[k]);
            }
        }

        if (filteredBlocks.size() == 1) {
            return 0.0;
        }

        int comparison = 0;

        for (int k = 0; k < filteredBlocks.size(); k++) {
            for (int i = 1; i < filteredBlocks.size(); i++) {

                String[] document1 = new String[2];
                document1[0] = "block_" + k;

                String[] document2 = new String[2];
                document2[0] = "block_" + i;

                document1[1] = filteredBlocks.get(k);
                document2[1] = filteredBlocks.get(i);

                smellyness += cosineSimilarity.computeSimilarity(document1, document2);
                comparison++;
            }
        }

        smellyness = 1 - (smellyness / comparison);

        if (smellyness.isNaN()) {
            return 0.0;
        } else if (smellyness.isInfinite()) {
            return 0.0;
        } else return smellyness;

    }

}
