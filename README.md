# Anagram Finder
A simple command line utility for finding anagrams in a specified file

## Software required to run this
* Java 17
* Groovy 3

## Building and Running the tests
```
./gradlew clean build
```

## Running the program
```
./gradlew bootRun --args="example2.txt" 
```
where example2.txt is the text file that we want to search for anagrams

## Notes

Streams in a data file and processes batches of Strings of the same length.
Each String is sorted and compared to determine anagrams.

The <b>AnagramProcessor</b>'s sole responsibility is to find any anagrams in the file, it delegates any post processing to a <b>Consumer</b>. 
The <b>AnagramService</b> orchestrates this process and provides said consumer to the processor to handle each batch.
The consumer implementation (provided via the service) prints the batch to stdout.

Streaming the data, printing each batch inline and clearing the cache for the next batch ensures that the application can handle files of any size.

Unit tests (written in Groovy and Spock) have been added to exercise the <b>AnagramProcessor</b> and <b>AnagramService</b>.
Tests cover happy path, error scenarios and edge cases (more can obviously be added if time allows).


## Assumptions (inferred from AnagramExercise.pdf)

- File is ordered by string length.
- Any group of anagrams will fit into memory, the whole file may not.
- Duplicates are not filtered out (replacing ArrayList with Set would allow for this).
- All Strings are returned regardless of whether they have anagrams.
- Each group size is separated by a blank line.

## Big O analysis
O(N)

## Next steps.

- Cater for non alpha-numeric characters.
- Improve performance
  - Introduce parallelism to process each batch of Strings of the same length in a separate thread (more complex and could result in out of order outputs without additional refinements to the orchestration).
- More test cases and potentially integration tests.




