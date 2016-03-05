package space.yixian.hadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.math3.optimization.linear.AbstractLinearOptimizer;
import org.apache.hadoop.RandomTextWriterJob.RandomTextMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


public class SpecifiedUserRecommendation{
		

		private final static Integer MOVIE_SUM = 1682;
		private final static Integer USER_SUM = 943;
		private static Scanner scanner;
		
		
		
		public static void main(String[] args) throws Exception {		
			
			Integer userId = 0;	
			scanner = new Scanner(System.in);


			while(userId < 1 || userId > USER_SUM){
				
				if(!scanner.hasNextInt()){
					System.out.println("Please input a number");
					scanner.nextLine();
				}else{
					userId = scanner.nextInt();
					if(userId < 1 || userId > USER_SUM){
						System.out.println("Please input a number(0-943)");
					}
				}
			}
			

			Configuration configuration = new Configuration();
			configuration.set("fs.defalutFS","hdfs://localhost:8088");
			FileSystem fs = FileSystem.get(configuration);
			
			FSDataInputStream inputStream = fs.open(new Path("hdfs://localhost:8020/u.data"));
			
			String newFileAdd = "hdfs://localhost:8020/SelectedUser";
			FSDataOutputStream outputStream = fs.create(new Path(newFileAdd));
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			
			String aLine = null;
			Integer[] userMatrix = new Integer[MOVIE_SUM];
			Arrays.fill(userMatrix,-1); // default -1: not rated
			
			
			 //find the specified user's rated movies  
			while (( aLine = reader.readLine() ) !=null) {
				String[] split = aLine.split(" |\t");
				Integer user = Integer.valueOf(split[0]);
				if(user == userId){
					Integer rate = Integer.valueOf(split[2]); 
					Integer  movieId = Integer.valueOf(split[1]);
					userMatrix[movieId] = rate;
				}				
			}
			
			/**
			 * 生成指定用户的评分矩阵 	form the specified user's rated matrix 
			 * 矩阵存储格式	Matrix format: rowNumber \t columnNumber \t rate
			 * 矩阵的行号rowNumber包含了所有电影id，如果某些电影该用户没有打分，那么分数为-1
			 * rowNumber covers all movieIDs. if the user didn't rate certain movies, the rate will be -1
			 */
			for(int i = 0; i < userMatrix.length; i++){
				writer.write( (i+1) + "\t" + "1" + "\t" + userMatrix[i]); 
				//System.out.println( (i+1) + "\t" + "1" + "\t" + userMatrx[i]);
			}

		}
}


