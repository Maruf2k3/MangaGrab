import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class App {
    public static void main(String[] args) throws Exception {
        // Prompt the user for the manga name and chapter number
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Manga name: ");
        String mangaName = scanner.nextLine();
        int chapterNumber = userVal(scanner, "Enter chapter number: ");
        scanner.close();

        //getting the websites search and searching 
        mangaLinkmanager(mangaName);
        
        // Construct the URL for the manga chapter page
        String chpLink = "https://kissmanga.org/chapter" + mangaLinkmanager(mangaName) +"/chapter-";
        Document chapter = Jsoup.connect(chpLink + chapterNumber).get();

        //chapter counter to check with the user given chapter [coming soon!]

        //to select each img
        Elements mangaPicLink = chapter.select("#centerDivVideo img");

        // Create a directory to save the images
        String dirName = mangaName+" " + chapterNumber;
        File dir = new File(dirName);
        dir.mkdir();

        int imgCount = 1;
        //going through the img and finding the link for each img
        for (Element element : mangaPicLink) {
            // Get the src attribute of the img element, which contains the image URL
            String imgUrl = element.attr("src");

            // Open a connection to the image URL
            URL url = new URL(imgUrl);
            URLConnection connection = url.openConnection();

            // Set the user agent to mimic a browser, to avoid getting blocked
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

            // Get the MIME type of the image from the "Content-Type" response header
            String contentType = connection.getHeaderField("Content-Type");
            String fileExtension = contentType.substring(contentType.indexOf("/") + 1);

            // Open a stream to read the image data
            InputStream in = connection.getInputStream();

            // Create a file to save the image
            File file = new File(dirName + "/" + imgCount + "." + fileExtension);
            FileOutputStream fileOutput = new FileOutputStream(file);

            // Read the image data from the stream and write it to the file
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                //writting the img info
                fileOutput.write(buffer, 0, bytesRead);
            }
            // Close the streams
            in.close();
            fileOutput.close();
            imgCount++;
        } 
    }

    private static String mangaLinkmanager(String mangaName)throws Exception{
        // Construct the URL for the manga search page
        String searchUrl = "https://kissmanga.org//manga_list?q=" + mangaName + "&action=search" ;

        // Make an HTTP GET request to the URL and parse the HTML content
        Document searchDoc = Jsoup.connect(searchUrl).get();

        // Find the link to the manga page
        Elements mangaLinkElement = searchDoc.select(".item_movies_link");
        //finding the link from the href
        String mangaLink = mangaLinkElement.attr("href").substring(6);  

        return mangaLink;
    }

    //user validation so that we get only integer
    public static int userVal(Scanner sc , String promt){

        int userInput = -1;//var to neglect any -ve number

        while (userInput<=0){
            System.out.print(promt);

            String userString = sc.nextLine();//takig a string from user

            try{

                userInput = Integer.valueOf(userString);//try to convert it to int
            }
            catch(NumberFormatException nFE){//if can't be converted to string then throw this error
                System.out.println(userString+ " is not a vlid input!");
                userInput = -1;//restarts the while loop
            }
        }
        return userInput;//finally returns the new value
    }


}
