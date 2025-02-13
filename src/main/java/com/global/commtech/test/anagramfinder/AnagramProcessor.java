package com.global.commtech.test.anagramfinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class AnagramProcessor {

    /**
     * Processes all anagrams found in a file located at the supplied location.
     *
     * Once a batch has been isolated, it delegates any further processing to the supplied consumer.
     * Use of a consumer (which acts on a subset of the total potential number of results) ensures the processor can
     * scale to handle very large files (since we minimise the data held in memory to a subset that we know will fit).
     *
     * @param filePath the location of the file containing anagrams.
     * @param consumer allows for further processing of the batch (as defined by the implementation).
     */
    public void processAnagrams(Path filePath, AnagramConsumer consumer) {
        // In memory cache of a string in the file plus its anagrams.
        // (as per the specification this is guaranteed to fit in memory as opposed to the whole file which may not).
        Map<String, List<String>> anagramGroups = new HashMap<>();
        // Have to use a single element array here as it needs to be final so we need to access it from within out stream processing logic.
        final Integer[] currentLength = {-1};

        // Stream the file data and process it.
        try (var lines = Files.lines(filePath)) {
            lines.map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(str -> {
                        // As per the specification the strings in the file are ordered by size so when it increases we know we have completed a batch.
                        if (currentLength[0] != -1 && str.length() != currentLength[0]) {
                            // Send current batch to consumer to process further and clear memory cache ready for the next batch.
                            consumer.consume(anagramGroups);
                            anagramGroups.clear();
                        }

                        anagramGroups.computeIfAbsent(sortedString(str), k -> new ArrayList<>()).add(str);
                        currentLength[0] = str.length();
                    });

            // Process the final batch.
            consumer.consume(anagramGroups);

        } catch (IOException e) {
            System.err.println("Error reading anagrams file: " + e.getMessage());
        }
    }

    static String sortedString(String str) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    @FunctionalInterface
    public interface AnagramConsumer {
        void consume(Map<String, List<String>> anagrams);
    }
}