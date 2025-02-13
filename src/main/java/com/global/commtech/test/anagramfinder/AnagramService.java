package com.global.commtech.test.anagramfinder;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnagramService {

    private final AnagramProcessor anagramProcessor;

    /**
     * Processes a file, printing each anagram group it finds on separate lines to stdout.
     * This will scale to handle arbitrarily large files.
     *
     * @param filePath the location of the file containing anagrams.
     */
    public void processFile(Path filePath) {
        anagramProcessor.processAnagrams(filePath, this::printAnagramGroups);
    }

    // Basic implementation that prints out the supplied anagram groups to stdout.
    private void printAnagramGroups(Map<String, List<String>> anagramGroups) {
        anagramGroups.values()
                .stream()
                .map(group -> String.join(",", group))
                .forEach(System.out::println);

        System.out.println(); // Blank line to separate groups
    }
}