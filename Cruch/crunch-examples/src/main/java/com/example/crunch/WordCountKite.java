package com.example.crunch;

import org.apache.crunch.PCollection;
import org.apache.crunch.Pipeline;
import org.apache.crunch.PipelineResult;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.To;
import org.apache.crunch.types.avro.Avros;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.kitesdk.data.crunch.CrunchDatasets;

public class WordCountKite extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		//Check for arguments
		if (args.length != 2) {
			System.err.println("This program expects 2 arguments."
					+ "\n\tUsage: WordCount <input> <output>");
			System.exit(2);
		}
		ToolRunner.run(new Configuration(), new WordCountKite(), args);
	}
	
	public int run(String[] args) throws Exception {
		
		//Create a Pipeline
		Pipeline wcpipeline = new MRPipeline(WordCountKite.class, getConf());
		// Read lines from a textfile
		PCollection<String> lines = wcpipeline.readTextFile(args[0]);
		//Split lines into words
		PCollection<String> words = lines.parallelDo(new Tokenizer(), Avros.strings());
		//Associate a count to each word in the Pcollection<String> words -> PTable<String, Integer>
		//Unlike typical MapReduce, Kite dataset must already exist.
		/*
			Warning: Job might fail due to 'Java heap space' issue.
			Solution : Update kite writer cache-size setting using kite properties
				'kite-dataset update <URI> --set kite.writer.cache-size=2'
		*/
		words.count().write(CrunchDatasets.asTarget(args[1]));
		//Execute using done() method
		PipelineResult result = wcpipeline.done();
		//Return exit code
		return result.succeeded() ? 0 : 1 ;
	}
}
