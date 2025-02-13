package com.global.commtech.test.anagramfinder

import spock.lang.Specification

import java.nio.file.Path

class AnagramServiceSpec extends Specification {
    def anagramProcessor = Mock(AnagramProcessor)
    def service = new AnagramService(anagramProcessor)
    def path = Path.of("test.txt")
    def output = new ByteArrayOutputStream()

    def "should print multiple anagram groups, one group per line with each word separated by a comma"() {
        given: 'we have the service output'
            System.out = new PrintStream(output)

        when: 'we process the file'
            service.processFile(path)

        then: 'the output is as expected'
            1 * anagramProcessor.processAnagrams(path, _) >> { Path p, AnagramProcessor.AnagramConsumer consumer ->
                consumer.consume([
                        "aet": ["eat", "tea"],
                        "ops": ["sop", "pos"]
                ])
            }

        output.toString() == "eat,tea\nsop,pos\n\n"
    }

    def "should print a single anagram group correctly"() {
        given: 'we have the service output'
            System.out = new PrintStream(output)

        when: 'we process the file'
            service.processFile(path)

        then: 'the output is as expected'
            1 * anagramProcessor.processAnagrams(path, _) >> { Path p, AnagramProcessor.AnagramConsumer consumer ->
                consumer.consume([
                        "aet": ["eat", "tea", "ate"]
                ])
            }

        output.toString() == "eat,tea,ate\n\n"
    }

    def "should print each unique word on separate line when no anagrams exist"() {
        given: 'we have the service output'
            System.out = new PrintStream(output)

        when: 'we process the file'
            service.processFile(path)

        then: 'the output is as expected'
            1 * anagramProcessor.processAnagrams(path, _) >> { Path p, AnagramProcessor.AnagramConsumer consumer ->
                consumer.consume([
                        "cat": ["cat"],
                        "dog": ["dog"]
                ])
            }

        output.toString() == "cat\ndog\n\n"
    }

    def "should handle large number of anagram groups by processing in batches"() {
        given: 'we have the service output'
            System.out = new PrintStream(output)

        when: 'we process the file'
            service.processFile(path)

        then: 'the output is as expected'
            1 * anagramProcessor.processAnagrams(path, _) >> { Path p, AnagramProcessor.AnagramConsumer consumer ->
                consumer.consume(["aet": ["eat", "tea"]])
                consumer.consume(["ops": ["sop", "pos"]])
                consumer.consume(["rst": ["str", "trs"]])
            }

        output.toString() == "eat,tea\n\nsop,pos\n\nstr,trs\n\n"
    }

    def "should process single letter words correctly"() {
        given: 'we have the service output'
            System.out = new PrintStream(output)

        when: 'we process the file'
            service.processFile(path)

        then: 'the output is as expected'
            1 * anagramProcessor.processAnagrams(path, _) >> { Path p, AnagramProcessor.AnagramConsumer consumer ->
                consumer.consume([
                        "a": ["a"],
                        "b": ["b"]
                ])
            }

        output.toString() == "a\nb\n\n"
    }
}
