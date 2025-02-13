package com.global.commtech.test.anagramfinder

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

import static com.global.commtech.test.anagramfinder.AnagramProcessor.sortedString

class AnagramProcessorSpec extends Specification {
    def processor = new AnagramProcessor()
    def consumer = Mock(AnagramProcessor.AnagramConsumer)

    def anagramFile

    void setup() {
        anagramFile = Files.createTempFile("test", ".txt")
    }

    void cleanup() {
        Files.deleteIfExists(anagramFile)
    }

    def "should process multiple word groups and find anagrams when file contains words of different lengths"() {
        given: 'a file containing anagrams of different length'
            Files.write(anagramFile, ["cat", "act", "slain", "nails"].join("\n").getBytes())

        when: 'the file is processed'
            processor.processAnagrams(anagramFile, consumer)

        then: 'the result is as expected'
            1 * consumer.consume([(sortedString("act")): ["cat", "act"]])
            1 * consumer.consume([(sortedString("ailns")): ["slain", "nails"]])
    }

    def "should process empty groups when file contains empty lines"() {
        given: 'a file containing blank lines'
            Files.write(anagramFile, ["cat", "", "act", ""].join("\n").getBytes())

        when: 'the file is processed'
            processor.processAnagrams(anagramFile, consumer)

        then: 'the result is as expected'
            1 * consumer.consume([(sortedString("cat")): ["cat", "act"]])
    }

    def "should process single word length group when file contains words of same length"() {
        given: 'a file containing words of the same length'
            Files.write(anagramFile, ["cat", "act", "tac"].join("\n").getBytes())

        when: 'the file is processed'
            processor.processAnagrams(anagramFile, consumer)

        then: 'the result is as expected'
            1 * consumer.consume([(sortedString("cat")): ["cat", "act", "tac"]])
    }

    def "should process unique words when file contains no anagrams"() {
        given: 'a file containing unique words'
            Files.write(anagramFile, ["cat", "nails"].join("\n").getBytes())

        when: 'the file is processed'
            processor.processAnagrams(anagramFile, consumer)

        then: 'the result is as expected'
            1 * consumer.consume([(sortedString("act")): ["cat"]])
            1 * consumer.consume([(sortedString("ailns")): ["nails"]])
    }

    def "should print an error message if the file path is invalid"() {
        given: 'an invalid file path'
            def invalidFile = Path.of("non_existent_file.txt")
            def errorOutput = new ByteArrayOutputStream()
            System.setErr(new PrintStream(errorOutput)) // Redirect System.err

        when: 'the file is processed'
            processor.processAnagrams(invalidFile, consumer)

        then: 'an error message is printed to System.err'
            errorOutput.toString().contains("Error reading anagrams file") // Check error message

        cleanup:
            System.setErr(System.err) // Restore System.err after test
    }


}
