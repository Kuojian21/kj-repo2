package com.kj.repo.hystrix.h;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class CommandUsingSemaphoreIsolation extends HystrixCommand<String> {
 
    private final int id;
    private long start,end ;
 
    public CommandUsingSemaphoreIsolation(int id) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                // since we're doing an in-memory cache lookup we choose SEMAPHORE isolation
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE) //设置使用信号量隔离策略
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(3)  //设置信号量隔离时的最大并发请求数
                                .withFallbackIsolationSemaphoreMaxConcurrentRequests(5)     //设置fallback的最大并发数
                        .withExecutionTimeoutInMilliseconds(300)));   //设置超时时间
        this.id = id;
        this.start = System.currentTimeMillis();
    }
 
    @Override
    protected String run() throws InterruptedException {
        // a real implementation would retrieve data from in memory data structure
        TimeUnit.MILLISECONDS.sleep(id*30);
        System.out.println("running normal, id="+id);
        return "ValueFromHashMap_" + id;
    }
 
    @Override
    protected String getFallback(){
        System.out.println(" fallback, id="+id);
        return "fallback:"+id;
    }
    
//    @Test
    public void maxCurrentRequst() throws InterruptedException {
        int count =10;
        while (count >0){
            int id = count--;
            new Thread(() -> {
                try {
                    new CommandUsingSemaphoreIsolation(id).execute();
                }catch (Exception ex){
                    System.out.println("Exception:"+ex.getMessage()+" id="+id);
                }
            }).start();
        }
     
        TimeUnit.SECONDS.sleep(100);
    }
     //注：使用信号量隔离，在同一个线程中即使循环调用new CommandUsingSemaphoreIsolation(id).queue()，run方法也是顺序执行;
 
}
  
