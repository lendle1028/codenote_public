/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lendle
 */
public class ThreadUtils {
    public static ExecutorService DEFAULT_EXECUTOR_SERVICE=null;
    public static ExecutorService CODECOUCH_EXECUTOR_SERVICE=null;
    private static Map<String, ExecutorService> sessionWorkers=new HashMap<>();
    private static Map<String, Future> lastTaskResultMap=new HashMap<>();
    static{
        DEFAULT_EXECUTOR_SERVICE=Executors.newFixedThreadPool(10);
        CODECOUCH_EXECUTOR_SERVICE=Executors.newFixedThreadPool(3);
    }
    
    public static synchronized void submit(Runnable r){
        ThreadPoolExecutor threadPoolExecutor=(ThreadPoolExecutor) DEFAULT_EXECUTOR_SERVICE;
        Logger.getLogger(ThreadUtils.class.getName()).info("Submit task to DEFAULT_EXECUTOR_SERVICE, active task count="+threadPoolExecutor.getActiveCount());
        submit(r, DEFAULT_EXECUTOR_SERVICE);
    }
    
    public static synchronized void submit(Runnable r, ExecutorService es){
        if(es==CODECOUCH_EXECUTOR_SERVICE){
            ThreadPoolExecutor threadPoolExecutor=(ThreadPoolExecutor) CODECOUCH_EXECUTOR_SERVICE;
            Logger.getLogger(ThreadUtils.class.getName()).info("Submit task to CODECOUCH_EXECUTOR_SERVICE, active task count="+threadPoolExecutor.getActiveCount());
        }
        es.submit(r);
    }
    /**
     * submit to session worker (a single thread sequential queue)
     * @param session
     * @param r 
     */
    public static void submit(HttpSession session, Runnable r){
        synchronized (session) {
            String id=session.getId();
            ExecutorService es=sessionWorkers.get(id);
            if(es==null){
                es=Executors.newSingleThreadExecutor();
                sessionWorkers.put(id, es);
            }
            Future f=es.submit(r);
            lastTaskResultMap.put(id, f);
        }
    }
    /**
     * waiting for all jobs in session worker queue are done
     * @param session 
     */
    public static void awaitFinished(HttpSession session){
        synchronized (session) {
            String id=session.getId();
            Future f=lastTaskResultMap.get(id);
            if(f!=null){
                try {
                    f.get(1, TimeUnit.DAYS);
                } catch (Exception ex) {
                    Logger.getLogger(ThreadUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
