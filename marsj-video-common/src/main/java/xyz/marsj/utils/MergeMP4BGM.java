package xyz.marsj.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeMP4BGM {
	private String ffmpegEXE;

	public MergeMP4BGM(String ffmpegEXE) {
		this.ffmpegEXE = ffmpegEXE;
	}
	
	public void convertor(String vedioInputPath,String bgmPath,double duration,String vedioOutputPath) throws Exception {
		//ffmpeg.exe -i input.mp4 -i 夜访吸血鬼.mp3 -t 3 -y output.mp4
		List<String> command=new ArrayList<>();
		command.add(ffmpegEXE);
		command.add("-i");
		command.add(vedioInputPath);
		command.add("-i");
		command.add(bgmPath);
		command.add("-t");
		command.add(String.valueOf(duration));
		command.add("-y");
		command.add(vedioOutputPath);
		
		ProcessBuilder builder=new ProcessBuilder(command);
		Process process = builder.start();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String line="";
		while((line=bufferedReader.readLine())!=null) {
		}
		if(bufferedReader!=null) {
			bufferedReader.close();
		}
		if(inputStreamReader!=null) {
			inputStreamReader.close();
		}
		if(errorStream!=null) {
			errorStream.close();
		}
	}
	public static void main(String[] args) {
	MergeMP4BGM mergeMP4BGM = new MergeMP4BGM("D:\\Program Files\\ffmpeg-win64-static\\bin\\ffmpeg.exe");
		
		try {
			mergeMP4BGM.convertor("D:\\Program Files\\ffmpeg-win64-static\\bin\\的.mp4", 
					"D:\\Program Files\\ffmpeg-win64-static\\bin\\夜访吸血鬼.mp3", 3,
					"D:\\Program Files\\ffmpeg-win64-static\\bin\\java.mp4");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
