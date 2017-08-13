import java.io.BufferedInputStream; 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
 
public class Simple {
	
	static int cloudNumber = 3;		
	
	static String srcPath = "c:/";		//�������� ��ġ
	static String dstPath = "c:/Download/Distribution/Seg";	//Segment���ϵ� ���� ��ġ
	static String combinePath = "c:/Download/Distribution/";	//combine���� ���� ��ġ
	
	
	static String name = "Presentation.pptx";
	//static String name = "Moon.mp4";
	static int index = 0;
	
	
	private static void splitFile(String nFilePath, String nFileName, InputStream fi, long fs){
		
		
		
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
			File nFile = new File((nFilePath+" "+index+"/") + nFileName +"_segment "+(index));
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
          			File nfile = new File((nFilePath+" "+index+"/") + nFileName +"_segment "+(index));
          			fo = new FileOutputStream(nfile);                           
              	}else if(total==packetSize+(int)(fs%cloudNumber) && index == cloudNumber){
              		fo.flush();
          			fo.close();    
          			++index;
          			File nfile = new File((nFilePath+" "+index+"/") + nFileName +"_segment "+(index));
          			fo = new FileOutputStream(nfile);   
              	}
			}while(true);          
               	fi.close();
               	fo.flush();
               	fo.close();          
               	
    		}catch (Exception e) {
    			e.printStackTrace();     
    		}
        System.err.println("----------------���ҿϷ�----------------");
        
    	}

	
	private static void combineFile(String oriFileName, String nFilePath, long fs) throws FileNotFoundException, IOException {
    	
		long packetSize = fs/cloudNumber; 
		File nFiles = new File(combinePath);
    	//String[] files = nFiles.list();
		
    	String[] files = {nFilePath+" 1/",nFilePath+" 2/",nFilePath+" 3/",nFilePath+" 4/"};
    	for(int i=0;i<files.length;i++){
    		files[i]=nFilePath+" "+(i+1)+"/"+ oriFileName +"_segment "+(i+1);
    	}
    	
    	
    	
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
        	System.err.println("----------------���տϷ�----------------");
	}
 
	
   public static void main(String[] args) {
     	try{
     		String filePath = srcPath;
     		String fileName = name;
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
                           
            //����
            //splitFile(nFilePath, nFileName, fi, fileSize);
 
            //����
            
            combineFile(fileName, nFilePath, fileSize);
                          
        }catch(Exception e){
        	e.printStackTrace();
        }
           
            
   }

}


//hi my name is hangbok