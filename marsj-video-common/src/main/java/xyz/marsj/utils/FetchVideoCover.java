package xyz.marsj.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchVideoCover {
	private String ffmpegEXE;

	public FetchVideoCover(String ffmpegEXE) {
		this.ffmpegEXE = ffmpegEXE;
	}
	
	public void convertor(String vedioInputPath,String coverOutputPath) throws Exception {
		//ffmpeg.exe -ss 00:00:01 -y -i input.mp4  -vframes 1 output.jpg
		List<String> command=new ArrayList<>();
		command.add(ffmpegEXE);
		command.add("-ss");
		command.add("00:00:01");
		command.add("-y");
		command.add("-i");
		command.add(vedioInputPath);
		command.add("-vframes");
		command.add("1");
		command.add(coverOutputPath);
		
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
	FetchVideoCover cover = new FetchVideoCover("D:\\Program Files\\ffmpeg-win64-static\\bin\\ffmpeg.exe");
		
		try {
			cover.convertor("D:\\temp\\1807137T15W15RKP\\video\\input.mp4", "D:\\temp\\1807137T15W15RKP\\video\\output.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
