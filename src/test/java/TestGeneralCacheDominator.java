import com.myz.base.cache.CacheContext;
import com.myz.base.cache.CacheElement;
import com.myz.base.cache.CacheManager;
import com.myz.base.cache.CacheOperator;
import com.myz.base.cache.CachedObject;
import com.myz.base.cache.GeneralCacheDominator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: TestGeneralCacheDominator
 * @author: mingyu.zhao
 * @date: 15/5/29 下午7:40
 */
public class TestGeneralCacheDominator {
    private static AtomicInteger lck = new AtomicInteger();
    static final CacheContext context = new CacheContext("test", 30, 5);

    public static void main(String[] args) throws InterruptedException {
        /*
        while(true) {
            String o = getCache();
            System.out.println(o);
            Thread.sleep(10000);
        }*/

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + ":" + getCache());
                }
            }, "t-" + i);
            t.start();
        }
    }


    private static String getCache() {
        return GeneralCacheDominator.getCachedValue("test", context, new CacheOperator<String>() {
            @Override
            public boolean lock(String key) {
                int current = lck.get();
                return lck.compareAndSet(current, current + 1);
            }

            @Override
            public void unlock(String key) {

            }

            @Override
            public CachedObject<String> getCachedObject(String key) {
                CacheElement element = CacheManager.getInstance().getElement(key);
                if (element != null) {
                    return new CachedObject<>(element.getTimeToLiveSeconds(), (String) (element.getValue()));
                }

                return null;
            }

            @Override
            public void setToCache(String key, String s, long expiredTime) {
                CacheManager.getInstance().put(key, s, context.getExpiredTime());
            }

            @Override
            public String reload() {
                Random rand = new Random();
                return "I'm cached " + Math.abs(rand.nextInt()) % 10000;
            }
        });
    }
}
