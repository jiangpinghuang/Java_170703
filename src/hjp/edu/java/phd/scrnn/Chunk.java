package hjp.edu.java.phd.scrnn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Chunk {

	public static String binDir = "E:\\MacBook\\Workspace\\Tempshop";

	public static String Sequence(String[] tokens) {
		String str = "";

		for (String tok : tokens) {
			if (str.length() == 0) {
				str = tok;
			} else {
				str = str + " " + tok;
			}
		}
		str = str + "\t";
		return str;

	}

	public static void MSRPSpliter(String filePath, String outputFile) {
		File file = new File(filePath);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("0") || line.startsWith("1")) {
					String strline = "";
					System.out.println(line);
					String[] sents = line.split("\t");

					String[] ltokens = Tokener(sents[3]);
					String[] rtokens = Tokener(sents[4]);

					String[] ltags = POSTagger(ltokens);
					String[] rtags = POSTagger(rtokens);

					String[] lchunkers = Chunker(ltokens, ltags);
					String[] rchunkers = Chunker(rtokens, rtags);

					strline = sents[0] + "\t" + sents[3] + "\t" + Sequence(ltokens) + Sequence(ltags)
							+ Sequence(lchunkers) + sents[4] + "\t" + Sequence(rtokens) + Sequence(rtags)
							+ Sequence(rchunkers);
					System.out.println(strline);
					writeFile(outputFile, strline);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String[] Tokener(String sent) {
		InputStream modelIn = null;
		TokenizerModel model = null;

		try {
			modelIn = new FileInputStream(binDir + "/Library/OpenNLP/bin/en-token.bin");
			model = new TokenizerModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize(sent);

		return tokens;
	}

	public static String[] POSTagger(String[] tok) {
		InputStream modelIn = null;
		POSModel model = null;

		try {
			modelIn = new FileInputStream(binDir + "/Library/OpenNLP/bin/en-pos-maxent.bin");
			model = new POSModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		POSTaggerME tagger = new POSTaggerME(model);
		String tags[] = tagger.tag(tok);

		return tags;
	}

	public static String[] Chunker(String[] tok, String[] tag) throws IOException {
		InputStream modelIn = null;
		ChunkerModel model = null;

		try {
			modelIn = new FileInputStream(binDir + "/Library/OpenNLP/bin/en-chunker.bin");
			model = new ChunkerModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		ChunkerME chunker = new ChunkerME(model);
		String chk[] = chunker.chunk(tok, tag);

		return chk;
	}

	public static void writeFile(String filePath, String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
			writer.write(content + "\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();

		String inputFile = "E:\\MacBook\\Workspace\\Workshop\\Corpus\\msc\\train.txt";
		String outputFile = "E:\\Downloads\\Workspace\\Tempshop\\train.txt";

		String tinputFile = "E:\\MacBook\\Workspace\\Workshop\\Corpus\\msc\\test.txt";
		String toutputFile = "E:\\Downloads\\Workspace\\Tempshop\\test.txt";

		MSRPSpliter(inputFile, outputFile);
		MSRPSpliter(tinputFile, toutputFile);

		long end = System.currentTimeMillis();

		System.out.println("It costs " + (end - start) / 1000 + " seconds!");

	}

}
