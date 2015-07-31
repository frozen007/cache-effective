# cache-effective
a common cache operation lib

## 说明
开发了这么长时间Java服务器端，每次涉及到从缓存加载、获取时，都要写很多重复的代码。于是创建了这个项目，采用函数式编程，用于封装缓存的一些通用操作。

## 用法
### Jcs抽象类：JcsCacheDao
```
//定义缓存类
@Service
public class AccountOfficialJcsDao extends JcsCacheDao<Set<String>> {

    @Autowired
    private IAccountService accountService;

    public AccountOfficialJcsDao() {
        this.cachedKey = "account:official:all";
        this.expiredTime = 300;
        this.timeToLiveLimit = 30;
    }

    @Override
    public Set<String> reload() {
        return accountService.getAllAccountOfficial();
    }
}


//在其他类中使用
@Autowired
private AccountOfficialJcsDao accountOfficialJcsDao;
......

Set<String> accountOfficialUids = accountOfficialJcsDao.get();
......

```

### 通用缓存控制类：GeneralCacheDominator
```
//简单的局部Map缓存方法-getCachedMapValue
Map<Long, UserInfo> userInfoCache = new HashMap<>();
......
UserInfo userInfo = GeneralCacheDominator.getCachedMapValue(uid, userInfoCache,
                new ValueGetter<UserInfo>() {
                    @Override
                    public UserInfo get() {
                        return userService.getUserInfo(uid);
                    }
                });

//更复杂的全局缓存方法getCachedValue
String value = GeneralCacheDominator.getCachedValue("test", context, new CacheOperator<String>() {
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
        })

```