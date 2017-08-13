import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
 
public class Simple2 {
	
	static int cloudNumber = 4;		
	
	static String srcPath = "C:/Users/조민재/Desktop/";		//원본파일 위치
	static String dstPath = "C:/Users/조민재/Desktop/Distribution/";	//Segment파일들 저장 위치 및 병합한 파일 저장 위치
	
	
//	static String combinePath = "C:/Users/조익환/Desktop/Complete/";	//combine파일 저장 위치
	
	
	static String name = "KTNet SWOT.pptx";
	//static String name = "Moon.mp4";
	static int index = 0;
	
	
	
	private static void mkdir(){
		
	}
	
	// ID, Path, FileName을 가지고 hash 만들기
	private static String hashname(String ID, String nFileName, String defaultpath) throws NoSuchAlgorithmException{
		String hashedPath = sha1((ID+"_"+nFileName));
		String motherdir = hashedPath.substring(1,3);
		String sondir = hashedPath.substring(3);
		
		//부모 디렉토리 만들기
		File mother = new File((defaultpath+motherdir));
		if(!mother.exists()){
			mother.mkdirs();
			System.out.println("Make mother directory");
		}
		else{
			System.out.println("Already the dir is existed");
		}
		//자식 디렉토리 만들기
		File son = new File((defaultpath+"/"+motherdir +"/"+ sondir));
		if(!son.exists()){
			son.mkdirs();
			System.out.println("Make son directory");
		}
		else{
			System.out.println("Already the dir is existed");
		}
		return (defaultpath+"/"+motherdir+"/"+sondir);
	}
	

    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
    }
	
	
	private static void splitFile(String nFilePath, String nFileName, InputStream fi, long fs , String ID){
		
		
		
		try{
			
			
    		long packetSize = fs/cloudNumber;    	
			int read = 0;
			int total = 0;
               
			System.out.println("Packet Size: "+packetSize+" bytes");
			
			BufferedInputStream bfi = new BufferedInputStream(fi);                      
			ByteBuffer buf = ByteBuffer.allocate((int)packetSize);  
			
			/*ByteBuffer buf = ByteBuffer.allocate(8);  
			buf.putLong(packetSize);*/	
			
			byte[] readBuffer = buf.array();
			
			++index;
			
			//ID를 이용한 디렉토리 만들기
			String logpath = hashname( ID, nFileName, nFilePath);
			
			// 로그 파일			
			File log = new File((logpath+"/log.txt"));
			FileWriter fw = new FileWriter(log, true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			File nFile = new File((nFilePath+"_"+index+"/") + nFileName +"_segment "+(index));
			String content1 = (nFilePath+"_"+index+"/") + nFileName +"_segment "+(index);
			
  			bw.write(content1);
  			bw.newLine();
			FileOutputStream fo = new FileOutputStream(nFile);
			do{  	   	  
				
				read = bfi.read(readBuffer);
              	if(read == -1){
                    break;
              	}
              	fo.write(readBuffer,0,read);
	
              	total += read;
              	
              	if(index == cloudNumber){			
              		total = total + (int)(fs%cloudNumber);
              	}
              	
              	
              	if(total%packetSize==0 && index != cloudNumber){       		
          			//System.out.println(total);
          			fo.flush();
          			fo.close();                            
          			++index;
          			File nfile = new File((nFilePath+"_"+index+"/") + nFileName +"_segment "+(index));
          			
          			//로그 파일에 내용 저장
          			String contents = (nFilePath+"_"+index+"/") + nFileName +"_segment "+(index);
          			bw.write(contents);
          			bw.newLine();
          			
          			
          			fo = new FileOutputStream(nfile);                           
              	}else if(total==packetSize+(int)(fs%cloudNumber) && index == cloudNumber){
              		fo.flush();
          			fo.close();    
          			++index;
          			File nfile = new File((nFilePath+"_"+index+"/") + nFileName +"_segment "+(index));
          			String contents = (nFilePath+"_"+index+"/") + nFileName +"_segment "+(index);
          			bw.write(contents);
          			bw.newLine();
          			
          			fo = new FileOutputStream(nfile);   
              	}
			}while(true);          
               	fi.close();
               	fo.flush();
               	fo.close();     
               	bw.close();
               	
    		}catch (Exception e) {
    			e.printStackTrace();     
    		}
        System.err.println("----------------분할완료----------------");
        
    	}

	private static String[] readPath(String hash , String nFilePath) throws IOException{
		String[] files= new String[4];
		int i = 0 ;
		String motherdir = hash.substring(1,3);
		String sondir = hash.substring(3);
		File file = new File((nFilePath+"/"+motherdir+"/"+sondir+"/"+"log.txt"));
		FileReader fr = null;
		BufferedReader br = null;
		String read = null;
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		
		while((read=br.readLine())!=null){
		files[i]=read;
		System.out.println(read);
		i++;
		}
		if(fr!=null)fr.close();
		if(br!=null)br.close();
		return files;
	}
	
	private static void combineFile(String oriFileName, String nFilePath, long fs, String ID) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
    	
		long packetSize = fs/cloudNumber; 
		//File nFiles = new File(combinePath);
    	//String[] files = nFiles.list();
		
		//로그 파일 찾기
		String hashedcombine = ID + "_" + oriFileName;
		hashedcombine = sha1(hashedcombine);
		
		
//    	String[] files = {nFilePath+"_1/",nFilePath+"_2/",nFilePath+"_3/",nFilePath+"_4/"};
//    	for(int i=0;i<files.length;i++){
//    		files[i]=nFilePath+"_"+(i+1)+"/"+ oriFileName +"_segment "+(i+1);
//    	}
		String[] files = readPath(hashedcombine, nFilePath);
    	
    	
    	FileOutputStream nFo = new FileOutputStream(nFilePath+oriFileName);
    	//System.out.println(files[3]);
        	
    	for(int i=0;i<files.length;i++){
    		//FileInputStream nFi = new FileInputStream(nFilePath+files[i]);
    		
    		FileInputStream nFi = new FileInputStream(files[i]);

    		byte[] buf = new byte[(int)packetSize];

    		int readCnt = 0;
    		while((readCnt =  nFi.read(buf)) >-1){
    			nFo.write(buf,0,readCnt);
    		}
    	}	
        	nFo.flush();
        	nFo.close();
        	System.err.println("----------------병합완료----------------");
	}
 
	
   public static void main(String[] args) {
	   
     	try{
     		String filePath = srcPath;
     		String fileName = name;
     		
     		String ID = "ChoIKHWan";
     		System.out.println("ID : "+ ID);
     		
     		long fileSize;
     		File file = new File(filePath + fileName);
     		if (file.exists()){
     			long size = file.length();
     			fileSize = size;
           	}else{
           		fileSize=0;
           	}
     		System.out.println("File size: "+fileSize+" bytes");
                           
                        	   
            FileInputStream fi = new FileInputStream(file);
                          
            String nFilePath = dstPath;	
            String nFileName = name;	
                           
            //분할
            //splitFile(nFilePath, nFileName, fi, fileSize, ID);
 
            //병합
            
           combineFile(fileName, nFilePath, fileSize, ID);
                          
        }catch(Exception e){
        	e.printStackTrace();
        }
            
            
   }

}