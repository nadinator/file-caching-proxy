/* Sample skeleton for proxy */

import java.io.*;
import java.rmi.*;
import java.util.logging.*;

class Proxy {

  // Logger variables
  public static Logger logger = Logger.getLogger("MyLog");  
  public static FileHandler fh_log;  

  private static class FileHandler implements FileHandling {

    /** 
     * Handle open() request from client
     * @param path path of target file
     * @param o open option (READ, WRITE, CREATE, CREATE_NEW)
     * @return file descriptor when succeed, error code when fail
    */
    public int open(String path, OpenOption o) {
      RandomAccessFile f = null;

      switch (o) {
        case READ:
          try {
            f = new RandomAccessFile(path, "r");
          } catch (FileNotFoundException e) {
            logger.throwing("FileHandler", "open", e);
            return FileHandling.Errors.ENOENT;
          }
          break;

        case WRITE:
          try {
            f = new RandomAccessFile(path, "w");
          } catch (FileNotFoundException e) {
            return FileHandling.Errors.ENOENT;
          } catch (SecurityException e) {
            return FileHandling.Errors.EPERM;
          }
          break;

        case CREATE:
          try {
            f = new RandomAccessFile(path, "rw");
          } catch (FileNotFoundException e) {
            return FileHandling.Errors.ENOENT;
          }
          break;

        case CREATE_NEW:
          try {
            f = new RandomAccessFile(path, "rw");
          } catch (FileNotFoundException e) {
            return FileHandling.Errors.ENOENT;
          }
          break;
      
        default:
          break;
      
      }

      return f;
    }

    public int close(int fd) {
      return Errors.ENOSYS;
    }

    public long write(int fd, byte[] buf) {
      return Errors.ENOSYS;
    }

    public long read(int fd, byte[] buf) {
      return Errors.ENOSYS;
    }

    public long lseek(int fd, long pos, LseekOption o) {
      return Errors.ENOSYS;
    }

    public int unlink(String path) {
      return Errors.ENOSYS;
    }

    public void clientdone() {
      return;
    }

  }

  private static class FileHandlingFactory implements FileHandlingMaking {
    public FileHandling newclient() {
      return new FileHandler();
    }
  }

  // From stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger 
  public static void createLogger() throws IOException, SecurityException {
    try {
      fh_log = new FileHandler("../logs/MyLogFile.log");  
      logger.addHandler(fh_log);
      logger.setUseParentHandlers(false); // turn off console output
      logger.setLevel(Level.ALL); // output all message levels
      SimpleFormatter formatter = new SimpleFormatter();  
      fh_log.setFormatter(formatter);  
    } catch (SecurityException e) {  
      logger.throwing("Server", "main", e); 
    } catch (IOException e) {  
      logger.throwing("Server", "main", e);
    } 
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Proxy activated...");
    createLogger();    
    (new RPCreceiver(new FileHandlingFactory())).run();
  }
}
