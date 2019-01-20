package controller;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import parser.Parse;

public class Main extends model.order.Order {

	public static final String SOURCE_HTML_FILE_PATH = "C:\\Sharekhan\\Algo\\10.01\\source.html";

	public static void main(String[] args) throws IOException {

		Scanner sc = new Scanner(System.in);
		while (true) {

			System.out.print("Please Enter Option:");

			int i = sc.nextInt();

			switch (i) {

			case 1:
				System.out.println("Executing ORB");
				new Main().startORB();
				break;

			default:
				System.out.println("Invalid Option");
			}
		}
	}

	private void prepareHTML(String destFileName, String destContent) {

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(destFileName);
			bw = new BufferedWriter(fw);
			bw.write(new String(Files.readAllBytes(Paths.get(SOURCE_HTML_FILE_PATH))).replace("KITE_REPLACE_DATA_HERE",
					destContent));

			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {

			System.err.println("File Write not possible");
			e.printStackTrace();
		}
	}

	private void startORB() throws IOException {

		String DEST_HTML_FILE_ORB = "C:\\Sharekhan\\TradeTigerNew\\ScripList\\sampleSheet.html";
		ArrayList<List<String>> sourceContent = new ArrayList<List<String>>();

		sourceContent = Parse.parseCSV("C:\\Sharekhan\\TradeTigerNew\\ScripList\\sampleSheet.csv");

		prepareHTML(DEST_HTML_FILE_ORB, csv2Json(sourceContent, 1));
		Desktop.getDesktop().browse(new File(DEST_HTML_FILE_ORB).toURI());
	}
}
