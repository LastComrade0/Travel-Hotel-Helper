package hoteltools.reviewparse;

import hoteltools.ReviewCollectionsThreadSafe;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class ReviewCollectionLoader {
    private Phaser phaser = new Phaser();
    private ExecutorService threadPool;
    private ReviewCollectionsThreadSafe reviewCollectionsThreadSafe;
    //private Logger logger = LogManager.getLogger();

    /**
     * Contstructor
     * Decides how many thread pool to create based on input thread count
     * @param reviewCollectionsThreadSafe
     * @param threadCount
     */
    public ReviewCollectionLoader(ReviewCollectionsThreadSafe reviewCollectionsThreadSafe, int threadCount){
        this.reviewCollectionsThreadSafe = reviewCollectionsThreadSafe;
        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Inner class for worker to parse JSON file
     */
    class ParseWorker implements Runnable{
        private Path file;

        /**
         * Constructor
         * Registers phaser and inputs current directory
         */
        public ParseWorker(Path file){
            this.file = file;
            phaser.register();
        }

        /**
         * Method to run current worker
         */
        @Override
        public void run() {

            try {

                //logger.debug("Parsing: " + file);
                List<HotelReview> reviewList = ReviewParser.parseReviewJson(file);

                reviewCollectionsThreadSafe.addReview(reviewList);

                //logger.debug("Finished Parsing: " + file);
            }
            finally {
                phaser.arriveAndDeregister();
            }

        }
    }

    /**
     * Method to traverse directory to parse all possible JSON files given the argument
     * @param directory
     */
    public void traverseDirectory(Path directory){
        try(DirectoryStream<Path> filesAndFolders = Files.newDirectoryStream(directory)){
            for(Path path : filesAndFolders){
                if(Files.isDirectory(path)){
                    traverseDirectory(path);
                }
                else{
                    if(path.toString().endsWith(".json")){
                        //logger.debug("Created a worker for " + path);
                        threadPool.submit(new ParseWorker(path));
                    }
                }
            }

        }

        catch(IOException e){
            //logger.error("IOException: not able to open the directory " + directory);
            System.out.println(e);
        }
    }

    /**
     * Method for thread to wait then shutdown
     */
    public void waitToFinish(){
        phaser.awaitAdvance(0);
        threadPool.shutdownNow();
    }
}
