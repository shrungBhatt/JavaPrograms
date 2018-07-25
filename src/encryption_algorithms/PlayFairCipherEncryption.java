package encryption_algorithms;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class PlayFairCipherEncryption {

    /**
     * Steps of play fair cipher encryption
     * <p>
     * 1) Take a key and put that key in an matrix/array of 25(Letters should not repeat)
     * 2) Fill the rest of the slots of matrix using other alphabets and i,j exists together in a single cell.
     * 3) Take plain text combine all the letters and split consecutive repeating letters with X.
     * 4) Divide the combined plain text in 2 letters each and obtain cipher text by applying the following rules:
     * a. If both the letter are in same row, shift one index to the right of each letter.
     * b. If both the letter are in same column, shift one index below of each letter.
     * c. If both the letter are in different rows and columns, form a rectangle with two diagonal endpoints
     * of plain text letter and the remaining two diagonal endpoints
     *
     * @param args
     */


    private Scanner mScanner;

    private final char[] mKeyMatrix = new char[25];
    private final char[] mAlphabets = "abcdefghiklmnopqrstuvwxyz".toCharArray();
    private final char[][] mFinalKeyMatrix = new char[5][5];

    private ArrayList<Character> mAlphabetDictionary = new ArrayList<>();
    private ArrayList<Character> mKeyMatrixList = new ArrayList<>();

    private String mKey;
    private String mPlainText;
    private String mNonRepeatingPlainText;


    public PlayFairCipherEncryption() {
        mScanner = new Scanner(System.in);
    }


    void enterKey() {
        System.out.print("Enter the key: ");
        mKey = mScanner.nextLine();

        formMatrixOfKey(mKey);

    }

    /**
     * This method is used to generate the 5x5 matrix of the given key.
     *
     * @param key
     */
    private void formMatrixOfKey(String key) {

        char[] alphabets = key.toCharArray();
        ArrayList<Character> temp = new ArrayList<>();

        //creating an alphabet dictionary arraylist
        for (char alphabet : alphabets) {
            if (mAlphabetDictionary != null) {
                if (!mAlphabetDictionary.contains(alphabet)) {
                    mAlphabetDictionary.add(alphabet);
                }
            }
        }


        if (mAlphabetDictionary != null) {
            for (int i = 0; i < mAlphabetDictionary.size(); i++) {
                mKeyMatrix[i] = mAlphabetDictionary.get(i);
            }

            temp.addAll(mAlphabetDictionary);
        }

        for (int i = mAlphabetDictionary.size(); i < mKeyMatrix.length; i++) {
            for (char alphabet : mAlphabets) {
                if (!temp.contains(alphabet)) {
                    temp.add(alphabet);
                    mKeyMatrix[i] = alphabet;
                    break;
                }
            }
        }

        for (char alphabet : mKeyMatrix)
            mKeyMatrixList.add(alphabet);

        int totalIndex = 0;

        System.out.println("\n" + "The Key matrix is...." + "\n");

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                mFinalKeyMatrix[i][j] = mKeyMatrix[totalIndex];
                System.out.print(mKeyMatrix[totalIndex] + "\t");
                totalIndex++;
            }
            System.out.print("\n");
        }

    }

    /**
     * This method is used to separate two repeating letters with x.
     */
    public void convertPlainTextToCipherText() {

        System.out.println("Enter the plain text: ");

        mPlainText = mScanner.nextLine();

        String formattedPlainText = mPlainText.replaceAll("\\s+", "");

        StringBuilder formattedPlainTextSb = new StringBuilder(formattedPlainText);


        //this is to keep a count of how many x were inserted previously.
        int indexCount = 0;

        for (int i = 0; i < formattedPlainText.length(); i++) {

            char currentChar = formattedPlainText.charAt(i);
            char nextChar = 0;

            if (i + 1 < formattedPlainText.length()) {
                nextChar = formattedPlainText.charAt(i + 1);
            }

            if (currentChar == nextChar) {
                formattedPlainTextSb.insert((i + indexCount) + 1, 'x');
                indexCount++;

            }
        }

        mNonRepeatingPlainText = formattedPlainTextSb.toString();
        System.out.println("The formatted plain text is: "+mNonRepeatingPlainText);



    }

    public void generateCipherText(){
        ArrayList<String> dividedPlainText = getDividedPlainTextArray();




    }

    private ArrayList<String> getDividedPlainTextArray() {

        ArrayList<String> tokens = new ArrayList<>();

        String nonRepeatingPlainText = mNonRepeatingPlainText.replaceAll("(..)", "$1 ");

        StringTokenizer stringTokenizer = new StringTokenizer(nonRepeatingPlainText," ");

        while (stringTokenizer.hasMoreTokens()){
            tokens.add(stringTokenizer.nextToken());
        }

        return tokens;
    }

}
