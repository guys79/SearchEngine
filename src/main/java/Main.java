import Model.Indexer;

public class Main {

    public static void main(String[] args) {

            long start = System.nanoTime();
            //String pathToCorpus = "C:\\Users\\guysc\\Downloads\\small";
            String pathToCorpus = "C:\\Users\\guysc\\Downloads\\corpus";
            String pathToStopWords = "C:\\Users\\guysc\\Downloads\\stop_words.txt";
            String des = "C:\\Users\\guysc\\Downloads\\New folder";
            Indexer indexer = new Indexer(pathToCorpus, pathToStopWords, des, true);
        try {

            indexer.parseDocumentsThread();
            long elapsedTime = System.nanoTime() - start;
            System.out.println("the size of the dictionary " + indexer.getDicSize());
            System.out.println(elapsedTime / 1000000000 + " second ~ " + (elapsedTime / 1000000000) / 60 + " minutes and " + (elapsedTime / 1000000000) % 60 + " seconds");


        } catch (Exception e) {
            e.printStackTrace();
            indexer.shutDown();
        }

    }

}
